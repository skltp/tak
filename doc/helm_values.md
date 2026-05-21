# Helm Values Reference

This page documents every configurable value in `helm/values.yaml` for the TAK (Tjänsteadresseringskatalogen) Helm chart.

---

## repository

| Key          | Description                                                                                                                                                                           |
|--------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `repository` | Container image registry prefix (e.g. `docker.drift.inera.se/ntjp/`). Prepended to the image name when constructing the full image reference. **Must be overridden per environment.** |

---

## ingressroute

Traefik IngressRoute hostname. **Must be overridden per environment.**

| Key                        | Description                                                                                             |
|----------------------------|---------------------------------------------------------------------------------------------------------|
| `ingressroute.bksHostName` | Base hostname for the TAK ingress (e.g. `tak.prod.ntjp.se`). Used in the IngressRoute rule for routing. |

---

## container

Container image settings for each TAK component.

### container.takweb

| Key                                 | Description                                                                                          |
|-------------------------------------|------------------------------------------------------------------------------------------------------|
| `container.takweb.image.tag`        | Image tag / version for the tak-web container. Defaults to the Helm chart `appVersion` when not set. |
| `container.takweb.image.pullPolicy` | Kubernetes image pull policy (`Always`, `IfNotPresent`, `Never`).                                    |

### container.takservices

| Key                                      | Description                                                                                               |
|------------------------------------------|-----------------------------------------------------------------------------------------------------------|
| `container.takservices.image.tag`        | Image tag / version for the tak-services container. Defaults to the Helm chart `appVersion` when not set. |
| `container.takservices.image.pullPolicy` | Kubernetes image pull policy (`Always`, `IfNotPresent`, `Never`).                                         |

### container.takmonitor

| Key                                     | Description                                                                                              |
|-----------------------------------------|----------------------------------------------------------------------------------------------------------|
| `container.takmonitor.image.tag`        | Image tag / version for the tak-monitor container. Defaults to the Helm chart `appVersion` when not set. |
| `container.takmonitor.image.pullPolicy` | Kubernetes image pull policy (`Always`, `IfNotPresent`, `Never`).                                        |

### container.takintegration

| Key                                         | Description                                                                                                  |
|---------------------------------------------|--------------------------------------------------------------------------------------------------------------|
| `container.takintegration.image.tag`        | Image tag / version for the tak-integration container. Defaults to the Helm chart `appVersion` when not set. |
| `container.takintegration.image.pullPolicy` | Kubernetes image pull policy (`Always`, `IfNotPresent`, `Never`).                                            |

---

## exportCronJob

Kubernetes CronJob settings for the TAK data export (tak-integration).

| Key                         | Description                                                                                     |
|-----------------------------|-------------------------------------------------------------------------------------------------|
| `exportCronJob.schedule`    | Cron expression defining when the export job runs. **Override to enable the CronJob.**          |
| `exportCronJob.timeZone`    | IANA time zone for the cron schedule (requires Kubernetes ≥ 1.27). Default: `Europe/Stockholm`. |
| `exportCronJob.nodePattern` | Node selector pattern to restrict execution to certain nodes. Override per environment.         |

---

## deployments

Per-component deployment configuration. Each sub-key (`takweb`, `takservices`, `takmonitor`, `takintegration`) configures a distinct TAK component.

### deployments.takweb

| Key                                                             | Description                                                                                                                                           |
|-----------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| `deployments.takweb.replicaCount`                               | Number of pod replicas to run for tak-web.                                                                                                            |
| `deployments.takweb.elasticGrokFilter`                          | Value injected as a label for Elastic log pipeline grok-filter matching.                                                                              |
| `deployments.takweb.resources`                                  | Kubernetes resource requests and limits (`cpu`, `memory`). Set to `{}` to omit. Structure follows `requests.cpu`, `requests.memory`, `limits.memory`. |
| `deployments.takweb.environment.variables._default_config_maps` | List of ConfigMap names whose keys are injected as environment variables by default.                                                                  |
| `deployments.takweb.environment.variables.config_maps`          | Additional ConfigMap names to inject. Override per environment.                                                                                       |
| `deployments.takweb.environment.variables.secrets`              | Kubernetes Secret names whose keys are injected as environment variables. Override per environment.                                                   |
| `deployments.takweb.environment.variables.map`                  | Inline key-value pairs injected as environment variables (e.g. `CATALINA_OPTS`).                                                                      |

### deployments.takservices

| Key                                                                  | Description                                                                                         |
|----------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|
| `deployments.takservices.replicaCount`                               | Number of pod replicas to run for tak-services.                                                     |
| `deployments.takservices.elasticGrokFilter`                          | Value injected as a label for Elastic log pipeline grok-filter matching.                            |
| `deployments.takservices.resources`                                  | Kubernetes resource requests and limits (`cpu`, `memory`). Set to `{}` to omit.                     |
| `deployments.takservices.environment.variables._default_config_maps` | List of ConfigMap names whose keys are injected as environment variables by default.                |
| `deployments.takservices.environment.variables.config_maps`          | Additional ConfigMap names to inject. Override per environment.                                     |
| `deployments.takservices.environment.variables.secrets`              | Kubernetes Secret names whose keys are injected as environment variables. Override per environment. |
| `deployments.takservices.environment.variables.map`                  | Inline key-value pairs injected as environment variables.                                           |

### deployments.takmonitor

| Key                                                                 | Description                                                                                         |
|---------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|
| `deployments.takmonitor.replicaCount`                               | Number of pod replicas to run for tak-monitor.                                                      |
| `deployments.takmonitor.elasticGrokFilter`                          | Value injected as a label for Elastic log pipeline grok-filter matching.                            |
| `deployments.takmonitor.resources`                                  | Kubernetes resource requests and limits (`cpu`, `memory`). Set to `{}` to omit.                     |
| `deployments.takmonitor.environment.variables._default_config_maps` | List of ConfigMap names whose keys are injected as environment variables by default.                |
| `deployments.takmonitor.environment.variables.config_maps`          | Additional ConfigMap names to inject. Override per environment.                                     |
| `deployments.takmonitor.environment.variables.secrets`              | Kubernetes Secret names whose keys are injected as environment variables. Override per environment. |
| `deployments.takmonitor.environment.variables.map`                  | Inline key-value pairs injected as environment variables (e.g. `CATALINA_OPTS`).                    |

### deployments.takintegration

| Key                                                                     | Description                                                                                         |
|-------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|
| `deployments.takintegration.elasticGrokFilter`                          | Value injected as a label for Elastic log pipeline grok-filter matching.                            |
| `deployments.takintegration.environment.variables._default_config_maps` | List of ConfigMap names whose keys are injected as environment variables by default.                |
| `deployments.takintegration.environment.variables.config_maps`          | Additional ConfigMap names to inject. Override per environment.                                     |
| `deployments.takintegration.environment.variables.secrets`              | Kubernetes Secret names whose keys are injected as environment variables. Override per environment. |
| `deployments.takintegration.environment.variables.map`                  | Inline key-value pairs injected as environment variables (e.g. `CATALINA_OPTS`).                    |

---

## paths

File-system paths inside the TAK containers. Used by other values via Go template expressions.

| Key           | Description                                  |
|---------------|----------------------------------------------|
| `paths.certs` | Directory for TLS certificate and key files. |

---

## spring

Spring Boot datasource and profile configuration rendered into the shared ConfigMap (`tak-configmap-default`).

| Key                                 | Description                                                                                                                              |
|-------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| `spring.datasource.driverClassName` | JDBC driver class name. Default: `com.mysql.cj.jdbc.Driver`.                                                                             |
| `spring.datasource.url`             | JDBC connection URL for the TAK database. **Override per environment.** Default: `jdbc:mysql://localhost:3306/takv3?autoReconnect=true`. |
| `spring.profiles.active`            | Comma-separated list of active Spring profiles. Default: `production,ecslogging,forwardauth`.                                            |

---

## management

Spring Boot Actuator / management endpoint configuration.

| Key                                      | Description                                                                                 |
|------------------------------------------|---------------------------------------------------------------------------------------------|
| `management.serverPort`                  | Port for the management / Actuator HTTP server. Maps to `MANAGEMENT_SERVER_PORT`.           |
| `management.endpointsWebExposureInclude` | List of Actuator endpoint IDs to expose over HTTP (e.g. `health`, `metrics`, `prometheus`). |
| `management.endpointPrometheusEnabled`   | Enable the Prometheus metrics scrape endpoint.                                              |

---

## server

Embedded Tomcat web server settings.

### server.tomcat.accesslog

Tomcat access log configuration. Used by all TAK components running an embedded Tomcat.

| Key                                      | Description                                                                                   |
|------------------------------------------|-----------------------------------------------------------------------------------------------|
| `server.tomcat.accesslog.buffered`       | Whether access log writes are buffered.                                                       |
| `server.tomcat.accesslog.directory`      | Directory for access log files.                                                               |
| `server.tomcat.accesslog.enabled`        | Enable or disable Tomcat access logging.                                                      |
| `server.tomcat.accesslog.fileDateFormat` | Date format suffix appended to access log filenames. Empty string disables date-based naming. |
| `server.tomcat.accesslog.prefix`         | Filename prefix for access log files.                                                         |
| `server.tomcat.accesslog.suffix`         | Filename suffix for access log files.                                                         |
| `server.tomcat.accesslog.pattern`        | Access log format pattern. Default outputs JSON-formatted ECS entries to stdout.              |

---

## takWeb

TAK Web (admin GUI) application-specific settings rendered into `tak-web-configmap-default`.

| Key                           | Description                                                                                  |
|-------------------------------|----------------------------------------------------------------------------------------------|
| `takWeb.resourceDir`          | File-system path inside the container where TAK Web resources (templates, etc.) are located. |
| `takWeb.bestallning.on`       | Enable or disable the *beställning* (order) feature in the TAK Web GUI.                      |
| `takWeb.bestallning.url0Name` | Friendly name for the first beställning service URL. Displayed in the GUI.                   |
| `takWeb.bestallning.url0Url`  | URL of the beställning service API endpoint. **Override per environment.**                   |

---

## takMonitor

TAK Monitor configuration rendered into `tak-monitor-configmap-default`. Controls how the monitor discovers pods and triggers cache resets after a new `PubVersion` is published.

| Key                             | Description                                                                                                                                                                            |
|---------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `takMonitor.reset.usePodLookup` | When `true`, the monitor discovers pods via Kubernetes API using label selectors rather than static URLs.                                                                              |
| `takMonitor.reset.podNamespace` | Kubernetes namespace used for pod lookup. Supports template expressions (e.g. `{{ .Release.Namespace }}`).                                                                             |
| `takMonitor.reset.nodes`        | List of downstream services whose caches should be reset. Each entry has `label` (pod label selector) and `url` (reset endpoint URL where `0.0.0.0` is replaced by discovered pod IP). |

---

## logConfig

Log4j2 / ECS logging configuration rendered into ConfigMaps consumed by all TAK components.

| Key                                | Description                                                                            |
|------------------------------------|----------------------------------------------------------------------------------------|
| `logConfig.root.level`             | Log level for the root logger (`TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, `OFF`).      |
| `logConfig.loggers`                | List of logger entries. Each entry has a `name` (logger name / package) and a `level`. |
| `logConfig.jsonEventTemplate.path` | File-system path to the ECS JSON event template used by the Log4j2 ECS layout.         |

---

## ipWhiteList

IP whitelist applied to the TAK ingress route.

| Key                       | Description                                                                                                       |
|---------------------------|-------------------------------------------------------------------------------------------------------------------|
| `ipWhiteList.sourceRange` | List of allowed CIDR ranges. Default: `127.0.0.1/32` (denies all except localhost). **Override per environment.** |

---

## probes_monitor

Kubernetes health probes for the tak-monitor container.

### probes_monitor.startupProbe

| Key                                               | Description                                                                |
|---------------------------------------------------|----------------------------------------------------------------------------|
| `probes_monitor.startupProbe.httpGet.path`        | HTTP path to probe (Actuator readiness endpoint).                          |
| `probes_monitor.startupProbe.httpGet.port`        | Named or numeric port to probe.                                            |
| `probes_monitor.startupProbe.initialDelaySeconds` | Seconds to wait before the first probe after container start.              |
| `probes_monitor.startupProbe.periodSeconds`       | Seconds between probe attempts.                                            |
| `probes_monitor.startupProbe.timeoutSeconds`      | Seconds before a single probe attempt times out.                           |
| `probes_monitor.startupProbe.successThreshold`    | Number of consecutive successes required to mark the container as started. |
| `probes_monitor.startupProbe.failureThreshold`    | Number of consecutive failures before the container is restarted.          |

### probes_monitor.livenessProbe

| Key                                             | Description                                                        |
|-------------------------------------------------|--------------------------------------------------------------------|
| `probes_monitor.livenessProbe.httpGet.path`     | HTTP path to probe (Actuator liveness endpoint).                   |
| `probes_monitor.livenessProbe.httpGet.port`     | Named or numeric port to probe.                                    |
| `probes_monitor.livenessProbe.periodSeconds`    | Seconds between liveness probes.                                   |
| `probes_monitor.livenessProbe.timeoutSeconds`   | Seconds before a probe attempt times out.                          |
| `probes_monitor.livenessProbe.failureThreshold` | Consecutive failures before the container is killed and restarted. |
| `probes_monitor.livenessProbe.successThreshold` | Consecutive successes to clear a failed state.                     |

### probes_monitor.readinessProbe

| Key                                              | Description                                                            |
|--------------------------------------------------|------------------------------------------------------------------------|
| `probes_monitor.readinessProbe.httpGet.path`     | HTTP path to probe (Actuator readiness endpoint).                      |
| `probes_monitor.readinessProbe.httpGet.port`     | Named or numeric port to probe.                                        |
| `probes_monitor.readinessProbe.periodSeconds`    | Seconds between readiness probes.                                      |
| `probes_monitor.readinessProbe.timeoutSeconds`   | Seconds before a probe attempt times out.                              |
| `probes_monitor.readinessProbe.failureThreshold` | Consecutive failures before the pod is removed from service endpoints. |
| `probes_monitor.readinessProbe.successThreshold` | Consecutive successes to mark the pod as ready.                        |

---

## probes_services

Kubernetes health probes for the tak-services container.

### probes_services.startupProbe

| Key                                                | Description                                                                |
|----------------------------------------------------|----------------------------------------------------------------------------|
| `probes_services.startupProbe.httpGet.path`        | HTTP path to probe (Actuator readiness endpoint).                          |
| `probes_services.startupProbe.httpGet.port`        | Named or numeric port to probe.                                            |
| `probes_services.startupProbe.initialDelaySeconds` | Seconds to wait before the first probe after container start.              |
| `probes_services.startupProbe.periodSeconds`       | Seconds between probe attempts.                                            |
| `probes_services.startupProbe.timeoutSeconds`      | Seconds before a single probe attempt times out.                           |
| `probes_services.startupProbe.successThreshold`    | Number of consecutive successes required to mark the container as started. |
| `probes_services.startupProbe.failureThreshold`    | Number of consecutive failures before the container is restarted.          |

### probes_services.livenessProbe

| Key                                                 | Description                                                            |
|-----------------------------------------------------|------------------------------------------------------------------------|
| `probes_services.livenessProbe.httpGet.path`        | HTTP path to probe (Actuator liveness endpoint).                       |
| `probes_services.livenessProbe.httpGet.port`        | Named or numeric port to probe.                                        |
| `probes_services.livenessProbe.initialDelaySeconds` | Seconds to wait before the first liveness probe.                       |
| `probes_services.livenessProbe.periodSeconds`       | Seconds between liveness probes.                                       |
| `probes_services.livenessProbe.timeoutSeconds`      | Seconds before a probe attempt times out.                              |
| `probes_services.livenessProbe.failureThreshold`    | Consecutive failures before the container is killed and restarted.     |
| `probes_services.livenessProbe.successThreshold`    | Consecutive successes to clear a failed state.                         |

### probes_services.readinessProbe

| Key                                                  | Description                                                            |
|------------------------------------------------------|------------------------------------------------------------------------|
| `probes_services.readinessProbe.httpGet.path`        | HTTP path to probe (Actuator readiness endpoint).                      |
| `probes_services.readinessProbe.httpGet.port`        | Named or numeric port to probe.                                        |
| `probes_services.readinessProbe.initialDelaySeconds` | Seconds to wait before the first readiness probe.                      |
| `probes_services.readinessProbe.periodSeconds`       | Seconds between readiness probes.                                      |
| `probes_services.readinessProbe.timeoutSeconds`      | Seconds before a probe attempt times out.                              |
| `probes_services.readinessProbe.failureThreshold`    | Consecutive failures before the pod is removed from service endpoints. |
| `probes_services.readinessProbe.successThreshold`    | Consecutive successes to mark the pod as ready.                        |

---

## probes_web

Kubernetes health probes for the tak-web container.

### probes_web.startupProbe

| Key                                           | Description                                                                |
|-----------------------------------------------|----------------------------------------------------------------------------|
| `probes_web.startupProbe.httpGet.path`        | HTTP path to probe (Actuator readiness endpoint).                          |
| `probes_web.startupProbe.httpGet.port`        | Named or numeric port to probe.                                            |
| `probes_web.startupProbe.initialDelaySeconds` | Seconds to wait before the first probe after container start.              |
| `probes_web.startupProbe.periodSeconds`       | Seconds between probe attempts.                                            |
| `probes_web.startupProbe.timeoutSeconds`      | Seconds before a single probe attempt times out.                           |
| `probes_web.startupProbe.successThreshold`    | Number of consecutive successes required to mark the container as started. |
| `probes_web.startupProbe.failureThreshold`    | Number of consecutive failures before the container is restarted.          |

### probes_web.livenessProbe

| Key                                            | Description                                                        |
|------------------------------------------------|--------------------------------------------------------------------|
| `probes_web.livenessProbe.httpGet.path`        | HTTP path to probe (Actuator liveness endpoint).                   |
| `probes_web.livenessProbe.httpGet.port`        | Named or numeric port to probe.                                    |
| `probes_web.livenessProbe.initialDelaySeconds` | Seconds to wait before the first liveness probe.                   |
| `probes_web.livenessProbe.periodSeconds`       | Seconds between liveness probes.                                   |
| `probes_web.livenessProbe.timeoutSeconds`      | Seconds before a probe attempt times out.                          |
| `probes_web.livenessProbe.failureThreshold`    | Consecutive failures before the container is killed and restarted. |
| `probes_web.livenessProbe.successThreshold`    | Consecutive successes to clear a failed state.                     |

### probes_web.readinessProbe

| Key                                             | Description                                                            |
|-------------------------------------------------|------------------------------------------------------------------------|
| `probes_web.readinessProbe.httpGet.path`        | HTTP path to probe (Actuator readiness endpoint).                      |
| `probes_web.readinessProbe.httpGet.port`        | Named or numeric port to probe.                                        |
| `probes_web.readinessProbe.initialDelaySeconds` | Seconds to wait before the first readiness probe.                      |
| `probes_web.readinessProbe.periodSeconds`       | Seconds between readiness probes.                                      |
| `probes_web.readinessProbe.timeoutSeconds`      | Seconds before a probe attempt times out.                              |
| `probes_web.readinessProbe.failureThreshold`    | Consecutive failures before the pod is removed from service endpoints. |
| `probes_web.readinessProbe.successThreshold`    | Consecutive successes to mark the pod as ready.                        |

---

## git

Build metadata embedded in the chart for traceability.

| Key               | Description                                                       |
|-------------------|-------------------------------------------------------------------|
| `git.commit.hash` | Git commit hash of the build. Used for auditing and traceability. |

---

## tls

TLS bundle configuration for outbound HTTPS connections (e.g. beställning service calls).

| Key                              | Description                                                                            |
|----------------------------------|----------------------------------------------------------------------------------------|
| `tls.bundle.active`              | Enable or disable the TLS bundle. Default: `false`.                                    |
| `tls.bundle.name`                | Name of the TLS bundle (used as Spring SSL bundle identifier).                         |
| `tls.bundle.tlsSecret`           | Name of the Kubernetes TLS Secret containing the client certificate and private key.   |
| `tls.bundle.truststoreConfigMap` | Name of the ConfigMap containing trusted CA certificates for outbound TLS connections. |
