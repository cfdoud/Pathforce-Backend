package com.pathdx.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
@Table(name="slide_stain_panel")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlideStainPanelModel {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "slide_id")
    private Long slideId;

    @Column(name = "stain_panel_id")
    private Long stainPanelId;

    @Column(name = "stain_type")
    private String stainType;

   /* @ManyToMany(mappedBy = "slideStainPanelModels")
    private Collection<SlideDetails> slideDetails;*/
}
