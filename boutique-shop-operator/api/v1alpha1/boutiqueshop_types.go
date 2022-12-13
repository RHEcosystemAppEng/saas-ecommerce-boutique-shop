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

package v1alpha1

import (
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// EDIT THIS FILE!  THIS IS SCAFFOLDING FOR YOU TO OWN!
// NOTE: json tags are required.  Any new fields you add must have json tags for the fields to be serialized.

// BoutiqueShopSpec defines the desired state of BoutiqueShop
type BoutiqueShopSpec struct {
	// INSERT ADDITIONAL SPEC FIELDS - desired state of cluster
	// Important: Run "make" to regenerate code after modifying this file

	// LoadGeneratorUsers specifies how many fake users the load generator should simulate.
	//  When nil, the load generator Deployment will not run.
	LoadGeneratorUsers *int `json:"loadGeneratorUsers,omitempty"`
	// Tier specifies the tier that the tenant will sign up for.
	// Possible values are free, silver, gold, platinum.
	Tier string `json:"tier"`
	// TenantPrefix specifies the prefix for the boutique shop.
	// TenantPrefix should be specified for a tenant. It should be left empty
	// for the common services.
	TenantPrefix string `json:"tenantPrefix,omitempty"`
}

// BoutiqueShopStatus defines the observed state of BoutiqueShop
type BoutiqueShopStatus struct {
	// INSERT ADDITIONAL STATUS FIELD - define observed state of cluster
	// Important: Run "make" to regenerate code after modifying this file
	URL string `json:"url,omitempty"`
}

//+kubebuilder:object:root=true
//+kubebuilder:subresource:status

// BoutiqueShop is the Schema for the boutiqueshops API
type BoutiqueShop struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   BoutiqueShopSpec   `json:"spec,omitempty"`
	Status BoutiqueShopStatus `json:"status,omitempty"`
}

//+kubebuilder:object:root=true

// BoutiqueShopList contains a list of BoutiqueShop
type BoutiqueShopList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []BoutiqueShop `json:"items"`
}

func init() {
	SchemeBuilder.Register(&BoutiqueShop{}, &BoutiqueShopList{})
}
