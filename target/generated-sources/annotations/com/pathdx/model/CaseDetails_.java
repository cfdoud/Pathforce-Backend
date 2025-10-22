package com.pathdx.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(CaseDetails.class)
public abstract class CaseDetails_ extends com.pathdx.model.BaseModel_ {

	public static volatile SingularAttribute<CaseDetails, Long> submissionId;
	public static volatile SingularAttribute<CaseDetails, String> reportGeneratedBy;
	public static volatile SingularAttribute<CaseDetails, String> caseId;
	public static volatile SingularAttribute<CaseDetails, OrderMessages> orderMessages;
	public static volatile ListAttribute<CaseDetails, SlideDetails> slideDetails;
	public static volatile SingularAttribute<CaseDetails, Long> id;
	public static volatile SingularAttribute<CaseDetails, Date> reportGeneratedDate;

	public static final String SUBMISSION_ID = "submissionId";
	public static final String REPORT_GENERATED_BY = "reportGeneratedBy";
	public static final String CASE_ID = "caseId";
	public static final String ORDER_MESSAGES = "orderMessages";
	public static final String SLIDE_DETAILS = "slideDetails";
	public static final String ID = "id";
	public static final String REPORT_GENERATED_DATE = "reportGeneratedDate";

}

