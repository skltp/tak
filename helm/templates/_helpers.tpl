{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "tak.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "tak.labels" -}}
helm.sh/chart: {{ include "tak.chart" . }}
{{ include "tak.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}
{{/*
Selector labels
*/}}
{{- define "tak.selectorLabels" -}}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}