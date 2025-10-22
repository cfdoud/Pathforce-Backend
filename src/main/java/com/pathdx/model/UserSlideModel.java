package com.pathdx.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="user_slide")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSlideModel extends BaseModel {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserModel userModel;

    /*@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "barcode_id", referencedColumnName = "BarcodeID", nullable = false)*/
    @Column(name = "barcode_id", nullable = false)
    private String barcodeId;

    @Column(name = "lab_id", nullable = false)
    private String labId;



}
