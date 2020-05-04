package com.borwe.bonfireadventures.network;

import lombok.Getter;

public enum RestLink {

    VERIFY_USER_EXISTS("/CheckUserNameExists");

    @Getter
    private final String value;

    RestLink(String value){
        this.value=value;
    }

    public static String getHttpLink(RestLink link){
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("http://");
        stringBuffer.append(RestValues.IP.getValue());
        stringBuffer.append("/");
        stringBuffer.append(link.getValue());
        return stringBuffer.toString();
    }
}
