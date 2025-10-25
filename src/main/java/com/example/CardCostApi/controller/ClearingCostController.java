package com.example.CardCostApi.controller;

import com.example.CardCostApi.dto.ClearingCostRequest;
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
    public ResponseEntity<List<ClearingCost>> findAllClearingCosts() {
        return new ResponseEntity<>(clearingCostService.findAll(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<ClearingCost> findClearingCostById(@PathVariable("id") String countryCode) {
        return new ResponseEntity<>(clearingCostService.findById(countryCode), HttpStatus.OK);
    }


    @PostMapping()
    public ResponseEntity<Void> createClearingCost(@RequestBody @Valid ClearingCostRequest clearingCostRequest) {
        clearingCostService.createCost(clearingCostRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping()
    public ResponseEntity<Void> updateClearingCost(@RequestBody @Valid ClearingCostRequest clearingCostRequest) {
        clearingCostService.updateCost(clearingCostRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteClearingCost(@PathVariable("id") String countryCode) {
        clearingCostService.deleteCost(countryCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
