package com.pathdx.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "activity_log")
public class ActivityLog extends BaseModel {
    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="Name")
    String userName;

    @Column(name="Email")
    String email;

    @Column(name="AccessionId")
    String accessionId;

    @Column(name="CaseStatus")
    String caseStatus;

    @Column(name="ActionType")
    String action;

    @Column(name="Description")
    String description;

    @Column(name="ReqBody")
    String resBody;

}
