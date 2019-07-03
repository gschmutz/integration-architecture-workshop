# Producing and Consuming ActiveMQ using Java Message Service (JMS)
In this workshop we will learn how to use the [Java Message Service (JMS) API](https://en.wikipedia.org/wiki/Java_Message_Service).

JMS is a Java API and therefore only available from the Java Virtual Machine. So you need to use a language based on the JVM, such as of course Java. We will be using some sample Java classes to see how working with Queues and Topics over JMS works. 

We will first see how it is to work with Java messaging using plain JMS. Then we will be using the Spring Framework and see how it can simplify working with JMS by quite a bit. 

Start the Eclipse IDE if not yet done. 

## Working with Queues from JMS

Create a new Maven project [as shown here](../99-misc/97-working-with-eclipse/README.md) and in the last step use `com.trivadis.integration.ws` for the **Group Id** and `working-with-queue` for the **Artifact Id**.

### Creating the project definition (pom.xml)

Navigate to the **pom.xml** and double-click on it. The POM Editor will be displayed. 

![Alt Image Text](./images/eclipse-editing-pom-1.png "Edit Pom.xml")

You can either use the GUI to edit your pom.xml or click on the last tab **pom.xml** to switch to the "code view". Let's do that. 

You will see the still rather empty definition.

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.trivadis.integrationws</groupId>
  <artifactId>working-with-queue</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</project>
```

First let's add the dependencies for the project. Copy the following block right after the <version> tag, before the closing </project> tag. 

```
   <properties>
       <activemq-version>5.7.0</activemq-version>
		<slf4j-version>1.6.6</slf4j-version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-core</artifactId>
            <version>${activemq-version}</version>
        </dependency>
		<dependency>
		  	<groupId>org.slf4j</groupId>
		    <artifactId>slf4j-api</artifactId>
		    <version>${slf4j-version}</version>
		</dependency>
		<dependency>
		    <groupId>org.slf4j</groupId>
		    <artifactId>slf4j-log4j12</artifactId>
		    <version>${slf4j-version}</version>
		</dependency>
    </dependencies>
```

### Writing a Producer

First, create a new Java Package `org.apache.activemq.simple.queue` in the folder **src/main/java**.

Create a new Java Class `SimpleProducer` in the package `org.apache.activemq.simple.queue` just created. 

Add the following code to the empty class. 

``` 
package org.apache.activemq.simple.queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

public class SimpleProducer {
    private static final Log LOG = LogFactory.getLog(SimpleProducer.class);

    private static final Boolean NON_TRANSACTED = false;
    private static final long MESSAGE_TIME_TO_LIVE_MILLISECONDS = 0;
    private static final int MESSAGE_DELAY_MILLISECONDS = 100;
    private static final int NUM_MESSAGES_TO_BE_SENT = 100;
    private static final String CONNECTION_FACTORY_NAME = "myJmsFactory";
    private static final String DESTINATION_NAME = "queue/simple";

    public static void main(String args[]) {
        Connection connection = null;

        try {
            // JNDI lookup of JMS Connection Factory and JMS Destination
            Context context = new InitialContext();
            ConnectionFactory factory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);
            Destination destination = (Destination) context.lookup(DESTINATION_NAME);

            connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);

            producer.setTimeToLive(MESSAGE_TIME_TO_LIVE_MILLISECONDS);

            for (int i = 1; i <= NUM_MESSAGES_TO_BE_SENT; i++) {
                TextMessage message = session.createTextMessage(i + ". message sent");
                LOG.info("Sending to destination: " + destination.toString() + " this text: '" + message.getText());
                producer.send(message);
                Thread.sleep(MESSAGE_DELAY_MILLISECONDS);
            }

            // Cleanup
            producer.close();
            session.close();
        } catch (Throwable t) {
            LOG.error(t);
        } finally {
            // Cleanup code
            // In general, you should always close producers, consumers,
            // sessions, and connections in reverse order of creation.
            // For this simple example, a JMS connection.close will
            // clean up all other resources.
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    LOG.error(e);
                }
            }
        }
    }
}
```

### Create a log4j configuration

In the code we are using the [Log4J Logging Framework](https://logging.apache.org/log4j/2.x/), which we have to configure using a property file. 

Create a new file `log4j.properties` in the folder **src/main/resources** and add the following settings. 

```
## ---------------------------------------------------------------------------
## Licensed to the Apache Software Foundation (ASF) under one or more
## contributor license agreements.  See the NOTICE file distributed with
## this work for additional information regarding copyright ownership.
## The ASF licenses this file to You under the Apache License, Version 2.0
## (the "License"); you may not use this file except in compliance with
## the License.  You may obtain a copy of the License at
## 
## http://www.apache.org/licenses/LICENSE-2.0
## 
## Unless required by applicable law or agreed to in writing, software
## distributed under the License is distributed on an "AS IS" BASIS,
## WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
## See the License for the specific language governing permissions and
## limitations under the License.
## ---------------------------------------------------------------------------

#
# The logging properties used by the standalone ActiveMQ broker
#
log4j.rootLogger=INFO, stdout

# CONSOLE appender
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{HH:mm:ss} %-5p %m%n

# Log File appender
log4j.appender.logfile=org.apache.log4j.FileAppender
log4j.appender.logfile.layout=org.apache.log4j.PatternLayout
log4j.appender.logfile.layout.ConversionPattern=%d [%-15.15t] %-5p %-30.30c{1} - %m%n
log4j.appender.logfile.file=./target/log/exercises.log
log4j.appender.logfile.append=true

#
#  You can change logger levels here.
#
log4j.logger.org.apache.activemq=INFO
log4j.logger.org.apache.activemq.spring=WARN

```

### Define the JNDI settings for JMS

Create a new file `jndi.properties` in the folder **src/main/resources** and add the following settings. 

``` 
# JNDI properties file to setup the JNDI server within ActiveMQ

#
# Default JNDI properties settings
#
java.naming.factory.initial = org.apache.activemq.jndi.ActiveMQInitialContextFactory
java.naming.provider.url = tcp://localhost:61616

#
# Set the connection factory name(s) as well as the destination names. The connection factory name(s)
# as well as the second part (after the dot) of the left hand side of the destination definition
# must be used in the JNDI lookups.
#
connectionFactoryNames = myJmsFactory
queue.queue/simple = test.queue.simple
``` 

This will tell the program where to find the ActiveMQ broker as well as which Queue to use. 

### Running the Producer

In order to run the Producer class from the command line, we need to create a Maven profile. Add the following lines to the `pom.xml`, just before the ending `</project>` tag.

```
    <profiles>
        <profile>
            <id>producer</id>
            <build>
                <defaultGoal>package</defaultGoal>
                <plugins>
                    <plugin>
                        <groupId>org.codehaus.mojo</groupId>
                        <artifactId>exec-maven-plugin</artifactId>
                        <executions>
                            <execution>
                                <phase>package</phase>
                                <goals>
                                    <goal>java</goal>
                                </goals>
                                <configuration>
                                    <mainClass>org.apache.activemq.simple.queue.SimpleProducer</mainClass>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
``` 

In a terminal window, navigate to the project folder and enter

``` 
mvn package
``` 

to compile the project. If it is successful, you can then run the producer using

``` 
mvn -P producer
``` 

Check with the ActiveMQ Web Console that you have a queue **test.queue.simple** and that it contains 100 messages. 


### Writing a Consumer

Now let's write the consumer. In the same Java Package `org.apache.activemq.simple.queue` create a new Java class `SimpleConsumer` and add the following code.

``` 
package org.apache.activemq.simple.queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

public class SimpleConsumer {
    private static final Log LOG = LogFactory.getLog(SimpleConsumer.class);

    private static final Boolean NON_TRANSACTED = false;
    private static final String CONNECTION_FACTORY_NAME = "myJmsFactory";
    private static final String DESTINATION_NAME = "queue/simple";
    private static final int MESSAGE_TIMEOUT_MILLISECONDS = 120000;

    public static void main(String args[]) {
        Connection connection = null;

        try {
            // JNDI lookup of JMS Connection Factory and JMS Destination
            Context context = new InitialContext();
            ConnectionFactory factory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);
            Destination destination = (Destination) context.lookup(DESTINATION_NAME);

            connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(destination);

            LOG.info("Start consuming messages from " + destination.toString() + " with " + MESSAGE_TIMEOUT_MILLISECONDS + "ms timeout");

            // Synchronous message consumer
            int i = 1;
            while (true) {
                Message message = consumer.receive(MESSAGE_TIMEOUT_MILLISECONDS);
                if (message != null) {
                    if (message instanceof TextMessage) {
                        String text = ((TextMessage) message).getText();
                        LOG.info("Got " + (i++) + ". message: " + text);
                    }
                } else {
                    break;
                }
            }

            consumer.close();
            session.close();
        } catch (Throwable t) {
            LOG.error(t);
        } finally {
            // Cleanup code
            // In general, you should always close producers, consumers,
            // sessions, and connections in reverse order of creation.
            // For this simple example, a JMS connection.close will
            // clean up all other resources.
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    LOG.error(e);
                }
            }
        }
    }
}
``` 

### Running the Consumer

In order to run the Consumer class from the command line, we need to create another Maven profile. Add the following lines to the `pom.xml`, just before the ending `</profiles>` tag.

```
        <profile>
            <id>consumer</id>
            <build>
                <defaultGoal>package</defaultGoal>
                <plugins>
                    <plugin>
                        <groupId>org.codehaus.mojo</groupId>
                        <artifactId>exec-maven-plugin</artifactId>
                        <executions>
                            <execution>
                                <phase>package</phase>
                                <goals>
                                    <goal>java</goal>
                                </goals>
                                <configuration>
                                    <mainClass>org.apache.activemq.simple.queue.SimpleConsumer</mainClass>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
``` 

In a terminal window, navigate to the project folder and enter

``` 
mvn package
``` 

To compile the project. If it is successful, you can then run the producer using

``` 
mvn -P consumer
``` 

## Working with Topics from JMS

Create a new [Maven project](../99-misc/97-working-with-eclipse/README.md) and in the last step use `com.trivadis.integration.ws` for the **Group Id** and `working-with-topic` for the **Artifact Id**.

### Creating the project definition (pom.xml)

Navigate to the **pom.xml** and double-click on it. The POM Editor will be displayed. 

![Alt Image Text](./images/eclipse-editing-pom-1.png "Schema Registry UI")

You can either use the GUI to edit your pom.xml or click on the last tab **pom.xml** to switch to the "code view". Let's do that. 

You will see the still rather empty definition.

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.trivadis.integrationws</groupId>
  <artifactId>working-with-queue</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</project>
```

First let's add the dependencies for the project. Copy the following block right after the <version> tag, before the closing </project> tag. 

```
   <properties>
        <activemq-version>5.7.0</activemq-version>
		 <slf4j-version>1.6.6</slf4j-version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-core</artifactId>
            <version>${activemq-version}</version>
        </dependency>
		<dependency>
		  	<groupId>org.slf4j</groupId>
		    <artifactId>slf4j-api</artifactId>
		    <version>${slf4j-version}</version>
		</dependency>
		<dependency>
		    <groupId>org.slf4j</groupId>
		    <artifactId>slf4j-log4j12</artifactId>
		    <version>${slf4j-version}</version>
		</dependency>
    </dependencies>
```

### Writing a Publisher

First create a new Java Package `org.apache.activemq.simple.topic` in the folder **src/main/java**.

Create a new Java Class `SimplePublisher` in the package `org.apache.activemq.simple.topic` just created. 

Add the following code to the empty class. 

``` 
package org.apache.activemq.simple.topic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

public class SimplePublisher {
    private static final Log LOG = LogFactory.getLog(SimplePublisher.class);

    private static final Boolean NON_TRANSACTED = false;
    private static final int MESSAGE_DELAY_MILLISECONDS = 100;
    private static final int NUM_MESSAGES_TO_BE_SENT = 100;
    private static final String CONNECTION_FACTORY_NAME = "myJmsFactory";
    private static final String DESTINATION_NAME = "topic/simple";

    public static void main(String args[]) {
        Connection connection = null;

        try {
            // JNDI lookup of JMS Connection Factory and JMS Destination
            Context context = new InitialContext();
            ConnectionFactory factory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);
            Destination destination = (Destination) context.lookup(DESTINATION_NAME);

            connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);

            for (int i = 1; i <= (NUM_MESSAGES_TO_BE_SENT / 10); i++) {
                for (int j = 1; j <= 10; j++) {
                    TextMessage message = session.createTextMessage(j + ". message sent");
                    LOG.info("Sending to destination: " + destination.toString() + " this text: '" + message.getText());
                    producer.send(message);
                    Thread.sleep(MESSAGE_DELAY_MILLISECONDS);
                }
                LOG.info("Send the Report Message");
                producer.send(session.createTextMessage("REPORT"));
            }
            LOG.info("Send the Shutdown Message");
            producer.send(session.createTextMessage("SHUTDOWN"));

            // Cleanup
            producer.close();
            session.close();
        } catch (Throwable t) {
            LOG.error(t);
        } finally {
            // Cleanup code
            // In general, you should always close producers, consumers,
            // sessions, and connections in reverse order of creation.
            // For this simple example, a JMS connection.close will
            // clean up all other resources.
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    LOG.error(e);
                }
            }
        }
    }
}
```

### Create a log4j configuration

In the code we are using the [Log4J Logging Framework](https://logging.apache.org/log4j/2.x/), which we have to configure using a property file. 

Create a new file `log4j.properties` in the folder **src/main/resources** and add the following settings. 

```
## ---------------------------------------------------------------------------
## Licensed to the Apache Software Foundation (ASF) under one or more
## contributor license agreements.  See the NOTICE file distributed with
## this work for additional information regarding copyright ownership.
## The ASF licenses this file to You under the Apache License, Version 2.0
## (the "License"); you may not use this file except in compliance with
## the License.  You may obtain a copy of the License at
## 
## http://www.apache.org/licenses/LICENSE-2.0
## 
## Unless required by applicable law or agreed to in writing, software
## distributed under the License is distributed on an "AS IS" BASIS,
## WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
## See the License for the specific language governing permissions and
## limitations under the License.
## ---------------------------------------------------------------------------

#
# The logging properties used by the standalone ActiveMQ broker
#
log4j.rootLogger=INFO, stdout

# CONSOLE appender
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{HH:mm:ss} %-5p %m%n

# Log File appender
log4j.appender.logfile=org.apache.log4j.FileAppender
log4j.appender.logfile.layout=org.apache.log4j.PatternLayout
log4j.appender.logfile.layout.ConversionPattern=%d [%-15.15t] %-5p %-30.30c{1} - %m%n
log4j.appender.logfile.file=./target/log/exercises.log
log4j.appender.logfile.append=true

#
#  You can change logger levels here.
#
log4j.logger.org.apache.activemq=INFO
log4j.logger.org.apache.activemq.spring=WARN

```

### Define the JNDI settings for JMS

Create a new file `jndi.properties` in the folder **src/main/resources** and add the following settings. 

``` 
# JNDI properties file to setup the JNDI server within ActiveMQ

#
# Default JNDI properties settings
#
java.naming.factory.initial = org.apache.activemq.jndi.ActiveMQInitialContextFactory
java.naming.provider.url = tcp://localhost:61616

#
# Set the connection factory name(s) as well as the destination names. The connection factory name(s)
# as well as the second part (after the dot) of the left hand side of the destination definition
# must be used in the JNDI lookups.
#
connectionFactoryNames = myJmsFactory
topic.topic/simple = test.topic.simple
topic.topic/control = test.topic.control
``` 

This will tell the program where to find the ActiveMQ broker as well as which Topic to use. 

### Running the Publisher

In order to run the Producer class from the command line, we need to create a Maven profile. Add the following lines to the `pom.xml`, just before the ending `</project>` tag.

```
    <profiles>
        <profile>
            <id>publisher</id>
            <build>
                <defaultGoal>package</defaultGoal>
                <plugins>
                    <plugin>
                        <groupId>org.codehaus.mojo</groupId>
                        <artifactId>exec-maven-plugin</artifactId>
                        <executions>
                            <execution>
                                <phase>package</phase>
                                <goals>
                                    <goal>java</goal>
                                </goals>
                                <configuration>
                                    <mainClass>org.apache.activemq.simple.topic.SimplePublisher
                                    </mainClass>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
``` 

In a terminal window, navigate to the project folder and enter

``` 
mvn package
``` 

to compile the project. If it is successful, you can then run the producer using

``` 
mvn -P publisher
``` 

Check with the ActiveMQ Web Console that you have a new topic **test.topic.simple** and that it contains 100 messages. 

### Writing a Subscriber

Now let's write the subscriber. In the same Java Package `org.apache.activemq.simple.topic` create a new Java class `SimpleSubscriber` and add the following code.

``` 
package org.apache.activemq.simple.topic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

public class SimpleSubscriber {
    private static final Log LOG = LogFactory.getLog(SimpleSubscriber.class);

    private static final Boolean NON_TRANSACTED = false;
    private static final String CONNECTION_FACTORY_NAME = "myJmsFactory";
    private static final String DESTINATION_NAME = "topic/simple";
    private static final String CONTROL_DESTINATION_NAME = "topic/control";
    private static final int MESSAGE_TIMEOUT_MILLISECONDS = 10000;

    public static void main(String args[]) {
        Connection connection = null;

        try {
            // JNDI lookup of JMS Connection Factory and JMS Destination
            Context context = new InitialContext();
            ConnectionFactory factory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY_NAME);
            Destination destination = (Destination) context.lookup(DESTINATION_NAME);
            Destination controlDestination = (Destination) context.lookup(CONTROL_DESTINATION_NAME);

            connection = factory.createConnection();

            Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            MessageProducer controlProducer = session.createProducer(controlDestination);

            // Setup main topic MessageListener
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(new JmsMessageListener(session, controlProducer));

            // Must have a separate Session or Connection for the synchronous MessageConsumer
            // per JMS spec you can not have synchronous and asynchronous message consumers
            // on same session
            Session controlSession = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer controlConsumer = controlSession.createConsumer(controlDestination);

            // Note: important to ensure that connection.start() if
            // MessageListeners have been registered
            connection.start();

            LOG.info("Start control message consumer");
            int i = 1;
            while (true) {
                Message message = controlConsumer.receive(MESSAGE_TIMEOUT_MILLISECONDS);
                if (message != null) {
                    if (message instanceof TextMessage) {
                        String text = ((TextMessage) message).getText();
                        LOG.info("Got " + (i++) + ". message: " + text);

                        // Break from this loop when we receive a SHUTDOWN message
                        if (text.startsWith("SHUTDOWN")) {
                            break;
                        }
                    }
                }
            }

            // Cleanup
            controlConsumer.close();
            controlSession.close();
            consumer.close();
            controlProducer.close();
            session.close();
        } catch (Throwable t) {
            LOG.error(t);
        } finally {
            // Cleanup code
            // In general, you should always close producers, consumers,
            // sessions, and connections in reverse order of creation.
            // For this simple example, a JMS connection.close will
            // clean up all other resources.
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    LOG.error(e);
                }
            }
        }
    }

    private static class JmsMessageListener implements MessageListener {
        private static final Log LOG = LogFactory.getLog(JmsMessageListener.class);

        private Session session;
        private MessageProducer producer;

        private int count = 0;
        private long start = System.currentTimeMillis();

        public JmsMessageListener(Session session, MessageProducer producer) {
            this.session = session;
            this.producer = producer;
        }

        public void onMessage(Message message) {
            try {
                if (message instanceof TextMessage) {
                    String text = ((TextMessage) message).getText();

                    if ("SHUTDOWN".equals(text)) {
                        LOG.info("Got the SHUTDOWN command -> exit");
                        producer.send(session.createTextMessage("SHUTDOWN is being performed"));
                    } else if ("REPORT".equals(text)) {
                        long time = System.currentTimeMillis() - start;
                        producer.send(session.createTextMessage("Received " + count + " in " + time + "ms"));
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            LOG.info("Wait for the report message to be sent our was interrupted");
                        }
                        count = 0;
                    } else {
                        if (count == 0) {
                            start = System.currentTimeMillis();
                        }
                        count++;
                        LOG.info("Received " + count + " messages (" + text + ").");
                    }
                }
            } catch (JMSException e) {
                LOG.error("Got an JMS Exception handling message: " + message, e);
            }
        }
    }
}

``` 

### Running the Subscriber
In order to run the Consumer class from the command line, we need to create another Maven profile. Add the following lines to the `pom.xml`, just before the ending `</profiles>` tag.

```
        <profile>
            <id>subscriber</id>
            <build>
                <defaultGoal>package</defaultGoal>
                <plugins>
                    <plugin>
                        <groupId>org.codehaus.mojo</groupId>
                        <artifactId>exec-maven-plugin</artifactId>
                        <executions>
                            <execution>
                                <phase>package</phase>
                                <goals>
                                    <goal>java</goal>
                                </goals>
                                <configuration>
                                    <mainClass>org.apache.activemq.simple.topic.SimpleSubscriber
                                    </mainClass>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
``` 

In a terminal window, navigate to the project folder and enter

``` 
mvn package
``` 

to compile the project. If it is successful, you can then run the producer using

``` 
mvn -P subscriber
``` 

If you start it, you won't see any messages. The messages from the previous run of the Publisher are already gone. 

Run the publisher again in a new terminal window. 

``` 
mvn -P publisher
``` 

