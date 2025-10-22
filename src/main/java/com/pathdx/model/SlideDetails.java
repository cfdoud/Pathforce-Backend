package com.pathdx.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name="PF_SlideDetails")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlideDetails {
    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="CaseDetailId", referencedColumnName = "Id", nullable = false)
    private CaseDetails caseDetails;

    @Column(name="SpecimenID")
    private String specimenId;

    @Column(name="RescanFlag")
    private int rescanFlag;

    @OneToMany
    @JoinColumn(name="SlideDetailId", referencedColumnName="Id")
    private List<BarCodeComment> barCodeComments;

    @OneToMany(cascade = CascadeType.ALL)
    @NotNull
    @JoinColumn(name="SlideDetailId", referencedColumnName="Id")
    private List<SpecimenComment> specimenComments;

    @Column(name="BarcodeID")
    private String barCodeid;

    @Column(name="BlockID")
    private String blockId;

    @Column(name="Stain")
    private String stain;

    @Column(name="Comment")
    private String comment;

    @Column(name="ScannedDate")
    private String scannedDate;

    @Column(name="Date_Created")
    private Date createdDate;

    @Column(name="Last_Modified_Date")
    private Date lastModifiedDate;

}
