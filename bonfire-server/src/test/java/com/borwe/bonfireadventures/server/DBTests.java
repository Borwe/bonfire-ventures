package com.borwe.bonfireadventures.server;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.borwe.bonfireadventures.data.objects.Visitor;
import com.borwe.bonfireadventures.data.services.VisitorService;

import reactor.core.publisher.Mono;

@SpringBootTest
public class DBTests {
	
	@Autowired
	VisitorService visitorService;
	
	@Autowired
	ApplicationContext context;
	
	/**
	 * For asserting that context is available for usage
	 */
	public void testContextExists() {
		Assertions.assertNotNull(context);
	}

	@Test
	public void testVisitor() {
		testContextExists();
		Assertions.assertNotNull(visitorService, "visitorService is not autowired");
		
		//get a visitor and set him up
		Visitor vis1=(Visitor) context.getBean("genVisitor");
		vis1=visitorService.fillWithRandomValues(vis1);
		Assertions.assertNotNull(vis1);
		
		//get count of visitors on db
		long current_numbers=visitorService.getTotalNumberOfVisitors().block();
		
		//add vis1 to db
		Optional<Visitor> visitorOption1=visitorService.addVisitorToDB(vis1).blockOptional();
		boolean added=visitorOption1.isPresent();
		Assertions.assertTrue(added);
		
		//now get another visitor
		Visitor vis2=context.getBean("genVisitor", Visitor.class);
		vis2=visitorService.fillWithRandomValues(vis2);
		Assertions.assertNotNull(vis2);
		
		//add vis2 to db
		Optional<Visitor> visitorOption2=visitorService.addVisitorToDB(vis2).blockOptional();
		added=visitorOption2.isPresent();
		Assertions.assertTrue(added);
		
		//now get count of current items
		long new_current_numbers=visitorService.getTotalNumberOfVisitors().block();
		//check that current_numbers + 1 == new_current_numbers
		Assertions.assertTrue((current_numbers+2)==new_current_numbers);
		
		//now delete all items
		var list=new ArrayList<Visitor>();
		list.add(visitorOption1.get());
		list.add(visitorOption2.get());
		boolean deleted=visitorService.deleteVisitors(list).block();
		Assertions.assertTrue(deleted);
		
		//check that current_number is same to number of visitors on db
		long new_current_numbers_end=visitorService.getTotalNumberOfVisitors().block();
		Assertions.assertTrue(new_current_numbers_end==current_numbers);
	}
}
