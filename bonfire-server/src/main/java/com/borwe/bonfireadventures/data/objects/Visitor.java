package com.borwe.bonfireadventures.data.objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Visitor{
    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    private Long id;

    @Getter @Setter
    @Column(unique = true)
    private String name;

    @Getter @Setter
    @Column(unique = true)
    private String phone;

    @Getter @Setter
    @Column(unique = true)
    private String g_ig;
    
    //Make id -1, as invalid unsaved state
    public Visitor() {
    	this.id=-1L;
    }
}
