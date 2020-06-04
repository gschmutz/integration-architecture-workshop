package com.trivadis.integrationws.activemq.spring.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Marshaller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jms.support.converter.MarshallingMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
@EnableConfigurationProperties
public class JMSConfiguration {
	@Bean
	public MarshallingMessageConverter createMarshallingMessageConverter(Jaxb2Marshaller jaxb2Marshaller) {
		return new MarshallingMessageConverter(jaxb2Marshaller);

	}

	@Bean
	public Jaxb2Marshaller createJaxbMarshaller(@Value("${context.path}") final String contextPath,
			@Value("${schema.location}") final Resource schemaLocation) {// this method will provide jaxb marshaller ,we
																			// need to provide context path and schema
																			// resource

		Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
		jaxb2Marshaller.setContextPath(contextPath);
		jaxb2Marshaller.setSchema(schemaLocation);

		Map<String, Object> prop = new HashMap<>();
		prop.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxb2Marshaller.setMarshallerProperties(prop);

		return jaxb2Marshaller;
	}
}
