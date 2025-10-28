package com.example.CardCostApi.controller;

import com.example.CardCostApi.dto.ClearingCostRequest;
import com.example.CardCostApi.dto.GenericApiResponse;
import com.example.CardCostApi.model.ClearingCost;
import com.example.CardCostApi.service.ClearingCostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller providing CRUD operations for managing clearing cost matrix
 */
@RestController
@RequestMapping("api/clearingCosts")
@RequiredArgsConstructor
public class ClearingCostController {
    private final ClearingCostService clearingCostService;

    /**
     * Return all clearing costs
     *
     * @return
     */
    @GetMapping()
    public ResponseEntity<GenericApiResponse<List<ClearingCost>>> findAllClearingCosts() {
        GenericApiResponse<List<ClearingCost>> response = GenericApiResponse.success(clearingCostService.findAll());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Return a clearing cost by countryCode as id
     *
     * @param countryCode
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<GenericApiResponse<ClearingCost>> findClearingCostById(@PathVariable("id") String countryCode) {
        GenericApiResponse<ClearingCost> response =
                GenericApiResponse.success(clearingCostService.findById(countryCode));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Create a new clearing cost
     *
     * @param clearingCostRequest
     * @return
     */
    @PostMapping()
    public ResponseEntity<GenericApiResponse<Object>> createClearingCost(@RequestBody @Valid ClearingCostRequest clearingCostRequest) {
        clearingCostService.createCost(clearingCostRequest);
        GenericApiResponse<Object> response = GenericApiResponse.success(HttpStatus.CREATED.value(),
                "Successfully created clearing cost");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Update an existing clearing cost
     *
     * @param clearingCostRequest
     * @return
     */
    @PutMapping()
    public ResponseEntity<GenericApiResponse<Object>> updateClearingCost(@RequestBody @Valid ClearingCostRequest clearingCostRequest) {
        clearingCostService.updateCost(clearingCostRequest);
        GenericApiResponse<Object> response = GenericApiResponse.success(HttpStatus.CREATED.value(),
                "Successfully updated clearing cost");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Delete a clearing cost by country code
     *
     * @param countryCode
     * @return
     */
    @DeleteMapping("{id}")
    public ResponseEntity<GenericApiResponse<Object>> deleteClearingCost(@PathVariable("id") String countryCode) {
        clearingCostService.deleteCost(countryCode);
        GenericApiResponse<Object> response = GenericApiResponse.success(HttpStatus.OK.value(),
                "Successfully deleted clearing cost");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
