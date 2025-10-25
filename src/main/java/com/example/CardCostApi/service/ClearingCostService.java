package com.example.CardCostApi.service;

import com.example.CardCostApi.dto.ClearingCostRequest;
import com.example.CardCostApi.exception.CardCostException;
import com.example.CardCostApi.model.ClearingCost;
import com.example.CardCostApi.repository.ClearingCostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClearingCostService {
    private final ClearingCostRepository clearingCostRepository;


    public List<ClearingCost> findAll() {
        return clearingCostRepository.findAll();
    }

    public ClearingCost findById(String countryCode) {
        return clearingCostRepository.findById(countryCode)
                .orElseThrow(() -> new CardCostException("No such country code found"));
    }

    public void createCost(ClearingCostRequest clearingCostRequest) {
        clearingCostRepository.save(new ClearingCost(clearingCostRequest.countryCode(),
                clearingCostRequest.cost()));
    }

    public void updateCost(ClearingCostRequest clearingCostRequest) {
        ClearingCost foundClearingCost = clearingCostRepository.findById(clearingCostRequest.countryCode())
                .orElseThrow(() -> new CardCostException("No such country code found"));
        foundClearingCost.setClearingCost(clearingCostRequest.cost());

        clearingCostRepository.save(foundClearingCost);
    }

    public void deleteCost(String countryCode) {
        ClearingCost foundClearingCost = clearingCostRepository.findById(countryCode)
                .orElseThrow(() -> new CardCostException("No such country code found"));
        clearingCostRepository.delete(foundClearingCost);
    }
}
