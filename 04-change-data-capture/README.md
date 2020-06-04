# Change Data Capture with Debezium



```
wget https://d1i4a15mxbxib1.cloudfront.net/api/plugins/debezium/debezium-connector-mysql/versions/1.1.0/debezium-debezium-connector-mysql-1.1.0.zip
```

```
docker-compose restart kafka-connect-1 kafka-connect-2
```

```
docker stop mysql
docker rm mysql
```

```
docker run -d --rm --name mysql --network platys-integration_default -p 3306:3306 -e MYSQL_ROOT_PASSWORD=debezium -e MYSQL_USER=mysqluser -e MYSQL_PASSWORD=mysqlpw debezium/example-mysql:1.2
```



```
docker exec -it mysql bin/bash

# login as root user
mysql -u root -p"debezium" 
```

At the mysql> command prompt, switch to the inventory database:

```
use inventory
```

List the tables in the database:

```
show tables;
```

Use the MySQL command line client to explore the database and view the pre-loaded data in the database.

```
SELECT * FROM customers;
```


```
curl -X POST \
  "$DOCKER_HOST_IP:8083/connectors" \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
  "name": "inventory-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "tasks.max": "1",
    "database.hostname": "mysql",
    "database.port": "3306",
    "database.user": "debezium",
    "database.password": "dbz",
    "database.server.id": "184054",
    "database.server.name": "dbserver1",
    "database.whitelist": "inventory",
    "database.history.kafka.bootstrap.servers": "kafka-1:19092",
    "database.history.kafka.topic": "schema-changes.inventory"
  }
}'
```

```
kafkacat -b dataplatform -t dbserver1.inventory.customers -s avro -r http://dataplatform:8081
```


```
curl -X "DELETE" "$DOCKER_HOST_IP:8083/connectors/inventory-connector"
```


```
curl -X POST \
  "$DOCKER_HOST_IP:8083/connectors" \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
  "name": "inventory-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "tasks.max": "1",
    "database.hostname": "mysql",
    "database.port": "3306",
    "database.user": "debezium",
    "database.password": "dbz",
    "database.server.id": "184054",
    "database.server.name": "dbserver2",
    "database.whitelist": "inventory",
    "database.history.kafka.bootstrap.servers": "kafka-1:19092",
    "database.history.kafka.topic": "schema-changes.inventory",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "key.converter.schemas.enable": "false",
    "value.converter": "io.debezium.converters.CloudEventsConverter",
    "value.converter.serializer.type" : "avro",
    "value.converter.data.serializer.type" : "avro",
    "value.converter.avro.schema.registry.url": "http://schema-registry-1:8081" 
  }
}'

```

```
kafkacat -b dataplatform -t dbserver2.inventory.customers -s avro -r http://dataplatform:8081 -q -u | jq '.'
```


```
curl -X "DELETE" "$DOCKER_HOST_IP:8083/connectors/inventory-connector"
```



```
curl -X POST \
  "$DOCKER_HOST_IP:8083/connectors" \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
  "name": "inventory-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "tasks.max": "1",
    "database.hostname": "mysql",
    "database.port": "3306",
    "database.user": "debezium",
    "database.password": "dbz",
    "database.server.id": "184054",
    "database.server.name": "dbserver3",
    "database.whitelist": "inventory",
    "database.history.kafka.bootstrap.servers": "kafka-1:19092",
    "database.history.kafka.topic": "schema-changes.inventory",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "key.converter.schemas.enable": "false",
    "value.converter": "io.debezium.converters.CloudEventsConverter",
    "value.converter.serializer.type" : "json",
    "value.converter.data.serializer.type" : "json",
    "value.converter.avro.schema.registry.url": "http://schema-registry-1:8081" 
  }
}'

```

```
kafkacat -b dataplatform -t dbserver3.inventory.customers -q -u
```


```
kafkacat -b dataplatform -t dbserver3.inventory.customers -q -u | jq '.'
```





