package com.example.frontendservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(
	    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@ComponentScan(
		basePackages = {"com.example.frontendservice", "controller" , "service"},
	    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class MicroServFrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroServFrontendApplication.class, args);
		
	}

}
