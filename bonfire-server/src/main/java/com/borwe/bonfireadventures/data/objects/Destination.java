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

/**
 *
 * @author brian
 */
@Entity
public class Destination {
	
	@Id
	@GeneratedValue(generator = "shit")
	private long id;

	@Column
	private String name;

	@ManyToOne
	private Location location;

	@Column(length = 10000)
	private String region;

	@Column
	private Long vists;

	@Column
	private double price;

	@ManyToOne
	private DestType destinationType;
}
