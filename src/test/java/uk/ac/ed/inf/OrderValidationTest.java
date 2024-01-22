package uk.ac.ed.inf;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.ed.inf.Domain.OrderValidationImplementation;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OrderValidationTest {
    @Test
    public void testRestaurantClosedValidation() {

        Pizza[] pizzas = new Pizza[1];
        pizzas[0] = new Pizza("Proper Pizza", 1400);



        String lastMonth = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MM/yy"));
        CreditCardInformation creditCardInformation = new CreditCardInformation("1234567890123457",lastMonth, "222");
        Restaurant[] restaurants = new Restaurant[1];

        DayOfWeek[] openingDays = new DayOfWeek[]{DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};
        LngLat location = new LngLat(3.5, 9.7);
        restaurants[0] = new Restaurant("Restaurant Name", location, openingDays, pizzas);



        int totalPriceInPence = pizzas[0].priceInPence() + SystemConstants.ORDER_CHARGE_IN_PENCE;


        Order order = new Order("17ABDF4E", LocalDate.parse("2023-09-01"), OrderStatus.UNDEFINED, OrderValidationCode.UNDEFINED, totalPriceInPence, pizzas, creditCardInformation);


        OrderValidationImplementation orderValidationImplement = new OrderValidationImplementation();


        Assert.assertEquals(OrderValidationCode.UNDEFINED, orderValidationImplement.validateOrder(null, restaurants).getOrderValidationCode());
    }
    @Test
    public void testcardDate(){
        Pizza[] pizzas = new Pizza[1];
        pizzas[0] = new Pizza("p1", 70);
        // Set the credit card expiry date to the month before the current month
        String lastMonth = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MM/yy"));
        CreditCardInformation creditCardInformation = new CreditCardInformation("1234567890123457",lastMonth, "222");
        Restaurant[] restaurants = new Restaurant[1];
        DayOfWeek[] dayOfWeeks = new DayOfWeek[6];
        // The restaurant is open Monday to Saturday
        for (int i = 0; i < 6; i++) {
            dayOfWeeks[i] = DayOfWeek.of(i + 1);
        }
        restaurants[0] = new Restaurant("sss",new LngLat(3.5,9.7), dayOfWeeks,pizzas);

        // Set the total order price to the pizza price
        int totalPriceInPence = pizzas[0].priceInPence();
        Order order = new Order("1234", LocalDate.now(), OrderStatus.UNDEFINED, OrderValidationCode.UNDEFINED,totalPriceInPence,pizzas,creditCardInformation);
        order.setCreditCardInformation(creditCardInformation);
        OrderValidationImplementation orderValidationImplement = new OrderValidationImplementation();
        Assert.assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID,orderValidationImplement.validateOrder(order,restaurants).getOrderValidationCode());

    }
    @Test
    public void testPizzaFromDifferentRestaurants(){
        Pizza[] pizzas = new Pizza[2];
        pizzas[0] = new Pizza("p1", 70);
        pizzas[1] = new Pizza("p2", 80);
        CreditCardInformation creditCardInformation = new CreditCardInformation("1234567890123457","12/24", "222");
        Restaurant[] restaurants = new Restaurant[2];
        DayOfWeek[] dayOfWeeks = new DayOfWeek[6];

        for (int i = 0; i < 6; i++) {
            dayOfWeeks[i] = DayOfWeek.of(i + 1);
        }
        restaurants[0] = new Restaurant("sss",new LngLat(3.5,9.7), dayOfWeeks,new Pizza[]{pizzas[0]});
        restaurants[1] = new Restaurant("ttt",new LngLat(3.6,9.8), dayOfWeeks,new Pizza[]{pizzas[1]});


        int totalPriceInPence = pizzas[0].priceInPence() + pizzas[1].priceInPence();
        Order order = new Order("1234", LocalDate.now(), OrderStatus.UNDEFINED, OrderValidationCode.UNDEFINED,totalPriceInPence,pizzas,creditCardInformation);
        order.setCreditCardInformation(creditCardInformation);
        OrderValidationImplementation orderValidationImplement = new OrderValidationImplementation();
        Assert.assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS,orderValidationImplement.validateOrder(order,restaurants).getOrderValidationCode());

    }
    @Test

    public void testValidCreditCardNumber() {
        String cardNumber = "1234567890123456";
        Assert.assertTrue(isValidCreditCardNumber(cardNumber));
    }
    private boolean isValidCreditCardNumber(String cardNumber) {
        return cardNumber.matches("\\d{16}");
    }
    @Test

    public void testInvalidCreditCardNumber() {
        String cardNumber = "123456789012345";
        Assert.assertFalse(isValidCreditCardNumber(cardNumber));

        cardNumber = "12345678901234567";
        Assert.assertFalse(isValidCreditCardNumber(cardNumber));

        cardNumber = "12345678901234ab";
        Assert.assertFalse(isValidCreditCardNumber(cardNumber));
    }
    @Test
    public void testValidCVV() {
        String cvv = "123";
        Assert.assertTrue(isValidCVV(cvv));
    }
@Test

    public void testInvalidCVV() {
        String cvv = "12";
        Assert.assertFalse(isValidCVV(cvv));

        cvv = "1234";
        Assert.assertFalse(isValidCVV(cvv));

        cvv = "12a";
        Assert.assertFalse(isValidCVV(cvv));
    }

    private boolean isValidCVV(String cvv) {
        return cvv.matches("\\d{3}");
    }
    @Test
    public void testPizzaNotInMenu() {
        Pizza[] pizzas = new Pizza[2];
        pizzas[0] = new Pizza("p1", 70);
        pizzas[1] = new Pizza("p2", 80);
        CreditCardInformation creditCardInformation = new CreditCardInformation("1234567890123456","12/24", "222");
        Restaurant[] restaurants = new Restaurant[1];
        DayOfWeek[] dayOfWeeks = new DayOfWeek[6];
        //
        for (int i = 0; i < 6; i++) {
            dayOfWeeks[i] = DayOfWeek.of(i + 1);
        }
        restaurants[0] = new Restaurant("sss",new LngLat(3.5,9.7), dayOfWeeks,new Pizza[]{pizzas[0]});


        int totalPriceInPence = pizzas[0].priceInPence() + pizzas[1].priceInPence();
        Order order = new Order("1234", LocalDate.now(), OrderStatus.UNDEFINED, OrderValidationCode.UNDEFINED,totalPriceInPence,pizzas,creditCardInformation);
        order.setCreditCardInformation(creditCardInformation);
        OrderValidationImplementation orderValidationImplement = new OrderValidationImplementation();


        Assert.assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED,orderValidationImplement.validateOrder(order,restaurants).getOrderValidationCode());
    }
    @Test
    public void testIncorrectTotalPrice() {
        Pizza[] pizzas = new Pizza[1];
        pizzas[0] = new Pizza("p1", 70);
        CreditCardInformation creditCardInformation = new CreditCardInformation("1234567890123456","12/24", "222");
        Restaurant[] restaurants = new Restaurant[1];
        DayOfWeek[] dayOfWeeks = new DayOfWeek[6];

        for (int i = 0; i < 6; i++) {
            dayOfWeeks[i] = DayOfWeek.of(i + 1);
        }
        restaurants[0] = new Restaurant("sss",new LngLat(3.5,9.7), dayOfWeeks,pizzas);


        int totalPriceInPence = pizzas[0].priceInPence();
        Order order = new Order("1234", LocalDate.now(), OrderStatus.UNDEFINED, OrderValidationCode.UNDEFINED,totalPriceInPence,pizzas,creditCardInformation);
        order.setCreditCardInformation(creditCardInformation);
        OrderValidationImplementation orderValidationImplement = new OrderValidationImplementation();


        Assert.assertEquals(OrderValidationCode.TOTAL_INCORRECT,orderValidationImplement.validateOrder(order,restaurants).getOrderValidationCode());
    }




@Test

    public void testNoErrorValidation() {

        Pizza[] pizzas = new Pizza[1];
        pizzas[0] = new Pizza("p1", 70);


        String nextMonth = LocalDate.now().plusMonths(1).format(DateTimeFormatter.ofPattern("MM/yy"));
        CreditCardInformation creditCardInformation = new CreditCardInformation("1234567890123457", nextMonth, "222");

        Restaurant[] restaurants = new Restaurant[1];
        DayOfWeek[] dayOfWeeks = new DayOfWeek[7];

        for (int i = 0; i < 7; i++) {
            dayOfWeeks[i] = DayOfWeek.of(i + 1);
        }
        restaurants[0] = new Restaurant("sss", new LngLat(3.5,9.7), dayOfWeeks, pizzas);


        int totalPriceInPence = pizzas[0].priceInPence() + SystemConstants.ORDER_CHARGE_IN_PENCE;


        Order order = new Order("1234", LocalDate.now(), OrderStatus.UNDEFINED, OrderValidationCode.UNDEFINED, totalPriceInPence, pizzas, creditCardInformation);
        order.setCreditCardInformation(creditCardInformation);


        OrderValidationImplementation orderValidationImplement = new OrderValidationImplementation();


        Assert.assertEquals(OrderValidationCode.NO_ERROR, orderValidationImplement.validateOrder(order, restaurants).getOrderValidationCode());
    }





@Test
    public void testCardExpiry() {
        Pizza[] pizzas = new Pizza[1];
        pizzas[0] = new Pizza("p1", 70);


        String thisMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yy"));
        CreditCardInformation creditCardInformation = new CreditCardInformation("1234567890123457", thisMonth, "222");


        Restaurant[] restaurants = new Restaurant[1];
        DayOfWeek[] dayOfWeeks = new DayOfWeek[7];

        for (int i = 0; i < 7; i++) {
            dayOfWeeks[i] = DayOfWeek.of(i + 1);
        }
        restaurants[0] = new Restaurant("sss", new LngLat(3.5,9.7), dayOfWeeks, pizzas);


        int totalPriceInPence = pizzas[0].priceInPence() + SystemConstants.ORDER_CHARGE_IN_PENCE;


        Order order = new Order("1234", LocalDate.now(), OrderStatus.UNDEFINED, OrderValidationCode.UNDEFINED, totalPriceInPence, pizzas, creditCardInformation);


        OrderValidationImplementation orderValidationImplement = new OrderValidationImplementation();
        order.setCreditCardInformation(creditCardInformation);


        Assert.assertEquals(OrderValidationCode.NO_ERROR, orderValidationImplement.validateOrder(order, restaurants).getOrderValidationCode());
    }

@Test
    public void testNoErrorValidationFirst() {

        Pizza[] pizzas = new Pizza[1];
        pizzas[0] = new Pizza("p1", 70);


        String thisMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yy"));
        CreditCardInformation creditCardInformation = new CreditCardInformation("1234567890123457", thisMonth, "222");


        Restaurant[] restaurants = new Restaurant[1];
        DayOfWeek[] dayOfWeeks = new DayOfWeek[7];

        for (int i = 0; i < 7; i++) {
            dayOfWeeks[i] = DayOfWeek.of(i + 1);
        }
        restaurants[0] = new Restaurant("sss", new LngLat(3.5,9.7), dayOfWeeks, pizzas);


        int totalPriceInPence = pizzas[0].priceInPence() + SystemConstants.ORDER_CHARGE_IN_PENCE;


        Order order = new Order("1234", LocalDate.now(), OrderStatus.UNDEFINED, OrderValidationCode.UNDEFINED, totalPriceInPence, pizzas, creditCardInformation);


        if (order.getCreditCardInformation() == null) {
            System.out.println("Credit card information is not set!");
            return;
        }


        OrderValidationImplementation orderValidationImplement = new OrderValidationImplementation();


        Assert.assertEquals(OrderValidationCode.NO_ERROR, orderValidationImplement.validateOrder(order, restaurants).getOrderValidationCode());
    }




}
