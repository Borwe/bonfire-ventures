package com.borwe.bonfireadventures.data.services;

import com.borwe.bonfireadventures.data.objects.Destination;
import com.borwe.bonfireadventures.data.objects.Repositories.DestinationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Service
public class DestinationService{
    
    @Autowired
    private DestinationRepository destinRepository;

    public Flux<Destination> getTop5Destinations(){
        return Flux.fromIterable(destinRepository.findTop5ByOrderByVists());
    }
}
