/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borwe.bonfireadventures.data.objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author brian
 */
@Entity
public class Location {
	
	@Id
	private Long id;

	@Column @Getter @Setter
	private double loc_x;

	@Column @Getter @Setter
	private double loc_y;
}
