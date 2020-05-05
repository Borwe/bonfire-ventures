package com.borwe.bonfireadventures.data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Entity;

import lombok.Data;

@Data
@Entity
public class Visitor{
    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private String phone;

    @Column
    private String g_ig;
}
