package com.pathdx.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "case_order")
public class CaseOrderModel {

    @Id
    @Column(name = "SEQUENCEId")
    private Long id;

    @Column(name = "LabId")
    private String labId;

    @Column(name = "AccessionId")
    private Long accessionId;

    @Column(name = "MessageType")
    private String messageType;

    @Column(name = "OperationType")
    private String operationType;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
