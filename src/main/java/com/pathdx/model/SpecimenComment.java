package com.pathdx.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="PF_SpecimenComment")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpecimenComment {
    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    @Column(name="SlideDetailId")
    private Long slideDetailId;

    @Column(name="Comment")
    private String comment;

    @Column(name="Date_Created")
    private Date dateCreated;

    @Column(name="Last_Modified_Date")
    private Date lastModifiedDate;
}
