package com.pathdx.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(UserModel.class)
public abstract class UserModel_ extends com.pathdx.model.BaseModel_ {

	public static volatile SingularAttribute<UserModel, String> college;
	public static volatile SingularAttribute<UserModel, String> lastName;
	public static volatile SingularAttribute<UserModel, Date> lastLogin;
	public static volatile SingularAttribute<UserModel, String> gender;
	public static volatile SingularAttribute<UserModel, String> city;
	public static volatile ListAttribute<UserModel, Role> roles;
	public static volatile SingularAttribute<UserModel, Boolean> isActive;
	public static volatile SingularAttribute<UserModel, String> mobilePh;
	public static volatile SingularAttribute<UserModel, String> password;
	public static volatile SingularAttribute<UserModel, String> emergencyPh;
	public static volatile SingularAttribute<UserModel, Long> id;
	public static volatile ListAttribute<UserModel, LabDetail> labDetails;
	public static volatile SingularAttribute<UserModel, String> profileImg;
	public static volatile SingularAttribute<UserModel, String> email;
	public static volatile SingularAttribute<UserModel, String> zip;
	public static volatile SingularAttribute<UserModel, String> searchAddress;
	public static volatile ListAttribute<UserModel, StateModel> licensedStates;
	public static volatile SingularAttribute<UserModel, String> yearOfPassing;
	public static volatile SingularAttribute<UserModel, String> npi;
	public static volatile SingularAttribute<UserModel, String> degree;
	public static volatile SingularAttribute<UserModel, Boolean> isPasswordChangeRequired;
	public static volatile SingularAttribute<UserModel, String> homeState;
	public static volatile SingularAttribute<UserModel, String> homePh;
	public static volatile SingularAttribute<UserModel, String> firstName;
	public static volatile SingularAttribute<UserModel, String> streetAddress;
	public static volatile SetAttribute<UserModel, OrderMessages> orderMessages;
	public static volatile SingularAttribute<UserModel, String> middleName;
	public static volatile SingularAttribute<UserModel, String> jwtToken;
	public static volatile ListAttribute<UserModel, DesignationModel> designation;
	public static volatile SingularAttribute<UserModel, String> refreshToken;

	public static final String COLLEGE = "college";
	public static final String LAST_NAME = "lastName";
	public static final String LAST_LOGIN = "lastLogin";
	public static final String GENDER = "gender";
	public static final String CITY = "city";
	public static final String ROLES = "roles";
	public static final String IS_ACTIVE = "isActive";
	public static final String MOBILE_PH = "mobilePh";
	public static final String PASSWORD = "password";
	public static final String EMERGENCY_PH = "emergencyPh";
	public static final String ID = "id";
	public static final String LAB_DETAILS = "labDetails";
	public static final String PROFILE_IMG = "profileImg";
	public static final String EMAIL = "email";
	public static final String ZIP = "zip";
	public static final String SEARCH_ADDRESS = "searchAddress";
	public static final String LICENSED_STATES = "licensedStates";
	public static final String YEAR_OF_PASSING = "yearOfPassing";
	public static final String NPI = "npi";
	public static final String DEGREE = "degree";
	public static final String IS_PASSWORD_CHANGE_REQUIRED = "isPasswordChangeRequired";
	public static final String HOME_STATE = "homeState";
	public static final String HOME_PH = "homePh";
	public static final String FIRST_NAME = "firstName";
	public static final String STREET_ADDRESS = "streetAddress";
	public static final String ORDER_MESSAGES = "orderMessages";
	public static final String MIDDLE_NAME = "middleName";
	public static final String JWT_TOKEN = "jwtToken";
	public static final String DESIGNATION = "designation";
	public static final String REFRESH_TOKEN = "refreshToken";

}

