package com.example.CardCostApi.service;

import com.example.CardCostApi.dto.CardCostResponse;
import com.example.CardCostApi.exception.CardCostException;
import com.example.CardCostApi.model.ClearingCost;
import com.example.CardCostApi.repository.ClearingCostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * CardCostService determines the clearing cost of a card number
 */
@Service
@RequiredArgsConstructor
public class CardCostService {
    private final ClearingCostRepository clearingCostRepository;
    private final BinLookupService binLookupService;
    // Default country code used when a specific country’s clearing cost is not found.
    private final static String OTHERS = "OTH";

    /**
     * Retrieves the clearing cost for a given card number by looking up
     * the country code of the card in BinLookupService.
     *
     * @param cardNumber
     * @return CardCostResponse that contains countryCode and clearingCost
     */
    public CardCostResponse getClearingCostByCardNumber(String cardNumber) {
        String bin = cardNumber.substring(0, 6);
        // lookup bin number and fetch the country code
        String countryCode = binLookupService.fetchBinInfo(bin);

        // search the country code in DB and get the clearing cost
        Optional<ClearingCost> clearingCost = clearingCostRepository.findById(countryCode);
        if (clearingCost.isPresent()) {
            return new CardCostResponse(countryCode, clearingCost.get().getClearingCost());
        } else {
            // if country code not found return "OTHERS"
            ClearingCost clearingCostOfOth = clearingCostRepository.findById(OTHERS)
                    .orElseThrow(() -> new CardCostException("No clearing cost found"));
            return new CardCostResponse(countryCode, clearingCostOfOth.getClearingCost());
        }
    }
}
