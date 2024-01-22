package uk.ac.ed.inf;

import org.junit.Test;
import uk.ac.ed.inf.Constant.URLEndpoints;
import uk.ac.ed.inf.Controller.OrderProcessors;
import uk.ac.ed.inf.Domain.Deliveries;
import uk.ac.ed.inf.Domain.DroneMovement;
import uk.ac.ed.inf.Service.RestSeverClient;
import uk.ac.ed.inf.Service.getDataFromServer;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IntegrationTest {

    @Test
    public void testOrderProcessingIntegration() throws IOException {
        String baseUrl = "https://ilp-rest.azurewebsites.net";
        RestSeverClient restClient = new RestSeverClient(baseUrl);

        // get test data from server
        Order[] orders = restClient.getOrdersForDate("2023-12-01");
        Restaurant[] restaurants = getDataFromServer.getOrdersFromApi(baseUrl + URLEndpoints.RESTAURANTS, Restaurant[].class);
        NamedRegion[] noFlyZones = getDataFromServer.getOrdersFromApi(baseUrl + URLEndpoints.NO_FLY_ZONES, NamedRegion[].class);
        NamedRegion centralArea = getDataFromServer.getOrdersFromApi(baseUrl + URLEndpoints.CENTRAL_AREA, NamedRegion.class);

        // Validation Data Acquisition
        assertNotNull("orders should not be empty", orders);
        assertNotNull("restaurants should not be empty", restaurants);
        assertNotNull("none fly zone data should not be empty", noFlyZones);
        assertNotNull("central area data should not be empty", centralArea);

        //initialise OrderProcessors and deal with orders
        LngLat at = new LngLat(-3.186874, 55.944494);// appleton tower
        OrderProcessors processor = new OrderProcessors(at, noFlyZones, centralArea);
        List<Deliveries> deliveries = processor.processOrders(orders, restaurants);

        //validate orders
        assertNotNull("Order processing should generate distribution records", deliveries);
        assertFalse("Distribution record list should not be empty", deliveries.isEmpty());

        //validate flight paths
        List<DroneMovement> flightPaths = processor.getFlightPaths();
        assertNotNull("Flight paths should be generated", flightPaths);
        assertFalse("Flight path list should not be empty", flightPaths.isEmpty());
    }
    @Test
    public void testOrderProcessingIntegrationWithInvalidDate() throws IOException {
        String baseUrl = "https://ilp-rest.azurewebsites.net";
        RestSeverClient restClient = new RestSeverClient(baseUrl);

        // Use an invalid date for testing
        String invalidDate = "2020-02-30"; // February 30th does not exist

        Order[] orders = restClient.getOrdersForDate(invalidDate);
        Restaurant[] restaurants = getDataFromServer.getOrdersFromApi(baseUrl + URLEndpoints.RESTAURANTS, Restaurant[].class);
        NamedRegion[] noFlyZones = getDataFromServer.getOrdersFromApi(baseUrl + URLEndpoints.NO_FLY_ZONES, NamedRegion[].class);
        NamedRegion centralArea = getDataFromServer.getOrdersFromApi(baseUrl + URLEndpoints.CENTRAL_AREA, NamedRegion.class);

        // Check if the orders are null or empty due to invalid date
        assertTrue("Orders should be null or empty on invalid date", orders == null || orders.length == 0);

        // Even if orders are empty, other data should be correctly fetched
        assertNotNull("Restaurants should not be empty", restaurants);
        assertNotNull("No-fly zone data should not be empty", noFlyZones);
        assertNotNull("Central area data should not be empty", centralArea);

        // Initialize OrderProcessors and deal with orders
        LngLat at = new LngLat(-3.186874, 55.944494); // Appleton Tower
        OrderProcessors processor = new OrderProcessors(at, noFlyZones, centralArea);

        // If orders are null or empty, processing should be skipped or handled accordingly
        List<Deliveries> deliveries = (orders != null) ? processor.processOrders(orders, restaurants) : new ArrayList<>();

        // Validate that no deliveries are processed for invalid date
        assertTrue("No deliveries should be processed for invalid date", deliveries.isEmpty());

        // Flight paths should also be empty
        List<DroneMovement> flightPaths = processor.getFlightPaths();
        assertTrue("Flight paths should be empty for invalid date", flightPaths.isEmpty());
    }



}


