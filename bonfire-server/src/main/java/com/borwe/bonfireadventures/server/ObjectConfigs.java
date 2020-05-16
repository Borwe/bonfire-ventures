package com.borwe.bonfireadventures.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import com.borwe.bonfireadventures.replies.BasicReply;
import com.borwe.bonfireadventures.server.networkObjs.Base64Handler;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan(basePackages = "com.borwe.bonfireadventures.server.networkObjs")
public class ObjectConfigs {

	@Bean
	@Lazy
	@Scope("singleton")
	public Base64Handler base64Handler() {
		return new Base64Handler();
	}
	
	@Bean
	@Lazy
	@Scope("singleton")
	public ObjectMapper jsonObjMapper() {
		return new ObjectMapper();
	}
	
	@Bean(name = "user_negative")
	@Lazy
	@Scope("singleton")
	public BasicReply userFoundNegative() {
		BasicReply usernegative=new BasicReply();
		usernegative.setMessage("No user found");
		usernegative.setSuccess(false);
		return usernegative;
	}
	
	@Bean(name = "user_positive")
	@Lazy
	@Scope("singleton")
	public BasicReply userFoundPositive() {
		BasicReply userFound=new BasicReply();
		userFound.setMessage("User found");
		userFound.setSuccess(true);
		return userFound;
	}
	
	@Bean(name = "user_positive_phone")
	@Lazy
	@Scope("singleton")
	public BasicReply userPhoneFoundPositive() {
		BasicReply phoneFound=new BasicReply();
		phoneFound.setMessage("Similar Phone number found, already registered");
		phoneFound.setSuccess(true);
		return phoneFound;
	}
}
