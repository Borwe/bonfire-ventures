/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borwe.bonfireadventures.data.objects.Repositories;

import com.borwe.bonfireadventures.data.objects.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author brian
 */
public interface BookingRepository extends JpaRepository<Booking,Long>{ 
}
