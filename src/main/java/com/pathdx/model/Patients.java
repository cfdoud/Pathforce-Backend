package com.pathdx.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="PF_Patients")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Patients extends BaseModel{
    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="SequenceId")
    private int sequenceId;

    @Column(name="PatientId")
    private String patientId;

    @Column(name="MRN")
    private String mrn;

    @Column(name="AlternatePatientId")
    private String alternatePatientId;

    @Column(name="SSN")
    private String ssn;

    @Column(name="AccountNumber")
    private String accountNumber;

    @Column(name="Prefix")
    private String prefix;

    @Column(name="FirstName")
    private String firstName;

    @Column(name="MiddleName")
    private String middleName;

    @Column(name="LastName")
    private String lastName;

    @Column(name="Suffix")
    private String suffix;

    @Column(name="Dob")
    private String dob;

    @Column(name="Gender")
    private String gender;

    @Column(name="Ethnicity")
    private String ethnicity;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderMessageId")
    private OrderMessages orderMessages;

    /*@Formula( "group_concat(firstName,' ',middleName, ' ', lastName)"   )
    private String patientName;*/
}
