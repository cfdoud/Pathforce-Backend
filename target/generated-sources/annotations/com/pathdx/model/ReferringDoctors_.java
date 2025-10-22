package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ReferringDoctors.class)
public abstract class ReferringDoctors_ extends com.pathdx.model.BaseModel_ {

	public static volatile SingularAttribute<ReferringDoctors, String> firstName;
	public static volatile SingularAttribute<ReferringDoctors, String> lastName;
	public static volatile SingularAttribute<ReferringDoctors, String> contact;
	public static volatile SingularAttribute<ReferringDoctors, String> middleName;
	public static volatile SingularAttribute<ReferringDoctors, Long> id;
	public static volatile SingularAttribute<ReferringDoctors, String> suffix;

	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String CONTACT = "contact";
	public static final String MIDDLE_NAME = "middleName";
	public static final String ID = "id";
	public static final String SUFFIX = "suffix";

}

