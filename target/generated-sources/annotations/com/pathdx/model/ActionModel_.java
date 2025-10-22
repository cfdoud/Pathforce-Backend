package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ActionModel.class)
public abstract class ActionModel_ {

	public static volatile SingularAttribute<ActionModel, String> actionType;
	public static volatile SingularAttribute<ActionModel, String> name;
	public static volatile SingularAttribute<ActionModel, String> description;
	public static volatile SingularAttribute<ActionModel, Long> id;

	public static final String ACTION_TYPE = "actionType";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String ID = "id";

}

