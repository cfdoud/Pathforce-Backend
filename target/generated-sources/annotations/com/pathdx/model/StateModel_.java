package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(StateModel.class)
public abstract class StateModel_ {

	public static volatile ListAttribute<StateModel, UserModel> licensedStates;
	public static volatile SingularAttribute<StateModel, String> stateName;
	public static volatile SingularAttribute<StateModel, Long> id;
	public static volatile SingularAttribute<StateModel, String> sort_name;

	public static final String LICENSED_STATES = "licensedStates";
	public static final String STATE_NAME = "stateName";
	public static final String ID = "id";
	public static final String SORT_NAME = "sort_name";

}

