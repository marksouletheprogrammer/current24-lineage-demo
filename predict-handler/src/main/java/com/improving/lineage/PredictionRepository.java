package com.improving.lineage;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionRepository extends JpaRepository<LocationWeatherPrediction, Long> {
}
