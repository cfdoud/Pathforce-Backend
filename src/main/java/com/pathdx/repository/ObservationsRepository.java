package com.pathdx.repository;

import com.pathdx.model.Observations;
import com.pathdx.model.OrderMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObservationsRepository extends JpaRepository<Observations,Long> {

    List<Observations> findByOrderMessages(OrderMessages orderMessages);
}
