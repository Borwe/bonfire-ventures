package com.borwe.bonfireadventures.replies;

import lombok.Getter;

public interface Reply {

	public boolean getSuccess();
	public String getMessage();

	public enum BasicStrings implements ReplyBaseEnum{
		INVALID_INPUT("Sorry no valid input provided");
		
		@Getter
		private final String value;
		
		BasicStrings(String value){
			this.value=value;
		}
	}

	public enum VisitorStrings implements ReplyBaseEnum{

		NO_VISITOR_FOUND("No user found"),
		SIMIMAL_PHONE_NUMBER_FOUND("Similar phone number found, already registered"),
		VISITOR_FOUND("Visitor found");

		@Getter
		private final String value;
		
		VisitorStrings(String value){
			this.value=value;
		}
	}
}