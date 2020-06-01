# Integration Architecture Workshop Platform

The environment for this course is completely based on docker containers. 

In order to simplify the provisioning, a single docker-compose configuration is used. All the necessary software will be provisioned using Docker.  

You have the following options to start the environment:

 * [**Local Virtual Machine Environment**](./LocalVirtualMachine.md) - a Virtual Machine with Docker and Docker Compose pre-installed will be distributed at by the course infrastructure. You will need 50 GB free disk space.
 * [**Local Docker Environment**](./LocalDocker.md) - you have a local Docker and Docker Compose setup in place which you want to use
 * [**AWS Lightsail Environment**](./Lightsail.md) - AWS Lightsail is a service in Amazon Web Services (AWS) with which we can easily startup an environment and provide all the necessary bootstrapping as a script.

This forms the base environment with some base services such as *Apache Zeppelin** and **Jupyter** for Python development, a **PotgreSQL** instance, an **FTP Server** and **S3-compliant Object Storage based on MinIO**. Additionally some UI Tools for working with these services are part of this base environment as well. 

Each workshop may add additional services, just needed for this workshop. This way we can keep the requirements for memory on the Docker Machine to a minimum. 
It's recommended to add these additional services to a separate file called `docker-compose.override.yml` in the same folder as the `docker-compose.yml`. 

By default, Compose reads two files, the `docker-compose.yml` and an optional `docker-compose.override.yml` file. By convention, the `docker-compose.yml` contains the base configuration. The override file, as its name implies, can contain configuration overrides for existing services or entirely new services.

## Post Provisioning

These steps are necessary after the starting the docker environment. 

### Add entry to local `/etc/hosts` File

To simplify working with the Streaming Platform and for the links below to work, add the following entry to your local `/etc/hosts` file. 

```
40.91.195.92	dataplatform
```

Replace the IP address by the public IP address of the docker host. 

## Services accessible on Integration Data Platform

The following service are available as part of the platform:

Type | Service | Url
------|------- | -------------
Development | Apache Zeppelin | <http://dataplatform:28080>
Development | Jupyter | <http://dataplatform:28888>
Development | Penthao | <http://dataplatform:28154>
Management | Adminer | <http://dataplatform:28131>
Management | Minio | <http://dataplatform:9000>
Management | Presto | <http://dataplatform:28081>
Management | Hawtio | <http://dataplatform:28155/hawtio>
Management | ActiveMQ Admin | <http://dataplatform:8161>
Management | RabbitMQ Admin | <http://dataplatform:15672>
Management | MQTT UI | <http://dataplatform:28136>
Management | Filezilla | <http://dataplatform:5800>
Management | Servicemix | <http://dataplatform:xxxx>
Management | Cluster Manager for Apache Kafka (CMAK)  | <http://dataplatform:28104>
Management | Apache Kafka HQ (AKHQ) | <http://dataplatform:28107>
Management | Kafka Connect UI | <http://dataplatform:28103>
Development | StreamSets Data Collector | <http://dataplatform:18630>
Governance | Schema Registry UI  | <http://dataplatform:28102>


