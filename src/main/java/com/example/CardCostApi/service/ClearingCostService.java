package com.example.CardCostApi.service;

import com.example.CardCostApi.dto.ClearingCostRequest;
import com.example.CardCostApi.exception.CardCostException;
import com.example.CardCostApi.model.ClearingCost;
import com.example.CardCostApi.repository.ClearingCostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Provides CRUD operations for managing the clearing cost data
 */
@Service
@RequiredArgsConstructor
public class ClearingCostService {
    private final ClearingCostRepository clearingCostRepository;


    /**
     * Retrieves all clearing costs from DB
     *
     * @return {@code List<ClearingCost>}
     */
    public List<ClearingCost> findAll() {
        return clearingCostRepository.findAll();
    }

    /**
     * Retrieves a ClearingCost record found by given countryCode
     *
     * @param countryCode
     * @return {@link ClearingCost}
     */
    public ClearingCost findById(String countryCode) {
        return clearingCostRepository.findById(countryCode)
                .orElseThrow(() -> new CardCostException("No such country code found"));
    }

    /**
     * Creates a new ClearingCost
     *
     * @param clearingCostRequest
     */
    public void createCost(ClearingCostRequest clearingCostRequest) {
        clearingCostRepository.save(new ClearingCost(clearingCostRequest.countryCode(),
                clearingCostRequest.cost()));
    }

    /**
     * Updates an existing ClearingCost for a given country
     *
     * @param clearingCostRequest
     */
    public void updateCost(ClearingCostRequest clearingCostRequest) {
        ClearingCost foundClearingCost = clearingCostRepository.findById(clearingCostRequest.countryCode())
                .orElseThrow(() -> new CardCostException("No such country code found"));
        foundClearingCost.setClearingCost(clearingCostRequest.cost());

        clearingCostRepository.save(foundClearingCost);
    }

    /**
     * Deletes a ClearingCost record by its country code
     *
     * @param countryCode
     */
    public void deleteCost(String countryCode) {
        ClearingCost foundClearingCost = clearingCostRepository.findById(countryCode)
                .orElseThrow(() -> new CardCostException("No such country code found"));
        clearingCostRepository.delete(foundClearingCost);
    }
}
