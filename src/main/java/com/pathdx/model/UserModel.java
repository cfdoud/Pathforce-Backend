package com.pathdx.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.*;


@Entity
@Getter
@Setter
@Table(name="user_info")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserModel extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "FirstName")
    private String firstName;

    @Column(name = "MiddleName")
    private String middleName;

    @Column(name = "LastName")
    private String lastName;

    @Column(name="Gender")
    private String gender;

    @Column(name="NPI")
    private String npi;

    @Column(name="StreetAddress")
    private String streetAddress;

    @Column(name="SearchAddress")
    private String searchAddress;

    @Column(name="City")
    private String city;

    @Column(name="HomeState")
    private String homeState;

    @Column(name= "Zip")
    private String zip;

    @Column(name="MobilePhone")
    private String mobilePh;

    @Column(name="HomePhone")
    private String homePh;

    @Column(name="EmergenyPhone")
    private String emergencyPh;

    @Column(name="email_id")
    @Email
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "pf_users_licensed_states", joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns =@JoinColumn( name = "state_master_id"))
    private List<StateModel> licensedStates = new ArrayList<>();

    @Column(name="Degree")
    private String degree;

    @Column(name="College")
    private String college;

    @Column(name="YearOfPassing")
    private String yearOfPassing;


    @Column(name="Designation")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "pf_user_designation", joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns =@JoinColumn( name = "designationdesignation_master_id"))
    private List<DesignationModel> designation = new ArrayList<>();



    @JsonProperty
    @Column(name ="JwtToken" )
    private String jwtToken;

    @Column(name = "RefreshToken")
    private String refreshToken;

    @Column(name = "Password")
    private String password;

    @Column(name = "IsPasswordChangeRequired")
    private boolean isPasswordChangeRequired;

    @Column(name = "IsActive")
    private boolean isActive;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "pf_user_lab_detail", joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns =@JoinColumn( name = "lab_id"))
    private List<LabDetail> labDetails = new ArrayList<>();


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @Column(name = "ProfileImg")
    private String profileImg;

    @ManyToMany(mappedBy = "userModels", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<OrderMessages> orderMessages = new HashSet<>();

    @Column(name="last_Login")
    private Date lastLogin;
}
