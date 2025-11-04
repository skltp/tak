# README.md
## Tjänsteadresseringskatalogen (TAK)
TAK består av tre fristående men samverkande komponenter:

| Modul          | Beskrivning                                | Standardport |
|----------------|--------------------------------------------|--------------|
| **tak-web**    | Web-GUI (Spring MVC + Thymeleaf)           | 8080 |
| **tak-services** | REST/Batch-tjänster + cache              | 8080 |
| **tak-monitor** | Bevakar databasversion & rensar cache     | 8080 |

> **Tips:** Kör du flera moduler lokalt, ändra port i `application.yaml` eller placera allt bakom en reverse proxy som 
> terminerar TLS och routar paths.

---

## Förkrav
| Komponent | Version/krav |
|-----------|--------------|
| **Java JDK** | 17 eller senare |
| **Maven** | ≥ 3.8 |
| **MySQL** | ≥ 8.0 (utf-8mb4) |
| **Docker / Docker Compose** | *(valfritt men rekommenderas)* |

---

## Bygga
```shell
mvn clean package
# --> target/tak-<modul>-4.0.12.jar
