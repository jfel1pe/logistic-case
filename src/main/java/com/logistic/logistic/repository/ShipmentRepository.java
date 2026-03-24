package com.logistic.logistic.repository;

import com.logistic.logistic.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {

    boolean existsByGuideNumber(String guideNumber);
}
