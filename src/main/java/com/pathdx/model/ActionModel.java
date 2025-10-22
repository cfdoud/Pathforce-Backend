package com.pathdx.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name="action_master")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActionModel {
    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "description")
    private String description;
}
