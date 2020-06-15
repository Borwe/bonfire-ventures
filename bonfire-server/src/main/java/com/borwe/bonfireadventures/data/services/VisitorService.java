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
import com.borwe.bonfireadventures.data.objects.Repositories.VisitorRepository;

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
				sink.success(visitorRepository.count());
			}
		});
	}
	

	public Mono<Visitor> addVisitorToDB(Visitor visitor){
		return Mono.just(visitor).map((vis)->{
			//save the visitor to db
			try {
				vis=visitorRepository.saveAndFlush(vis);
				return vis;
			}catch(Exception ex) {
				//meaning not saved
				ex.printStackTrace();
				System.out.println("FUCKED: "+ex.getLocalizedMessage());
			}
			return vis;
		}).filter((vis)->{
			//filter out to only valid saved states
			if(vis.getId()==null || vis.getId()==-1 ) {
				System.out.println("Hmm, visitor has no valid id, id value is:"+vis.getId());
				return false;
			}else{
				System.out.println("Come on what the fuck?");
			}
			return true;
		});
	}
	
	public Mono<Boolean> checkIfVisitorExistsWithID(Visitor visitor){
		return Mono.just(visitor).map((vis)->{
			//if id is -1, means visitor already doesn't exist
			if(vis.getId()==-1) {
				return false;
			}
			//check if visitor exists, return true if so
			if(visitorRepository.existsById(visitor.getId())) {
				return true;
			}
			return false;
		});
	}
	
	/**
	 * Checks if visitor exists based on name
	 * @param @string name
	 * @return @Mono<Boolean> with value true if user exists, false otherwise
	 */
	public Mono<Boolean> checkIfVisitorExistByName(String name){
		return Mono.just(name).map(vis->{
			if(name!=null && name.trim().isEmpty()==false) {
				//returns true if user exists, false otherwise
				if(visitorRepository.findByName(name).isEmpty())
					return false;
			}
			//return true to avoid creating null users
			return true;
		});
	}
	
	/**
	 * Check if visitor exists with given phone
	 * @param @string phone
	 * @return @Mono<Boolean> true if phone number exists, false otherwise
	 */
	public Mono<Boolean> checkIfVisitorExistsByPhone(String phone){
		return Mono.just(phone).map(p->{
			if(p!=null && p.trim().isEmpty()==false) {
				if(visitorRepository.findByPhone(phone).isEmpty())
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
	
	public Mono<Boolean> deleteVisitor(Visitor visitor){
		return Mono.just(visitor).map(vis->{
			if(vis.getId()==-1) {
				//since user was never saved, so nothing to delete
				return true;
			}
			//otherwise go ahead and start deleting
			visitorRepository.delete(visitor);
			return true;
		});
	}
}
