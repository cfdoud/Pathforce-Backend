package com.pathdx.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AuditLogModel.class)
public abstract class AuditLogModel_ {

	public static volatile SingularAttribute<AuditLogModel, UserModel> userModel;
	public static volatile SingularAttribute<AuditLogModel, Date> dateCreated;
	public static volatile SingularAttribute<AuditLogModel, String> createdBy;
	public static volatile SingularAttribute<AuditLogModel, ActionModel> actionModel;
	public static volatile SingularAttribute<AuditLogModel, CaseDetails> caseDetails;
	public static volatile SingularAttribute<AuditLogModel, String> description;
	public static volatile SingularAttribute<AuditLogModel, Long> id;
	public static volatile SingularAttribute<AuditLogModel, LabDetail> labDetail;
	public static volatile SingularAttribute<AuditLogModel, String> accessionId;

	public static final String USER_MODEL = "userModel";
	public static final String DATE_CREATED = "dateCreated";
	public static final String CREATED_BY = "createdBy";
	public static final String ACTION_MODEL = "actionModel";
	public static final String CASE_DETAILS = "caseDetails";
	public static final String DESCRIPTION = "description";
	public static final String ID = "id";
	public static final String LAB_DETAIL = "labDetail";
	public static final String ACCESSION_ID = "accessionId";

}

