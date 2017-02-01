# MOTR Webapp

Webapp serving Lambda implemented with Java 

### Building
To build artefact:
```./gradle clean build ```
Expect ZIP artefact: in ${project-root}/build/distributions.

##### Testing

###### Unit testing
Unit tests are part of the artefact building process (see Building paragraph)

###### Integration testing

To run integration tests:
```
./gradle clean integrationTest -Dtest.dynamoDB.integration.region=<region> -Dtest.dynamoDB.integration.envId=<env-id> 
```
where <region> is aws region e.g. eu-west-1, and <env-id> id of the environment e.g. int.
