package com.pathdx.repository;

import com.pathdx.model.SlideStainPanelModel;
import com.pathdx.model.SlideStainsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlideStainPanelRepository extends JpaRepository<SlideStainPanelModel, Long> {
    public SlideStainPanelModel findBySlideId(Long slideId);
}
