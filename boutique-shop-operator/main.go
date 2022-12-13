/*
Copyright 2022.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package main

import (
	// "flag"
	"fmt"
	"os"

	// Import all Kubernetes client auth plugins (e.g. Azure, GCP, OIDC, etc.)
	// to ensure that exec-entrypoint and run can make use of them.
	"k8s.io/client-go/discovery"
	_ "k8s.io/client-go/plugin/pkg/client/auth"

	"github.com/go-logr/logr"
	routev1 "github.com/openshift/api/route/v1"
	"github.com/spf13/cobra"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/runtime/schema"
	utilruntime "k8s.io/apimachinery/pkg/util/runtime"
	clientgoscheme "k8s.io/client-go/kubernetes/scheme"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/healthz"
	"sigs.k8s.io/controller-runtime/pkg/log/zap"
	"sigs.k8s.io/yaml"

	demov1alpha1 "github.com/RHEcosystemAppEng/saas-ecommerce-boutique-shop/boutique-shop-operator/api/v1alpha1"
	"github.com/RHEcosystemAppEng/saas-ecommerce-boutique-shop/boutique-shop-operator/controllers"
	//+kubebuilder:scaffold:imports
)

var (
	scheme   = runtime.NewScheme()
	setupLog = ctrl.Log.WithName("setup")

	metricsAddr          string
	enableLeaderElection bool
	probeAddr            string
	externalAccessType   string
)

func init() {
	utilruntime.Must(clientgoscheme.AddToScheme(scheme))

	utilruntime.Must(routev1.AddToScheme(scheme))

	utilruntime.Must(demov1alpha1.AddToScheme(scheme))
	//+kubebuilder:scaffold:scheme

	externalUsage := fmt.Sprintf("The type of external access for the operand. Options are: %s, %s, %s, %s.",
		controllers.ExternalAccessIngress, controllers.ExternalAccessRoute,
		controllers.ExternalAccessServiceNodePort, controllers.ExternalAccessNone)

	rootCmd.PersistentFlags().StringVarP(&externalAccessType, "external-access-type", "e", string(controllers.ExternalAccessRoute), externalUsage)
	rootCmd.Flags().BoolVarP(&enableLeaderElection, "leader-elect", "", false, "enable leader election")
	rootCmd.Flags().StringVarP(&metricsAddr, "metrics-bind-address", "", ":8080", "The address the metric endpoint binds to.")
	rootCmd.Flags().StringVarP(&probeAddr, "health-probe-bind-address", "", ":8081", "The address the probe endpoint binds to.")

	rootCmd.AddCommand(manifestsCmd)
}

func main() {
	rootCmd.Execute()
}

var manifestsCmd = &cobra.Command{
	Use:   "manifests [path to BoutiqueShop yaml file]",
	Short: "Print manifests to stdout",
	Long:  "Print a yaml manifest to stdout that can be used to deploy the entire application stack without running the operator in the target cluster.",
	Args:  cobra.ExactArgs(1),
	Run: func(cmd *cobra.Command, args []string) {
		path := args[0]
		instance := demov1alpha1.BoutiqueShop{}
		data, err := os.ReadFile(path)
		if err != nil {
			setupLog.Error(err, "unable to read file")
			os.Exit(1)
		}
		err = yaml.Unmarshal(data, &instance)
		if err != nil {
			setupLog.Error(err, "unable to deserialize yaml")
			os.Exit(1)
		}

		reconciler := controllers.BoutiqueShopReconciler{
			Scheme:         scheme,
			ExternalAccess: controllers.ExternalAccess(externalAccessType),
		}

		err = reconciler.WriteManifests(&instance, os.Stdout)
		if err != nil {
			setupLog.Error(err, "unable to render manifests")
			os.Exit(1)
		}
	},
}

var rootCmd = &cobra.Command{
	Use:   "boutique-shop-operator",
	Short: "Boutique Shop Operator deploys and managed boutique shop in k8s clusters",
	PersistentPreRunE: func(cmd *cobra.Command, args []string) error {
		switch externalAccessType {
		case string(controllers.ExternalAccessIngress):
		case string(controllers.ExternalAccessNone):
		case string(controllers.ExternalAccessRoute):
		case string(controllers.ExternalAccessServiceNodePort):
		default:
			return fmt.Errorf("unknown external access type: %s", externalAccessType)
		}
		return nil
	},
	Run: func(cmd *cobra.Command, args []string) {
		opts := zap.Options{
			Development: true,
		}

		ctrl.SetLogger(zap.New(zap.UseFlagOptions(&opts)))
		mgr, err := ctrl.NewManager(ctrl.GetConfigOrDie(), ctrl.Options{
			Scheme:                 scheme,
			MetricsBindAddress:     metricsAddr,
			Port:                   9443,
			HealthProbeBindAddress: probeAddr,
			LeaderElection:         enableLeaderElection,
			LeaderElectionID:       "094f3f17.openshift.com",
			// LeaderElectionReleaseOnCancel defines if the leader should step down voluntarily
			// when the Manager ends. This requires the binary to immediately end when the
			// Manager is stopped, otherwise, this setting is unsafe. Setting this significantly
			// speeds up voluntary leader transitions as the new leader don't have to wait
			// LeaseDuration time first.
			//
			// In the default scaffold provided, the program ends immediately after
			// the manager stops, so would be fine to enable this option. However,
			// if you are doing or is intended to do any operation such as perform cleanups
			// after the manager stops then its usage might be unsafe.
			// LeaderElectionReleaseOnCancel: true,
		})
		if err != nil {
			setupLog.Error(err, "unable to start manager")
			os.Exit(1)
		}

		routeAvailable := isRouteAvailable(setupLog)

		if !routeAvailable && externalAccessType == string(controllers.ExternalAccessRoute) {
			setupLog.Error(nil, "cannot use Route for external access because Route API is not available from the apiserver")
			os.Exit(1)
		}

		if err = (&controllers.BoutiqueShopReconciler{
			Client:         mgr.GetClient(),
			Scheme:         mgr.GetScheme(),
			ExternalAccess: controllers.ExternalAccess(externalAccessType),
			RouteAvailable: routeAvailable,
		}).SetupWithManager(mgr); err != nil {
			setupLog.Error(err, "unable to create controller", "controller", "BoutiqueShop")
			os.Exit(1)
		}
		//+kubebuilder:scaffold:builder

		if err := mgr.AddHealthzCheck("healthz", healthz.Ping); err != nil {
			setupLog.Error(err, "unable to set up health check")
			os.Exit(1)
		}
		if err := mgr.AddReadyzCheck("readyz", healthz.Ping); err != nil {
			setupLog.Error(err, "unable to set up ready check")
			os.Exit(1)
		}

		setupLog.Info("starting manager")
		if err := mgr.Start(ctrl.SetupSignalHandler()); err != nil {
			setupLog.Error(err, "problem running manager")
			os.Exit(1)
		}
	},
}

// isRouteAvailable uses the discovery client to check if the
// route.openshift.io/v1 Route API exists in the apiserver.
func isRouteAvailable(setupLog logr.Logger) bool {
	discoveryClient, err := discovery.NewDiscoveryClientForConfig(ctrl.GetConfigOrDie())
	if err != nil {
		setupLog.Error(err, "unable to create discovery client")
		os.Exit(1)
	}

	routeAvailable, err := discovery.IsResourceEnabled(discoveryClient, schema.GroupVersionResource{
		Group:    "route.openshift.io",
		Version:  "v1",
		Resource: "routes",
	})
	if err != nil {
		setupLog.Error(err, "unable to use discovery client")
		os.Exit(1)
	}

	if routeAvailable {
		setupLog.Info("Route API is available")
	} else {
		setupLog.Info("Route API is not available")
	}
	return routeAvailable
}
