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