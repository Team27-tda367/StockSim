package org.team27.stocksim.model.market;

import java.math.BigDecimal;


public class OrderValidator {


    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult failure(String errorMessage) {
            return new ValidationResult(false, errorMessage);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        @Override
        public String toString() {
            return valid ? "Valid" : "Invalid: " + errorMessage;
        }
    }

    public ValidationResult validate(Order order) {

        if (order == null) {
            return ValidationResult.failure("Order cannot be null");
        }


        if (order.getTraderId() == null || order.getTraderId().isEmpty()) {
            return ValidationResult.failure("Trader ID is required");
        }


        if (order.getSymbol() == null || order.getSymbol().isEmpty()) {
            return ValidationResult.failure("Symbol is required");
        }

        if (order.getTotalQuantity() <= 0) {
            return ValidationResult.failure("Quantity must be positive");
        }


        if (!order.isMarketOrder()) {
            if (order.getPrice() == null) {
                return ValidationResult.failure("Limit order price cannot be null");
            }
            if (order.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                return ValidationResult.failure("Limit order price must be positive");
            }
        }

        return ValidationResult.success();
    }
}

