package com.borwe.bonfireadventures.server;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.borwe.bonfireadventures.data.objects.Visitor;
import com.borwe.bonfireadventures.data.services.VisitorService;
import com.borwe.bonfireadventures.replies.BasicReply;
import com.borwe.bonfireadventures.restServices.AndroidREST;
import com.borwe.bonfireadventures.server.networkObjs.Base64Handler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class AndroidRESTTest {

	@Autowired
	ApplicationContext appContext;
	
	@Autowired
	AndroidREST androidRest;
	
	@Autowired
	VisitorService visitorService;
	
	//logger
	Logger logger=LoggerFactory.getLogger(AndroidRESTTest.class);
	
	@Test
	public void checkUserTest() {
		//start by checking if a random user exists, it should fail
		Visitor randomVis=visitorService.fillWithRandomValues(appContext.getBean("genVisitor", Visitor.class));
		assertNotNull(randomVis);
		
		//encode randomVis to base64
		ObjectMapper mapper=appContext.getBean(ObjectMapper.class);
		String randomVisString;
		try {
			randomVisString = mapper.writeValueAsString(randomVis);
			String randomVisEncode=appContext.getBean(Base64Handler.class).encode(randomVisString);
			
			//check if user exists, should fail.
			androidRest.checkUserNameExists(randomVisEncode).map(encoded->{
				//decode
				return appContext.getBean(Base64Handler.class).decode(encoded);
			}).map(decoded->{
				// return a true or false, from BasicReply if possible
				try {
					return appContext.getBean(ObjectMapper.class).readValue(decoded, BasicReply.class).getSuccess();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (BeansException e) {
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				
				//meaning user exists
				return true;
			}).subscribe(val->{
				assertFalse(val);
				logger.info("okay");
			},throwable->{
				logger.error(throwable.getLocalizedMessage());
				throwable.printStackTrace();
			});
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//get count of items
		Long countVisitorsSaved=visitorService.getTotalNumberOfVisitors().block();
		assertNotNull(countVisitorsSaved);
		//now go ahead and save the visitor
		Visitor savedVisitor=visitorService.addVisitorToDB(randomVis).block();
		//chceck that visitor is not null
		assertNotNull(savedVisitor);
		
		try {
			//encode savedVisitor
			String encodedSavedVisitor=appContext.getBean(Base64Handler.class)
					.encode(appContext.getBean(ObjectMapper.class).writeValueAsString(savedVisitor));
			
			
			//check if user exists, should pass since saved.
			androidRest.checkUserNameExists(encodedSavedVisitor).map(encoded->{
				//decode
				return appContext.getBean(Base64Handler.class).decode(encoded);
			}).map(decoded->{
				// return a true or false, from BasicReply if possible
				try {
					return appContext.getBean(ObjectMapper.class).readValue(decoded, BasicReply.class).getSuccess();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (BeansException e) {
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				
				//meaning user exists
				return true;
			}).subscribe(val->{
				assertTrue(val);
				logger.info("okay");
			},throwable->{
				logger.error(throwable.getLocalizedMessage());
				throwable.printStackTrace();
			});
		} catch (BeansException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		
		//now delete the saved object
		visitorService.deleteVisitor(savedVisitor).block();
		//make sure db was actually cleared
		assertTrue(countVisitorsSaved==visitorService.getTotalNumberOfVisitors().block());
	}
}
