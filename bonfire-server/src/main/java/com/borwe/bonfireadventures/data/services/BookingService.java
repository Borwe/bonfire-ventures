/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borwe.bonfireadventures.data.services;

import com.borwe.bonfireadventures.data.objects.Booking;
import com.borwe.bonfireadventures.data.objects.Repositories.BookingRepository;
import com.borwe.bonfireadventures.data.objects.Visitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class BookingService {
	
	@Autowired
	BookingRepository bookingRepository;

	@Autowired
	VisitorService visitorService;

	public Flux<Booking> getBookingsByVisitorId(Visitor visitor){
		return Flux.just(visitor).flatMap(vis->{
			return Flux.fromIterable(bookingRepository.findByVisitor(vis));
		});
	}

	/**
	 * Get reactive stream of all bookings, in descending order of costs
	 * @return 
	 */
	public Flux<Booking> getTopAllBookingsAnonymouslyOrderedByCostDescending(){
		return Flux.fromIterable(bookingRepository.findAllByOrderByCost())
				.map(booking->{
					Booking toReturn=booking;
					toReturn.setId(-1L);

					toReturn.setVisitor(visitorService.generateFakeVisitor().block());
					return toReturn;
				});
	}
}
