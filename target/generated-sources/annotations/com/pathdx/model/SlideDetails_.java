package com.pathdx.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SlideDetails.class)
public abstract class SlideDetails_ {

	public static volatile SingularAttribute<SlideDetails, String> scannedDate;
	public static volatile SingularAttribute<SlideDetails, Integer> rescanFlag;
	public static volatile SingularAttribute<SlideDetails, Date> lastModifiedDate;
	public static volatile SingularAttribute<SlideDetails, String> specimenId;
	public static volatile SingularAttribute<SlideDetails, String> stain;
	public static volatile SingularAttribute<SlideDetails, String> blockId;
	public static volatile SingularAttribute<SlideDetails, String> barCodeid;
	public static volatile SingularAttribute<SlideDetails, Date> createdDate;
	public static volatile SingularAttribute<SlideDetails, CaseDetails> caseDetails;
	public static volatile SingularAttribute<SlideDetails, String> comment;
	public static volatile SingularAttribute<SlideDetails, Long> id;
	public static volatile ListAttribute<SlideDetails, SpecimenComment> specimenComments;
	public static volatile ListAttribute<SlideDetails, BarCodeComment> barCodeComments;

	public static final String SCANNED_DATE = "scannedDate";
	public static final String RESCAN_FLAG = "rescanFlag";
	public static final String LAST_MODIFIED_DATE = "lastModifiedDate";
	public static final String SPECIMEN_ID = "specimenId";
	public static final String STAIN = "stain";
	public static final String BLOCK_ID = "blockId";
	public static final String BAR_CODEID = "barCodeid";
	public static final String CREATED_DATE = "createdDate";
	public static final String CASE_DETAILS = "caseDetails";
	public static final String COMMENT = "comment";
	public static final String ID = "id";
	public static final String SPECIMEN_COMMENTS = "specimenComments";
	public static final String BAR_CODE_COMMENTS = "barCodeComments";

}

