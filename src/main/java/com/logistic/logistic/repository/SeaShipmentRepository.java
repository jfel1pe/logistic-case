package com.logistic.logistic.repository;

import com.logistic.logistic.entity.SeaShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeaShipmentRepository extends JpaRepository<SeaShipment, Integer> {
}
