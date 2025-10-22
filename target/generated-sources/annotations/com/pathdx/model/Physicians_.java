package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Physicians.class)
public abstract class Physicians_ extends com.pathdx.model.BaseModel_ {

	public static volatile SingularAttribute<Physicians, String> lastName;
	public static volatile SingularAttribute<Physicians, String> phone;
	public static volatile SingularAttribute<Physicians, String> FirstName;
	public static volatile SingularAttribute<Physicians, String> contact;
	public static volatile SingularAttribute<Physicians, OrderMessages> orderMessages;
	public static volatile SingularAttribute<Physicians, String> middleName;
	public static volatile SingularAttribute<Physicians, Long> id;

	public static final String LAST_NAME = "lastName";
	public static final String PHONE = "phone";
	public static final String FIRST_NAME = "FirstName";
	public static final String CONTACT = "contact";
	public static final String ORDER_MESSAGES = "orderMessages";
	public static final String MIDDLE_NAME = "middleName";
	public static final String ID = "id";

}

