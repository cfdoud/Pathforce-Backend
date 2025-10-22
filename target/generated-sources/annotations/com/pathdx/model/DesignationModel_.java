package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(DesignationModel.class)
public abstract class DesignationModel_ {

	public static volatile SingularAttribute<DesignationModel, String> designationName;
	public static volatile SingularAttribute<DesignationModel, Long> id;
	public static volatile CollectionAttribute<DesignationModel, UserModel> users;

	public static final String DESIGNATION_NAME = "designationName";
	public static final String ID = "id";
	public static final String USERS = "users";

}

