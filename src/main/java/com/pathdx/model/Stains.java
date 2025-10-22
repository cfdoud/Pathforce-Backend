package com.pathdx.model;


import lombok.*;

import javax.persistence.*;



@Entity
@Getter
@Setter
@Table(name="stains_master")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Stains {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Abbr")
    private String abbr;

    @Column(name = "StainType")
    private String stainType;

    @Column(name="Quantity")
    private String quantity;

    @Column(name="CptCode")
    private String cptCode;


}
