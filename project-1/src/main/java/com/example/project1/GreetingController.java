package com.example.project1;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

	@QueryMapping
	public String greeting1() {
		return "Hello world";
	}
}