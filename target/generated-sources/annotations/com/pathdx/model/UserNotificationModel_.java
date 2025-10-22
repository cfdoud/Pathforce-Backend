package com.pathdx.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(UserNotificationModel.class)
public abstract class UserNotificationModel_ {

	public static volatile SingularAttribute<UserNotificationModel, Long> notificationEventId;
	public static volatile SingularAttribute<UserNotificationModel, Boolean> viewed;
	public static volatile SingularAttribute<UserNotificationModel, Long> id;
	public static volatile SingularAttribute<UserNotificationModel, String> message;
	public static volatile SingularAttribute<UserNotificationModel, Long> userId;
	public static volatile SingularAttribute<UserNotificationModel, Date> createDate;

	public static final String NOTIFICATION_EVENT_ID = "notificationEventId";
	public static final String VIEWED = "viewed";
	public static final String ID = "id";
	public static final String MESSAGE = "message";
	public static final String USER_ID = "userId";
	public static final String CREATE_DATE = "createDate";

}

