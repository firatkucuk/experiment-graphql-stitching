package com.example.project2;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

	@QueryMapping
	public String greeting2() {
		return "Hello world";
	}
}