package org.team27.stocksim.model.market;

import java.math.BigDecimal;

/**
 * Validates orders before they enter the matching engine.
 *
 * <p>OrderValidator ensures order integrity by checking required fields and
 * business rules before processing. This prevents invalid orders from corrupting
 * market state and provides clear error messages for debugging.</p>
 *
 * <p><strong>Design Pattern:</strong> Validator + Result Object</p>
 * <ul>
 *   <li>Validates order completeness and correctness</li>
 *   <li>Returns detailed ValidationResult with error messages</li>
 *   <li>Prevents null pointer exceptions</li>
 *   <li>Enforces business rules (positive prices, quantities)</li>
 *   <li>Different rules for limit vs market orders</li>
 * </ul>
 *
 * <h2>Validation Rules:</h2>
 * <ul>
 *   <li>Order object cannot be null</li>
 *   <li>Trader ID must be present</li>
 *   <li>Symbol must be specified</li>
 *   <li>Quantity must be positive</li>
 *   <li>Limit orders must have positive price</li>
 *   <li>Market orders may have null/zero price</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * OrderValidator validator = new OrderValidator();
 * Order order = new Order(Side.BUY, "AAPL", new BigDecimal("150.00"), 10, "user1");
 *
 * ValidationResult result = validator.validate(order);
 * if (result.isValid()) {
 *     market.processOrder(order);
 * } else {
 *     System.err.println("Invalid order: " + result.getErrorMessage());
 * }
 * }</pre>
 *
 * @author Team 27
 * @version 1.0
 * @see Order
 * @see Market
 */
public class OrderValidator {

    /**
     * Result of order validation containing success/failure status and error message.
     *
     * <p>Uses static factory methods for cleaner validation result creation.</p>
     *
     * @author Team 27
     * @version 1.0
     */
    public static class ValidationResult {
        /**
         * Whether the validation passed.
         */
        private final boolean valid;

        /**
         * Error message if validation failed, null if successful.
         */
        private final String errorMessage;

        /**
         * Private constructor, use static factory methods instead.
         *
         * @param valid Whether validation passed
         * @param errorMessage Error message if failed
         */
        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        /**
         * Creates a successful validation result.
         *
         * @return ValidationResult indicating success
         */
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        /**
         * Creates a failed validation result with error message.
         *
         * @param errorMessage Description of validation failure
         * @return ValidationResult indicating failure
         */
        public static ValidationResult failure(String errorMessage) {
            return new ValidationResult(false, errorMessage);
        }

        /**
         * Checks if validation passed.
         *
         * @return true if valid, false if invalid
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * Gets the error message for failed validation.
         *
         * @return Error message, or null if validation succeeded
         */
        public String getErrorMessage() {
            return errorMessage;
        }

        @Override
        public String toString() {
            return valid ? "Valid" : "Invalid: " + errorMessage;
        }
    }

    /**
     * Validates an order against business rules.
     *
     * <p>Checks all required fields and enforces constraints. Returns
     * detailed result indicating success or specific failure reason.</p>
     *
     * @param order The order to validate
     * @return ValidationResult indicating success or failure with message
     */
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

