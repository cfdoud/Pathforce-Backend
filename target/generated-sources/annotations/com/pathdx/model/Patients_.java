package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Patients.class)
public abstract class Patients_ extends com.pathdx.model.BaseModel_ {

	public static volatile SingularAttribute<Patients, String> lastName;
	public static volatile SingularAttribute<Patients, String> gender;
	public static volatile SingularAttribute<Patients, String> ethnicity;
	public static volatile SingularAttribute<Patients, String> patientId;
	public static volatile SingularAttribute<Patients, String> prefix;
	public static volatile SingularAttribute<Patients, String> mrn;
	public static volatile SingularAttribute<Patients, String> alternatePatientId;
	public static volatile SingularAttribute<Patients, String> accountNumber;
	public static volatile SingularAttribute<Patients, String> suffix;
	public static volatile SingularAttribute<Patients, Integer> sequenceId;
	public static volatile SingularAttribute<Patients, String> ssn;
	public static volatile SingularAttribute<Patients, String> firstName;
	public static volatile SingularAttribute<Patients, String> dob;
	public static volatile SingularAttribute<Patients, OrderMessages> orderMessages;
	public static volatile SingularAttribute<Patients, String> middleName;
	public static volatile SingularAttribute<Patients, Long> id;

	public static final String LAST_NAME = "lastName";
	public static final String GENDER = "gender";
	public static final String ETHNICITY = "ethnicity";
	public static final String PATIENT_ID = "patientId";
	public static final String PREFIX = "prefix";
	public static final String MRN = "mrn";
	public static final String ALTERNATE_PATIENT_ID = "alternatePatientId";
	public static final String ACCOUNT_NUMBER = "accountNumber";
	public static final String SUFFIX = "suffix";
	public static final String SEQUENCE_ID = "sequenceId";
	public static final String SSN = "ssn";
	public static final String FIRST_NAME = "firstName";
	public static final String DOB = "dob";
	public static final String ORDER_MESSAGES = "orderMessages";
	public static final String MIDDLE_NAME = "middleName";
	public static final String ID = "id";

}

