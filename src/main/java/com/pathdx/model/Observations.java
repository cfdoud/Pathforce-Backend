package com.pathdx.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "PF_Observations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Observations extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "Identifier")
    private String identifier;

    @Column(name = "Value")
    private String value;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="OrderMessageId",referencedColumnName = "Id" )
    private OrderMessages orderMessages;


}