package com.pathdx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name="Lab_Headings")
@AllArgsConstructor
@NoArgsConstructor
public class LabHeadings {
    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Column(name="firstHeading")
    private String firstHeading;

    @Column(name="secondHeading")
    private String secondHeading;

    @Column(name="thirdHeading")
    private String thirdHeading;

    @Column(name="fourthHeading")
    private String fourthHeading;

    @Column(name="fifthHeading")
    private String fifthHeading;

    @Column(name="sixthHeading")
    private String sixthHeading;

    @Column(name="seventhHeading")
    private String seventhHeading;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "LabId",nullable = false)
    private LabDetail labDetail;

}
