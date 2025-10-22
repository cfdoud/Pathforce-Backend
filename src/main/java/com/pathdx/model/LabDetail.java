package com.pathdx.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name="Lab_Detail")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LabDetail extends BaseModel {
    @Id
    @Column(name = "LabId")
    private String labid;

    @Column(name="LabName")
    private String labName;

    @Column(name="UserName")
    private String userName;

    @Column(name="Password")
    private String password;

    @Column(name="APIKey")
    private String apiKey;

    @Column(name="LabContactNo")
    private String labContactNo;

    @Column(name="LabEmail")
    private String labEmail;

    @Column(name="LabWebsite")
    private String labWebsite;

    @Column(name="LabRegistrationNo")
    private String labRegistrationNo;

    @Column(name="LabRegistrationDocument")
    private String labRegistrationDocument;

    @Column(name="street")
    private String street;
    @Column(name="city")
    private String city;

    @Column(name="state")
    private String state;

    @Column(name="zip")
    private Long zip;


    @OneToMany(cascade = CascadeType.ALL)
    @NotNull
    @JoinColumn(name = "LabId", referencedColumnName = "LabId")
    private List<CaseDetails> caseDetails;

    }
