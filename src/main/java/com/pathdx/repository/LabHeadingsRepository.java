package com.pathdx.repository;

import com.pathdx.model.LabDetail;
import com.pathdx.model.LabHeadings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface LabHeadingsRepository extends JpaRepository<LabHeadings,String> {
    Optional<LabHeadings> findByLabDetail(LabDetail labDetail);
}
