package com.pathdx.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="PF_CaseComments")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaseComments {
    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="FirstAdditionalDiagnosis")
    private String firstAdditionalDiagnosis;

    @Column(name="TypeOfFirstDiagnosis")
    private String typeOfFirstDiagnosis;

    @Column(name="SecondAdditionalDiagnosis")
    private String secondAdditionalDiagnosis;

    @Column(name="TypeOfSecondDiagnosis")
    private String typeOfSecondDiagnosis;

    @Column(name="ThirdAdditionalDiagnosis")
    private String thirdAdditionalDiagnosis;

    @Column(name="TypeOfThirdDiagnosis")
    private String typeOfThirdDiagnosis;

    @Column(name="FinalDiagnosis")
    private  String finalDiagnosis;

    @Column(name="TypeOfFinalDiagnosis")
    private String typeOfFinalDiagnosis;

    @Column(name="Date_Created")
    private Date createdDate;

    @Column(name="Last_Modified_Date")
    private Date lastModifiedDate;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "CaseDetailId")
    private CaseDetails caseDetails;

}
