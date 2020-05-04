package com.borwe.bonfireadventures.network.json.reply;

import com.borwe.bonfireadventures.network.json.JSONReply;


public class BasicReply implements JSONReply {

    private boolean success;
    private String message;

    public synchronized void setMessage(String message) {
        this.message = message;
    }

    public synchronized void setSuccess(boolean success){
        this.success=success;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
