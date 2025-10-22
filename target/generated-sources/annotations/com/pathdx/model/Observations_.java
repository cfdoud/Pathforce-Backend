package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Observations.class)
public abstract class Observations_ extends com.pathdx.model.BaseModel_ {

	public static volatile SingularAttribute<Observations, String> identifier;
	public static volatile SingularAttribute<Observations, OrderMessages> orderMessages;
	public static volatile SingularAttribute<Observations, Long> id;
	public static volatile SingularAttribute<Observations, String> value;

	public static final String IDENTIFIER = "identifier";
	public static final String ORDER_MESSAGES = "orderMessages";
	public static final String ID = "id";
	public static final String VALUE = "value";

}

