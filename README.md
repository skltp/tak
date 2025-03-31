## Tjänsteadresseringskatalogen



### Bygga projektet med Maven
~~~shell
mvn clean install
~~~

### Starta projektet
Modulen tak-web är byggd med Spring Boot och Spring MVC. Det kan startas i utvecklarläge (med profilen dev) med följande kommando. Den kommer då starta upp med en H2 in-memory-databas och exponera webbgränssnittet på port 8080
~~~shell
cd tak-web
mvn spring-boot:run
~~~


### Använda webbgränssnittet
Gå till webadressen http://localhost:8080/tak-web 
### Att köra TAK i containers med Docker Compose
Med i källkoden finns docker-compose.yml som gör det enkelt att köra igång tak-web, tak-services samt en MySQL-databas, förutsatt att man har Docker installerat. Kör följande kommando i roten av källkodsrepot:
~~~
docker-compose up
~~~

### Lokal installation av MySQL och Tomcat

Som ett alternativ till att köra i containers kan Tomcat och MySQL installeras lokalt på traditionellt sätt.

När man kör i en lokal Tomcat måste konfiguration (för exempelvis databas) finns på liknande sätt som i en vanlig driftmiljö. Enklast är att sätta miljövariabeln TAK_HOME till en sökväg <repo>/resources/etc och modifiera filen tak-application.properties efter behov. 

#### För detaljer kring konfiguration se

[SKLTP TAK - Konfiguration - version 3.0 och senare.](https://inera.atlassian.net/wiki/spaces/SKLTP/pages/3187837717/SKLTP+TAK+-+Konfiguration+-+version+3.0+och+senare)

#### För teknisk dokumentation se

[TAK - Tjänsteadresseringskatalog](https://inera.atlassian.net/wiki/spaces/SKLTP/pages/3187836312/TAK+-+Tj+nsteadresseringskatalog)

#### För release notes se

[Release Note - Tjänstekatalogen (TAK)](https://inera.atlassian.net/wiki/spaces/SKLTP/pages/3187836461/Release+Note+-+Tj+nstekatalogen+TAK)