package com.borwe.bonfireadventures;

import com.borwe.bonfireadventures.network.RestLink;
import com.borwe.bonfireadventures.network.RestValues;

import org.junit.Assert;
import org.junit.Test;

public class ResTests {

    @Test
    public void testGettingLink(){
        String link= RestLink.getHttpLink(RestLink.VERIFY_USER_EXISTS);
        System.out.println("LINK: "+link);
        String realLink="http://"+ RestValues.IP.getValue()+"/"+RestLink.VERIFY_USER_EXISTS.getValue();
        System.out.println("LINK2: "+realLink);

        //now check that the match each other
        Assert.assertTrue(link.contentEquals(realLink));
    }
}
