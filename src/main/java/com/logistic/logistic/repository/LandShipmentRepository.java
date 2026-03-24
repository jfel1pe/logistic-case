package com.logistic.logistic.repository;

import com.logistic.logistic.entity.LandShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandShipmentRepository extends JpaRepository<LandShipment, Integer> {
}
