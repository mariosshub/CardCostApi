package com.example.CardCostApi.repository;

import com.example.CardCostApi.model.ClearingCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClearingCostRepository extends JpaRepository<ClearingCost,String> {
}
