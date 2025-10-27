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

@RestController
@RequestMapping("api/clearingCosts")
@RequiredArgsConstructor
public class ClearingCostController {
    private final ClearingCostService clearingCostService;

    @GetMapping()
    public ResponseEntity<GenericApiResponse<List<ClearingCost>>> findAllClearingCosts() {
        GenericApiResponse<List<ClearingCost>> response = GenericApiResponse.success(clearingCostService.findAll());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<GenericApiResponse<ClearingCost>> findClearingCostById(@PathVariable("id") String countryCode) {
        GenericApiResponse<ClearingCost> response =
                GenericApiResponse.success(clearingCostService.findById(countryCode));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<GenericApiResponse<Object>> createClearingCost(@RequestBody @Valid ClearingCostRequest clearingCostRequest) {
        clearingCostService.createCost(clearingCostRequest);
        GenericApiResponse<Object> response = GenericApiResponse.success(HttpStatus.CREATED.value(),
                "Successfully created clearing cost");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping()
    public ResponseEntity<GenericApiResponse<Object>> updateClearingCost(@RequestBody @Valid ClearingCostRequest clearingCostRequest) {
        clearingCostService.updateCost(clearingCostRequest);
        GenericApiResponse<Object> response = GenericApiResponse.success(HttpStatus.CREATED.value(),
                "Successfully updated clearing cost");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<GenericApiResponse<Object>> deleteClearingCost(@PathVariable("id") String countryCode) {
        clearingCostService.deleteCost(countryCode);
        GenericApiResponse<Object> response = GenericApiResponse.success(HttpStatus.OK.value(),
                "Successfully deleted clearing cost");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
