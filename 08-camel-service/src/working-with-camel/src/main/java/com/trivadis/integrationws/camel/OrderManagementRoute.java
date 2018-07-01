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
        
        /*
         * HTTP Endpoint 
         */
        from("jetty:http://0.0.0.0:8888/placeorder")
    		.inOnly("activemq:orders-xml")
    		.transform().constant("OK");
        

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
	}
}
