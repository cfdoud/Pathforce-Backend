package com.pathdx.repository;

import com.pathdx.model.SlideStainsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlideStainRepository extends JpaRepository<SlideStainsModel, Long> {
    public SlideStainsModel findBySlideId(Long slideId);
}
