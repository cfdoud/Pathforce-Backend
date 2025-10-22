package com.pathdx.model;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "designation_master")
public class DesignationModel {

    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "designation_name")
    private String designationName;

    @ManyToMany(mappedBy = "designation")
    private Collection<UserModel> users;
}
