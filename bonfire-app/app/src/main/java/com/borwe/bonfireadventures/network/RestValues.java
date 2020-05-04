package com.borwe.bonfireadventures.network;

import lombok.Getter;

public enum RestValues {
    IP("192.168.43.164");

    @Getter
    private final String value;

    RestValues(String value){
        this.value=value;
    }
}
