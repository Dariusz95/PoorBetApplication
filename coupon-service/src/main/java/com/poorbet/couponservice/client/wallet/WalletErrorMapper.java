package com.poorbet.couponservice.client.wallet;

import com.poorbet.commons.commons.error.ErrorCode;
import com.poorbet.commons.commons.error.ErrorResponse;

public class WalletErrorMapper {

    public static RuntimeException map(ErrorResponse err) {

        ErrorCode code;

        try {
            code = ErrorCode.valueOf(err.code());
        } catch (Exception e) {
            return new WalletTechnicalException("Unknown error: " + err.code());
        }

        return switch (code) {
            case INSUFFICIENT_FUNDS, WALLET_NOT_FOUND -> new WalletBusinessException(err.message());

            case INTERNAL_ERROR -> new WalletTechnicalException(err.message());

            default -> new WalletTechnicalException("Unhandled error: " + err.code());
        };
    }
}