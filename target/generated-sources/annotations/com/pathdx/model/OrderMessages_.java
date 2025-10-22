package com.pathdx.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(OrderMessages.class)
public abstract class OrderMessages_ extends com.pathdx.model.BaseModel_ {

	public static volatile SingularAttribute<OrderMessages, Date> assignedDate;
	public static volatile SingularAttribute<OrderMessages, String> closedBy;
	public static volatile SingularAttribute<OrderMessages, ReferringDoctors> referringDoctors;
	public static volatile SingularAttribute<OrderMessages, String> assignedBy;
	public static volatile SingularAttribute<OrderMessages, String> reportGeneratedBy;
	public static volatile SingularAttribute<OrderMessages, String> npi;
	public static volatile SingularAttribute<OrderMessages, String> caseStatus;
	public static volatile SingularAttribute<OrderMessages, Patients> patients;
	public static volatile SingularAttribute<OrderMessages, String> emailId;
	public static volatile SingularAttribute<OrderMessages, String> orderControl;
	public static volatile SetAttribute<OrderMessages, UserModel> userModels;
	public static volatile SingularAttribute<OrderMessages, String> submissionId;
	public static volatile SingularAttribute<OrderMessages, Physicians> physicians;
	public static volatile SingularAttribute<OrderMessages, String> messageType;
	public static volatile SingularAttribute<OrderMessages, Date> closedDate;
	public static volatile SingularAttribute<OrderMessages, Date> dateReported;
	public static volatile SingularAttribute<OrderMessages, String> operationType;
	public static volatile SingularAttribute<OrderMessages, Long> id;
	public static volatile SingularAttribute<OrderMessages, LabDetail> labDetail;
	public static volatile SingularAttribute<OrderMessages, String> hospital;
	public static volatile SingularAttribute<OrderMessages, String> accessionId;
	public static volatile SingularAttribute<OrderMessages, String> isScanned;
	public static volatile SingularAttribute<OrderMessages, Date> reportGeneratedDate;
	public static volatile SingularAttribute<OrderMessages, String> caseAcct;

	public static final String ASSIGNED_DATE = "assignedDate";
	public static final String CLOSED_BY = "closedBy";
	public static final String REFERRING_DOCTORS = "referringDoctors";
	public static final String ASSIGNED_BY = "assignedBy";
	public static final String REPORT_GENERATED_BY = "reportGeneratedBy";
	public static final String NPI = "npi";
	public static final String CASE_STATUS = "caseStatus";
	public static final String PATIENTS = "patients";
	public static final String EMAIL_ID = "emailId";
	public static final String ORDER_CONTROL = "orderControl";
	public static final String USER_MODELS = "userModels";
	public static final String SUBMISSION_ID = "submissionId";
	public static final String PHYSICIANS = "physicians";
	public static final String MESSAGE_TYPE = "messageType";
	public static final String CLOSED_DATE = "closedDate";
	public static final String DATE_REPORTED = "dateReported";
	public static final String OPERATION_TYPE = "operationType";
	public static final String ID = "id";
	public static final String LAB_DETAIL = "labDetail";
	public static final String HOSPITAL = "hospital";
	public static final String ACCESSION_ID = "accessionId";
	public static final String IS_SCANNED = "isScanned";
	public static final String REPORT_GENERATED_DATE = "reportGeneratedDate";
	public static final String CASE_ACCT = "caseAcct";

}

