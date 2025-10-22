package com.pathdx.repository;

import com.pathdx.model.LabDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabDetailRepository extends JpaRepository<LabDetail,String> {

    Optional<LabDetail> findByLabid(String id);

}
