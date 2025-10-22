package com.pathdx.constant;

public class UtilConstants {


  public static final int LENGTH =8;

  public static final String SUBJECT="Registration Successful";
  public static final String SUBJECT_FORGOT_PASS="Reset Your Password";
  public static final String CHANGE_PASS_SUBJECT="Password Change Successful";

  public static final String BODY_Start="Dear ";
  public static final String BODY=",</br></br> Your registration has been completed successfully. Your temporary password is ";
  public static final String BODY_FORGOT_PASS=",</br></br> We're sending you this email because you requested a password reset." +
                                               " </br>Please use following temporary password and change this password once you login." +
                                                " </br></br>Temporary password- ";
  public static final String BODY_CHANGE_PASS=",</br> Your request for password change has processed successfully. ";
  public static final String BODY_END=", please change your password once you login.";
  public static final String THANK="</br></br>"+"Thank you and regards,"+"</br>" + "PathForceDX Account team";
  public static final String CHARS="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%^&*()";
  public static final String SUCCESS_MESSAGE_REG="Registration Successful. Check your inbox for the password.";
  public static final String SUCCESS_MESSAGE="Request Successful";
  public static final String SUCCESS_MESSAGE_CHANGE_PASS="Password Changed Successfully";

  public static final String ERROR_MESSAGE_CHANGE_PASS="Something went wrong.";
  public static final String ERROR_MESSAGE_CHANGE_PWD="Old password is invalid, Please make sure your entry is correct.";

  public static final String SUCCESS_MESSAGE_FORGOT_PASS="Request Successful,Check your inbox for the Password";
  public static final String ERROR_MESSAGE_FORGOT_PASS="Sorry, this email id is not registered. Click on Sign up";
  public static final String ERROR_MESSAGE="Username/ Password Incorrect Try Again";
  public static final String ERROR_MESSAGE_REFRESH_TOKEN="Invalid User Found";
  public static final String SUCCESS_MESSAGE_UPLOAD_FILE="File uploaded successfully.";
  public static final String SUCCESS_MESSAGE_LEAD_CAPTURED="Hi ,Thank you for requesting a demo. We appreciate you giving us a chance! Our PathForceDX representative will contact you shortly.";
  public static final String CONTACT_US_SUCCESS_MESSAGE="Thank you for contacting us. Our representative will contact you shortly.";

  public static final String PUBLIC_API_ERROR_MESSAGE="Session time out, please try Again";

  public static final String ADDITIONAL_REQUEST_SUBJECT = "Request to take actions on slides";

  public static final String ADDITIONAL_REQUEST_BODY = "</br></br>"+"Pathologist Dr\n";
  public static final String ADDITIONAL_REQUEST_BODY2 = "\n has requested for additional lab for the following:";

  public static final String ADDITIONAL_REQUEST_THANK="</br></br>"+"Thank you";

  public static final String PENDING_CASES_SUBJECT="Gentle Reminder for Pending Case";

  private UtilConstants(){

}
}
