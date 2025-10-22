package com.pathdx.repository;

import com.pathdx.model.StateModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRespository extends JpaRepository<StateModel, Long> {
}
