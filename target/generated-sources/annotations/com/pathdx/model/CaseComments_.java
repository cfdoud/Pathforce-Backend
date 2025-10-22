package com.pathdx.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(CaseComments.class)
public abstract class CaseComments_ {

	public static volatile SingularAttribute<CaseComments, String> typeOfFirstDiagnosis;
	public static volatile SingularAttribute<CaseComments, Date> createdDate;
	public static volatile SingularAttribute<CaseComments, String> finalDiagnosis;
	public static volatile SingularAttribute<CaseComments, Date> lastModifiedDate;
	public static volatile SingularAttribute<CaseComments, String> secondAdditionalDiagnosis;
	public static volatile SingularAttribute<CaseComments, String> typeOfFinalDiagnosis;
	public static volatile SingularAttribute<CaseComments, CaseDetails> caseDetails;
	public static volatile SingularAttribute<CaseComments, String> typeOfSecondDiagnosis;
	public static volatile SingularAttribute<CaseComments, Long> id;
	public static volatile SingularAttribute<CaseComments, String> thirdAdditionalDiagnosis;
	public static volatile SingularAttribute<CaseComments, String> firstAdditionalDiagnosis;
	public static volatile SingularAttribute<CaseComments, String> typeOfThirdDiagnosis;

	public static final String TYPE_OF_FIRST_DIAGNOSIS = "typeOfFirstDiagnosis";
	public static final String CREATED_DATE = "createdDate";
	public static final String FINAL_DIAGNOSIS = "finalDiagnosis";
	public static final String LAST_MODIFIED_DATE = "lastModifiedDate";
	public static final String SECOND_ADDITIONAL_DIAGNOSIS = "secondAdditionalDiagnosis";
	public static final String TYPE_OF_FINAL_DIAGNOSIS = "typeOfFinalDiagnosis";
	public static final String CASE_DETAILS = "caseDetails";
	public static final String TYPE_OF_SECOND_DIAGNOSIS = "typeOfSecondDiagnosis";
	public static final String ID = "id";
	public static final String THIRD_ADDITIONAL_DIAGNOSIS = "thirdAdditionalDiagnosis";
	public static final String FIRST_ADDITIONAL_DIAGNOSIS = "firstAdditionalDiagnosis";
	public static final String TYPE_OF_THIRD_DIAGNOSIS = "typeOfThirdDiagnosis";

}

