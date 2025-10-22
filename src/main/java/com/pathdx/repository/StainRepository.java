package com.pathdx.repository;

import com.pathdx.model.Stains;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StainRepository extends JpaRepository<Stains,Long> {
}
