# MOTR (MOT Reminders)

Root project for the MOT Reminders service.

### Integration with GOV Notify
[Gov Notify integration details](docs/gov_notify_integration.md)

### Building
To build all sub-projects:

```./gradlew clean build ```

ZIP artefacts will reside in: ${sub-project-root}/build/distributions.

To build individual projects:

```./gradlew :<sub_project_name>:clean :<sub_project_name>:build ```

##### Testing

###### Unit testing
Unit tests are part of the artefact building process (see Building paragraph)

