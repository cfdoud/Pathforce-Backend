package com.pathdx.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "notification_master")
public class NotificationEventModel {

    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "category")
    private String category;

    @Column(name = "event_code")
    private String eventCode;

    @Column(name = "description")
    private String description;

    @Column(name = "message")
    private String message;
}
