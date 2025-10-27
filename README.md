# Notes (Swing + H2, Java Modules, Gradle)

## Requirements
- JDK 17+
- Gradle 8+

## Modules
- `domain` — entity model
- `persistence` — JDBC/H2 repository
- `service` — application services
- `app-swing` — desktop UI

## Build and run
```bash
./gradlew :app-swing:run
```
The H2 database will be created at `./notes-db.*` on first run.
