package com.pathdx.repository;

import com.pathdx.model.LabDetail;
import com.pathdx.model.OrderMessages;
import com.pathdx.model.Physicians;
import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhysiciansRepository extends JpaRepository<Physicians, Long> {

    Physicians findByOrderMessages(OrderMessages orderMessages);
}
