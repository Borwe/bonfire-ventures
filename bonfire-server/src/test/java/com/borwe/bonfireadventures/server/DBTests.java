package com.borwe.bonfireadventures.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.borwe.bonfireadventures.data.services.VisitorService;

@SpringBootTest
public class DBTests {
	
	@Autowired
	VisitorService visitorService;

	@Test
	public void testVisitor() {
		Assertions.assertNotNull(visitorService, "visitorService is not autowired");
	}
}
