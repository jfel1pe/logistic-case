package com.logistic.logistic.util;

import com.logistic.logistic.repository.ShipmentRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class GuideNumberGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 10;
    private final SecureRandom random = new SecureRandom();
    private final ShipmentRepository shipmentRepository;

    public GuideNumberGenerator(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public String generate() {
        String guideNumber;
        // Genera hasta encontrar uno que no exista en BD
        do {
            guideNumber = buildGuideNumber();
        } while (shipmentRepository.existsByGuideNumber(guideNumber));
        return guideNumber;
    }

    private String buildGuideNumber() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
