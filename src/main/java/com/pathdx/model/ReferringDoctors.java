package com.pathdx.model;

import lombok.*;

import javax.persistence.*;
@Entity
@Data
@Table(name="PF_ReferringDoctors")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReferringDoctors extends BaseModel {

    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name="MiddleName")
    private  String middleName;

    @Column(name="LastName")
    private  String lastName;

    @Column(name="Suffix")
    private  String suffix;

    @Column(name="Contact")
    private  String contact;

}
