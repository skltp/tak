# CHANGELOG.md
Alla större ändringar dokumenteras här. Formatet följer *Keep a Changelog* med kategorierna
**Added / Changed / Deprecated / Removed / Fixed / Security**.

## [4.0.12] – 2025-05-01
### Added
- **Java 17-stöd** – hela kodbasen byggs och körs nu på Java 17 (eller senare).
- **Spring Boot 3.3.3** – uppgradering till senaste 3.x-grenen.
- **Spring Security** ersätter Apache Shiro för inloggning och sessionshantering.
    - Bakåtkompatibel lösenordshashning (BCrypt/SHA + salter).
    - Databaslagrade sessioner kan aktiveras via  
      `spring.session.store-type=jdbc`.
- **Ny Spring-profil `ecslogging`** – loggar i Elastic Common Schema (JSON) via `EcsLayout`.
- **Self-contained JAR-artefakter**
    - `tak-web-4.0.12.jar`
    - `tak-services-4.0.12.jar`
    - `tak-monitor-4.0.12.jar`
- **Dockerfile & uppdaterad `docker-compose.yaml`**
    - Kör `docker compose up -d` för alla tjänster + MySQL + phpMyAdmin.
- **TAK-MONITOR** – valfri tjänst som tömmer cache-noder när en ny TAK-databas publiceras.
    - Konfigurationsexempel:
      ```properties
      tak.monitor.reset.nodes.0.label=app.kubernetes.io/name=tak-services
      tak.monitor.reset.nodes.0.url=http://0.0.0.0:8080/tak-services/reset/pv
      tak.monitor.reset.nodes.1.label=app.kubernetes.io/name=vp
      tak.monitor.reset.nodes.1.url=http://0.0.0.0:23000/resetcache
      ```

### Changed
- Maven-bygget producerar nu JAR i stället för WAR.  
  Deployment i extern Tomcat är inte längre supportat.
- Exempel-port för alla moduler är **8080**.  
  Kör du flera på samma VM – ändra port eller lägg en reverse proxy framför.

### Removed
- **Apache Shiro** och all relaterad konfiguration/kod.
- **WAR-distributioner** och Tomcat-specifika deploy-instruktioner.

### Deprecated
- Inget just nu.

### Fixed
- Diverse mindre buggar runt felhantering i REST-API och datakällor.

### Security
- Uppgraderade beroenden (Spring Framework, Hibernate ORM m.fl.) för CVE-stängning.

---

## Migrering 3.x → 4.x i korthet
1. Byt till Java 17.
2. Ta bort eventuell egen Tomcat-deployment; bygg istället med `mvn clean package` och kör `java -jar`.
3. (Valfritt) Aktivera databaslagrade sessioner.
4. Om du har egen loggkonfiguration: uppdatera till ECS eller inaktivera `ecslogging`.

_Länkar till detaljerad migreringsguide kommer på Confluence (placeholder)._  
