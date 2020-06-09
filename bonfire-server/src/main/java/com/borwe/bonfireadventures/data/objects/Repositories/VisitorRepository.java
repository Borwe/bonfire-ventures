package com.borwe.bonfireadventures.data.objects.Repositories;

import com.borwe.bonfireadventures.data.objects.Visitor;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor,Long>{
    //for finding objects by name
	List<Visitor> findByName(String name);
    
	//for finding objects by phone number
	List<Visitor> findByPhone(String phone);
}
