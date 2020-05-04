package com.borwe.bonfireadventures.network;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

public enum RestValues {
    IP("192.168.43.164"),
    FAKE_IP("62.62.62.62"),
    USER_NAME_JSON_KEY("user_name"),
    JSON_SUCCESS("success"),
    JSON_MESSAGE("message");

    @Getter
    private final String value;

    /**
     * used for marking if we are using for tests or not
     * if so, then we use FAKE_IP
     */
    private static Boolean test=false;

    public static boolean is_test(){
        synchronized (test){
            return test;
        }
    }

    public static void set_test(boolean value){
        synchronized (test){
            RestValues.test=new Boolean(value);
        }
    }

    RestValues(String value){
        this.value=value;
    }
}
