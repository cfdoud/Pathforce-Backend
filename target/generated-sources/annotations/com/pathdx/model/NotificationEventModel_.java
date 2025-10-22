package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(NotificationEventModel.class)
public abstract class NotificationEventModel_ {

	public static volatile SingularAttribute<NotificationEventModel, String> eventCode;
	public static volatile SingularAttribute<NotificationEventModel, String> description;
	public static volatile SingularAttribute<NotificationEventModel, Long> id;
	public static volatile SingularAttribute<NotificationEventModel, String> category;
	public static volatile SingularAttribute<NotificationEventModel, String> message;

	public static final String EVENT_CODE = "eventCode";
	public static final String DESCRIPTION = "description";
	public static final String ID = "id";
	public static final String CATEGORY = "category";
	public static final String MESSAGE = "message";

}

