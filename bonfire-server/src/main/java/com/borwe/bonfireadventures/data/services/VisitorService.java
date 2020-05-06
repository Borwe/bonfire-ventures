package com.borwe.bonfireadventures.data.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.borwe.bonfireadventures.data.objects.VisitorRepository;

@Service
public class VisitorService {
	
	@Autowired
	private VisitorRepository visitorRepository;
}
