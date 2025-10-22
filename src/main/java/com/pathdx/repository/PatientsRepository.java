package com.pathdx.repository;

import com.pathdx.model.OrderMessages;
import com.pathdx.model.Patients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientsRepository extends JpaRepository<Patients,Long> {

    Patients findByOrderMessages(OrderMessages orderMessages);
}
