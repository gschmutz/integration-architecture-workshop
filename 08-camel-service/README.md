# Using Apache Camel Integration Framework

In this workshop we will learn how to use the [Apache Camel](https://camel.apache.org) Integration Framework. 

We will see how Apache Camel can help in integration different applications/systems in a very lightweight manner. 

Start the Eclipse IDE if not yet done. 

### Create the project and the project definition (pom.xml)

Create a new [Maven project](../99-misc/97-working-with-eclipse/README.md) and in the last step use `com.trivadis.integration.ws` for the **Group Id** and `working-with-camel` for the **Artifact Id**.

Navigate to the **pom.xml** and double-click on it. The POM Editor will be displayed. 

You can either use the GUI to edit your pom.xml or click on the last tab **pom.xml** to switch to the "code view". Let's do that. 

You will see the still rather empty definition.

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.trivadis.integrationws</groupId>
  <artifactId>working-with-camel</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</project>
```

Let's add some initial dependencies for our project. We will add some more depencencies to the POM throughout this workshop.. 

Copy the following block right after the <version> tag, before the closing </project> tag.

```
   <properties>
		<activemq-version>5.15.4</activemq-version>
		<camel-version>2.21.1</camel-version>
		<spring-version>4.1.7.RELEASE</spring-version>
		<slf4j-version>1.7.5</slf4j-version>

		<!-- use utf-8 encoding -->
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    </properties>

    <dependencies>
		<dependency>
			<groupId>org.apache.camel</groupId>
			<artifactId>camel-core</artifactId>
			<version>${camel-version}</version>
		</dependency>

		<dependency>
			<groupId>org.apache.camel</groupId>
			<artifactId>camel-spring</artifactId>
			<version>${camel-version}</version>
		</dependency>

		<dependency>
		    <groupId>org.slf4j</groupId>
		    <artifactId>slf4j-log4j12</artifactId>
		    <version>${slf4j-version}</version>
		</dependency>
    </dependencies>
    
	<build>
		<defaultGoal>install</defaultGoal>

		<pluginManagement>
			<plugins>
				<plugin>
					<groupId>org.apache.maven.plugins</groupId>
					<artifactId>maven-compiler-plugin</artifactId>
					<version>2.5</version>
					<configuration>
						<source>1.7</source>
						<target>1.7</target>
						<maxmem>256M</maxmem>
						<showDeprecation>true</showDeprecation>
					</configuration>
				</plugin>
				<plugin>
					<groupId>org.codehaus.mojo</groupId>
					<artifactId>exec-maven-plugin</artifactId>
					<version>1.1.1</version>
				</plugin>
				<plugin>
					<groupId>org.apache.camel</groupId>
					<artifactId>camel-maven-plugin</artifactId>
					<version>${camel-version}</version>
				</plugin>
			</plugins>
		</pluginManagement>
	</build>    
```

In a terminal window, perform the following command to update the Eclipse IDE project settings. 

```
mvn eclipse:eclipse
```

Refresh the project in Eclipse to re-read the project settings.

### Create log4j settings

Let's also create the necessary log4j configuration. 

In the code we are using the [Log4J Logging Framework](https://logging.apache.org/log4j/2.x/), which we have to configure using a property file. 

Create a new file `log4j.properties` in the folder **src/main/resources** and add the following configuration properties. 

```
## ------------------------------------------------------------------------
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
## ------------------------------------------------------------------------

#
# The logging properties used for eclipse testing, We want to see INFO output on the console.
#
log4j.rootLogger=INFO, out

#log4j.logger.org.apache.activemq=DEBUG

# uncomment the next line to debug Camel
log4j.logger.org.apache.camel=DEBUG

log4j.logger.org.apache.camel.impl.converter=INFO
log4j.logger.org.apache.camel.util.ResolverUtil=INFO

log4j.logger.org.springframework=WARN
log4j.logger.org.hibernate=WARN

# CONSOLE appender not used by default
log4j.appender.out=org.apache.log4j.ConsoleAppender
log4j.appender.out.layout=org.apache.log4j.PatternLayout
log4j.appender.out.layout.ConversionPattern=[%30.30t] %-30.30c{1} %-5p %m%n
#log4j.appender.out.layout.ConversionPattern=%d [%-15.15t] %-5p %-30.30c{1} - %m%n

log4j.throwableRenderer=org.apache.log4j.EnhancedThrowableRenderer
```

This finishes the setup steps and our new project is ready to be used. We will start with a simple Camel flow. 

## Create simple Camel flow using the File component

Let's start with a simple File-based data flow. We will be reading files from a local folder and store it again in another local folder. We will see how this can be done using the Spring DSL as well as the Java DSL of Apache Camel. 

We will first implement a class using the Java DSL

### Using the Java DSL

First create a new Java Package `com.trivadis.integrationws.camel` in the folder **src/main/java**.

Create a new Java Class `FileCopierJava` in the package `com.trivadis.integrationws.camel` just created. 

Add the following code to the empty class. 

``` 
package com.trivadis.integrationws.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

public class FileCopierJava {

    public static void main(String args[]) throws Exception {

    	 Main main = new Main();

        // add our route to the CamelContext
        main.addRouteBuilder(new RouteBuilder() {
            public void configure() {
                from("file:data/inbox?delay=5s")
                .to("file:data/outbox");
            }
        });

        // run the route and let it do its work
        main.run();
    }
}
``` 

We can see that the Java DSL is used inside a public static main method. We can just start the class as a Java application in order to test it. 

Now Right click on the class in the **Package Explorer** and select **Run As** | **Java Application**.

![Alt Image Text](./images/eclipse-javadsl-run-as-java-app.png "Edit Pom.xml")

The Java application will start the Camel route and the you should see in the console output window that the [File](http://camel.apache.org/file2.html) component is starting to poll from the `data/inbox` folder. 

```
[el Thread #1 - LRUCacheFactory] LRUCacheFactory                DEBUG Warming up LRUCache ...
[el Thread #1 - LRUCacheFactory] LRUCacheFactory                DEBUG Warming up LRUCache complete in 603 millis
[                          main] DefaultCamelContext            DEBUG Adding routes from builder: Routes: []
[                          main] DefaultCamelContext            INFO  Apache Camel 2.21.1 (CamelContext: camel-1) is starting
[                          main] DefaultCamelContext            DEBUG Using ClassResolver=org.apache.camel.impl.DefaultClassResolver@66a3ffec, PackageScanClassResolver=org.apache.camel.impl.DefaultPackageScanClassResolver@77caeb3e, ApplicationContextClassLoader=null, RouteController=org.apache.camel.impl.DefaultRouteController@1e88b3c
[                          main] ManagedManagementStrategy      INFO  JMX is enabled
[                          main] DefaultManagementAgent         DEBUG Starting JMX agent on server: com.sun.jmx.mbeanserver.JmxMBeanServer@39c0f4a
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=context,name="camel-1"
[                          main] TimerListenerManager           DEBUG Added TimerListener: org.apache.camel.management.mbean.ManagedCamelContext@710726a3
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=health,name="camel-1"
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=routecontrollers,name="camel-1"
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultTypeConverter
[                          main] DefaultTypeConverter           INFO  Type converters loaded (core: 194, classpath: 1)
[                          main] ResolverHelper                 DEBUG Lookup Language with name simple in registry. Found: null
[                          main] ResolverHelper                 DEBUG Lookup Language with name simple-language in registry. Found: null
[                          main] SimpleLanguage                 DEBUG Simple language predicate/expression cache size: 1000
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultEndpointRegistry
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultExecutorServiceManager
[                          main] SharedProducerServicePool      DEBUG Starting service pool: org.apache.camel.impl.SharedProducerServicePool@1df82230
[                          main] aredPollingConsumerServicePool DEBUG Starting service pool: org.apache.camel.impl.SharedPollingConsumerServicePool@22635ba0
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultInflightRepository
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultAsyncProcessorAwaitManager
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultShutdownStrategy
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultRestRegistry
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultRuntimeCamelCatalog
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultTransformerRegistry
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultValidatorRegistry
[                          main] DefaultCamelContext            DEBUG Using ComponentResolver: org.apache.camel.impl.DefaultComponentResolver@60d8c9b7 to resolve component with name: file
[                          main] ResolverHelper                 DEBUG Lookup Component with name file in registry. Found: null
[                          main] ResolverHelper                 DEBUG Lookup Component with name file-component in registry. Found: null
[                          main] DefaultComponentResolver       DEBUG Found component: file via type: org.apache.camel.component.file.FileComponent via: META-INF/services/org/apache/camel/component/file
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=components,name="file"
[                          main] DefaultComponent               DEBUG Cannot resolve property placeholders on component: org.apache.camel.component.file.FileComponent@35047d03 as PropertiesComponent is not in use
[                          main] DefaultComponent               DEBUG Creating endpoint uri=[file://data/inbox?delay=5s&delete=true], path=[data/inbox]
[                          main] DefaultCamelContext            DEBUG file://data/inbox?delay=5s&delete=true converted to endpoint: file://data/inbox?delay=5s&delete=true by component: org.apache.camel.component.file.FileComponent@35047d03
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=endpoints,name="file://data/inbox\?delay=5s&delete=true"
[                          main] DefaultComponent               DEBUG Creating endpoint uri=[file://data/outbox], path=[data/outbox]
[                          main] DefaultCamelContext            DEBUG file://data/outbox converted to endpoint: file://data/outbox by component: org.apache.camel.component.file.FileComponent@35047d03
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=endpoints,name="file://data/outbox"
[                          main] DefaultChannel                 DEBUG Initialize channel for target: 'To[file:data/outbox]'
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=tracer,name=BacklogTracer
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=tracer,name=BacklogDebugger
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=errorhandlers,name="DefaultErrorHandlerBuilder(ref:CamelDefaultErrorHandlerBuilder)"
[                          main] DefaultCamelContext            INFO  StreamCaching is not in use. If using streams then its recommended to enable stream caching. See more details at http://camel.apache.org/stream-caching.html
[                          main] HeadersMapFactoryResolver      DEBUG Creating default HeadersMapFactory
[                          main] DefaultCamelContext            DEBUG Using HeadersMapFactory: org.apache.camel.impl.DefaultHeadersMapFactory@35e2d654
[                          main] DefaultCamelContext            DEBUG Warming up route id: route1 having autoStartup=true
[                          main] RouteService                   DEBUG Starting services on route: route1
[                          main] RouteService                   DEBUG Starting child service on route: route1 -> Channel[sendTo(file://data/outbox)]
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=producers,name=GenericFileProducer(0x1aafa419)
[                          main] ProducerCache                  DEBUG Adding to producer cache with key: file://data/outbox for producer: Producer[file://data/outbox]
[                          main] RouteService                   DEBUG Starting child service on route: route1 -> sendTo(file://data/outbox)
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=processors,name="to1"
[                          main] RouteService                   DEBUG Starting child service on route: route1 -> Channel[sendTo(file://data/outbox)]
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=routes,name="route1"
[                          main] TimerListenerManager           DEBUG Added TimerListener: org.apache.camel.management.mbean.ManagedSuspendableRoute@459e9125
[                          main] ultManagementLifecycleStrategy DEBUG Load performance statistics disabled
[                          main] GenericFileProducer            DEBUG Starting producer: Producer[file://data/outbox]
[                          main] DefaultCamelContext            DEBUG Route: route1 >>> EventDrivenConsumerRoute[file://data/inbox?delay=5s&delete=true -> Channel[sendTo(file://data/outbox)]]
[                          main] DefaultCamelContext            DEBUG Starting consumer (order: 1000) on route: route1
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=consumers,name=FileConsumer(0x2a4fb17b)
[                          main] FileConsumer                   DEBUG Starting consumer: Consumer[file://data/inbox?delay=5s&delete=true]
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=threadpools,name="FileConsumer(0x2a4fb17b)"
[                          main] DefaultExecutorServiceManager  DEBUG Created new ScheduledThreadPool for source: Consumer[file://data/inbox?delay=5s&delete=true] with name: file://data/inbox?delay=5s&delete=true -> org.apache.camel.util.concurrent.SizedScheduledExecutorService@79d8407f[file://data/inbox?delay=5s&delete=true]
[                          main] ScheduledPollConsumerScheduler DEBUG Scheduling poll (fixed delay) with initialDelay: 1000, delay: 5000 (milliseconds) for: file://data/inbox?delay=5s&delete=true
[                          main] DefaultCamelContext            INFO  Route: route1 started and consuming from: file://data/inbox?delay=5s&delete=true
[                          main] DefaultCamelContext            INFO  Total 1 routes, of which 1 are started
[                          main] DefaultCamelContext            INFO  Apache Camel 2.21.1 (CamelContext: camel-1) started in 8.790 seconds
[ thread #2 - file://data/inbox] FileEndpoint                   DEBUG Parameters for Generic file process strategy {readLockDeleteOrphanLockFiles=true, readLockRemoveOnRollback=true, readLockCheckInterval=1000, readLock=none, readLockRemoveOnCommit=false, readLockTimeout=10000, readLockMarkerFile=true, delete=true, readLockLoggingLevel=DEBUG, readLockMinAge=0, readLockMinLength=1}
[ thread #2 - file://data/inbox] FileEndpoint                   DEBUG Using Generic file process strategy: org.apache.camel.component.file.strategy.GenericFileDeleteProcessStrategy@25f6ca72
[ thread #2 - file://data/inbox] FileConsumer                   DEBUG Took 0.016 seconds to poll: data/inbox
[ thread #2 - file://data/inbox] FileConsumer                   DEBUG Took 0.001 seconds to poll: data/inbox
[ thread #2 - file://data/inbox] FileConsumer                   DEBUG Took 0.000 seconds to poll: data/inbox
[ thread #2 - file://data/inbox] FileConsumer                   DEBUG Took 0.001 seconds to poll: data/inbox
```

Copy a file to the `data/inbox` folder and you should see the following output in the console window. 

```
[ thread #2 - file://data/inbox] FileConsumer                   DEBUG Total 1 files to consume
[ thread #2 - file://data/inbox] FileConsumer                   DEBUG About to process file: GenericFile[Order.xml] using exchange: Exchange[]
[ thread #2 - file://data/inbox] SendProcessor                  DEBUG >>>> file://data/outbox Exchange[ID-cas-1530461241581-0-1]
[ thread #2 - file://data/inbox] GenericFileProducer            DEBUG Wrote [data/outbox/Order.xml] to [file://data/outbox]
[ thread #2 - file://data/inbox] GenericFileOnCompletion        DEBUG Done processing file: GenericFile[Order.xml] using exchange: Exchange[ID-cas-1530461241581-0-1]
[ thread #2 - file://data/inbox] FileUtil                       DEBUG Retrying attempt 0 to delete file: /home/cas/eclipse-workspace/working-with-camel/data/inbox/Order.xml
[ thread #2 - file://data/inbox] FileUtil                       DEBUG Tried 1 to delete file: /home/cas/eclipse-workspace/working-with-camel/data/inbox/Order.xml with result: true
[ thread #2 - file://data/inbox] FileConsumer                   DEBUG Took 0.001 seconds to poll: data/inbox
```

Check that the file has been copied to the `data/outbox` folder. After the file is consumed, it is deleted from the `inbox` folder. 

Stop the running Java application.

Now let's create the same flow but using the Spring DSL. 

## Using the Spring DSL

Create a new an xml file `camel-context.xml` in the source folder `src/main/resources`. To do that, in the **Package Explorer** right-click on the folder `src/main/resources` and select **New** | **File**. Enter `camel-context.xml` into the **File name** field.

Add the following XML definition to the empty file.  

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:broker="http://activemq.apache.org/schema/core"
	xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://activemq.apache.org/schema/core http://activemq.apache.org/schema/core/activemq-core.xsd
       http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">
	<camelContext
		xmlns="http://camel.apache.org/schema/spring">
		<route>
			<from uri="file:data/inbox?delay=5s" />
			<to uri="file:data/outbox" />

		</route>
	</camelContext>
</beans>
```

Create a new Java Class `FileCopierSpring` in the package `com.trivadis.integrationws.camel` just next to the class created above. 

Add the following code to the empty class. 

```
package com.trivadis.integrationws.camel;

import org.apache.camel.spring.Main;

public class FileCopierSpring {
	public static void main(String... args) throws Exception {
		Main camel = new Main();
		camel.enableHangupSupport();
		camel.setApplicationContextUri("classpath:camel-context.xml");
		camel.run(args);
	}

}
```

Now Right click on the class in the **Package Explorer** and select **Run As** | **Java Application**.

![Alt Image Text](./images/eclipse-javadsl-run-as-java-app.png "Edit Pom.xml")

The Java application will start the Camel route and the you should see in the console output window that the [File](http://camel.apache.org/file2.html) component is starting to poll from same the `data/inbox` folder as before.

Again copy a file to the `data/inbox` folder and make sure that it is moved to `data/outbox`.

Stop the application. 

## Create the Order Management Flow

After taking our first steps with Apache Camel, let's implement a more practical use case. The following diagram shows the the dataflow we are now going to implement in a step-wise manner. It's an order management flow, where orders are processed in the backend by a legacy system, which we are integrating with other systems using both Files as well as a HTTP service. 

![Alt Image Text](./images/order-management-use-case.png "Edit Pom.xml")

As you can see, there are two different file formats, CSV and XML. The XML is the same one we will write to the local file system at the end of the data flow. The HTTP service will also accept the exact same XML format. So in the case of the CSV forat, we have to transform it to XML in the data flow. 

Before we can start implementing the Camel flow, we need to add an FTP server to the services of the platform. 

Add the following definition to the `docker-compose.yml` file. 

```
  ftp:
    image: stilliard/pure-ftpd
    hostname: ftp
    environment:
      - FTP_USER_NAME=order
      - FTP_USER_PASS=order
      - FTP_USER_HOME=/home/orderentry
    ports:
      - "21:21"
      - "30000-30009:30000-30009"
    restart: always
```

Perform a `docker-compose up -d` to add the FTP service to the running platform. 

Now with the platform updated, let's start implementing the data flow. We will be using a combination of Spring context for registring some beans and using the Java DSL to define the Camel pipeline. 

### Read XML data from FTP Server and send to ActiveMQ
We will start with the XML data sources and implement the FTP Sever source on the left hand side, polling it for new files and sending it to the Active MQ queue. 

For that we will create a new Java application. It will be similar to the one we have seen in Spring DSL example above. 

But first let's add some additional dependencies to the Maven project definition. Double-click on the `pom.xml` file and navigate to the **pom.xml** tab. 

Add the following dependencies just after the `camel-spring` dependency.

```
		<dependency>
			<groupId>org.apache.camel</groupId>
			<artifactId>camel-ftp</artifactId>
			<version>${camel-version}</version>
		</dependency>

		<dependency>
			<groupId>org.apache.camel</groupId>
			<artifactId>camel-jetty</artifactId>
			<version>${camel-version}</version>
		</dependency>

		<dependency>
			<groupId>org.apache.camel</groupId>
			<artifactId>camel-bindy</artifactId>
			<version>${camel-version}</version>
		</dependency>

		<dependency>
			<groupId>org.apache.camel</groupId>
			<artifactId>camel-jaxb</artifactId>
			<version>${camel-version}</version>
		</dependency>

		<dependency>
			<groupId>org.apache.camel</groupId>
			<artifactId>camel-jms</artifactId>
			<version>${camel-version}</version>
		</dependency>

		<dependency>
			<groupId>org.apache.activemq</groupId>
			<artifactId>activemq-camel</artifactId>
			<version>${activemq-version}</version>
		</dependency>
```

Execute a `mvn eclipse:eclipse` to refresh the Eclipse project settings and to a refresh in Eclipse IDE.

Create a new Java Class `OrderManagementApplication ` in the package `com.trivadis.integrationws.camel` just next to the classes created before and add the following code.  

```
package com.trivadis.integrationws.camel;

import org.apache.camel.spring.Main;

public class OrderManagementApplication {

    public static void main(String args[]) throws Exception {
        // create CamelContext
    	Main main = new Main();
		main.setApplicationContextUri("classpath:camel-order-management-context.xml");

		main.run(args);
    	
    }
}
```

As we can see, it references a `camel-order-management-context.xml` file. Create this file in the source folder `src/main/resources` and add the following XML definition:

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:broker="http://activemq.apache.org/schema/core"
	xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://activemq.apache.org/schema/core http://activemq.apache.org/schema/core/activemq-core.xsd
       http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">


	<camelContext xmlns="http://camel.apache.org/schema/spring">
  		<routeBuilder ref="orderRouteBuilder" />    
	</camelContext>

	<bean id="orderManagmentRouteBuilder" class="com.trivadis.integrationws.camel.OrderManagementRoute"/>

	<bean id="activemq"
		class="org.apache.activemq.camel.component.ActiveMQComponent">
		<property name="brokerURL" value="tcp://192.168.25.136:61616" />
	</bean>

</beans>
```

It defines the activemq bean, which we will use to produce and conume to/from ActiveMQ. Make sure that you adapt the IP address to your environment (should be the IP address of the Docker Host). 

Additionally we also register a Route Builder as a bean, and reference it in the `camelConext`. This route builder defines the Camel route using the Java DSL, instead of defining it using the Spring DSL. 

So let's also create the `OrderManagementRoute` Java class. It is a class which extends the Camel `RouteBuilder` class. 

```
package com.trivadis.integrationws.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;

public class OrderManagementRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		/*
		 * Consume XML file from FTP server from the "xml" folder and send it to the "orders-xml" queue
		 */
        from("ftp://localhost:21/xml?autoCreate=true&username=order&password=order&passiveMode=true&binary=false" + 
        		"&localWorkDirectory=target/ftp-work&delay=5s&delete=true")
        	.to("activemq:orders-xml");

	}

}
```

You can se that we are using some new components, the [ftp](http://camel.apache.org/ftp2.html) component for polling the FTP server, the [activemq](http://camel.apache.org/activemq.html) for producing and later alos consuming to/from ActiveMQ 

Run the Java application to see our initial data flow implementation in use. You should see the following output on the console window. 

```
[                          main] CamelNamespaceHandler          DEBUG Using org.apache.camel.spring.CamelContextFactoryBean as CamelContextBeanDefinitionParser
[                          main] CamelNamespaceHandler          DEBUG Registered default: org.apache.camel.spring.CamelProducerTemplateFactoryBean with id: template on camel context: camel-1
[                          main] CamelNamespaceHandler          DEBUG Registered default: org.apache.camel.spring.CamelFluentProducerTemplateFactoryBean with id: fluentTemplate on camel context: camel-1
[                          main] CamelNamespaceHandler          DEBUG Registered default: org.apache.camel.spring.CamelConsumerTemplateFactoryBean with id: consumerTemplate on camel context: camel-1
[el Thread #1 - LRUCacheFactory] LRUCacheFactory                DEBUG Warming up LRUCache ...
[                          main] SpringCamelContext             DEBUG Set the application context classloader to: sun.misc.Launcher$AppClassLoader@14ae5a5
[el Thread #1 - LRUCacheFactory] LRUCacheFactory                DEBUG Warming up LRUCache complete in 364 millis
[                          main] CamelContextFactoryBean        DEBUG afterPropertiesSet() took 707 millis
[                          main] bstractCamelContextFactoryBean DEBUG Setting up routes
[                          main] bstractCamelContextFactoryBean DEBUG Found JAXB created routes: []
[                          main] SpringCamelContext             DEBUG Adding routes from builder: Routes: []
[                          main] SpringCamelContext             INFO  Apache Camel 2.21.1 (CamelContext: camel-1) is starting
[                          main] SpringCamelContext             DEBUG Using ClassResolver=org.apache.camel.impl.DefaultClassResolver@301ec38b, PackageScanClassResolver=org.apache.camel.impl.DefaultPackageScanClassResolver@17a1e4ca, ApplicationContextClassLoader=sun.misc.Launcher$AppClassLoader@14ae5a5, RouteController=org.apache.camel.impl.DefaultRouteController@10ded6a9
[                          main] ManagedManagementStrategy      INFO  JMX is enabled
[                          main] DefaultManagementAgent         DEBUG Starting JMX agent on server: com.sun.jmx.mbeanserver.JmxMBeanServer@373ebf74
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=context,name="camel-1"
[                          main] TimerListenerManager           DEBUG Added TimerListener: org.apache.camel.management.mbean.ManagedCamelContext@58fe0499
[                          main] ultManagementLifecycleStrategy DEBUG Registering 1 pre registered services
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=components,name="spring-event"
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=health,name="camel-1"
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=routecontrollers,name="camel-1"
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultTypeConverter
[                          main] StaxConverter                  DEBUG StaxConverter pool size: 2
[                          main] DefaultTypeConverter           INFO  Type converters loaded (core: 194, classpath: 11)
[                          main] ResolverHelper                 DEBUG Lookup Language with name simple in registry. Found: null
[                          main] ResolverHelper                 DEBUG Lookup Language with name simple-language in registry. Found: null
[                          main] SimpleLanguage                 DEBUG Simple language predicate/expression cache size: 1000
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultEndpointRegistry
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultExecutorServiceManager
[                          main] SharedProducerServicePool      DEBUG Starting service pool: org.apache.camel.impl.SharedProducerServicePool@132ddbab
[                          main] aredPollingConsumerServicePool DEBUG Starting service pool: org.apache.camel.impl.SharedPollingConsumerServicePool@297ea53a
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultInflightRepository
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultAsyncProcessorAwaitManager
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultShutdownStrategy
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultRestRegistry
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultRuntimeCamelCatalog
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultTransformerRegistry
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=services,name=DefaultValidatorRegistry
[                          main] DefaultComponent               DEBUG Cannot resolve property placeholders on component: org.apache.camel.component.event.EventComponent@53e211ee as PropertiesComponent is not in use
[                          main] SpringCamelContext             DEBUG Using ComponentResolver: org.apache.camel.impl.DefaultComponentResolver@35beb15e to resolve component with name: ftp
[                          main] ResolverHelper                 DEBUG Lookup Component with name ftp in registry. Found: null
[                          main] ResolverHelper                 DEBUG Lookup Component with name ftp-component in registry. Found: null
[                          main] DefaultComponentResolver       DEBUG Found component: ftp via type: org.apache.camel.component.file.remote.FtpComponent via: META-INF/services/org/apache/camel/component/ftp
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=components,name="ftp"
[                          main] DefaultComponent               DEBUG Cannot resolve property placeholders on component: org.apache.camel.component.file.remote.FtpComponent@4fcee388 as PropertiesComponent is not in use
[                          main] DefaultComponent               DEBUG Creating endpoint uri=[ftp://localhost:21/xml?autoCreate=true&binary=false&delay=5s&delete=true&localWorkDirectory=target%2Fftp-work&passiveMode=true&password=xxxxxx&username=order], path=[localhost:21/xml]
[                          main] SpringCamelContext             DEBUG ftp://localhost:21/xml?autoCreate=true&binary=false&delay=5s&delete=true&localWorkDirectory=target%2Fftp-work&passiveMode=true&password=xxxxxx&username=order converted to endpoint: ftp://localhost:21/xml?autoCreate=true&binary=false&delay=5s&delete=true&localWorkDirectory=target%2Fftp-work&passiveMode=true&password=xxxxxx&username=order by component: org.apache.camel.component.file.remote.FtpComponent@4fcee388
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=endpoints,name="ftp://localhost:21/xml\?autoCreate=true&binary=false&delay=5s&delete=true&localWorkDirectory=target%2Fftp-work&passiveMode=true&password=xxxxxx&username=order"
[                          main] SpringCamelContext             DEBUG Using ComponentResolver: org.apache.camel.impl.DefaultComponentResolver@35beb15e to resolve component with name: activemq
[                          main] ResolverHelper                 DEBUG Lookup Component with name activemq in registry. Found: org.apache.activemq.camel.component.ActiveMQComponent@62923ee6
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=components,name="activemq"
[                          main] DefaultComponent               DEBUG Creating endpoint uri=[activemq://orders-xml], path=[orders-xml]
[                          main] SpringCamelContext             DEBUG activemq://orders-xml converted to endpoint: activemq://orders-xml by component: org.apache.activemq.camel.component.ActiveMQComponent@62923ee6
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=endpoints,name="activemq://orders-xml"
[                          main] DefaultChannel                 DEBUG Initialize channel for target: 'To[activemq:orders-xml]'
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=tracer,name=BacklogTracer
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=tracer,name=BacklogDebugger
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=errorhandlers,name="DefaultErrorHandlerBuilder(ref:CamelDefaultErrorHandlerBuilder)"
[                          main] SpringCamelContext             INFO  StreamCaching is not in use. If using streams then its recommended to enable stream caching. See more details at http://camel.apache.org/stream-caching.html
[                          main] HeadersMapFactoryResolver      DEBUG Creating default HeadersMapFactory
[                          main] SpringCamelContext             DEBUG Using HeadersMapFactory: org.apache.camel.impl.DefaultHeadersMapFactory@21d5c1a0
[                          main] SpringCamelContext             DEBUG Warming up route id: route1 having autoStartup=true
[                          main] RouteService                   DEBUG Starting services on route: route1
[                          main] FtpEndpoint                    DEBUG Created FTPClient [connectTimeout: 10000, soTimeout: 300000, dataTimeout: 30000, bufferSize: 32768, receiveDataSocketBufferSize: 0, sendDataSocketBufferSize: 0]: org.apache.commons.net.ftp.FTPClient@61a002b1
[                          main] RouteService                   DEBUG Starting child service on route: route1 -> Channel[sendTo(activemq://orders-xml)]
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=producers,name=JmsProducer(0x407cf41)
[                          main] ProducerCache                  DEBUG Adding to producer cache with key: activemq://orders-xml for producer: Producer[activemq://orders-xml]
[                          main] RouteService                   DEBUG Starting child service on route: route1 -> sendTo(activemq://orders-xml)
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=processors,name="to1"
[                          main] RouteService                   DEBUG Starting child service on route: route1 -> Channel[sendTo(activemq://orders-xml)]
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=routes,name="route1"
[                          main] TimerListenerManager           DEBUG Added TimerListener: org.apache.camel.management.mbean.ManagedSuspendableRoute@24a1c17f
[                          main] ultManagementLifecycleStrategy DEBUG Load performance statistics disabled
[                          main] JmsProducer                    DEBUG Starting producer: Producer[activemq://orders-xml]
[                          main] SpringCamelContext             DEBUG Route: route1 >>> EventDrivenConsumerRoute[ftp://localhost:21/xml?autoCreate=true&binary=false&delay=5s&delete=true&localWorkDirectory=target%2Fftp-work&passiveMode=true&password=xxxxxx&username=order -> Channel[sendTo(activemq://orders-xml)]]
[                          main] SpringCamelContext             DEBUG Starting consumer (order: 1000) on route: route1
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=consumers,name=FtpConsumer(0x58c540cf)
[                          main] FtpConsumer                    DEBUG Starting consumer: FtpConsumer[ftp://localhost:21/xml?autoCreate=true&binary=false&delay=5s&delete=true&localWorkDirectory=target%2Fftp-work&passiveMode=true&password=xxxxxx&username=order]
[                          main] DefaultManagementAgent         DEBUG Registered MBean with ObjectName: org.apache.camel:context=camel-1,type=threadpools,name="FtpConsumer(0x58c540cf)"
[                          main] DefaultExecutorServiceManager  DEBUG Created new ScheduledThreadPool for source: FtpConsumer[ftp://localhost:21/xml?autoCreate=true&binary=false&delay=5s&delete=true&localWorkDirectory=target%2Fftp-work&passiveMode=true&password=xxxxxx&username=order] with name: ftp://localhost:21/xml?autoCreate=true&binary=false&delay=5s&delete=true&localWorkDirectory=target%2Fftp-work&passiveMode=true&password=xxxxxx&username=order -> org.apache.camel.util.concurrent.SizedScheduledExecutorService@6c4f9535[ftp://localhost:21/xml?autoCreate=true&binary=false&delay=5s&delete=true&localWorkDirectory=target%2Fftp-work&passiveMode=true&password=xxxxxx&username=order]
[                          main] FtpConsumer                    DEBUG Auto creating directory: xml
[                          main] FtpConsumer                    DEBUG Exception checking connection status: File operation failed: null Connection is not open. Code: 0
[                          main] FtpConsumer                    DEBUG Not connected/logged in, connecting to: ftp://order@localhost:21
[                          main] FtpConsumer                    DEBUG Connected and logged in to: ftp://order@localhost:21
[                          main] ScheduledPollConsumerScheduler DEBUG Scheduling poll (fixed delay) with initialDelay: 1000, delay: 5000 (milliseconds) for: ftp://localhost:21/xml?autoCreate=true&binary=false&delay=5s&delete=true&localWorkDirectory=target%2Fftp-work&passiveMode=true&password=xxxxxx&username=order
[                          main] SpringCamelContext             INFO  Route: route1 started and consuming from: ftp://localhost:21/xml?autoCreate=true&binary=false&delay=5s&delete=true&localWorkDirectory=target%2Fftp-work&passiveMode=true&password=xxxxxx&username=order
[                          main] SpringCamelContext             INFO  Total 1 routes, of which 1 are started
[                          main] SpringCamelContext             INFO  Apache Camel 2.21.1 (CamelContext: camel-1) started in 2.481 seconds
[                          main] SpringCamelContext             DEBUG start() took 2570 millis
[                          main] SpringCamelContext             DEBUG onApplicationEvent: org.springframework.context.event.ContextRefreshedEvent[source=org.springframework.context.support.ClassPathXmlApplicationContext@6c629d6e: startup date [Sun Jul 01 21:31:48 CEST 2018]; root of context hierarchy]
[                          main] MainSupport                    DEBUG Starting Spring ApplicationContext: org.springframework.context.support.ClassPathXmlApplicationContext@6c629d6e
[                          main] SpringCamelContext             DEBUG onApplicationEvent: org.springframework.context.event.ContextStartedEvent[source=org.springframework.context.support.ClassPathXmlApplicationContext@6c629d6e: startup date [Sun Jul 01 21:31:48 CEST 2018]; root of context hierarchy]
[ad #2 - ftp://localhost:21/xml] FtpEndpoint                    DEBUG Parameters for Generic file process strategy {readLockDeleteOrphanLockFiles=true, readLockRemoveOnRollback=true, readLockCheckInterval=5000, readLock=none, readLockRemoveOnCommit=false, readLockTimeout=20000, readLockMarkerFile=true, fastExistsCheck=false, delete=true, readLockLoggingLevel=DEBUG, readLockMinAge=0, readLockMinLength=1}
[ad #2 - ftp://localhost:21/xml] FtpEndpoint                    DEBUG Using Generic file process strategy: org.apache.camel.component.file.strategy.GenericFileDeleteProcessStrategy@7bd3a7a9
[ad #2 - ftp://localhost:21/xml] FtpConsumer                    DEBUG Took 0.159 seconds to poll: xml
[ad #2 - ftp://localhost:21/xml] FtpConsumer                    DEBUG Took 0.010 seconds to poll: xml
[ad #2 - ftp://localhost:21/xml] FtpConsumer                    DEBUG Took 0.010 seconds to poll: xml
```

We can see that the `FtpConsumer` is polling the `xml` folder for some Order XML documents. 

Create a new file `order-1.xml` and copy an order similar to the one shown below:

```
<?xml version="1.0" encoding="UTF-8"?>
<order name="IPad" amount="100" customer="Peter Muster"/>
```

Upload it to the xml folder on the FTP server using either the ftp command line utility or the FileZilla GUI application (which you can download from here: <https://filezilla-project.org/download.php?type=client>). 

The FTP Server can be reached on Host **localhost** and Port **21**. Use **order** for the **Username** and **order** also for the **Password**.

To connect to the FTP server using the `ftp` CLI, perform the following command

```
ftp -p localhost 21
```

You will be asked to enter the **Name** (Username) and the **Password**. You can see the output of the CLI below. 

```
Connected to localhost.
220---------- Welcome to Pure-FTPd [privsep] [TLS] ----------
220-You are user number 3 of 5 allowed.
220-Local time is now 19:45. Server port: 21.
220-This is a private system - No anonymous login
220-IPv6 connections are also welcome on this server.
220 You will be disconnected after 15 minutes of inactivity.
Name (localhost:cas): order
331 User order OK. Password required
Password:
230 OK. Current directory is /
Remote system type is UNIX.
Using binary mode to transfer files.
ftp> ascii
200 TYPE is now ASCII
ftp> 
```

You can now upload a file using the `put` command. 

```
> put order-1.xml xml/order-1.xml
local: order-1.xml remote: xml/order-1.xml
227 Entering Passive Mode (127,0,0,1,117,56)
150 Accepted data connection
226-File successfully transferred
226 0.002 seconds (measured here), 47.82 Kbytes per second
97 bytes sent in 0.00 secs (2.5002 MB/s)
ftp> 
```

The following screenshot shows how the FileZilla client application look like. You can connect in the line on the top and then use drag-and-drop to move a file from the local filesystem on the left to the FTP server on the right. 

![Alt Image Text](./images/filezilla-homepage.png "Edit Pom.xml")

The file will be picked up by the FtpConsumer within 5 seconds (the polling interval set in the Camel route) and you should see that in the console output of the running application. 

```
[ad #2 - ftp://localhost:21/xml] FtpConsumer                    DEBUG Took 0.013 seconds to poll: xml
[ad #2 - ftp://localhost:21/xml] FtpConsumer                    DEBUG Total 1 files to consume
[ad #2 - ftp://localhost:21/xml] FtpClientActivityListener      DEBUG Downloading from host: ftp://order@localhost:21 file: xml/order-1.xml starting  (size: 97 B)
[ad #2 - ftp://localhost:21/xml] FileUtil                       DEBUG Retrying attempt 0 to delete file: target/ftp-work/order-1.xml
[ad #2 - ftp://localhost:21/xml] FileUtil                       DEBUG Tried 1 to delete file: target/ftp-work/order-1.xml with result: true
[ad #2 - ftp://localhost:21/xml] FtpOperations                  DEBUG Retrieve file to local work file result: true
[ad #2 - ftp://localhost:21/xml] FileUtil                       DEBUG Tried 1 to rename file: target/ftp-work/order-1.xml.inprogress to: target/ftp-work/order-1.xml with result: true
[ad #2 - ftp://localhost:21/xml] FtpClientActivityListener      DEBUG Downloading from host: ftp://order@localhost:21 file: xml/order-1.xml completed (size: 97 B) (took: 0.025 seconds)
[ad #2 - ftp://localhost:21/xml] FtpConsumer                    DEBUG About to process file: RemoteFile[order-1.xml] using exchange: Exchange[]
[ad #2 - ftp://localhost:21/xml] SendProcessor                  DEBUG >>>> activemq://orders-xml Exchange[ID-cas-1530473515033-0-1]
[ad #2 - ftp://localhost:21/xml] Configuration$CamelJmsTemplate DEBUG Executing callback on JMS Session: PooledSession { ActiveMQSession {id=ID:cas-46097-1530474783480-1:1:1,started=false} java.lang.Object@358797ef }
[ad #2 - ftp://localhost:21/xml] JmsConfiguration               DEBUG Sending JMS message to: queue://orders-xml with message: ActiveMQBytesMessage {commandId = 0, responseRequired = false, messageId = null, originalDestination = null, originalTransactionId = null, producerId = null, destination = null, transactionId = null, expiration = 0, timestamp = 0, arrival = 0, brokerInTime = 0, brokerOutTime = 0, correlationId = null, replyTo = null, persistent = true, type = null, priority = 0, groupID = null, groupSequence = 0, targetConsumerId = null, compressed = false, userID = null, content = null, marshalledProperties = null, dataStructure = null, redeliveryCounter = 0, size = 0, properties = {CamelFileLastModified=1530467520000, CamelFileParent=xml, CamelFilePath=xml/order-1.xml, CamelFileLength=97, CamelFileAbsolute=false, CamelFtpReplyString=226-File successfully transferred
226 0.005 seconds (measured here), 19.21 Kbytes per second
, CamelFileName=order-1.xml, CamelFileNameConsumed=order-1.xml, breadcrumbId=ID-cas-1530473515033-0-1, CamelFileRelativePath=order-1.xml, CamelFileLocalWorkPath=target/ftp-work/order-1.xml, CamelFileAbsolutePath=xml/order-1.xml, CamelFileNameOnly=order-1.xml, CamelFtpReplyCode=226}, readOnlyProperties = false, readOnlyBody = false, droppable = false, jmsXGroupFirstForConsumer = false} ActiveMQBytesMessage{ bytesOut = org.apache.activemq.util.ByteArrayOutputStream@33e7d53c, dataOut = java.io.DataOutputStream@18764c1b, dataIn = null }
[ad #2 - ftp://localhost:21/xml] GenericFileOnCompletion        DEBUG Done processing file: RemoteFile[order-1.xml] using exchange: Exchange[ID-cas-1530473515033-0-1]
[ad #2 - ftp://localhost:21/xml] FileUtil                       DEBUG Retrying attempt 0 to delete file: target/ftp-work/order-1.xml
[ad #2 - ftp://localhost:21/xml] FileUtil                       DEBUG Tried 1 to delete file: target/ftp-work/order-1.xml with result: true
[ad #2 - ftp://localhost:21/xml] FtpOperations                  DEBUG Deleting file: xml/order-1.xml
[ad #2 - ftp://localhost:21/xml] FtpConsumer                    DEBUG Took 0.013 seconds to poll: xml
```

Check with the ActiveMQ Web console that there is a message waiting for consumption in the queue `orders-xml` as shown in the screenshot below:

![Alt Image Text](./images/activemq-console-browse-queue.png "Edit Pom.xml")

Click on the message to see the headers, properties and payload of this first message. 

![Alt Image Text](./images/activemq-console-browse-message.png "Edit Pom.xml")

You can see that the order in XML format can be found in the queue. It works as expected!

Stop the application. 

### Add the Order Processing Backend

Now let's finish the Camel route by consuming from the `orders-xml` queue and storing it in a local file in the XML format. 

Add the follwing two route definitions to the `OrderManagementRoute` Java class. 

```        
		/*
		 * Consume the "orders-xml" queue and send it to the "order-processing" central route
		 */
        from("activemq:orders-xml")
        	.to("direct:order-processing");

		/*
		 * the "order processing", here it only writes the document to a file
		 */
		 from("direct:order-processing")
        	.to("log:DEBUG")
        	.to("file:data/processed?fileName=orders/${date:now:yyyy-MM-dd-HH}/${id}.xml");
```
        	
It consumes the messages from the queue and sends it to a [direct](http://camel.apache.org/direct.html) component for reusing a central flow (which we will see in use later). For the [file](http://camel.apache.org/file2.html) component we are using the capability for defining the name of the output file.  

Restart the application and you should see that the message got consumed from the queue and a file is written to the `data/processed` folder. Inside this folder, there is an `orders/<datetime>/` folder, where the files are stored by hour. 

![Alt Image Text](./images/local-file-list.png "Edit Pom.xml")

We can see that the first version of the Camel flow is working. Now let's add the HTTP endpoint to the Application. 

### Add an HTTP endpoint

The HTTP endpoint accepts also the XML document, exactly like the one used for the files on the FTP server. 

Add the follwing two route definitions to the `OrderManagementRoute` Java class. 

```
        /*
         * HTTP Endpoint 
         */
        from("jetty:http://0.0.0.0:8888/placeorder")
        	.inOnly("activemq:orders-xml")
        	.transform().constant("OK");
```

We are using one new component, the [jetty](http://camel.apache.org/jetty.html) component to expose the HTTP interface. You can see that the message is sent to the same queue `orders-xml` as before. It's an `inOnly` flow, as we are not getting any return value from the queue. We are using a `transform().constant()` expression to return a response message on the HTTP request. 

Restart the application and let's test the HTTP endpoint. You can use the `curl` command line utility to send a POST request on `http://localhost:8888/placeorder` with the XML message. 

```
curl -X POST -i http://localhost:8888/placeorder --data '<?xml version="1.0" encoding="UTF-8"?>
<order name="IPad" amount="100" customer="Peter Muster"/>'
```

Alternatively you can also use the Firefox Add-on **RESTClient**. The following screenshot shows it in action.  

![Alt Image Text](./images/firefox-restclient.png "Edit Pom.xml")

After sending a message on the HTTP endpoint, you should see another file in the   `data/processed` folder.

Stop the application. 

Last but not least, let's add the support for CSV files as input on the FTP server.

### Read CSV files from FTP Server
 
Add the follwing two route definitions to the `OrderManagementRoute` Java class. 

```
		/*
		 * Consume CSV file from FTP server from the "csv" folder and send it to the "orders-csv" queue
		 */
        from("ftp://localhost:21/csv?autoCreate=false&username=order&password=order&passiveMode=true&binary=false" + 
        		"&localWorkDirectory=target/ftp-work&delay=5s&delete=true")
        	.to("activemq:orders-csv");
        	
        /*
         * Consume the "orders-csv" queue and send it to the "order-processing" central route
         */
        from("activemq:orders-csv")
        	.unmarshal().bindy(BindyType.Csv, Order.class)
        	.marshal().jaxb()
    		.to("direct:order-processing");
```

For the transformation from CSV to XML, we can use the [bindy](http://camel.apache.org/bindy.html) component and the [jaxb](http://camel.apache.org/jaxb.html) component. The transformation works by first converting the CSV into a Java object and from the Java object to XML. The output of the transformation is sent to the direct route, to reuse the route for the "order-processing". 

Create a new Java class `Order` which represents the Order domain object and add the following code:

```
package com.trivadis.integrationws.camel;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@CsvRecord(separator = ",", skipFirstLine = true)
public class Order implements Serializable {
    @XmlAttribute
    @DataField(pos = 1)
    private String name;
    
    @XmlAttribute
    @DataField(pos = 2)
    private int amount;
    
    @XmlAttribute
    @DataField(pos = 3)
    private String customer;    
    
    public Order() {
    }
    
    public Order(String name, int amount, String customer) {
        this.name = name;
        this.amount = amount;
        this.customer = customer;
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (this.getClass() != other.getClass()) {
            return false;
        }
        Order that = (Order) other;
        return this.name.equals(that.name) && this.amount == that.amount && this.customer.equals(that.customer);
    }
    
    @Override
    public String toString() {
        return "Order[" + name + " , " + amount + " , " + customer +"]";
    }
}
```

The mapping from CSV and to XML is done using Java annotation.

Now let's restart the application, so we can test the CSV file behaviour. 
Create an order-1.csv file and add an order in the format shown below. 

```
name,amount,customer
iphone,2,Paul Jones
```

Upload it to the FTP Server into the `csv` folder. It should be picked up within 5 seconds, transformed to XML and you should see another file in the `data/processed` folder.



