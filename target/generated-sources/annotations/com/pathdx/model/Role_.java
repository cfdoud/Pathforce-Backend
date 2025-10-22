package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Role.class)
public abstract class Role_ {

	public static volatile ListAttribute<Role, Privilege> privileges;
	public static volatile SingularAttribute<Role, String> roleName;
	public static volatile SingularAttribute<Role, Long> id;
	public static volatile CollectionAttribute<Role, UserModel> users;

	public static final String PRIVILEGES = "privileges";
	public static final String ROLE_NAME = "roleName";
	public static final String ID = "id";
	public static final String USERS = "users";

}

