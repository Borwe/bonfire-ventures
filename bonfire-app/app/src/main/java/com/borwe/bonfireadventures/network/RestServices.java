package com.borwe.bonfireadventures.network;

import android.util.Log;

import com.borwe.bonfireadventures.network.json.reply.BasicReply;
import com.borwe.bonfireadventures.network.json.send.UserNameSend;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RestServices {

    private static final MediaType JSON_TYPE=MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client=new OkHttpClient();

    /**
     * For getting Http client to operate with
     * @return OkHttpClient that is singular
     */
    public static Single<OkHttpClient> getOkHttpClient(){
        return Observable.just(client).single(client);
    }

    public static Single<BasicReply> checkIfUserNameUsed(String name){
        return Single.just(name).flatMap(new Function<String, SingleSource<BasicReply>>() {
            @Override
            public SingleSource<BasicReply> apply(String s) throws Exception {
                return RestServices.getJSONIfUserExists(s).map(new Function<String, BasicReply>() {
                    @Override
                    public BasicReply apply(String s) throws Exception {
                        //parse string into json
                        return new ObjectMapper().readValue(s,BasicReply.class);
                    }
                });
            }
        });
    }

    private static Single<String> getJSONIfUserExists(String user_name){
        return Single.just(user_name).map(new Function<String, String>() {
            @Override
            public String apply(String name) throws Exception {
                //if string name is empty, or doesn't exist, then continue
                //with an empty string
                if(name==null || name.trim().isEmpty()){
                    return "";
                }
                return name;
            }
        }).map(new Function<String, RequestBody>() {
            @Override
            public RequestBody apply(String user_name) throws Exception {
                //create json body to use with request
                ObjectMapper mapper=new ObjectMapper();
                UserNameSend user=new UserNameSend();
                user.setUser_name(user_name);
                String json=mapper.writeValueAsString(user);
                RequestBody requestBody=RequestBody.create(RestServices.JSON_TYPE,json);
                return requestBody;
            }
        }).map(new Function<RequestBody, Request>() {
            @Override
            public Request apply(RequestBody requestBody) throws Exception {
                //now get the reply from the server here
                Request request=new Request.Builder().url(RestLink.getHttpLink(RestLink.VERIFY_USER_EXISTS))
                        .post(requestBody)
                        .build();


                return request;
            }
        }).flatMap(new Function<Request, SingleSource<? extends String>>() {
            @Override
            public SingleSource<? extends String> apply(final Request request) throws Exception {
                //do the request here
                return RestServices.getOkHttpClient().map(new Function<OkHttpClient, String>() {
                    @Override
                    public String apply(OkHttpClient okHttpClient) throws Exception {
                        try{
                            Response response=okHttpClient.newCall(request).execute();
                            return response.body().toString();
                        }catch (Exception ex){
                            //meaning there was a problem with getting access to server
                            //log error here
                            return RestServices.getFailedErrorJSON();
                        }
                    }
                });
            }
        });
    }

    private static String getFailedErrorJSON() throws JsonProcessingException {
        BasicReply falseReply=new BasicReply();
        falseReply.setSuccess(false);
        falseReply.setMessage("Error, failed, no internet, please retry later");

        return new ObjectMapper().writeValueAsString(falseReply);
    }
}
