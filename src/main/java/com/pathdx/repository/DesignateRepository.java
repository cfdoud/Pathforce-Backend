package com.pathdx.repository;

import com.pathdx.model.DesignationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignateRepository extends JpaRepository<DesignationModel, Long> {
}
