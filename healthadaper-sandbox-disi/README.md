# Health Adapter Sandbox

## Project content

This project contains three main modules:

**healtadapter-api**: contains the formalization of Health Adapter API as a Java interface

**smarteven-mock**: mocks the behavior of SmartEven, reacting to an enrollment missing some required data
by requiring an import to the Health Adapter

**healthadapter**: a stub for the implementation of the health adapter

**docker-compose.yml**: a Docker Compose file to set up a testing environment, including a FHIR Server (`itel-fhir`)
and an instance of `smarteven-mock`.

## Setting up the environment

1. Run `gradle clean build` from project root
1. Run `docker-compose up --build`

**Note**: the `smarteven-mock` service will restart many times before the FHIR Server is up and running. This
approach can be improved by specifying a [wait command](https://docs.docker.com/compose/startup-order/) to make
`smarteven-mock` wait for `itel-fhir`, by polling it's healthcheck url at `/fhir-server/api/v4/$healthcheck`
(tools that may help in this task [here](https://gist.github.com/rgl/f90ff293d56dbb0a1e0f7e7e89a81f42) and 
[here](https://github.com/cec/wait-for-endpoint/blob/master/wait-for-endpoint.sh)).
