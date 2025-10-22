package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ActivityLog.class)
public abstract class ActivityLog_ extends com.pathdx.model.BaseModel_ {

	public static volatile SingularAttribute<ActivityLog, String> caseStatus;
	public static volatile SingularAttribute<ActivityLog, String> resBody;
	public static volatile SingularAttribute<ActivityLog, String> action;
	public static volatile SingularAttribute<ActivityLog, String> description;
	public static volatile SingularAttribute<ActivityLog, Long> id;
	public static volatile SingularAttribute<ActivityLog, String> userName;
	public static volatile SingularAttribute<ActivityLog, String> email;
	public static volatile SingularAttribute<ActivityLog, String> accessionId;

	public static final String CASE_STATUS = "caseStatus";
	public static final String RES_BODY = "resBody";
	public static final String ACTION = "action";
	public static final String DESCRIPTION = "description";
	public static final String ID = "id";
	public static final String USER_NAME = "userName";
	public static final String EMAIL = "email";
	public static final String ACCESSION_ID = "accessionId";

}

