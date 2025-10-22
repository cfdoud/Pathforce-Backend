package com.pathdx.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(BaseModel.class)
public abstract class BaseModel_ {

	public static volatile SingularAttribute<BaseModel, Date> createdDate;
	public static volatile SingularAttribute<BaseModel, Date> lastModifiedDate;
	public static volatile SingularAttribute<BaseModel, String> createdBy;
	public static volatile SingularAttribute<BaseModel, String> lastModifiedBy;

	public static final String CREATED_DATE = "createdDate";
	public static final String LAST_MODIFIED_DATE = "lastModifiedDate";
	public static final String CREATED_BY = "createdBy";
	public static final String LAST_MODIFIED_BY = "lastModifiedBy";

}

