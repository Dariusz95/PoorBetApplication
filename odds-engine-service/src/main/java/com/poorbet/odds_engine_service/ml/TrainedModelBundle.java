package com.poorbet.odds_engine_service.ml;

import smile.classification.LogisticRegression;

import java.io.Serializable;

public record TrainedModelBundle(
        LogisticRegression matchResultModel,
        LogisticRegression over2_5Model,
        LogisticRegression over3_5Model
) implements Serializable {
}
