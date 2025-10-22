package com.pathdx.pdfGenerate;

import java.util.ArrayList;

public class ReportDetails {
    public String firstHeading ;
    public String secondHeading ;
    public String thirdHeading ;
    public String fourthHeading ;
    public String fifthHeading ;
    public String sixthHeading ;
    public String seventhHeading;
    public String labName ;
    public String labWebsite;
    public String labLogo;
    public String labPhoneNumber;
    public String accessionId;
    public String resultId;
    public String reportedDate;
    public String receivedDate;
    public String pathologistName;
    public String pathologistDegree;
    public String pathologistRole;
    public String reviewedDate ;
    public String comment;
    public ArrayList<String> images;
    public String patientName;
    public String physicianName;
    public String mrn;
    public String phoneNumber ;
    public String labAddress;
    public String clinicalHistory;
    public String finalDiagnosis;





    public void ReportHeadings(String firstHeading, String secondHeading, String thirdHeading, String fourthHeading, String fifthHeading, String sixthHeading, String seventhHeading){
        this.firstHeading = firstHeading;
        this.secondHeading = secondHeading;
        this.thirdHeading = thirdHeading;
        this.fourthHeading = fourthHeading;
        this.fifthHeading = fifthHeading;
        this.sixthHeading = sixthHeading;
        this.seventhHeading = seventhHeading;
    }
    public void CaseDetails(String patientName, String physicianName, String accessionId, String resultId, String mrn, String phoneNumber ,String comment, String clinicalHistory, String finalDiagnosis, ArrayList<String> images)
    {
        this.patientName = patientName;
        this.physicianName = physicianName;
        this.accessionId = accessionId;
        this.resultId = resultId;
        this.comment = comment;
        this.images = images;
        this.mrn = mrn;
        this.phoneNumber = phoneNumber;
        this.clinicalHistory = clinicalHistory;
        this.finalDiagnosis =finalDiagnosis;

    }
    public void ReportDate(String reportedDate){
        this.reportedDate =reportedDate;

    }
    public void LabDetails(String labName, String labPhoneNumber, String labWebSite, String labLogo, String labAddress){
        this.labName =labName;
        this.labLogo = labLogo;
        this.labWebsite = labWebSite;
        this.labPhoneNumber = labPhoneNumber;
        this.labAddress = labAddress;
    }

    public void PathologistDetails(String pathologistName, String degree, String role)
    {
        this.pathologistName = pathologistName;
        this.pathologistDegree = degree;
        this.pathologistRole = role;

    }
}
