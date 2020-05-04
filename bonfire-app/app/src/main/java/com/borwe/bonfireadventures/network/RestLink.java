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
        appendLinkIFTestOrNot(stringBuffer);
        stringBuffer.append(link.getValue());
        return stringBuffer.toString();
    }

    /**
     * Handles appending IP to be used by
     * test or not
     */
    private static void appendLinkIFTestOrNot(StringBuffer buffer){
        if(RestValues.is_test()){
            //meaning use fake ip
            buffer.append(RestValues.FAKE_IP.getValue());
        }else{
            //meaning not a test
            buffer.append(RestValues.IP.getValue());
        }
    }
}
