package com.borwe.bonfireadventures.replies;

import com.borwe.bonfireadventures.server.networkObjs.Base64Handler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class BasicReply implements Reply{
	
	private String message;

	private Logger logger=LoggerFactory.getLogger(BasicReply.class);

	@Autowired
	Base64Handler base64;

	@Autowired
	ApplicationContext context;

	@Setter
	private boolean success;

	@Override
	public boolean getSuccess() {
		return success;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public boolean matches(Reply reply){
		//check that success rates match
		if(this.success==reply.getSuccess()){
			//check that messages match
			if(this.message.contentEquals(reply.getMessage())){
				return true;
			}
			return false;
		}
		return false;
	}

	public void setMessage(ReplyBaseEnum baseEnum){
		this.message=baseEnum.getValue();
	}
	
	public void setMessage(String message){
		this.message=message;
	}

	@Override
	public String encode() {
		try {
			//used for producing an encoded string
			return base64.encode(context.getBean(ObjectMapper.class)
					.writeValueAsString(this));
		} catch (JsonProcessingException ex) {
			logger.error("JsonPropertiesException Occurred");
			return "";
		}
	}
}
