package com.pathdx.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(UserSlideModel.class)
public abstract class UserSlideModel_ extends com.pathdx.model.BaseModel_ {

	public static volatile SingularAttribute<UserSlideModel, UserModel> userModel;
	public static volatile SingularAttribute<UserSlideModel, String> labId;
	public static volatile SingularAttribute<UserSlideModel, Long> id;
	public static volatile SingularAttribute<UserSlideModel, String> barcodeId;

	public static final String USER_MODEL = "userModel";
	public static final String LAB_ID = "labId";
	public static final String ID = "id";
	public static final String BARCODE_ID = "barcodeId";

}

