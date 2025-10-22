package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(UserNotificationSubscriptionModel.class)
public abstract class UserNotificationSubscriptionModel_ {

	public static volatile SingularAttribute<UserNotificationSubscriptionModel, Long> notificationEventId;
	public static volatile SingularAttribute<UserNotificationSubscriptionModel, Boolean> isSelect;
	public static volatile SingularAttribute<UserNotificationSubscriptionModel, Long> id;
	public static volatile SingularAttribute<UserNotificationSubscriptionModel, Long> userId;

	public static final String NOTIFICATION_EVENT_ID = "notificationEventId";
	public static final String IS_SELECT = "isSelect";
	public static final String ID = "id";
	public static final String USER_ID = "userId";

}

