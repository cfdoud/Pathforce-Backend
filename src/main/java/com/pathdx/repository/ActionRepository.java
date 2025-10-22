package com.pathdx.repository;

import com.pathdx.model.ActionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionRepository extends JpaRepository<ActionModel, Long> {
    public ActionModel findByName(String name);
}
