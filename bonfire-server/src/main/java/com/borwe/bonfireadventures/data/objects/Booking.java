/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borwe.bonfireadventures.data.objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author brian
 */
@Entity
public class Booking {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter @Setter
	private Long id;

	@ManyToOne
	@Getter @Setter
	private Visitor visitor;

	@ManyToOne
	@Getter @Setter
	private Location location;

	/*
	Mark the date in milliseconds when booked
	*/
	@Column
	@Getter @Setter
	private Long  dateDone;

	/**
	 * Mark if the booking has been done
	 */
	@Column
	@Getter @Setter
	private boolean  done;

	/**
	 * Mark cost of the booking
	 */
	@Column
	@Getter @Setter
	private double cost;
}
