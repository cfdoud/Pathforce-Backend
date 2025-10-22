package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(CaseImageAudit.class)
public abstract class CaseImageAudit_ {

	public static volatile SingularAttribute<CaseImageAudit, String> bucketName;
	public static volatile SingularAttribute<CaseImageAudit, String> slideId;
	public static volatile SingularAttribute<CaseImageAudit, String> lastModifiedDate;
	public static volatile SingularAttribute<CaseImageAudit, String> imagePath;
	public static volatile SingularAttribute<CaseImageAudit, String> caseStatus;
	public static volatile SingularAttribute<CaseImageAudit, Boolean> imageCopied;
	public static volatile SingularAttribute<CaseImageAudit, String> mpp;
	public static volatile SingularAttribute<CaseImageAudit, Boolean> qualityCheckDone;
	public static volatile SingularAttribute<CaseImageAudit, String> dateCreated;
	public static volatile SingularAttribute<CaseImageAudit, String> imageSourceDate;
	public static volatile SingularAttribute<CaseImageAudit, String> labId;
	public static volatile SingularAttribute<CaseImageAudit, String> caseId;
	public static volatile SingularAttribute<CaseImageAudit, String> width;
	public static volatile SingularAttribute<CaseImageAudit, String> slideProperties;
	public static volatile SingularAttribute<CaseImageAudit, Long> id;
	public static volatile SingularAttribute<CaseImageAudit, Boolean> tileGenerated;
	public static volatile SingularAttribute<CaseImageAudit, String> accessionId;
	public static volatile SingularAttribute<CaseImageAudit, String> barcodeId;
	public static volatile SingularAttribute<CaseImageAudit, String> height;

	public static final String BUCKET_NAME = "bucketName";
	public static final String SLIDE_ID = "slideId";
	public static final String LAST_MODIFIED_DATE = "lastModifiedDate";
	public static final String IMAGE_PATH = "imagePath";
	public static final String CASE_STATUS = "caseStatus";
	public static final String IMAGE_COPIED = "imageCopied";
	public static final String MPP = "mpp";
	public static final String QUALITY_CHECK_DONE = "qualityCheckDone";
	public static final String DATE_CREATED = "dateCreated";
	public static final String IMAGE_SOURCE_DATE = "imageSourceDate";
	public static final String LAB_ID = "labId";
	public static final String CASE_ID = "caseId";
	public static final String WIDTH = "width";
	public static final String SLIDE_PROPERTIES = "slideProperties";
	public static final String ID = "id";
	public static final String TILE_GENERATED = "tileGenerated";
	public static final String ACCESSION_ID = "accessionId";
	public static final String BARCODE_ID = "barcodeId";
	public static final String HEIGHT = "height";

}

