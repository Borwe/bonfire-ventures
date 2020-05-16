package com.borwe.bonfireadventures.restServices;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.borwe.bonfireadventures.data.services.VisitorService;
import com.borwe.bonfireadventures.replies.BasicReply;
import com.borwe.bonfireadventures.server.networkObjs.Base64Handler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@RestController()
public class AndroidREST{
	
	//used for decoding and encoding
	@Autowired
	Base64Handler base64Handler;
	
	//used for json management
	@Autowired
	ObjectMapper jsonMapper;
	
	//for handling contexts
	@Autowired
	ApplicationContext appContext;
	
	//for handling visitors
	@Autowired
	VisitorService visitorService;
    
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public Mono<String> helloTest(){
        return Mono.just("hello");
    }
    
    @RequestMapping(value = "/checkUserNameExists")
    public Mono<String> checkUserNameExists(String json){
    	return Mono.just(json).map(js->{
    		//decode base 64
    		return base64Handler.decode(json);
    	}).map(decodedJson->{
    		//turn to json node
    		try {
				return jsonMapper.readTree(decodedJson);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return null;
    	}).flatMap(jsonNode->{
    		//check if user exists
    		return checkIfUserExists(jsonNode);
    	}).map(basicReply->{
    		//turn to encoded json string
			try {
				String basicReplyString = appContext.getBean(ObjectMapper.class).writeValueAsString(basicReply);
				return appContext.getBean(Base64Handler.class).encode(basicReplyString);
			} catch (BeansException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//if we reach here means error occured
			//so return a BasicReply of true to avoid user keep registering
			return "";
    	});
    }
    
    private Mono<BasicReply> checkIfUserExists(JsonNode userNode){
    	return Mono.just(userNode).map(node->{
    		//check if user_name, or user_phone field exists
    		//if not return with user_positive, to prevent user from
    		//registering fake users
    		if(node==null || node.has("name")==false || node.has("phone")) {
    			return appContext.getBean("user_positive", BasicReply.class);
    		}
    		
    		//if "user_name", check that such a user exists with given name
    		String name=node.findValue("name").asText();
    		if(visitorService.checkIfVisitorExistByName(name).block()) {
    			//true, meaning user name already taken
    			return appContext.getBean("user_positive", BasicReply.class);
    		}
    		
    		//if "user_phone", check that such a user exists, if so return user_positive_phone
    		String phone=node.findValue("phone").asText();
    		if(visitorService.checkIfVisitorExistsByPhone(phone).block()) {
    			//true meaning such a user exists, so return a user_positive_phone
    			return appContext.getBean("user_positive_phone", BasicReply.class);
    		}
    		
    		//otherwise no such user, or phone number exists, so return a
    		//user_negative BasicReply
    		return appContext.getBean("user_negative", BasicReply.class);
    	});
    }
}
