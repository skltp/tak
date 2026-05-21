# App-of-Apps Helm Configuration Guide

This guide explains how to deploy **TAK** (Tjänsteadresseringskatalogen) using the **app-of-apps** pattern with ArgoCD. It is aimed at operators setting up a new environment from scratch.

For per-key documentation of all TAK Helm values, see [Helm Values Reference].

---

## 1. The App-of-Apps Pattern

The SKLTP platform uses the [ArgoCD App-of-Apps](https://argo-cd.readthedocs.io/en/stable/operator-manual/cluster-bootstrapping/#app-of-apps-pattern) approach to manage multiple applications from a single Git repository.

### How It Works

You create a **central repository** (the "app-of-apps" repo) containing a Helm chart. When rendered, this chart produces an ArgoCD **ApplicationSet** resource that drives the deployment of all platform services — including TAK.

The **ApplicationSet** uses a **list generator** that iterates over an `applications[]` list in `values.yaml`. For each entry it:

1. Reads `valuefiles/common-values.yaml` (shared settings: image registry, ingress hostnames, SKLTP instance ID).
2. Reads `valuefiles/<name>-values.yaml` (application-specific overrides).
3. Merges both into the `helm.values` field of the generated ArgoCD Application.
4. Points ArgoCD at the application's own Git repository + `helm/` path + pinned tag.

ArgoCD then renders each application's Helm chart (e.g. `tak/helm/`) with the merged values and syncs the resulting Kubernetes resources to the target cluster.

### Value Precedence (highest → lowest)

1. `valuefiles/tak-values.yaml` (environment-specific overrides)
2. `valuefiles/common-values.yaml` (shared across all apps)
3. `tak/helm/values.yaml` (chart defaults in the TAK repository)

---

## 2. Setting Up Your App-of-Apps Repository

Create a new Git repository with the following structure:

```
my-platform-apps/
├── Chart.yaml
├── values.yaml
├── valuefiles/
│   ├── common-values.yaml
│   └── tak-values.yaml
└── templates/
    ├── applicationset.yaml
    ├── configmaps/
    │   ├── common-configmap.yaml
    │   ├── tak-configmap.yaml
    │   ├── tak-web-configmap.yaml
    │   ├── tak-services-configmap.yaml
    │   └── tak-letsencrypt-configmap.yaml
    └── secrets/
        └── (SealedSecrets or references)
```

### 2.1 `Chart.yaml`

```yaml
apiVersion: v2
name: my-platform-applicationset
description: App-of-apps chart for deploying SKLTP services
type: application
version: 0.1.0
appVersion: "0.0.1"
```

### 2.2 `values.yaml` — Cluster & Application List

This is the top-level values file for your app-of-apps chart. It defines the target cluster/namespace and lists which applications to deploy.

```yaml
destination:
  cluster: a                              # CHANGE: cluster identifier
  environment: myenv                      # CHANGE: environment name (dev, qa, prod, etc.)
  project: my-platform-project            # CHANGE: ArgoCD project name
  namespace: my-platform-myenv            # CHANGE: target Kubernetes namespace
  server: https://kubernetes.default.svc

repo:
  path: helm                              # Path within each app repo where Helm chart lives

applications:
- name: tak
  repourl: https://github.com/skltp/tak.git
  targetrevision: v4.3.0                  # CHANGE: pin to desired TAK release tag
```

### 2.3 `templates/applicationset.yaml` — The List Generator

This is the core template that generates one ArgoCD Application per entry in `applications[]`. It merges `common-values.yaml` and the per-app values file into the Helm values for each application.

```yaml
apiVersion: argoproj.io/v1alpha1
kind: ApplicationSet
metadata:
  name: {{ .Chart.Name }}-{{ .Values.destination.environment }}
  namespace: argocd
spec:
  generators:
  - list:
      elements:
      {{- range .Values.applications }}
      - application: {{ .name }}-{{ $.Values.destination.environment }}
        repourl: {{ .repourl }}
        targetrevision: {{ .targetrevision }}
        app-values: | {{ $.Files.Get "valuefiles/common-values.yaml" | nindent 10 }}
          {{ $.Files.Get (printf "valuefiles/%s-values.yaml" .name) | nindent 10 }}
      {{- end }}
  template:
    metadata:
      name: '{{`{{application}}`}}'
    spec:
      destination:
        namespace: {{ .Values.destination.namespace }}
        server: {{ .Values.destination.server }}
      project: {{ .Values.destination.project }}
      source:
        repoURL: '{{`{{repourl}}`}}'
        path: {{ .Values.repo.path }}
        targetRevision: '{{`{{targetrevision}}`}}'
        helm:
          values: '{{`{{app-values}}`}}'
```

> **Key points about this template:**
> - It uses `$.Files.Get` to read the value files from your app-of-apps repo and inject them as inline Helm values.
> - The double-brace escaping (`{{` `` ` `` `{{...}}` `` ` `` `}}`) is required because the inner `{{application}}`, `{{repourl}}`, etc. are ArgoCD ApplicationSet template parameters — not Go template expressions.
> - Each application gets its own ArgoCD Application resource pointing at the application's own Git repo and Helm chart.

---

## 3. Minimal TAK Deployment Configuration

TAK is a multi-component application consisting of four workloads:

| Component          | Type        | Purpose                                                        |
|--------------------|-------------|----------------------------------------------------------------|
| `tak-web`          | Deployment  | Admin GUI (Spring MVC + Thymeleaf) for managing TAK entries.   |
| `tak-services`     | Deployment  | SOAP/REST services (CXF) exposing TAK data + cache reset API.  |
| `tak-monitor`      | Deployment  | Polls DB version and triggers cache resets on downstream apps.  |
| `tak-integration`  | CronJob     | Periodic TAK data export to SFTP.                              |

### 3.1 `valuefiles/common-values.yaml` — Shared Values

Settings consumed by TAK and potentially other SKLTP services you deploy:

```yaml
repository: registry.example.com/skltp/        # CHANGE: your container registry prefix
```

### 3.2 `valuefiles/tak-values.yaml` — TAK-Specific Overrides

Minimum overrides for TAK:

| Concern               | Keys to set                                                                                         |
|-----------------------|-----------------------------------------------------------------------------------------------------|
| Scaling               | `deployments.<component>.replicaCount`, `deployments.<component>.resources`                         |
| Images                | `container.takweb.image.tag`, `container.takservices.image.tag`, etc.                               |
| Environment variables | `deployments.<component>.environment.variables.config_maps`, `.secrets`                             |
| Export CronJob        | `exportCronJob.schedule`, `exportCronJob.nodePattern`                                               |
| IP whitelist          | `ipWhiteList.sourceRange`                                                                           |
| TLS bundle            | `tls.bundle.active`, `tls.bundle.tlsSecret`, `tls.bundle.truststoreConfigMap`                       |

See section 4 for the full example.

### 3.3 Kubernetes Resources (Created via `templates/`)

These must exist in the target namespace before (or alongside) the TAK deployment. Create them as additional templates in your app-of-apps chart:

| Resource                                  | Purpose                                                                                       |
|-------------------------------------------|-----------------------------------------------------------------------------------------------|
| `ConfigMap/common-configmap`              | Shared values across SKLTP services (e.g. TAK cache endpoint used by VP).                     |
| `ConfigMap/tak-configmap`                 | Database URL, beställning settings, mail config, platform name, export settings.               |
| `ConfigMap/tak-web-configmap`             | `JAVA_OPTS` for tak-web (e.g. heap size).                                                     |
| `ConfigMap/tak-services-configmap`        | `JAVA_OPTS` for tak-services (e.g. heap size).                                                |
| `ConfigMap/tak-letsencrypt-configmap`     | CA certificate bundle for TLS trust (e.g. Let's Encrypt root + intermediates).                |
| `Secret/tak-secrets`                      | Database credentials (`SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`), export DB credentials. |
| `Secret/regcred`                          | Image-pull credentials for the container registry.                                            |

> **Note:** Secrets should be provisioned via SealedSecrets, external-secrets-operator, or your organization's secret management solution. Never commit plaintext secrets to Git.

#### About `regcred` (Image-Pull Secret)

The `regcred` Secret is a Kubernetes `kubernetes.io/dockerconfigjson` secret that stores credentials for authenticating against the container image registry. Without it, the kubelet cannot pull the TAK container images and pods will fail with `ErrImagePull` / `ImagePullBackOff`.

The TAK Helm chart references this secret via `imagePullSecrets`:

```yaml
imagePullSecrets:
  - name: regcred
```

**Creating `regcred` manually** (for testing/bootstrapping):

```bash
kubectl create secret docker-registry regcred \
  --namespace=<your-namespace> \
  --docker-server=registry.example.com \
  --docker-username=<service-account> \
  --docker-password=<token-or-password>
```

**In production**, use SealedSecrets or an external-secrets-operator to manage this secret declaratively. The secret must exist in the same namespace as the TAK Deployments.

---

## 4. Complete Minimal App-of-Apps Example

Below is a self-contained set of all files needed in your app-of-apps repository to deploy TAK. Each file is separated by `---` with a header comment.

> Replace placeholder values (marked with `# CHANGE`) with your environment-specific settings.

```yaml
##############################################################################
# FILE: Chart.yaml
##############################################################################
apiVersion: v2
name: my-platform-applicationset
description: App-of-apps chart for deploying SKLTP services
type: application
version: 0.1.0
appVersion: "0.0.1"
---
##############################################################################
# FILE: values.yaml — Cluster & application list
##############################################################################
destination:
  cluster: a                                    # CHANGE: cluster identifier
  environment: myenv                            # CHANGE: environment name
  project: my-platform-project                  # CHANGE: ArgoCD project
  namespace: my-platform-myenv                  # CHANGE: target namespace
  server: https://kubernetes.default.svc

repo:
  path: helm

applications:
- name: tak
  repourl: https://github.com/skltp/tak.git
  targetrevision: v4.3.0                       # CHANGE: desired TAK version
---
##############################################################################
# FILE: templates/applicationset.yaml — The list generator
##############################################################################
apiVersion: argoproj.io/v1alpha1
kind: ApplicationSet
metadata:
  name: {{ .Chart.Name }}-{{ .Values.destination.environment }}
  namespace: argocd
spec:
  generators:
  - list:
      elements:
      {{- range .Values.applications }}
      - application: {{ .name }}-{{ $.Values.destination.environment }}
        repourl: {{ .repourl }}
        targetrevision: {{ .targetrevision }}
        app-values: | {{ $.Files.Get "valuefiles/common-values.yaml" | nindent 10 }}
          {{ $.Files.Get (printf "valuefiles/%s-values.yaml" .name) | nindent 10 }}
      {{- end }}
  template:
    metadata:
      name: '{{`{{application}}`}}'
    spec:
      destination:
        namespace: {{ .Values.destination.namespace }}
        server: {{ .Values.destination.server }}
      project: {{ .Values.destination.project }}
      source:
        repoURL: '{{`{{repourl}}`}}'
        path: {{ .Values.repo.path }}
        targetRevision: '{{`{{targetrevision}}`}}'
        helm:
          values: '{{`{{app-values}}`}}'
---
##############################################################################
# FILE: valuefiles/common-values.yaml — Shared values for all applications
##############################################################################
repository: registry.example.com/skltp/         # CHANGE: your registry prefix
---
##############################################################################
# FILE: valuefiles/tak-values.yaml — TAK-specific overrides
##############################################################################
container:
  takweb:
    image:
      tag:                                       # CHANGE: pin image tag per env
      pullPolicy: IfNotPresent
  takservices:
    image:
      tag:
      pullPolicy: IfNotPresent
  takmonitor:
    image:
      tag:
      pullPolicy: IfNotPresent
  takintegration:
    image:
      tag:
      pullPolicy: IfNotPresent

exportCronJob:
  schedule: "5 0 * * *"                          # CHANGE: cron expression
  nodePattern: "^my-node-pattern"                # CHANGE: restrict to certain nodes

deployments:
  takweb:
    replicaCount: 1
    elasticGrokFilter: tak-web
    resources:
      limits:
        memory: 2563Mi
      requests:
        cpu: 50m
        memory: 2563Mi
    environment:
      variables:
        config_maps:
          - common-configmap
          - tak-configmap
          - tak-web-configmap
        secrets:
          - tak-secrets
  takservices:
    replicaCount: 1
    elasticGrokFilter: tak-services
    resources:
      limits:
        memory: 2563Mi
      requests:
        cpu: 50m
        memory: 2563Mi
    environment:
      variables:
        config_maps:
          - common-configmap
          - tak-configmap
          - tak-services-configmap
        secrets:
          - tak-secrets
  takmonitor:
    replicaCount: 1
    elasticGrokFilter: tak-monitor
    resources:
      limits:
        memory: 512Mi
      requests:
        cpu: 50m
        memory: 512Mi
    environment:
      variables:
        config_maps:
          - common-configmap
          - tak-configmap
        secrets:
          - tak-secrets
  takintegration:
    elasticGrokFilter: cronjob
    environment:
      variables:
        config_maps:
          - common-configmap
          - tak-configmap
        secrets:
          - tak-secrets

ipWhiteList:
  sourceRange:
    - 10.0.0.0/8                                 # CHANGE: allowed CIDRs

tls:
  bundle:
    active: true
    tlsSecret: myenv-tak-tls                     # CHANGE: TLS Secret name
    truststoreConfigMap: tak-letsencrypt-configmap
---
##############################################################################
# FILE: templates/configmaps/common-configmap.yaml
##############################################################################
apiVersion: v1
kind: ConfigMap
metadata:
  name: common-configmap
  namespace: {{ .Values.destination.namespace }}
data:
  TAKCACHE_ENDPOINT_ADDRESS: "http://tak-services-svc:8080/tak-services/SokVagvalsInfo/v2"
---
##############################################################################
# FILE: templates/configmaps/tak-configmap.yaml
##############################################################################
apiVersion: v1
kind: ConfigMap
metadata:
  name: tak-configmap
  namespace: {{ .Values.destination.namespace }}
data:
  # Database
  SPRING_DATASOURCE_URL: "jdbc:mysql://db.example.com:3306/takv3?autoReconnect=true"  # CHANGE

  # Beställning (order integration)
  TAK_BESTALLNING_ON: "true"
  TAK_BESTALLNING_URL_0_NAME: "BeSt_MYENV"                        # CHANGE
  TAK_BESTALLNING_URL_0_URL: "https://bs.example.com/bs-api/api/takOrders/"  # CHANGE

  # Platform identifier shown in TAK Web GUI
  TAK_PLATFORM: "MY-PLATFORM"                                      # CHANGE

  # Mail (for publication alerts)
  TAK_ALERT_ON_PUBLICERA: "false"
  SPRING_MAIL_HOST: "mail.example.com"                             # CHANGE
  SPRING_MAIL_PROPERTIES_MAIL_TRANSPORT_PROTOCOL: "smtp"
  SPRING_MAIL_PROPERTIES_MAIL_SMTP_PORT: "25"
  SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH: "false"

  # Session management
  SPRING_SESSION_STORE_TYPE: "jdbc"
  SPRING_SESSION_JDBC_INITIALIZE_SCHEMA: "always"
  SERVER_SERVLET_SESSION_TIMEOUT: "30m"

  # TAK export job
  TAK_EXPORT_DB_HOST: "db.example.com"                             # CHANGE
  TAK_EXPORT_DB_NAME: "takv3"
  TAK_EXPORT_ENVIRONMENT: "myenv"                                  # CHANGE
  TAK_EXPORT_SFTP_HOST: "sftp.example.com"                         # CHANGE
  TAK_EXPORT_SFTP_KEYFILE: "/home/groovy/.ssh/my-sftp-key"         # CHANGE
  TAK_EXPORT_SFTP_PATH: "/upload/"
  TAK_EXPORT_SFTP_USER: "my-sftp-user"                             # CHANGE
  TAK_EXPORT_SITE: "mysite"                                        # CHANGE
---
##############################################################################
# FILE: templates/configmaps/tak-web-configmap.yaml
##############################################################################
apiVersion: v1
kind: ConfigMap
metadata:
  name: tak-web-configmap
  namespace: {{ .Values.destination.namespace }}
data:
  JAVA_OPTS: "-Xmx2g"
---
##############################################################################
# FILE: templates/configmaps/tak-services-configmap.yaml
##############################################################################
apiVersion: v1
kind: ConfigMap
metadata:
  name: tak-services-configmap
  namespace: {{ .Values.destination.namespace }}
data:
  JAVA_OPTS: "-Xmx2g"
---
##############################################################################
# FILE: templates/configmaps/tak-letsencrypt-configmap.yaml
##############################################################################
apiVersion: v1
kind: ConfigMap
metadata:
  name: tak-letsencrypt-configmap
  namespace: {{ .Values.destination.namespace }}
data:
  ca.crt: |
    # Paste your trusted CA certificate chain here (PEM format).
    # Include your TLS provider's root and intermediate CA certificates.
    -----BEGIN CERTIFICATE-----
    <YOUR-CA-CERTIFICATE>
    -----END CERTIFICATE-----
---
##############################################################################
# FILE: templates/secrets/tak-sealed-secrets.yaml (use SealedSecret)
##############################################################################
# apiVersion: bitnami.com/v1alpha1
# kind: SealedSecret
# metadata:
#   name: tak-secrets
#   namespace: {{ .Values.destination.namespace }}
# spec:
#   encryptedData:
#     SPRING_DATASOURCE_USERNAME: <sealed-value>
#     SPRING_DATASOURCE_PASSWORD: <sealed-value>
#     TAK_EXPORT_DB_USER: <sealed-value>
#     TAK_EXPORT_DB_PASSWORD: <sealed-value>
---
##############################################################################
# FILE: templates/secrets/regcred.yaml (placeholder — use SealedSecret)
##############################################################################
# apiVersion: bitnami.com/v1alpha1
# kind: SealedSecret
# metadata:
#   name: regcred
#   namespace: {{ .Values.destination.namespace }}
# spec:
#   encryptedData:
#     .dockerconfigjson: <sealed-value>
#   template:
#     type: kubernetes.io/dockerconfigjson
```

---

## 5. Deployment Workflow

1. **Create your app-of-apps repository** — Use the structure and files from section 4.
2. **Provision secrets** — Create SealedSecrets (or use your secrets operator) for database credentials, SFTP keys, image-pull credentials, and TLS certificates.
3. **Register in ArgoCD** — Create an ArgoCD Application that points at your app-of-apps repository (the "root" application). ArgoCD will render the chart, producing the ApplicationSet.
4. **Sync** — ArgoCD detects the ApplicationSet, generates one Application per entry in `applications[]`, renders each app's Helm chart with the merged values, and applies the resources to the cluster.
5. **Verify** — Check pod status, Actuator health (`/actuator/health` on port 8089), and TAK startup logs for successful database connectivity and cache initialisation.

### Registering the Root Application in ArgoCD

The "root application" is the single ArgoCD Application that bootstraps everything else. It tells ArgoCD where your app-of-apps repository lives and how to render it. Without this, ArgoCD has no knowledge of your chart.

You can create the root application declaratively or via the ArgoCD UI/CLI:

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: my-platform-apps
  namespace: argocd
spec:
  project: my-platform-project                                       # CHANGE: must exist in ArgoCD
  source:
    repoURL: https://git.example.com/my-org/my-platform-apps.git     # CHANGE: your app-of-apps repo
    path: .                                                          # Chart.yaml is at the repo root
    targetRevision: main                                             # CHANGE: branch or tag to track
  destination:
    server: https://kubernetes.default.svc                           # The cluster ArgoCD runs on
    namespace: argocd                                                # ApplicationSet is created here
  syncPolicy:
    automated:
      prune: true       # Remove resources ArgoCD no longer manages
      selfHeal: true    # Revert manual drift automatically
```

Once this root Application is synced, ArgoCD renders your `Chart.yaml` + `values.yaml` + templates, producing the ApplicationSet which in turn creates the TAK Application (and any other applications you list).

---

## 6. Additional Override Examples

### 6.1 Resource Limits

```yaml
deployments:
  takweb:
    resources:
      limits:
        memory: 4Gi
      requests:
        cpu: 100m
        memory: 4Gi
  takservices:
    resources:
      limits:
        memory: 4Gi
      requests:
        cpu: 200m
        memory: 4Gi
```

### 6.2 Log Configuration

Override root log level and per-package loggers (rendered into the chart's default ConfigMap):

```yaml
logConfig:
  root:
    level: WARN
  loggers:
    - name: se.skltp.tak
      level: INFO
    - name: org.springframework.web
      level: WARN
    - name: org.apache.cxf
      level: WARN
    - name: org.hibernate
      level: WARN
```

### 6.3 JAVA_OPTS via ConfigMap

Increase heap size for tak-web and tak-services:

```yaml
# In templates/configmaps/tak-web-configmap.yaml:
data:
  JAVA_OPTS: "-Xmx3g"

# In templates/configmaps/tak-services-configmap.yaml:
data:
  JAVA_OPTS: "-Xmx3g -Dorg.apache.cxf.stax.maxChildElements=200000"
```

### 6.4 Export CronJob Schedule

```yaml
exportCronJob:
  schedule: "30 2 * * *"           # Run daily at 02:30
  nodePattern: "^my-cluster-node"  # Restrict to specific nodes
```

### 6.5 TAK Monitor — Custom Reset Targets

Override the cache-reset node list in `helm/values.yaml`:

```yaml
takMonitor:
  reset:
    usePodLookup: "true"
    podNamespace: "{{ .Release.Namespace }}"
    nodes:
      - label: app.kubernetes.io/name=tak-services
        url: "http://0.0.0.0:8080/tak-services/reset/pv"
      - label: app.kubernetes.io/name=vp
        url: "http://0.0.0.0:23000/resetcache"
      - label: app.kubernetes.io/name=kat-application
        url: "http://0.0.0.0:8082/kat/resetcache"
```

### 6.6 IP Whitelist

Restrict ingress access to specific CIDRs:

```yaml
ipWhiteList:
  sourceRange:
    - 10.0.0.0/8       # Internal network
    - 192.168.1.0/24   # VPN subnet
```

### 6.7 TLS Bundle for Beställning

Enable the TLS bundle used by tak-web when calling the Beställningsstöd API over mTLS:

```yaml
tls:
  bundle:
    active: true
    tlsSecret: myenv.tak.example.se          # CHANGE: Kubernetes TLS Secret name
    truststoreConfigMap: tak-ca-configmap     # CHANGE: ConfigMap holding CA certs
```

### 6.8 Beställning Configuration

```yaml
# In templates/configmaps/tak-configmap.yaml:
data:
  TAK_BESTALLNING_ON: "true"
  TAK_BESTALLNING_URL_0_NAME: "BeSt_PROD"
  TAK_BESTALLNING_URL_0_URL: "https://bestallningsstod.example.com/bs-api/api/takOrders/"
  # Additional endpoints (optional):
  # TAK_BESTALLNING_URL_1_NAME: "BeSt_STAGE"
  # TAK_BESTALLNING_URL_1_URL: "https://stage.example.com/bs-api/api/takOrders/"
```

---

## See Also

- [Helm Values Reference] — complete per-key documentation of `helm/values.yaml`.

[//]: # (Reference links)

[Helm Values Reference]: <helm_values.md>

