package com.pathdx.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseModel {

    @CreatedDate
    @Column(name="Date_Created")
    Date createdDate;

    @LastModifiedDate
    @Column(name="Last_Modified_Date")
    Date lastModifiedDate;

    @CreatedBy
    @Column(name="Last_Created_By")
    String createdBy;

    @LastModifiedBy
    @Column(name="Last_Modified_By")
    String lastModifiedBy;

}
