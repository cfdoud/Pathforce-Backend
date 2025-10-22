package com.pathdx.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(BarCodeComment.class)
public abstract class BarCodeComment_ {

	public static volatile SingularAttribute<BarCodeComment, String> blockID;
	public static volatile SingularAttribute<BarCodeComment, Date> createdDate;
	public static volatile SingularAttribute<BarCodeComment, Long> caseDetailId;
	public static volatile SingularAttribute<BarCodeComment, String> scannedDate;
	public static volatile SingularAttribute<BarCodeComment, Date> lastModifiedDate;
	public static volatile SingularAttribute<BarCodeComment, Integer> RescanFlag;
	public static volatile SingularAttribute<BarCodeComment, String> specimenID;
	public static volatile SingularAttribute<BarCodeComment, String> comment;
	public static volatile SingularAttribute<BarCodeComment, Long> id;
	public static volatile SingularAttribute<BarCodeComment, String> barcodeID;
	public static volatile SingularAttribute<BarCodeComment, String> stain;

	public static final String BLOCK_ID = "blockID";
	public static final String CREATED_DATE = "createdDate";
	public static final String CASE_DETAIL_ID = "caseDetailId";
	public static final String SCANNED_DATE = "scannedDate";
	public static final String LAST_MODIFIED_DATE = "lastModifiedDate";
	public static final String RESCAN_FLAG = "RescanFlag";
	public static final String SPECIMEN_ID = "specimenID";
	public static final String COMMENT = "comment";
	public static final String ID = "id";
	public static final String BARCODE_ID = "barcodeID";
	public static final String STAIN = "stain";

}

