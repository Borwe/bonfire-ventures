package com.borwe.bonfireadventures.replies;

import lombok.Setter;

public class BasicReply implements Reply{
	
	@Setter
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

}
