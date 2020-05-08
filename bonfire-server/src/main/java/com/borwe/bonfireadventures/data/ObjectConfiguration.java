package com.borwe.bonfireadventures.data;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.borwe.bonfireadventures.data.objects.Visitor;

@Configuration
public class ObjectConfiguration {

	@Bean()
	@Scope("prototype")
	public Visitor genVisitor() {
		return new Visitor();
	}
}
