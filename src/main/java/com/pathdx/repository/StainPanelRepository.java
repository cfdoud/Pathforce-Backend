package com.pathdx.repository;

import com.pathdx.model.StainPanelModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StainPanelRepository extends JpaRepository<StainPanelModel,Long> {
}
