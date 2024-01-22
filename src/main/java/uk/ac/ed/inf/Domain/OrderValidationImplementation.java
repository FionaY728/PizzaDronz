package uk.ac.ed.inf.Domain;


import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;



public class OrderValidationImplementation implements OrderValidation {
    public Restaurant getRestaurant() {
        return restaurant;
    }

    Restaurant restaurant;

    /**
     * validate an order and deliver a validated version where the
     * OrderStatus and OrderValidationCode are set accordingly.
     * The order validation code is defined in the enum @link uk.ac.ed.inf.ilp.constant.OrderValidationStatus
     *
     * <p>
     * Fields to validate include (among others - for details please see the OrderValidationStatus):
     * <p>
     * number (16 digit numeric)
     * CVV
     * expiration date
     * the menu items selected in the order
     * the involved restaurants
     * if the maximum count is exceeded
     *
     *
     * @param orderToValidate    is the order which needs validation
     * @param definedRestaurants is the vector of defined restaurants with their according menu structure
     * @return the validated order
     */
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        /**
         * Validates the credit card number in the order.
         * <p>
         * This method retrieves the credit card number from the order's credit card information,
         * and checks if it is a 16-digit number. If the card number is not valid,
         * it sets the order's validation code to CARD_NUMBER_INVALID and returns the order.
         *
         * @param orderToValidate The order that needs to be validated.
         * @return The validated order with updated validation code.
         */
        if (orderToValidate == null) {
            Order order = new Order();
            order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
            order.setOrderStatus(OrderStatus.INVALID);
            return order;
        }
        try {
            String cardNumber = orderToValidate.getCreditCardInformation().getCreditCardNumber();
            if (!cardNumber.matches("\\d{16}")) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                return orderToValidate;
            }
            /**
             * Validates the CVV in the order's credit card information.
             * <p>
             * This method retrieves the CVV from the order's credit card information,
             * and checks if it is a 3-digit number. If the CVV is not valid,
             * it sets the order's validation code to CVV_INVALID and returns the order.
             *
             * @param orderToValidate The order that needs to be validated.
             * @return The validated order with updated validation code.
             */

            if (!isValidCVV(orderToValidate.getCreditCardInformation().getCvv())) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                return orderToValidate;
            }
            /**
             * This method checks if the number of pizzas in the order exceeds the maximum limit.
             * If the number of pizzas is greater than the maximum limit (4 pizzas), it sets the
             * order validation code to MAX_PIZZA_COUNT_EXCEEDED and returns the order to validate.
             *
             * @param orderToValidate The order that needs to be validated.
             * @return The order to validate with updated validation code if the pizza count exceeds the limit.
             */
            if (orderToValidate.getPizzasInOrder().length > SystemConstants.MAX_PIZZAS_PER_ORDER) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                return orderToValidate;
            }
            /**
             * Checks if the credit card associated with an order is expired.
             *
             * @param orderToValidate The order to be validated.
             * @return The same order with an updated validation code. If the credit card is expired, the validation code is set to EXPIRY_DATE_INVALID.
             */
            if (!validateExpiryDate(orderToValidate.getCreditCardInformation().getCreditCardExpiry(), orderToValidate.getOrderDate())) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                return orderToValidate;

            }
            /**
             * This loop checks each pizza in the order to validate if it's in the menu of defined restaurants.
             *
             * @param orderToValidate The order that needs to be validated.
             * @return If a pizza in the order is not in the menu, it sets the order's validation code to PIZZA_NOT_DEFINED and returns the order.
             */

            for (Pizza pizza : orderToValidate.getPizzasInOrder()) {
                if (!isPizzaInMenu(pizza, definedRestaurants)) {
                    orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
                    orderToValidate.setOrderStatus(OrderStatus.INVALID);
                    return orderToValidate;
                }
            }
            /**
             * This code checks if the pizzas in the order come from multiple restaurants.
             *
             * @param orderToValidate The order that needs to be validated.
             * @return If the pizzas in the order come from multiple restaurants, it sets the order's validation code to PIZZA_FROM_MULTIPLE_RESTAURANTS and returns the order.
             */

            Restaurant restaurant = isMutipleRestaurant(orderToValidate.getPizzasInOrder(), definedRestaurants);
            if (restaurant == null) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                return orderToValidate;
            }

            /**
             * This code checks if the total price of the order is correct.
             *
             * @param orderToValidate The order that needs to be validated.
             * @return If the total price of the order is incorrect, it sets the order's validation code to TOTAL_INCORRECT and returns the order.
             */

            if (!isTotalPriceCorrect(orderToValidate.getPriceTotalInPence(), orderToValidate.getPizzasInOrder(), restaurant)) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                return orderToValidate;
            }


            /**
             * This code checks if the restaurant is open on the day the order was placed.
             *
             * @param orderToValidate The order that needs to be validated.
             * @return If the restaurant is closed on the day the order was placed, it sets the order's validation code to RESTAURANT_CLOSED and returns the order.
             */

            LocalDate orderDate = orderToValidate.getOrderDate();
            boolean restaurantIsOpen = false;
            DayOfWeek orderDay = orderDate.getDayOfWeek();

            for (DayOfWeek day : restaurant.openingDays()) {
                if (day.equals(orderDay)) {
                    restaurantIsOpen = true;
                    break;
                }
            }
            if (!restaurantIsOpen) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);

                return orderToValidate;
            }


            orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
            orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
            //CHANGED
            this.restaurant= isMutipleRestaurant(orderToValidate.getPizzasInOrder(), definedRestaurants);

            return orderToValidate;

        } catch (NullPointerException e) {

            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.UNDEFINED);
            return orderToValidate;
        }
    }
        /**
         * Checks if a pizza is in the menu of any defined restaurants.
         *
         * @param pizza The pizza to be checked.
         * @param definedRestaurants The array of defined restaurants.
         * @return true if the pizza is in the menu of any restaurant, false otherwise.
         */
        private boolean isPizzaInMenu(Pizza pizza, Restaurant[] definedRestaurants) {
            for (Restaurant restaurant : definedRestaurants) {
                for (Pizza menuPizza : restaurant.menu()) {
                    if (menuPizza.name().equals(pizza.name())) {
                        return true;
                    }
                }
            }
            return false;
        }
    /**
     * This method validates the CVV (Card Verification Value) of a credit card.
     * The CVV is valid if it is a three-digit number.
     *
     * @param cvv The CVV that needs to be validated.
     * @return true if the CVV is valid, false otherwise.
     */

    private boolean isValidCVV(String cvv) {
        String regex = "\\d{3}";
        return cvv.matches(regex);
    }
    /**
     * Validates the expiry date of a credit card.
     *
     * @param expiryCheck The expiry date of the credit card to be checked. The date is in the format "MM/yy".
     * @param orderDate The date when the order was placed.
     * @return true if the credit card is not expired at the time of the order, false otherwise.
     */
    private static boolean validateExpiryDate(String expiryCheck, LocalDate orderDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth expiryDate;

        try {
            expiryDate = YearMonth.parse(expiryCheck, formatter);

        } catch (DateTimeParseException dtpe) {
            return false;
        }
        return expiryDate.getYear() >= orderDate.getYear() && (expiryDate.getMonthValue() >= orderDate.getMonthValue() || expiryDate.getYear() != orderDate.getYear());
    }

    /**
     * Validates if the total price of an order is correct.
     *
     * @param totalPriceInPence The total price of the order in pence.
     * @param pizzasInOrder The array of pizzas in the order.
     //* @param definedRestaurants The array of defined restaurants.
     * @return true if the calculated total price (including the order charge) matches the total price of the order, false otherwise.
     */


    private boolean isTotalPriceCorrect(int totalPriceInPence, Pizza[] pizzasInOrder, Restaurant restaurant) {
        int calculatedTotalPriceInPence = 0;
        for (Pizza pizza : pizzasInOrder) {
            String itemName = pizza.name();
            for (Pizza menuPizza : restaurant.menu()) {
                if (menuPizza.name().equals(itemName)) {
                    calculatedTotalPriceInPence += menuPizza.priceInPence();
                    break;
                }
            }
        }
        calculatedTotalPriceInPence += SystemConstants.ORDER_CHARGE_IN_PENCE;
        return calculatedTotalPriceInPence == totalPriceInPence;
    }
    /**
     * Checks if all pizzas in an order are from the same restaurant.
     *
     * @param pizzasInOrder An array of Pizza objects representing the pizzas in the order.
     * @param restaurants An array of Restaurant objects representing the available restaurants.
     * @return The Restaurant object if all pizzas in the order are from the same restaurant, null otherwise.
     * <p>
     * This method iterates over each restaurant and for each restaurant, it checks each pizza in the order against the restaurant's menu.
     * If a pizza with the same name and price is found on the menu, a counter is incremented.
     * If the counter equals the number of pizzas in the order, it means all pizzas are from the same restaurant and that restaurant is returned.
     * If no such restaurant is found after checking all restaurants, null is returned.
     */

    private static Restaurant isMutipleRestaurant(Pizza[] pizzasInOrder, Restaurant[] restaurants) {
        for (Restaurant restaurant : restaurants) {
            int counter = 0;
            for (Pizza pizza : pizzasInOrder) {
                for (Pizza pizzaMenu : restaurant.menu()) {
                    if (pizza.name().equals(pizzaMenu.name()) && pizza.priceInPence() == pizzaMenu.priceInPence()) {
                        counter++;
                    }
                }
            }
            if (counter == pizzasInOrder.length) {
                return restaurant;
            }
        }
        return null;
    }

}

