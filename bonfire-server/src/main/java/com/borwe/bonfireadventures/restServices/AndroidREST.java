package com.borwe.bonfireadventures.restServices;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.borwe.bonfireadventures.data.services.VisitorService;
import com.borwe.bonfireadventures.replies.BasicReply;
import com.borwe.bonfireadventures.replies.Reply;
import com.borwe.bonfireadventures.server.ObjectConfigs;
import com.borwe.bonfireadventures.server.networkObjs.Base64Handler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestBody;

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
    
    @RequestMapping(value = "/hello")
    public Mono<String> helloTest(@RequestBody(required = false) String json){
		if(json==null || json.trim().isEmpty()){
			return ifNoValuePassed(json).flatMap(reply->encodeReply(reply));
		}

        return Mono.just("hello"+json);
    }

	@RequestMapping(value = "/topLists")
	public Mono<String> getTopLists(@RequestBody(required = false) String json){
		//if nothing is passed by the user here
		if(json==null || json.trim().isEmpty()){
			return ifNoValuePassed(json).flatMap(reply->encodeReply(reply));
		}

		//if json was actually passed by the user then decode it
		return Mono.just(json).map(js->base64Handler.decode(js))
				.map(jsDecoded->{
					try {
						//map to JsonNode
						return jsonMapper.readTree(jsDecoded);
					} catch (JsonProcessingException ex) {
						ex.printStackTrace();
						return jsonMapper.createObjectNode();
					}
				}).flatMap(jsNode->{
					//check if user exists and can actually do this request
					return verifyVisitorLegit(jsNode).flatMap(verified->{
						if(verified==false){
							return Mono.just(appContext
									.getBean(ObjectConfigs.ObjectConfigsBeansNames
										.ERROR_VISITOR_VERIFICATION))
									.flatMap(reply->encodeReply((Reply)reply));
						}else{
							return Mono.just("");
						}
					});
				});
				
	}
    
    @RequestMapping(value = "/checkUserNameExists")
    public Mono<String> checkUserNameExists(@RequestBody(required = false) String json){

		if(json==null || json.trim().isEmpty()){
			return ifNoValuePassed(json).flatMap(reply->encodeReply(reply));
		}

		
		//if json not empty
    	return Mono.just(json).map(js->{
    		//decode base 64
    		return base64Handler.decode(json);
    	}).map(decodedJson->{
    		//turn to json node
    		try {
				return jsonMapper.readTree(decodedJson);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
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
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			//if we reach here means error occured
			//so return a BasicReply of true to avoid user keep registering
			return "";
    	});
    }

	private Mono<Boolean> verifyVisitorLegit(JsonNode visitorNode){
		return Mono.just(visitorNode).map(node->{
    		//check if user_name, or user_phone field exists
    		//if not return with user_positive, to prevent user from
    		//registering fake users
    		if(node==null || node.has("name")==false || node.has("phone")==false) {
    			return appContext.getBean(ObjectConfigs.ObjectConfigsBeansNames.VISITOR_UNAUTH,
						BasicReply.class);
    		}

			//check if user exists by "user_phone" and "user_name"
    		String name=node.findValue("name").asText();
    		String phone=node.findValue("phone").asText();
			if(visitorService.checkIfVisitorExistByName(name).block() &&
					visitorService.checkIfVisitorExistsByPhone(phone).block()){
				//so we return a basic reply with VISITOR_POSITIVE_fULL
				return appContext.getBean(ObjectConfigs.ObjectConfigsBeansNames.VISITOR_POSITIVE_FULLY,
						BasicReply.class);
			}else{
				return appContext.getBean(ObjectConfigs.ObjectConfigsBeansNames.VISITOR_UNAUTH,BasicReply.class);
			}
		}).map(reply->{

			//check if user exists or not
			BasicReply unauthorized=appContext.
					getBean(ObjectConfigs.ObjectConfigsBeansNames.
							VISITOR_UNAUTH,BasicReply.class);

			//for checking if user is fully authorized
			BasicReply fullyAuthorized=appContext
					.getBean(ObjectConfigs.ObjectConfigsBeansNames.VISITOR_POSITIVE_FULLY
					,BasicReply.class);

			if(reply.matches(unauthorized)){
				//meaning no user authorized, so return false
				return false;
			}else if(reply.matches(fullyAuthorized)){
				//meaning user is fully authorised
				return true;
			}
			return false;
		});
	}

    private Mono<BasicReply> checkIfUserExists(JsonNode userNode){
    	return Mono.just(userNode).map(node->{
    		//check if user_name, or user_phone field exists
    		//if not return with user_positive, to prevent user from
    		//registering fake users
    		if(node==null || node.has("name")==false || node.has("phone")==false) {
    			return appContext.getBean(ObjectConfigs.ObjectConfigsBeansNames.VISITOR_UNAUTH,
						BasicReply.class);
    		}

			//check if user exists by "user_phone" and "user_name"
    		String name=node.findValue("name").asText();
    		String phone=node.findValue("phone").asText();
			if(visitorService.checkIfVisitorExistByName(name).block() &&
					visitorService.checkIfVisitorExistsByPhone(phone).block()){
				//so we return a basic reply with VISITOR_POSITIVE_fULL
				return appContext.getBean(ObjectConfigs.ObjectConfigsBeansNames.VISITOR_POSITIVE_FULLY,
						BasicReply.class);
			}
    		
    		//if "user_name", check that such a user exists with given name
    		if(visitorService.checkIfVisitorExistByName(name).block()) {
    			//true, meaning user name already taken
    			return appContext.getBean(ObjectConfigs.ObjectConfigsBeansNames.VISITOR_POSITIVE_NAME,
						BasicReply.class);
    		}
    		
    		//if "user_phone", check that such a user exists, if so return user_positive_phone
    		if(visitorService.checkIfVisitorExistsByPhone(phone).block()) {
    			//true meaning such a user exists, so return a user_positive_phone
    			return appContext.getBean(ObjectConfigs.ObjectConfigsBeansNames.VISITOR_POSITIVE_PHONE, BasicReply.class);
    		}
    		
    		//otherwise no such user, or phone number exists, so return a
    		//user_negative BasicReply
    		return appContext.getBean(ObjectConfigs.ObjectConfigsBeansNames.VISITOR_NEGATIVE, BasicReply.class);
    	});
    }

	//if json is null, then skip doing alot of shit, just return a failed basic Reply
	private Mono<BasicReply> ifNoValuePassed(String json){

		return Mono.just(appContext
				.getBean(ObjectConfigs.ObjectConfigsBeansNames.INVALID_INPUT,BasicReply.class));
	}

	//turn reply into a base64 encoded
	private Mono<String> encodeReply(Reply reply){
		return Mono.just(reply.encode());
	}
}
