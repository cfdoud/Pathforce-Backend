package com.pathdx.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Data
@Table(name = "Case_Image_Audit")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaseImageAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "SEQUENCEId", nullable = false)
    private Long id;

    @Column(name = "LabId")
    private String labId;

    @Column(name = "AccessionId")
    private String accessionId;

    @Column(name = "CaseId")
    private String caseId;

    @Column(name = "BarcodeId")
    private String barcodeId;

    @Column(name = "SlideId")
    private String slideId;

    @Column(name = "BucketName")
    private String bucketName;

    @Column(name = "ImageCopied")
    private boolean imageCopied;

    @Column(name = "ImagePath")
    private String imagePath;

    @Column(name = "QualityCheckDone")
    private boolean qualityCheckDone;

    @Column(name = "ImageSourceDate")
    private String imageSourceDate;

    @Column(name = "TileGenerated")
    private boolean tileGenerated;

    @Column(name = "CaseStatus")
    private String caseStatus;

    @Column(name = "Date_Created")
    private String dateCreated;


    @Column(name = "Last_Modified_Date")
    private String lastModifiedDate;

    @Column(name = "Slide_Properties")
    private String slideProperties;

    @Column(name = "width")
    private String width;

    @Column(name = "height")
    private String height;

    @Column(name = "mpp")
    private String mpp;

}
