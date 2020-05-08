package com.borwe.bonfireadventures.data.services;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.borwe.bonfireadventures.data.objects.Visitor;
import com.borwe.bonfireadventures.data.objects.VisitorRepository;

import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

@Service
public class VisitorService {
	private static Logger logger=LoggerFactory.getLogger(VisitorService.class);
	
	@Autowired
	private VisitorRepository visitorRepository;
	
	private ThreadLocalRandom random=ThreadLocalRandom.current();
	
	public Visitor fillWithRandomValues(Visitor visitor) {
		var number=Long.toString(random.nextLong(1111111111L, 9999999999L));
		var name=new StringBuffer().append("abc").append(number).toString();
		
		visitor.setName(name);
		visitor.setPhone(number);
		visitor.setG_ig(name);
		return visitor;
	}
	
	public Mono<Long> getTotalNumberOfVisitors(){
		return Mono.create(new Consumer<MonoSink<Long>>() {

			@Override
			public void accept(MonoSink<Long> sink) {
				// TODO Auto-generated method stub
				sink.success(visitorRepository.count());
			}
		});
	}
	

	public Mono<Visitor> addVisitorToDB(Visitor visitor){
		return Mono.just(visitor).map((vis)->{
			//save the visitor to db
			try {
				vis=visitorRepository.save(vis);
			}catch(Exception ex) {
				//meaning not saved
				ex.printStackTrace();
			}
			return vis;
		}).filter((vis)->{
			//filter out to only valid saved states
			if(vis.getId()==null || vis.getId()==-1 ) {
				logger.debug("Hmm, visitor has no valid id, id value is:"+vis.getId());
				return false;
			}
			return true;
		});
	}
	
	public Mono<Boolean> deleteVisitors(List<Visitor>visitors){
		return Mono.just(visitors).map((visz)->{
			try {
				visitorRepository.deleteAll(visz);
				return true;
			}catch(Exception ex){
				return false;
			}
		});
	}
}
