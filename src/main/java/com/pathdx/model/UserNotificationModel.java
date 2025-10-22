package com.pathdx.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="user_notifications")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserNotificationModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "notification_id")
    private Long notificationEventId;

    @Column(name = "message")
    private String message;

    @Column(name = "created_date")
    private Date createDate;

    @Column(name = "viewed")
    private boolean viewed;
}
