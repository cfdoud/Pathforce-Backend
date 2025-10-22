package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(LabDetail.class)
public abstract class LabDetail_ extends com.pathdx.model.BaseModel_ {

	public static volatile SingularAttribute<LabDetail, Long> zip;
	public static volatile SingularAttribute<LabDetail, String> labWebsite;
	public static volatile SingularAttribute<LabDetail, String> apiKey;
	public static volatile SingularAttribute<LabDetail, String> city;
	public static volatile SingularAttribute<LabDetail, String> labRegistrationNo;
	public static volatile SingularAttribute<LabDetail, String> labName;
	public static volatile SingularAttribute<LabDetail, String> userName;
	public static volatile SingularAttribute<LabDetail, String> password;
	public static volatile SingularAttribute<LabDetail, String> labContactNo;
	public static volatile SingularAttribute<LabDetail, String> labRegistrationDocument;
	public static volatile SingularAttribute<LabDetail, String> street;
	public static volatile SingularAttribute<LabDetail, String> labid;
	public static volatile ListAttribute<LabDetail, CaseDetails> caseDetails;
	public static volatile SingularAttribute<LabDetail, String> labEmail;
	public static volatile SingularAttribute<LabDetail, String> state;

	public static final String ZIP = "zip";
	public static final String LAB_WEBSITE = "labWebsite";
	public static final String API_KEY = "apiKey";
	public static final String CITY = "city";
	public static final String LAB_REGISTRATION_NO = "labRegistrationNo";
	public static final String LAB_NAME = "labName";
	public static final String USER_NAME = "userName";
	public static final String PASSWORD = "password";
	public static final String LAB_CONTACT_NO = "labContactNo";
	public static final String LAB_REGISTRATION_DOCUMENT = "labRegistrationDocument";
	public static final String STREET = "street";
	public static final String LABID = "labid";
	public static final String CASE_DETAILS = "caseDetails";
	public static final String LAB_EMAIL = "labEmail";
	public static final String STATE = "state";

}

