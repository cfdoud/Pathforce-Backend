package com.pathdx.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="PF_BarCodeComment")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BarCodeComment {
    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /*@NonNull
    @Column(name="SlideDetailId")
    private Long slideDetailId;*/

    @Column(name="Comment")
    private String comment;

    @Column(name="Date_Created")
    private Date createdDate;

    @Column(name="Last_Modified_Date")
    private Date lastModifiedDate;

    @Column(name="BarcodeID")
    private String barcodeID;

    @Column(name="BlockID")
    private String blockID;

    @Column(name="CaseDetailId")
    private Long caseDetailId;

    @Column(name="RescanFlag")
    private int RescanFlag;

    @Column(name="ScannedDate")
    private String scannedDate;

    @Column(name="SpecimenID")
    private String specimenID;

    @Column(name="Stain")
    private String stain;
}
