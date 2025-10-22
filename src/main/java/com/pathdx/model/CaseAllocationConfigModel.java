package com.pathdx.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name="Case_Allocation_Settings")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaseAllocationConfigModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Id")
    private Long id;

    @Column(name="MaxNumberOfCases")
    private int maxNumberOfCases;

    @Column(name="MaxPendingDays")
    private int maxPendingDays;

    @OneToOne
    @JoinColumn(name = "user_id",nullable = false)
    private UserModel userModel;

}
