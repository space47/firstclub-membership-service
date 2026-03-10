package com.firstclub.membership.modules.tier.domain.convertor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstclub.membership.modules.tier.domain.model.TierEligibilityRules;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TierRuleConverter implements AttributeConverter<TierEligibilityRules, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(TierEligibilityRules attribute) {

        if (attribute == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Error converting rules to JSON", e);
        }
    }

    @Override
    public TierEligibilityRules convertToEntityAttribute(String dbData) {

        if (dbData == null || dbData.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readValue(dbData, TierEligibilityRules.class);
        } catch (Exception e) {
            throw new RuntimeException("Error reading rules JSON", e);
        }
    }
}