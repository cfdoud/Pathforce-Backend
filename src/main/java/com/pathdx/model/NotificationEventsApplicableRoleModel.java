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
@Table(name = "notification_role")
public class NotificationEventsApplicableRoleModel {

    @Id
    @Column(name="Id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "role_id")
    private Long roleId;
    @Column(name = "notification_id")
    private Long notificationEventId;

}
