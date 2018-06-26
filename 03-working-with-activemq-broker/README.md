# Getting started with Apache ActiveMQ
In this workshop we will learn the basics of working with [Apache ActiveMQ](http://activemq.apache.org). Make sure that you have created the environment as described in [Preparing the Environment](../01-environment/01-environment.md).

The main units of interest in Apache ActiveMQ are queues, topics and messages.

In this workshop you will learn how to use and work with Queues and Topics and send and consume messages to/from both of them.

## Adding Apache ActiveMQ to the Integration Platform
Our integration platform does not yet contain a message broker.

So let's add a new service to the docker-compose.yml file we have created in [Setup of the Integration Platform](../01-environment/README.md).

```
  activemq:
    image: rmohr/activemq:5.15.4
    hostname: activemq
    ports:
      - 61616:61616
      - 8161:8161
    volumes:
      - ./container_data/activemq/data:/opt/activemq/data
    restart: always
    
  hawtio:
    image: "indigo/hawtio"
    hostname: hawtio
    ports:
      - "8090:8090"
    restart: always
```

The service will map the data folder to a folder on the docker host. Therefore we have to create it below the container_data created in the setup and give it the necessary access privileges.

```
cd integrationplatform
mkdir -p container_data/activemq/data
chmod 777 -R container_data
```
	
Now let's start that sevice by executing `docker-compose up` once more. 

```
docker-compose up -d	
```

With Docker Compose, you can easily later add some new services, even if the platform is currently running. If you redo a `docker-compose up -d`, Docker Compose will check if there is a delta between what is currently running and what the `docker-compose.yml` file tells. 

If there is a new service added, such as here with Mosquitto, Docker Compose will start the service, leaving the other, already running services untouched. 

If you change configuration on an already running service, then Docker will recreate that service applying the new settings. 

However, removing a service from the `docker-compose.yml` will not cause a running service to be stopped and removed. You have to do that manually. 

## Testing Broker with built-in Command Line Utility

ActveMQ contains a built-in utiliity which can be used to easily test sending and consuming from a queue in ActiveMQ. The utility uses a queue with a fixed name `TEST`.

### Connecting to a ActiveMQ Broker 

To connect to the ActiveMQ broker we have started above, open a new terminal window on the Docker Host. 
In that window, execute a `docker exec` command to run a shell in the `activemq` docker container.

```
docker exec -ti integrationplatform_activemq_1 bash
```

Now we are connected to bash shell in the running ActiveMQ container. This is similar to an SSH connect to a remote machine, but SSH is not easily done in a containerized environment, as both the credentials as well as the IP address of the container are easily known. You can ask for the IP address of a running container, but it will not be static over multiple runs. Using the `docker exec` command simplifies that a lot.

Navigate to the ActiveMQ installation and into the `/bin` folder. 

```
cd /opt/activemq/bin
```
### Using the activemq CLI utility

Here we can find the `activemq` CLI utility. If you just enter `activemq` without any parameter, the help page will be show. 

```
./activemq
```

This is the output you should get:

```
INFO: Loading '/opt/activemq/bin/env'
INFO: Using java '/docker-java-home/jre/bin/java'
Java Runtime: Oracle Corporation 1.8.0_171 /usr/lib/jvm/java-8-openjdk-amd64/jre
  Heap sizes: current=62976k  free=61992k  max=932352k
    JVM args: -Xms64M -Xmx1G -Djava.util.logging.config.file=logging.properties -Djava.security.auth.login.config=/opt/activemq/conf/login.config -Dactivemq.classpath=/opt/activemq/conf:/opt/activemq/../lib/: -Dactivemq.home=/opt/activemq -Dactivemq.base=/opt/activemq -Dactivemq.conf=/opt/activemq/conf -Dactivemq.data=/opt/activemq/data
Extensions classpath:
  [/opt/activemq/lib,/opt/activemq/lib/camel,/opt/activemq/lib/optional,/opt/activemq/lib/web,/opt/activemq/lib/extra]
ACTIVEMQ_HOME: /opt/activemq
ACTIVEMQ_BASE: /opt/activemq
ACTIVEMQ_CONF: /opt/activemq/conf
ACTIVEMQ_DATA: /opt/activemq/data
Usage: ./activemq [--extdir <dir>] [task] [task-options] [task data]

Tasks:
    browse                   - Display selected messages in a specified destination.
    bstat                    - Performs a predefined query that displays useful statistics regarding the specified broker
    consumer                 - Receives messages from the broker
    create                   - Creates a runnable broker instance in the specified path.
    decrypt                  - Decrypts given text
    dstat                    - Performs a predefined query that displays useful tabular statistics regarding the specified destination type
    encrypt                  - Encrypts given text
    export                   - Exports a stopped brokers data files to an archive file
    list                     - Lists all available brokers in the specified JMX context
    producer                 - Sends messages to the broker
    purge                    - Delete selected destination's messages that matches the message selector
    query                    - Display selected broker component's attributes and statistics.
    start                    - Creates and starts a broker using a configuration file, or a broker URI.
    stop                     - Stops a running broker specified by the broker name.

Task Options (Options specific to each task):
    --extdir <dir>  - Add the jar files in the directory to the classpath.
    --version       - Display the version information.
    -h,-?,--help    - Display this help information. To display task specific help, use Main [task] -h,-?,--help

Task Data:
    - Information needed by each specific task.

JMX system property options:
    -Dactivemq.jmx.url=<jmx service uri> (default is: 'service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi')
    -Dactivemq.jmx.user=<user name>
    -Dactivemq.jmx.password=<password>


Tasks provided by the sysv init script:
    kill            - terminate instance in a drastic way by sending SIGKILL
    restart         - stop running instance (if there is one), start new instance
    console         - start broker in foreground, useful for debugging purposes
    status          - check if activemq process is running

Configuration of this script:
    The configuration of this script is read from the following files:
    /etc/default/activemq /opt/activemq/.activemqrc /opt/activemq/bin/env
    This script searches for the files in the listed order and reads the first available file.
    Modify /opt/activemq/bin/env or create a copy of that file on a suitable location.
    To use additional configurations for running multiple instances on the same operating system
    rename or symlink script to a name matching to activemq-instance-<INSTANCENAME>.
    This changes the configuration location to /etc/default/activemq-instance-<INSTANCENAME> and
    $HOME/.activemqrc-instance-<INSTANCENAME>.
```

Now let's use this utility to produce some messages to a queue.

### Producing messages
Messages can be easliy produced by using the producer option, as shown here. 

```
./activemq producer
```

This will produce a 1000 messages to the `TEST` queue. It will run for a few seconds and then the prompt will be shown again. 

In the next step, let's consume the messages we have produced.

### Consuming messages

Messages from the `TEST` queue can easily be consumed using the `consumer` option of the `activemq` CLI utlity.

```
./activemq consumer
```

you should get an output similar to the one shown below, showing the 1000 messages consumed from the queue. 

```
INFO: Loading '/opt/activemq/bin/env'
INFO: Using java '/docker-java-home/jre/bin/java'
Java Runtime: Oracle Corporation 1.8.0_171 /usr/lib/jvm/java-8-openjdk-amd64/jre
  Heap sizes: current=62976k  free=61992k  max=932352k
    JVM args: -Xms64M -Xmx1G -Djava.util.logging.config.file=logging.properties -Djava.security.auth.login.config=/opt/activemq/conf/login.config -Dactivemq.classpath=/opt/activemq/conf:/opt/activemq/../lib/: -Dactivemq.home=/opt/activemq -Dactivemq.base=/opt/activemq -Dactivemq.conf=/opt/activemq/conf -Dactivemq.data=/opt/activemq/data
Extensions classpath:
  [/opt/activemq/lib,/opt/activemq/lib/camel,/opt/activemq/lib/optional,/opt/activemq/lib/web,/opt/activemq/lib/extra]
ACTIVEMQ_HOME: /opt/activemq
ACTIVEMQ_BASE: /opt/activemq
ACTIVEMQ_CONF: /opt/activemq/conf
ACTIVEMQ_DATA: /opt/activemq/data
 INFO | Connecting to URL: failover://tcp://localhost:61616 as user: null
 INFO | Consuming queue://TEST
 INFO | Sleeping between receives 0 ms
 INFO | Running 1 parallel threads
 INFO | Successfully connected to tcp://localhost:61616
 INFO | consumer-1 wait until 1000 messages are consumed
 INFO | consumer-1 Received test message: 0
 INFO | consumer-1 Received test message: 1
 INFO | consumer-1 Received test message: 2
 INFO | consumer-1 Received test message: 3
 INFO | consumer-1 Received test message: 4
 INFO | consumer-1 Received test message: 5
 INFO | consumer-1 Received test message: 6
 INFO | consumer-1 Received test message: 7
 INFO | consumer-1 Received test message: 8
 INFO | consumer-1 Received test message: 9
 ...
 INFO | consumer-1 Received test message: 999
 INFO | consumer-1 Consumed: 1000 messages
 INFO | consumer-1 Consumer thread finished
```

After that the consumer will stop. If you rerun the consumer, then it will be blocked waiting for messages, as all the messages have been consumed by the previous run. 

In a new terminal window, connect to the broker 

```
docker exec -ti integrationplatform-activemq_1 bash
cd /opt/activemq/bin
``` 

and execute the producer a second time.

``` 
activemq producer
``` 

As soon as it is running, you should see messages appearing on the consumer.
 
**Optional:** Start two consumers in two independent windows and see what happens when you produce messages.

### Browsing messages

You can also use the activemq utility to browse for messages. This will show all messages, which have not yet been conumed. 

``` 
./activemq browse --amqurl tcp://localhost:61616 TEST
``` 

## Using the ActiveMQ Web Console

The [ActiveMQ Web Console](http://activemq.apache.org/web-console.html) is a web based administration tool for working with ActiveMQ. When used with the JMX support it can be an invaluable tool for working with ActiveMQ. 

The ActiveMQ Web Console is included in the binary option of ActiveMQ and threfore also available inside the docker container. In a browser, navigate to the following URL (assuming that you have added `integrationplatform` to your `/etc/hosts` file). 

<http://integrationplatform:8161/admin/>

Enter **admin** into the **User Name** field and **admin** into the **Password** field and click **OK**. 
You should see the Web Console running:

![Alt Image Text](./images/activemq-webconsole-overview.png "Schema Registry UI")

Click on the **Queues** link in the menu on top. 

![Alt Image Text](./images/activemq-webconsole-queues.png "Schema Registry UI")

You can use the Web Console to produce messages, as well as purge and delete topics. You can also copy/move single messages. 

With the third party console, Hawt.IO presented below, you can do a bit more - such as bulk operations.

## Using the Hawt.io Web Console with ActiveMQ
[Hawtio](http://hawt.io/) is a modular web console for managing your Java stuff.

In a Web browser navigate to the following URL:

<http://integrationplatform:8090/hawtio/>

You should see the Hawtio Web Console running:

![Alt Image Text](./images/hawtio-webconsole-overview.png "Schema Registry UI")

Let's connect to the ActiveMQ broker. In the **Connection Settings**, enter **ActiveMQ** into the **Name** field, **activemq** into the **Host** field, change the **Port** field to **8161** and enter **api/jolokia** into the **Path** field. Then click on **Connect to remote server**.

The ActiveMQ dashboard will be shown.

![Alt Image Text](./images/hawtio-activemq-overview.png "Schema Registry UI")

On the right side you can navigate to the **Queue**s and **Topic**s available on the broker. On the right side you can see metrics for the queue.

Click on **Browse** to see the messages currently in the queue waiting for consumption.

![Alt Image Text](./images/hawtio-activemq-queue-browse.png "Schema Registry UI")

Navigate and familiarize yourself with the possibilities of Hawtio. 

## Using the "A" command line utility

Both the console as well as Hawt.io do not behave like a real client. You can't simulate a client subscribing to a topic. The [ActiveMQ CLI testing and message management](https://github.com/fmtn/a) utility, implemented by Peter Nordlander and available on his GitHub account, simplifies testing and message management quite a bit. It is a command line utility and also known as the "a" command. 

### Installing the "A" utility

Create a bin folder in the user home, if it does not yet exists.

```
mkdir ~/bin
cd ~/bin
```

Now let's download the latest release from [GitHub](https://github.com/fmtn/a) project.


```
wget https://github.com/fmtn/a/releases/download/v1.4.6/a-1.4.6-dist.tar.gz
```

Then unpack it using the followin command

```
tar xvf a-1.4.6-dist.tar.gz
```

Remove the files which are not needed (asuming we are on Linux). On windows, leave the a.bat and remove the a file instead). 

```
rm a-1.4.6-dist.tar.gz LICENSE README.md a.bat
chmod +x a
```

Now the "A" script should be available. Enter a without any parameter to get the usage page with an overview of the various options.

```
~/bin> a
usage: java -jar a-<version>-with-dependencies.jar [-A] [-a] [-B
       <property=value>] [-b <arg>] [-C <arg>] [-c <arg>] [-E <arg>] [-e
       <arg>] [-F <arg>] [-f <arg>] [-g] [-H <property=value>] [-I
       <property=value>] [-i <arg>] [-J <arg>] [-j] [-L <property=value>]
       [-l] [-M <arg>] [-n] [-O] [-o <arg>] [-P <arg>] [-p <arg>] [-R
       <arg>] [-r <arg>] [-S <arg>] [-s <arg>] [-T] [-t <arg>] [-U <arg>]
       [-v] [-W <arg>] [-w <arg>] [-X <arg>] [-x <arg>] [-y <arg>]
 -A,--amqp                     Set protocol to AMQP. Defaults to OpenWire
 -a,--artemis-core             Set protocol to ActiveMQ Artemis Core.
                               Defaults to OpenWire
 -B <property=value>           use value for given Boolean property. Can
                               be used several times.
 -b,--broker <arg>             URL to broker. defaults to:
                               tcp://localhost:61616
 -C,--copy-queue <arg>         Copy all messages from this to target.
                               Limited by maxBrowsePageSize in broker
                               settings (default 400).
 -c,--count <arg>              A number of messages to browse,get,move or
                               put (put will put the same message <count>
                               times). 0 means all messages.
 -E,--correlation-id <arg>     Set CorrelationID
 -e,--encoding <arg>           Encoding of input file data. Default UTF-8
 -F,--jndi-cf-name <arg>       Specify JNDI name for ConnectionFactory.
                               Defaults to connectionFactory. Use with -J
 -f,--find <arg>               Search for messages in queue with this
                               value in payload. Use with browse.
 -g,--get                      Get a message from destination
 -H <property=value>           use value for given String property. Can be
                               used several times.
 -I <property=value>           use value for given Integer property. Can
                               be used several times.
 -i,--priority <arg>           sets JMSPriority
 -J,--jndi <arg>               Connect via JNDI. Overrides -b and -A
                               options. Specify context file on classpath
 -j,--jms-headers              Print JMS headers
 -L <property=value>           use value for given Long property. Can be
                               used several times.
 -l,--list-queues              List queues and topics on broker (OpenWire
                               only)
 -M,--move-queue <arg>         Move all messages from this to target
 -n,--non-persistent           Set message to non persistent.
 -O,--openwire                 Set protocol to OpenWire. This is default
                               protocol
 -o,--output <arg>             file to write payload to. If multiple
                               messages, a -1.<ext> will be added to the
                               file. BytesMessage will be written as-is,
                               TextMessage will be written in UTF-8
 -P,--pass <arg>               Password to connect to broker
 -p,--put <arg>                Put a message. Specify data. if starts with
                               @, a file is assumed and loaded
 -R,--read-folder <arg>        Read files in folder and put to queue. Sent
                               files are deleted! Specify path and a
                               filename. Wildcards are supported '*' and
                               '?'. If no path is given, current directory
                               is assumed.
 -r,--reply-to <arg>           Set reply to destination, i.e. queue:reply
 -S,--transform-script <arg>   JavaScript code (or @path/to/file.js). Used
                               to transform messages with the dump
                               options. Access message in JavaScript by
                               msg.JMSType = 'foobar';
 -s,--selector <arg>           Browse or get with selector
 -T,--no-transaction-support   Set to disable transactions if not
                               supported by platform. I.e. Azure Service
                               Bus. When set to false, the Move option is
                               NOT atomic.
 -t,--type <arg>               Message type to put, [bytes, text, map] -
                               defaults to text
 -U,--user <arg>               Username to connect to broker
 -v,--version                  Show version of A
 -W,--batch-file <arg>         Line separated batch file. Used with -p to
                               produce one message per line in file. Used
                               together with Script where each batch line
                               can be accessed with variable 'entry'
 -w,--wait <arg>               Time to wait on get operation. Default 50.
                               0 equals infinity
 -X,--restore-dump <arg>       Restore a dump of messages in a
                               file,created with -x. Can be used with
                               transformation option.
 -x,--write-dump <arg>         Write a dump of messages to a file. Will
                               preserve metadata and type. Can  be used
                               with transformation option
 -y,--jms-type <arg>           Sets JMSType header
```

### A in "Action"

To put message with payload "foobar" to queue ow-queue on local broker

```
a -p "foobar" ow-queue
```

Put message with payload of file foo.bar to queue ow-queue on local broker, also set a property

```
a -p "@foo.bar" -Hfoo=bar ow-queue
```

Browse five messages from queue ow-queue.

```
a -c 5 ow-queue
```

Get message from queue and show JMS headers

```
a -g -j ow-queue
```

Put 100 messages to queue ow-queue (for load test etc)

```
a -p "foobar" -c 100 ow-queue
```

Put file foo.bar as a byte message on queue ow-queue

```
a -p "@foo.bar" -t bytes ow-queue
```

Read all XML files in a folder input an put them on queue ow-queue. Files are deleted afterwards.

```
a -R "input/*.xml" ow-queue
```

Put file foo.json as map message on queue ow-queue

```
a -p "@foo.json" -t map ow-queue
```

Put a map message on a queue using json as the format of the payload

```
a -p "{\"a\":\"a message tool\"}" -t map ow-queue
```

A defaults to ActiveMQ default protocol, OpenWire. You can also use AMQP 1.0. In theory, it should work with all AMQP 1.0 compliant brokers. It does not work with older versions of AMQP.

```
a -A -b "amqp://guest:guest@localhost:5672" -p "foobar" aqmp-queue
```
