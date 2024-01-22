package uk.ac.ed.inf.Domain;
public record Deliveries(String orderNo, String orderStatus, String orderValidationCode, int costInPence) {
    /**
     * Constructs a new {@code Deliveries} instance with the specified parameters.
     * Validation or modification logic can be added here if needed.
     *
     * @param orderNo              The unique identifier for the delivery order.
     * @param orderStatus          The status of the delivery order.
     * @param orderValidationCode  The validation code associated with the delivery order.
     * @param costInPence          The cost of the delivery in pence.
     * @throws IllegalArgumentException If {@code orderNo}, {@code orderStatus}, or {@code orderValidationCode} is null.
     */
    public Deliveries {
        if (orderNo == null || orderStatus == null || orderValidationCode == null) {
            throw new IllegalArgumentException("orderNo, orderStatus, and orderValidationCode cannot be null");
        }
    }
}
