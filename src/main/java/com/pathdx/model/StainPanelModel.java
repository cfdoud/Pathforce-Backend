package com.pathdx.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="stain_panel_master")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StainPanelModel {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "stain_type")
    private String stainType;
}
