package com.example.CardCostApi.controller;

import com.example.CardCostApi.dto.CardCostRequest;
import com.example.CardCostApi.dto.CardCostResponse;
import com.example.CardCostApi.dto.GenericApiResponse;
import com.example.CardCostApi.service.CardCostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class CardCostController {
    private final CardCostService cardCostService;

    /**
     * Return clearing cost of the given payment card
     * @param cardCostRequest request DTO containing the PAN number
     * @return CardCostResponse wrapped in GenericApiResponse
     */
    @PostMapping("payment-cards-cost")
    public ResponseEntity<GenericApiResponse<CardCostResponse>> getPaymentCardCost(
            @RequestBody @Valid CardCostRequest cardCostRequest) {
        CardCostResponse cardCostResponse = cardCostService.getClearingCostByCardNumber(cardCostRequest.card_number());
        return new ResponseEntity<>(GenericApiResponse.success(cardCostResponse), HttpStatus.OK);
    }
}
