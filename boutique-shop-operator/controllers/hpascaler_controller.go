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

package controllers

import (
	"context"
	"encoding/json"
	"fmt"

	autoscalingv2beta2 "k8s.io/api/autoscaling/v2beta2"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
	"sigs.k8s.io/controller-runtime/pkg/log"
	ctrllog "sigs.k8s.io/controller-runtime/pkg/log"

	demov1alpha1 "github.com/RHEcosystemAppEng/saas-ecommerce-boutique-shop/boutique-shop-operator/api/v1alpha1"
)

// HpaScalerReconciler reconciles a HpaScaler object
type HpaScalerReconciler struct {
	client.Client
	Scheme *runtime.Scheme
}

type HpaResource struct {
	MinReplicas int32   `json:"minReplicas"`
	MaxReplicas int32   `json:"maxReplicas"`
	Replicas    float32 `json:"replicas"`
	Comment     string  `json:"comment"`
}

type ResourcesByMicroservice struct {
	Bucket       string      `json:"bucket"`
	Tenant       string      `json:"tenant"`
	Namespace    string      `json:"namespace"`
	Microservice string      `json:"microservice"`
	HpaResources HpaResource `json:"hpaResources"`
}

type ResourcesByTier struct {
	Tier                     string                    `json:"tier"`
	ResourcesByMicroservices []ResourcesByMicroservice `json:"resourcesByMicroservices"`
}

type Scaler struct {
	ResourcesByTiers []ResourcesByTier `json:"resourcesByTier"`
}

//+kubebuilder:rbac:groups=demo.openshift.com,resources=hpascalers,verbs=get;list;watch;create;update;patch;delete
//+kubebuilder:rbac:groups=demo.openshift.com,resources=hpascalers/status,verbs=get;update;patch
//+kubebuilder:rbac:groups=demo.openshift.com,resources=hpascalers/finalizers,verbs=update

// Reconcile is part of the main kubernetes reconciliation loop which aims to
// move the current state of the cluster closer to the desired state.
// TODO(user): Modify the Reconcile function to compare the state specified by
// the HpaScaler object against the actual cluster state, and then
// perform operations to make the cluster state reflect the state specified by
// the user.
//
// For more details, check Reconcile and its Result here:
// - https://pkg.go.dev/sigs.k8s.io/controller-runtime@v0.12.2/pkg/reconcile
func (r *HpaScalerReconciler) Reconcile(ctx context.Context, req ctrl.Request) (ctrl.Result, error) {
	_ = log.FromContext(ctx)

	log := ctrllog.FromContext(ctx)
	instance := &demov1alpha1.HpaScaler{}

	err := r.Client.Get(ctx, req.NamespacedName, instance)
	if err != nil {
		return ctrl.Result{}, client.IgnoreNotFound(err)
	}

	scaler := Scaler{}
	err = json.Unmarshal([]byte(instance.Spec.Scaler), &scaler)

	if err != nil {
		return ctrl.Result{}, err
	}

	for i := range scaler.ResourcesByTiers {
		resourcesByTier := scaler.ResourcesByTiers[i]
		fmt.Println(resourcesByTier)
		for j := range resourcesByTier.ResourcesByMicroservices {
			err = r.CreateHorizontalPodAutoscaler(ctx, req, resourcesByTier.ResourcesByMicroservices[j])
			if err != nil {
				log.Error(err, "Failed to create HorizontalPodAutoscaler", resourcesByTier.Tier, resourcesByTier.ResourcesByMicroservices[j])
			}
		}
	}

	return ctrl.Result{}, nil
}

// Int32 returns a pointer to the int32 value passed in.
func Int32(v int32) *int32 {
	return &v
}

func (r *HpaScalerReconciler) CreateHorizontalPodAutoscaler(ctx context.Context, req ctrl.Request, resource ResourcesByMicroservice) error {

	log := ctrllog.FromContext(ctx)
	name := resource.Microservice + "-autoscaler"
	horizontalPodAutoScaler := autoscalingv2beta2.HorizontalPodAutoscaler{
		TypeMeta: metav1.TypeMeta{APIVersion: "autoscaling/v2beta2", Kind: "HorizontalPodAutoscaler"},
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: resource.Namespace,
		},
	}
	mutateFn := func() error {
		horizontalPodAutoScaler.Spec = autoscalingv2beta2.HorizontalPodAutoscalerSpec{
			MaxReplicas: resource.HpaResources.MaxReplicas,
			MinReplicas: &resource.HpaResources.MinReplicas,
			Metrics: []autoscalingv2beta2.MetricSpec{
				{
					Type: "Resource",
					Resource: &autoscalingv2beta2.ResourceMetricSource{
						Name: "cpu",
						Target: autoscalingv2beta2.MetricTarget{
							AverageUtilization: Int32(70),
							Type:               "Utilization",
						},
					},
				},
			},
		}
		horizontalPodAutoScaler.Spec.ScaleTargetRef.APIVersion = "apps/v1"
		horizontalPodAutoScaler.Spec.ScaleTargetRef.Kind = "Deployment"
		horizontalPodAutoScaler.Spec.ScaleTargetRef.Name = resource.Microservice
		return nil
	}

	result, err := controllerutil.CreateOrUpdate(ctx, r.Client, &horizontalPodAutoScaler, mutateFn)
	if err != nil {
		log.Error(err, "Failed to create or update autoscaler: "+name)
		return err
	}
	switch result {
	case controllerutil.OperationResultCreated:
		log.Info("Created Autoscaler " + name)
	case controllerutil.OperationResultUpdated:
		log.Info("Updated Autoscaler " + name)
	}

	return nil
}

func (r *HpaScalerReconciler) DeleteHorizontalPodAutoscaler(ctx context.Context, req ctrl.Request, resource ResourcesByMicroservice) error {
	//Ensure the resource does not exist, and call Delete if necessary
	// key := client.ObjectKeyFromObject(&horizontalPodAutoScaler)
	// if err := r.Client.Get(ctx, key, &horizontalPodAutoScaler); err != nil {
	// 	if apierrors.IsNotFound(err) {
	// 		// "not found" is the desired state. Nothing else to do.
	// 		return nil
	// 	}
	// 	log.Error(err, "Get request for resource failed: "+name)
	// 	return err
	// }
	// err = r.Client.Delete(ctx, &horizontalPodAutoScaler)
	// if err != nil {
	// 	log.Error(err, "Delete request for resource failed: "+name)
	// 	return err
	// }
	// log.Info("Deleted " + name)
	return nil
}

// SetupWithManager sets up the controller with the Manager.
func (r *HpaScalerReconciler) SetupWithManager(mgr ctrl.Manager) error {
	return ctrl.NewControllerManagedBy(mgr).
		For(&demov1alpha1.HpaScaler{}).
		Complete(r)
}
