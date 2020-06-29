/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borwe.bonfireadventures.server;

import com.borwe.bonfireadventures.data.objects.Visitor;
import com.borwe.bonfireadventures.data.services.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class VisitorFactoryForTestsHandler {

	@Autowired
	private ApplicationContext appContext;

	@Autowired
	private VisitorService visitorService;
	
	public Mono<Visitor> generateAUserForLoggingIn(){
		return visitorService.addVisitorToDB(visitorService
				.fillWithRandomValues(new Visitor()));
	}

	public Mono<Boolean> logOutVisitorAndDelete(Visitor visitor){
		return visitorService.removeVisitorFromDB(visitor);
	}
}
