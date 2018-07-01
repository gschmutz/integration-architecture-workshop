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
