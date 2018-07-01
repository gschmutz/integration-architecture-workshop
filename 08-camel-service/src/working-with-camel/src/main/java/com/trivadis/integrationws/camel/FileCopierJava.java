package com.trivadis.integrationws.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

public class FileCopierJava {

    public static void main(String args[]) throws Exception {

    	Main main = new Main();
    	
    	main.enableHangupSupport();
        
        // add our route to the CamelContext
        main.addRouteBuilder(new RouteBuilder() {
            public void configure() {
                from("file:data/inbox?delete=true&delay=5s")
                .to("file:data/outbox");
            }
        });

        // start the route and let it do its work
        main.run();
    }
}
