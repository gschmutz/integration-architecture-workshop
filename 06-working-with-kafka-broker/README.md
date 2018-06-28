# Getting started with Apache Kafka
In this workshop we will learn the basics of working with Apache Kafka. Make sure that you have created the environment as described in [Preparing the Environment](../01-environment/01-environment.md).

The main units of interest in Kafka are topics and messages. A topic is simply what you publish a message to, topics are a stream of messages.

In this workshop you will learn how to create topics, how to produce messsages, how to consume messages and how to descibe/view metadata in Apache Kafka. 
    
## Adding Apache Kafka to the Integration Platform

Our integration platform does not yet contain a Kafka message broker.A Zookeeper node is already up and running and we will reuse it. 

So let's add a new service to the docker-compose.yml file we have created in [Setup of the Integration Platform](../01-environment/README.md).

```
  broker-1:
    image: confluentinc/cp-enterprise-kafka:5.0.0-beta30-1
    hostname: broker-1
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_BROKER_RACK: 'r1'
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_ADVERTISED_LISTENERS: 'PLAINTEXT://${DOCKER_HOST_IP}:9092'
      KAFKA_METRIC_REPORTERS: io.confluent.metrics.reporter.ConfluentMetricsReporter
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
      KAFKA_DELETE_TOPIC_ENABLE: 'true'
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'false'
      KAFKA_JMX_PORT: 9994
      CONFLUENT_METRICS_REPORTER_BOOTSTRAP_SERVERS: broker-1:9092
      CONFLUENT_METRICS_REPORTER_ZOOKEEPER_CONNECT: zookeeper:2181
      CONFLUENT_METRICS_REPORTER_TOPIC_REPLICAS: 1
      CONFLUENT_METRICS_ENABLE: 'true'
      CONFLUENT_SUPPORT_CUSTOMER_ID: 'anonymous'
    restart: always

  broker-2:
    image: confluentinc/cp-enterprise-kafka:5.0.0-beta30-1
    hostname: broker-2
    depends_on:
      - zookeeper
    ports:
      - "9093:9093"
    environment:
      KAFKA_BROKER_ID: 2
      KAFKA_BROKER_RACK: 'r1'
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_ADVERTISED_LISTENERS: 'PLAINTEXT://${DOCKER_HOST_IP}:9093'
      KAFKA_METRIC_REPORTERS: io.confluent.metrics.reporter.ConfluentMetricsReporter
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
      KAFKA_DELETE_TOPIC_ENABLE: 'true'
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'false'
      KAFKA_JMX_PORT: 9993
      CONFLUENT_METRICS_REPORTER_BOOTSTRAP_SERVERS: broker-2:9093
      CONFLUENT_METRICS_REPORTER_ZOOKEEPER_CONNECT: zookeeper:2181
      CONFLUENT_METRICS_REPORTER_TOPIC_REPLICAS: 1
      CONFLUENT_METRICS_ENABLE: 'true'
      CONFLUENT_SUPPORT_CUSTOMER_ID: 'anonymous'
    restart: always

  broker-3:
    image: confluentinc/cp-enterprise-kafka:5.0.0-beta30-1
    hostname: broker-3
    depends_on:
      - zookeeper
    ports:
      - "9094:9094"
    environment:
      KAFKA_BROKER_ID: 3
      KAFKA_BROKER_RACK: 'r1'
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_ADVERTISED_LISTENERS: 'PLAINTEXT://${DOCKER_HOST_IP}:9094'
      KAFKA_METRIC_REPORTERS: io.confluent.metrics.reporter.ConfluentMetricsReporter
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
      KAFKA_DELETE_TOPIC_ENABLE: 'true'
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'false'
      KAFKA_JMX_PORT: 9994
      CONFLUENT_METRICS_REPORTER_BOOTSTRAP_SERVERS: broker-3:9094
      CONFLUENT_METRICS_REPORTER_ZOOKEEPER_CONNECT: zookeeper:2181
      CONFLUENT_METRICS_REPORTER_TOPIC_REPLICAS: 1
      CONFLUENT_METRICS_ENABLE: 'true'
      CONFLUENT_SUPPORT_CUSTOMER_ID: 'anonymous'
    restart: always
      
  schema_registry:
    image: confluentinc/cp-schema-registry:5.0.0-beta30-1
    hostname: schema_registry
    depends_on:
      - zookeeper
      - broker-1
    ports:
      - "8081:8081"
    environment:
      SCHEMA_REGISTRY_HOST_NAME: schema_registry
      SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL: 'zookeeper:2181'
      SCHEMA_REGISTRY_ACCESS_CONTROL_ALLOW_ORIGIN: '*'
      SCHEMA_REGISTRY_ACCESS_CONTROL_ALLOW_METHODS: 'GET,POST,PUT,OPTIONS'
    restart: always

  kafka-manager:
    image: trivadisbds/kafka-manager
    hostname: kafka-manager
    depends_on:
      - zookeeper
    ports:
      - "39000:9000"
    environment:
      ZK_HOSTS: 'zookeeper:2181'
      APPLICATION_SECRET: 'letmein'
    restart: always    
```

Now let's start that sevice by executing `docker-compose up` once more. 

```
docker-compose up -d	
```

With Docker Compose, you can easily later add some new services, even if the platform is currently running. If you redo a `docker-compose up -d`, Docker Compose will check if there is a delta between what is currently running and what the `docker-compose.yml` file tells. 

If there is a new service added, such as here with Mosquitto, Docker Compose will start the service, leaving the other, already running services untouched. 

If you change configuration on an already running service, then Docker will recreate that service applying the new settings. 

However, removing a service from the `docker-compose.yml` will not cause a running service to be stopped and removed. You have to do that manually. 

## Working with built-in Command Line Utilities 

### Connect to a Kafka Broker 
The environment contains of a Kafka cluster with 3 brokers, all running on the Docker host of course. So it's of course not meant to really fault-tolerant but to demonstrate how to work with a Kafka cluster. 

To work with Kafka you need the command line utilities. The are available on each broker. 
The `kafka-topics` utility is used to create, alter, describe, and delete topics. The `kafka-console-producer` and `kafka-console-consumer` can be used to produce/consume messages to/from a Kafka topic. 

So let's connect into one of the broker through a terminal window. 

1. open a terminal window on the Docker Host
2. run a `docker exec` command to run a shell in the docker container of broker-1

```
docker exec -ti streamingplatform_broker-1_1 bash
```

### List topics in Kafka

First, let's list the topics availble on a given Kafka Cluster.
For that we use the `kafka-topics` utility with the `--list` option. 

```
kafka-topics --list --zookeeper zookeeper:2181
```
We can see that there are some techical topics, _schemas being the one, where the Confluent Schema Registry stores its schemas. 

### Creating a topic in Kafka

Now let's create a new topic. For that we again use the `kafka-topics` utility but this time with the `--create` option. We will create a test topic with 6 partitions and replicated 2 times. The `--if-not-exists` option is handy to avoid errors, if a topic already exists. 

```
kafka-topics --create \
             --if-not-exists \
             --zookeeper zookeeper:2181 \
             --topic test-topic \
	     --partitions 6 \
	     --replication-factor 2
```

Re-Run the command to list the topics. You should see the new topic you have just created. 

### Describe a Topic

```
kafka-topics --describe --zookeeper zookeeper:2181 --topic test-topic
```

```
Topic:test-topic	PartitionCount:6	ReplicationFactor:2	Configs:
	Topic: test-topic	Partition: 0	Leader: 3	Replicas: 3,2	Isr: 3,2
	Topic: test-topic	Partition: 1	Leader: 1	Replicas: 1,3	Isr: 1,3
	Topic: test-topic	Partition: 2	Leader: 2	Replicas: 2,1	Isr: 2,1
	Topic: test-topic	Partition: 3	Leader: 3	Replicas: 3,1	Isr: 3,1
	Topic: test-topic	Partition: 4	Leader: 1	Replicas: 1,2	Isr: 1,2
	Topic: test-topic	Partition: 5	Leader: 2	Replicas: 2,3	Isr: 2,3
```

### Produce and Consume to Kafka topic with command line utility
Now let's see the topic in use. The most basic way to test it is through the command line. Kafka comes with two handy utilities `kafka-console-consumer` and `kafka-console-producer` to consume and produce messages trought the command line. 

In a new terminal window, first let's run the consumer

```
kafka-console-consumer --bootstrap-server broker-1:9092,broker-2:9093 \
				--topic test-topic
```
After it is started, the consumer just waits for newly produced messages. 

In a another terminal, connect into broker-1 using `docker exec` and run the following command to start the producer. The console producer reads from stdin, and takes a broker list instead of a zookeeper address. We specify 2 of the 3 brokers of our streaming platform.  
 
```
kafka-console-producer --broker-list broker-1:9092,broker-2:9093 \
				--topic test-topic
```

On the `>` prompt enter a few messages, execute each single message by hitting the Enter key.<br>
**Hint:** Try to enter them as quick as possible.

```
>aaa
>bbb
>ccc
>ddd
>eee
```

You should see the messages being consumed by the consumer. 

```
root@broker-1:/# kafka-console-consumer --bootstrap-server broker-1:9092,broker-2:9093 --topic test-topic
aaa
bbb
ccc
eee
ddd
```

You can see that they do not arrive in the same order (if you are entering them fast enough on the producer side)

You can stop the consumer by hitting Ctrl-C. If you want to consume from the beginning of the log, use the `--from-beginning` option.

You can also echo a longer message and pipe it into the console producer, as he is reading the next message from the command line:

```
echo "This is my first message!" | kafka-console-producer \
                  --broker-list broker-1:9092,broker-2:9093 \
                  --topic test-topic
```

And of course you can send messages inside a bash for loop:

```
for i in 1 2 3 4 5 6 7 8 9 10
do
   echo "This is message $i"| kafka-console-producer \
                  --broker-list broker-1:9092,broker-2:9093 \
                  --topic test-topic \
                  --batch-size 1 &
done 
```

By ending the command in the loop with an & character, we run each command in the background and by that in parallel. 

If you check the consumer, you can see that they are not in the same order as sent, because of the different partitions, and the messages being published in multiple partitions. We can force order, by using a key when publishing the messages and always using the same value for the key. 

### Working with Keyed Messages
A message produced to Kafka always consists of a key and a value, the value being necessary and representing the message/event payload. If a key is not specified, such as we did so far, then it is passed as a null value and Kafka distributes such messages in a round-robin fashion over the different partitions. 

We can check that by just listing the messages we have created so far specifying the properties `print.key` and `key.separator` together with the `--from-beginning` in the console consumer. 

```
kafka-console-consumer --bootstrap-server broker-1:9092,broker-2:9093 \
							--topic test-topic \
							--property print.key=true \
							--property key.separator=, \
							--from-beginning
```
To produce messages with a key, use the properties `parse.key` and `key.separator`. 

```
kafka-console-producer --broker-list broker-1:9092,broker-2:9093 \
							--topic test-topic \
							--property parse.key=true \
							--property key.separator=,
```

Enter your messages so that a key and messages are separated by a comma, i.e. `key1,message1`.

### Dropping a Kafka topic
A Kafka topic can be droped using the `kafka-topics` utility with the `--delete` option. 

```
kafka-topics --zookeeper zookeeper:2181 --delete --topic test-topic
```

## Working with the Kafkacat utility
[Kafkacat](https://docs.confluent.io/current/app-development/kafkacat-usage.html#kafkacat-usage) is a command line utility that you can use to test and debug Apache Kafka deployments. You can use kafkacat to produce, consume, and list topic and partition information for Kafka. Described as “netcat for Kafka”, it is a swiss-army knife of tools for inspecting and creating data in Kafka.

It is similar to the `kafka-console-producer` and `kafka-console-consumer` you have learnt and used above, but much more powerful. 

kafkacat is an open-source utility, available at <https://github.com/edenhill/kafkacat>. It is not part of the Confluent platform and also not part of the streaming platform we run in docker. 

### Install kafakcat

You can install kafkacat directly on the Ubuntu environment. First let's install the required packages:

Install the Confluent public key, which is used to sign the packages in the APT repository:

```
wget -qO - https://packages.confluent.io/deb/4.1/archive.key | sudo apt-key add -
```

Add the repository to the `/etc/apt/sources.list`:

```
sudo add-apt-repository "deb [arch=amd64] https://packages.confluent.io/deb/4.1 stable main"
```

Run apt-get update and install the 2 dependencies as well as kafkacat:
 
```
sudo apt-get update
sudo apt-get install librdkafka-dev libyajl-dev
apt-get install kafkacat
```

### Show kafkacat options
kafkacat has many options. If you just enter `kafkacat` without any options, all the options and some description is shown on the console:

```
> kafkacat
Error: -b <broker,..> missing

Usage: kafkacat <options> [file1 file2 ..]
kafkacat - Apache Kafka producer and consumer tool
https://github.com/edenhill/kafkacat
Copyright (c) 2014-2015, Magnus Edenhill
Version KAFKACAT_VERSION (JSON) (librdkafka 0.11.4)


General options:
  -C | -P | -L       Mode: Consume, Produce or metadata List
  -t <topic>         Topic to consume from, produce to, or list
  -p <partition>     Partition
  -b <brokers,..>    Bootstrap broker(s) (host[:port])
  -D <delim>         Message delimiter character:
                     a-z.. | \r | \n | \t | \xNN
                     Default: \n
  -K <delim>         Key delimiter (same format as -D)
  -c <cnt>           Limit message count
  -X list            List available librdkafka configuration properties
  -X prop=val        Set librdkafka configuration property.
                     Properties prefixed with "topic." are
                     applied as topic properties.
  -X dump            Dump configuration and exit.
  -d <dbg1,...>      Enable librdkafka debugging:
                     all,generic,broker,topic,metadata,producer,queue,msg,protocol
  -q                 Be quiet (verbosity set to 0)
  -v                 Increase verbosity

Producer options:
  -z snappy|gzip     Message compression. Default: none
  -p -1              Use random partitioner
  -D <delim>         Delimiter to split input into messages
  -K <delim>         Delimiter to split input key and message
  -T                 Output sent messages to stdout, acting like tee.
  -c <cnt>           Exit after producing this number of messages
  -Z                 Send empty messages as NULL messages
  file1 file2..      Read messages from files.
                     The entire file contents will be sent as
                     one single message.

Consumer options:
  -o <offset>        Offset to start consuming from:
                     beginning | end | stored |
                     <value>  (absolute offset) |
                     -<value> (relative offset from end)
  -e                 Exit successfully when last message received
  -f <fmt..>         Output formatting string, see below.
                     Takes precedence over -D and -K.
  -J                 Output with JSON envelope
  -D <delim>         Delimiter to separate messages on output
  -K <delim>         Print message keys prefixing the message
                     with specified delimiter.
  -O                 Print message offset using -K delimiter
  -c <cnt>           Exit after consuming this number of messages
  -Z                 Print NULL messages and keys as "NULL"(instead of empty)
  -u                 Unbuffered output

Metadata options:
  -t <topic>         Topic to query (optional)


Format string tokens:
  %s                 Message payload
  %S                 Message payload length (or -1 for NULL)
  %k                 Message key
  %K                 Message key length (or -1 for NULL)
  %t                 Topic
  %p                 Partition
  %o                 Message offset
  \n \r \t           Newlines, tab
  \xXX \xNNN         Any ASCII character
 Example:
  -f 'Topic %t [%p] at offset %o: key %k: %s\n'


Consumer mode (writes messages to stdout):
  kafkacat -b <broker> -t <topic> -p <partition>
 or:
  kafkacat -C -b ...

Producer mode (reads messages from stdin):
  ... | kafkacat -b <broker> -t <topic> -p <partition>
 or:
  kafkacat -P -b ...

Metadata listing:
  kafkacat -L -b <broker> [-t <topic>]
```

Now let's use it to Produce and Consume messages.

### Consuming messages using kafkacat

The simplest way to consume a topic is just specfiying the broker and the topic. By default all messages from the beginning of the topic will be shown 

```
kafkacat -b 10.0.1.4 -t test-topic
```

If you want to start at the end of the topic, i.e. only show new messages, add the `-o` option. 

```
kafkacat -b 10.0.1.4 -t test-topic -o end
```

To show only the last message (one for each partition), set the `-o` option to -1. -2 would show the last 2 messages.

```
kafkacat -b 10.0.1.4 -t test-topic -o -1
```

You can use the `-f` option to format the output. Here we shwo the partition (%p) as well as key (%k) and message (%s):

```
kafkacat -b 10.0.1.4 -t test-topic -f 'Part-%p => %k:%s\n'
```

If there are keys wich are Null, then you can use -Z to actually show NULL in the output:

```
kafkacat -b 10.0.1.4 -t test-topic -f 'Part-%p => %k:%s\n' -Z
```

### Producing messages using kafkacat

Producing messages with kafacat is as easy as consuming. Just add the `-P` option to switch to Producer mode. Just enter data se

```
kafkacat -b 10.0.1.4 -t test-topic -P
```

To produce with key, specify the delimiter to split key and message, using the `-K` option. 

```
kafkacat -b 10.0.1.4 -t test-topic -P -K , -X topic.partitioner=murmur2_random
```


### Send realistic test mesages to Kafka using Mockaroo and Kafkacat

In his [blog article](https://rmoff.net/2018/05/10/quick-n-easy-population-of-realistic-test-data-into-kafka-with-mockaroo-and-kafkacat/) Robin Moffatt shows an interesting and easy approach to send realistic mock data to Kafka. He is using [Mockaroo](https://mockaroo.com/), a free test data generator and API mocking tool, together with [Kafkacat](https://github.com/edenhill/kafkacat) to produce mock messages. 

Taking his example, you can send 10 orders to test-topic.

```
curl -s "https://api.mockaroo.com/api/d5a195e0?count=20&key=ff7856d0"| \
	kafkacat -b 10.0.1.4:9092 -t test-topic -P
```

