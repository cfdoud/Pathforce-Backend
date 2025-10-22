package com.pathdx.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="user_notification_subscription")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserNotificationSubscriptionModel {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "notification_id")
    private Long notificationEventId;
    @Column(name = "isSelect")
    private boolean isSelect;
}
