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
public class DestinationOfVisitor {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter @Setter
	private Long id;

	/**
	 * To hold the information on destination
	 * related
	 */
	@ManyToOne
	@Getter @Setter
	private Destination destination;

	/**
	 * Mark date visited
	 */
	@Column
	@Getter @Setter
	private Long date;

	/**
	 * Destination of visit can have many visitors
	 */
	@ManyToOne
	@Getter @Setter
	private Visitor visitor;
}
