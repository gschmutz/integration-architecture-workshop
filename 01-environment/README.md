# Setup of the Integration Demo Platform
The environment for this course is completly based on docker containers. 

Either use the Virtual Machine image made available in the course, containing a docker installation or provisiong your own Docker environemnt including Docker Compose. Find the information for installing [Docker](https://docs.docker.com/install/#supported-platforms) and [Docker Compose](https://docs.docker.com/compose/install/).
 
[Here](../00-setup/README.md) you can find the installation of an Virtual Machine in Azure. 

In order to simplify the provisioning, a single docker-compose configuration is used. All the necessary software will be provisioned using Docker. 

## What is this Integration Demo Platform?

The following services are provisioned as part of the Integration Demo Platform: 

 * Apache Zookeeper
 * Drill
 * ...

## Prepare the Docker Compose configuration

On your Docker Machine, create a folder `integrationplatform`. 

```
mkdir integrationplatform
cd integrationplatform
```

Copy the following code fragment into a file named `docker-compose.yml`.

```
version: "2.1"

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:5.0.0-beta30-1
    hostname: zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    restart: always
    
  drill:
    image: harisekhon/apache-drill
    hostname: drill
    ports: 
      - 8047:8047
    command: ['supervisord', '-n']  
    environment:
      - ZOOKEEPER_HOST=zookeepr:2181
    restart: always

  presto:
    image: starburstdata/presto:0.195-e.0.2
    hostname: presto
    ports:
      - 28080:8080
    restart: always

  adminer:
    image: adminer
    ports:
      - 38080:8080
    restart: always

  db:
    image: mujz/pagila
    environment:
      - POSTGRES_PASSWORD=sample
      - POSTGRES_USER=sample
      - POSTGRES_DB=sample
    restart: always
      
```

Some of the container will store data outside on the docker host. Therefore let's create the parent directory **container_data** for holding all that data. 

```
mkdir container_data
```

## Start the environment

Before we can start the environment, we have to set the environment variable DOCKER_HOST_IP to contain the IP-address of the Docker Host and environment variable PUBLIC_IP to the public IP address (not the same if you are using Azure). 
You can find the IP-Address of the Docker host using the `ifconfig` config from the linux shell. The public IP address of the VM in Azure can be found in the Azure portal.

```
export PUBLIC_IP=40.91.195.92
export DOCKER_HOST_IP=10.0.1.4
```

Now let's run all the container specified by the Docker Compose configuration.

```
docker-compose up -d
```

The first time it will take a while, as it has to download many Docker images.

After it has started, you can get the log output of all services by executing
 
```
docker-compose logs -f
```

if you want to see the services running for our environment, perform the following command:

```
docker ps

```

### Add inbound rules for the follwing ports
If you are using a VM on Azure, make sure to add inbound rules for the following ports:

Service | Url
------- | -------------
ActiveMQ | 8161, 61616, 1883


### Add entry to local /etc/hosts File
To simplify working with the Integration Platform, add the following entry to your local `/etc/hosts` file. 

```
40.91.195.92	integrationplatform
```

## Services accessible on Streaming Platform
The following service are available as part of the platform:

Type | Service | Url
------|------- | -------------
Development | StreamSets Data Collector | <http://streamingplatform:18630>
Governance | Schema Registry UI  | <http://streamingplatform:8002>
Governance | Schema Registry Rest API  | <http://streamingplatform:8081>
Management | Hawtio | <http://integrationplatform:8090/hawtio>
Management | ActiveMQ Admin | <http://integrationplatform:8161>
Management | Servicemix | <http://integrationplatform:8161>
Management | Kafka Manager  | <http://streamingplatform:39000>


## Stop the environment
To stop the environment, execute the following command:

```
docker-compose stop
```

after that it can be re-started using `docker-compose start`.

To stop and remove all running container, execute the following command:

```
docker-compose down
```

Remove "dangling" volumes

```
docker volume rm (docker volume ls -qf dangling=true)
```


