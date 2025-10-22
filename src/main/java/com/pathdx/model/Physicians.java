package com.pathdx.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="PF_Physicians")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Physicians extends BaseModel {
    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

   /* @OneToOne(fetch = FetchType.LAZY,optional = false)
    @MapsId
    @JoinColumn(name = "OrderMessageId")
    private OrderMessages orderMessages;*/

    @Column(name="firstName")
    private  String FirstName;

    @Column(name="MiddleName")
    private  String middleName;

    @Column(name="LastName")
    private  String lastName;

    @Column(name="Phone")
    private  String phone;

    @Column(name="Contact")
    private  String contact;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderMessageId",nullable = false)
    private OrderMessages orderMessages;

   }
