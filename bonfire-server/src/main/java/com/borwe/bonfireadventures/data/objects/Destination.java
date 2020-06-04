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
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author brian
 */
@Entity
public class Destination {
	
	@Id
	@GeneratedValue(generator = "shit")
	@Getter @Setter
	private long id;

	@Column
	@Getter @Setter
	private String name;

	@ManyToOne
	@Getter @Setter
	private Location location;

	@Column(length = 10000)
	@Getter @Setter
	private String region;

	@Column
	@Getter @Setter
	private Long vists;

	@Column
	@Getter @Setter
	private double price;

	@ManyToOne
	@Getter @Setter
	private DestType destinationType;
}
