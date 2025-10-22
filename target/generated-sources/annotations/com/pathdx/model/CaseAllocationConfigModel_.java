package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(CaseAllocationConfigModel.class)
public abstract class CaseAllocationConfigModel_ {

	public static volatile SingularAttribute<CaseAllocationConfigModel, Integer> maxNumberOfCases;
	public static volatile SingularAttribute<CaseAllocationConfigModel, UserModel> userModel;
	public static volatile SingularAttribute<CaseAllocationConfigModel, Long> id;
	public static volatile SingularAttribute<CaseAllocationConfigModel, Integer> maxPendingDays;

	public static final String MAX_NUMBER_OF_CASES = "maxNumberOfCases";
	public static final String USER_MODEL = "userModel";
	public static final String ID = "id";
	public static final String MAX_PENDING_DAYS = "maxPendingDays";

}

