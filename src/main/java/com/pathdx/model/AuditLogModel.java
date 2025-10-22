package com.pathdx.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="audit_log")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditLogModel {

    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserModel userModel;

    @ManyToOne
    @JoinColumn(name = "action_id", referencedColumnName = "id")
    private ActionModel actionModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", referencedColumnName = "labid")
    private LabDetail labDetail;

    /*@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_message_id", referencedColumnName = "id")
    private OrderMessages orderMessages;*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", referencedColumnName = "id")
    private CaseDetails caseDetails;

    @Column(name = "accession_id")
    private String accessionId;

    @Column(name = "description")
    private String description;

    @Column(name = "date_created")
    private Date dateCreated;

    @Column(name = "created_by")
    private String createdBy;
}
