package com.borwe.bonfireadventures.server.networkObjs;

import java.util.Base64;

public class Base64Handler {
	public String encode(String value) {
		String toReturn="";
		
		Base64.Encoder encoder=Base64.getEncoder();
		toReturn=encoder.encodeToString(value.getBytes());
		return toReturn;
	}
	
	public String decode(String value) {
		String toReturn="";
		
		Base64.Decoder decoder=Base64.getDecoder();
		toReturn=decoder.decode(value).toString();
		return toReturn;
	}
}
