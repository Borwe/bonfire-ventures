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
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author brian
 */
@Entity
public class DestinationPayment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter @Setter
	private Long id;	

	/**
	 * For destination visiting
	 */
	@OneToOne
	@Getter @Setter
	private DestinationOfVisitor destVisitor;

	/**
	 * For checking if user has accepted, paid, and is waiting to go
	 * or has already gone
	 */
	@Column
	@Getter @Setter
	private boolean  verified;
}
