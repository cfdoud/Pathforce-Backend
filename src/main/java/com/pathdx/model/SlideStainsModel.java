package com.pathdx.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
@Table(name="slide_stains")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlideStainsModel {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "slide_id")
    private Long slideId;

    @Column(name = "stain_id", length = 2000)
    private String stainId;


    @Column(name = "slide_comment")
    private String comment;

}
