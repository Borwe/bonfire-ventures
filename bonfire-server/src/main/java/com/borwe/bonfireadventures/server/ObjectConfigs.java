package com.borwe.bonfireadventures.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import com.borwe.bonfireadventures.replies.BasicReply;
import com.borwe.bonfireadventures.replies.Reply;
import com.borwe.bonfireadventures.server.networkObjs.Base64Handler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

@Configuration
@ComponentScan(basePackages = "com.borwe.bonfireadventures.server.networkObjs")
public class ObjectConfigs {

	public class ObjectConfigsBeansNames{
		public static final String VISITOR_NEGATIVE="visitor_negative";
		public static final String VISITOR_POSITIVE="visitor_positive";
		public static final String VISITOR_POSITIVE_PHONE="visitor_positive_phone"; 
		public static final String INVALID_INPUT="invalid_input";
	}

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
	
	@Bean(name = ObjectConfigsBeansNames.VISITOR_NEGATIVE)
	@Lazy
	@Scope("singleton")
	public BasicReply userFoundNegative() {
		BasicReply usernegative=new BasicReply();
		usernegative.setMessage(Reply.VisitorStrings.NO_VISITOR_FOUND);
		usernegative.setSuccess(false);
		return usernegative;
	}
	
	@Bean(name = ObjectConfigsBeansNames.VISITOR_POSITIVE)
	@Lazy
	@Scope("singleton")
	public BasicReply userFoundPositive() {
		BasicReply userFound=new BasicReply();
		userFound.setMessage(Reply.VisitorStrings.VISITOR_FOUND);
		userFound.setSuccess(true);
		return userFound;
	}
	
	@Bean(name = ObjectConfigsBeansNames.VISITOR_POSITIVE_PHONE)
	@Lazy
	@Scope("singleton")
	public BasicReply userPhoneFoundPositive() {
		BasicReply phoneFound=new BasicReply();
		phoneFound.setMessage(Reply.VisitorStrings.SIMIMAL_PHONE_NUMBER_FOUND);
		phoneFound.setSuccess(true);
		return phoneFound;
	}

	@Bean(name = ObjectConfigsBeansNames.INVALID_INPUT)
	@Lazy
	@Scope("singleton")
	public BasicReply invalidInput(){
		BasicReply invalidReply=new BasicReply();
		invalidReply.setMessage(Reply.BasicStrings.INVALID_INPUT);
		invalidReply.setSuccess(false);
		return invalidReply;
	}
}
