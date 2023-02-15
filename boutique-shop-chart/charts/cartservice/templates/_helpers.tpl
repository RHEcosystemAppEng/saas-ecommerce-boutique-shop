{{/*
Expand the name of the chart.
*/}}
{{- define "cartservice.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "cartservice.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "cartservice.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "cartservice.labels" -}}
helm.sh/chart: {{ include "cartservice.chart" . }}
{{ include "cartservice.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "cartservice.selectorLabels" -}}
app.kubernetes.io/name: {{ include "cartservice.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "cartservice.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "cartservice.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{- define "cartservice.namespace" -}}
{{- if eq .Values.global.tier "free" }}
{{- "boutique-free" }}
{{- else if eq .Values.global.tier "silver" }}
{{- "boutique-silver" }}
{{- else if eq .Values.global.tier "gold" }}
{{- "boutique-ops"  }}
{{- else if or (eq .Values.global.tier "platinum") (eq .Values.global.tier "premium") }}
{{- printf "%s" .Values.global.tenant }}
{{- end }}
{{- end }}

{{- define "redis_cart.namespace" -}}
{{- if eq .Values.global.tier "free" }}
{{- "boutique-free" }}
{{- else if eq .Values.global.tier "silver" }}
{{- "boutique-silver" }}
{{- else if eq .Values.global.tier "gold" }}
{{- printf "%s" .Values.global.tenant }}
{{- else if or (eq .Values.global.tier "platinum") (eq .Values.global.tier "premium") }}
{{- printf "%s" .Values.global.tenant }}
{{- end }}
{{- end }}

{{- define "ops.namespace" -}}
{{- if eq .Values.global.tier "free" }}
{{- "boutique-free" }}
{{- else if eq .Values.global.tier "silver" }}
{{- "boutique-silver" }}
{{- else if eq .Values.global.tier "gold" }}
{{- "boutique-ops" }}
{{- else if or (eq .Values.global.tier "platinum") (eq .Values.global.tier "premium") }}
{{- .Values.global.tenant }}
{{- end }}
{{- end }}

{{- define "tenant.namespace" -}}
{{- if eq .Values.global.tier "free" }}
{{- "boutique-free" }}
{{- else if eq .Values.global.tier "silver" }}
{{- "boutique-silver" }}
{{- else if eq .Values.global.tier "gold" }}
{{- .Values.global.tenant }}
{{- else if or (eq .Values.global.tier "platinum") (eq .Values.global.tier "premium") }}
{{- .Values.global.tenant }}
{{- end }}
{{- end }}

{{- define "utilities.namespace" -}}
{{- if eq .Values.global.tier "free" }}
{{- "boutique-free" }}
{{- else if eq .Values.global.tier "silver" }}
{{- "boutique-silver" }}
{{- else if eq .Values.global.tier "gold" }}
{{- "enterprise-utilities" }}
{{- else if or (eq .Values.global.tier "platinum") (eq .Values.global.tier "premium") }}
{{- .Values.global.tenant }}
{{- end }}
{{- end }}


{{- define "cartservice.keep" -}}
{{- if eq .Values.global.tier "free" }}
{{- true }}
{{- else if eq .Values.global.tier "silver" }}
{{- true }}
{{- else if eq .Values.global.tier "gold" }}
{{- false }}
{{- else if or (eq .Values.global.tier "platinum") (eq .Values.global.tier "premium") }}
{{- false }}
{{- end }}
{{- end }}