package com.pathdx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name="PF_CaseDetails")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaseDetails extends BaseModel {
    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="CaseId")
    private String caseId;

    @Column(name="SubmissionId")
    private Long submissionId;

    @Column(name="ReportGeneratedBy")
    private String reportGeneratedBy;

    @Column(name="ReportGeneratedDate")
    private Date reportGeneratedDate;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="CaseDetailId", referencedColumnName = "Id")
    private List<SlideDetails> slideDetails;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="OrderMessageId",referencedColumnName = "Id" )
    private OrderMessages orderMessages;
}
