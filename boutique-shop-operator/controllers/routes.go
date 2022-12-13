package controllers

import (
	"context"

	routev1 "github.com/openshift/api/route/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	demov1alpha1 "github.com/RHEcosystemAppEng/saas-ecommerce-boutique-shop/boutique-shop-operator/api/v1alpha1"
)

func (r *BoutiqueShopReconciler) newFrontendRoute(ctx context.Context, instance *demov1alpha1.BoutiqueShop) (*appResource, error) {
	route := &routev1.Route{
		TypeMeta: metav1.TypeMeta{APIVersion: "route.openshift.io/v1", Kind: "Route"},
		ObjectMeta: metav1.ObjectMeta{
			Name:      instance.Spec.TenantPrefix,
			Namespace: instance.Namespace,
		},
	}

	mutateFn := func() error {
		if err := controllerutil.SetControllerReference(instance, route, r.Scheme); err != nil {
			return err
		}
		route.Spec.Port = &routev1.RoutePort{
			TargetPort: intstr.FromInt(8080),
		}
		route.Spec.TLS = &routev1.TLSConfig{
			Termination:                   routev1.TLSTerminationEdge,
			InsecureEdgeTerminationPolicy: routev1.InsecureEdgeTerminationPolicyRedirect,
		}
		route.Spec.To.Kind = "Service"
		route.Spec.To.Name = frontendName()

		return nil
	}

	return &appResource{
		route,
		mutateFn,
		r.shouldRouteExist(instance, routeName()) &&
			r.ExternalAccess == ExternalAccessRoute,
	}, nil
}
