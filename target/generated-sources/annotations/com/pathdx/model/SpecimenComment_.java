package com.pathdx.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SpecimenComment.class)
public abstract class SpecimenComment_ {

	public static volatile SingularAttribute<SpecimenComment, Date> dateCreated;
	public static volatile SingularAttribute<SpecimenComment, Date> lastModifiedDate;
	public static volatile SingularAttribute<SpecimenComment, Long> slideDetailId;
	public static volatile SingularAttribute<SpecimenComment, String> comment;
	public static volatile SingularAttribute<SpecimenComment, Long> id;

	public static final String DATE_CREATED = "dateCreated";
	public static final String LAST_MODIFIED_DATE = "lastModifiedDate";
	public static final String SLIDE_DETAIL_ID = "slideDetailId";
	public static final String COMMENT = "comment";
	public static final String ID = "id";

}

