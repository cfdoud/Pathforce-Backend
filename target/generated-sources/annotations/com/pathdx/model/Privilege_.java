package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Privilege.class)
public abstract class Privilege_ {

	public static volatile SingularAttribute<Privilege, String> privilageName;
	public static volatile CollectionAttribute<Privilege, Role> roles;
	public static volatile SingularAttribute<Privilege, Long> id;

	public static final String PRIVILAGE_NAME = "privilageName";
	public static final String ROLES = "roles";
	public static final String ID = "id";

}

