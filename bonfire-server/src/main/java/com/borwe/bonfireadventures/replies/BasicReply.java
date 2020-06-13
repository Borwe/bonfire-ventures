package com.borwe.bonfireadventures.replies;

import lombok.Setter;

public class BasicReply implements Reply{
	
	private String message;
	
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

	public void setMessage(ReplyBaseEnum baseEnum){
		this.message=baseEnum.getValue();
	}
	
	public void setMessage(String message){
		this.message=message;
	}
}
