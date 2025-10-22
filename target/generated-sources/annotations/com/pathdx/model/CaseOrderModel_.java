package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(CaseOrderModel.class)
public abstract class CaseOrderModel_ {

	public static volatile SingularAttribute<CaseOrderModel, String> messageType;
	public static volatile SingularAttribute<CaseOrderModel, String> labId;
	public static volatile SingularAttribute<CaseOrderModel, String> operationType;
	public static volatile SingularAttribute<CaseOrderModel, Long> id;
	public static volatile SingularAttribute<CaseOrderModel, Long> accessionId;

	public static final String MESSAGE_TYPE = "messageType";
	public static final String LAB_ID = "labId";
	public static final String OPERATION_TYPE = "operationType";
	public static final String ID = "id";
	public static final String ACCESSION_ID = "accessionId";

}

