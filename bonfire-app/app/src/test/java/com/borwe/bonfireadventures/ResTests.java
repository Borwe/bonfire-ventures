package com.borwe.bonfireadventures;

import com.borwe.bonfireadventures.network.RestLink;
import com.borwe.bonfireadventures.network.RestServices;
import com.borwe.bonfireadventures.network.RestValues;
import com.borwe.bonfireadventures.network.json.reply.BasicReply;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class ResTests {

    @Test
    public void testGettingLink(){
        RestValues.set_test(true);
        String link= RestLink.getHttpLink(RestLink.VERIFY_USER_EXISTS);
        System.out.println("LINK: "+link);
        String realLink="http://"+ RestValues.IP.getValue()+RestLink.VERIFY_USER_EXISTS.getValue();
        System.out.println("LINK2: "+realLink);

        //now check that the match each other
        Assert.assertTrue(link.contentEquals(realLink));
    }

    @Test
    public void testVerifyingUserExists() throws JSONException {
        RestValues.set_test(true);
        //with fake should return a JSON object with success false
        BasicReply reply =RestServices.checkIfUserNameUsed("shit").blockingGet();
        //since its a fake test, success in json should be false
        Assert.assertTrue(reply.isSuccess()==false);

        //now disable tests
        RestValues.set_test(false);
    }
}
