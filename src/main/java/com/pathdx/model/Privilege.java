package com.pathdx.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
@Table(name="privilege_info")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Privilege {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="PrivilegeName")
    private String privilageName;

    @ManyToMany(mappedBy = "privileges")
    private Collection<Role> roles;
}
