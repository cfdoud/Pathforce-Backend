package com.pathdx.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="PF_OrderMessages")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderMessages extends BaseModel{

    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="Npi")
    private  String npi;

    @Column(name="EmailID")
    private String emailId;

    @Column(name="AssignedBy")
    private String assignedBy;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "LabId",nullable = false)
    private LabDetail labDetail;

    @Column(name="AccessionID")
    private String accessionId;

    @Column(name="CaseAcct")
    private String caseAcct;

    @Column(name="Hospital")
    private String hospital;

    @Column(name="CaseStatus")
    private String caseStatus;

    @Column(name="OrderControl")
    private String orderControl;

    @Column(name="AssignedDate")
    private Date assignedDate;

    @Column(name="IsScanned")
    private String isScanned;

    @Column(name="MessageType")
    private String messageType;

    @Column(name="OperationType")
    private String operationType;

    @Column(name="DateReported")
    private Date dateReported;

    @Column(name="SubmissionId")
    private String submissionId;

    @Column(name="ClosedBy")
    private String closedBy;

    @Column(name="ClosedDate")
    private Date closedDate;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderMessageId",referencedColumnName = "Id", insertable=false, updatable=false)
    private ReferringDoctors referringDoctors;

    @OneToOne(mappedBy = "orderMessages")
    @JsonIgnore
    private Patients patients;

    @OneToOne(mappedBy = "orderMessages")
    @JsonIgnore
    private Physicians physicians;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinTable(name="user_order_messages",joinColumns = @JoinColumn(name="order_message_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserModel> userModels = new HashSet<>();

    @Column(name = "ReportGeneratedBy")
    private String reportGeneratedBy;

    @Column(name = "ReportGeneratedDate")
    private Date reportGeneratedDate;
}
