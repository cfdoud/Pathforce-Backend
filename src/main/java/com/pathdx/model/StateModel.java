package com.pathdx.model;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "state_master")
public class StateModel {
    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "StateName")
    private String stateName;

    @Column(name = "SortName")
    private String sort_name;


    @ManyToMany(mappedBy = "roles")
    private List<UserModel> licensedStates;


}
