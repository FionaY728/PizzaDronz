package uk.ac.ed.inf.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.ac.ed.inf.Controller.OrderProcessors;
import uk.ac.ed.inf.Domain.Deliveries;
import uk.ac.ed.inf.Constant.URLEndpoints;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
/**
 * This class is a client for the REST server.
 */
public class RestSeverClient {
    private final String baseurl;
    /**
     * Constructs a new RestServerClient with the specified base URL.
     *
     * @param baseurl the base URL of the REST server
     */
    public RestSeverClient(String baseurl) {
        this.baseurl = baseurl;
    }
    /**
     * Retrieves orders for a specific date from the server.
     *
     * @param date the date for which to retrieve orders
     * @return an array of Order objects for the specified date
     */
    public Order[] getOrdersForDate(String date) {
        String endpoint = baseurl + URLEndpoints.ORDERS_DATE.replace("{date}", date);
        try {
            return getDataFromServer.getOrdersFromApi(endpoint, Order[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Runs the client, retrieving and processing orders for a specific date.
     *
     * @param date the date for which to retrieve and process orders
     * @throws IOException if an I/O error occurs
     */
    public void run(String date) throws IOException {
        Order[] orders = getOrdersForDate(date);
        if (orders == null) {
            return;
        }
        String restaurantsEndpoint = baseurl + URLEndpoints.RESTAURANTS;
        Restaurant[] restaurants = getDataFromServer.getOrdersFromApi(restaurantsEndpoint, Restaurant[].class);

        String noFlyZonesEndpoint = baseurl + URLEndpoints.NO_FLY_ZONES;
        NamedRegion[] noFlyZones = getDataFromServer.getOrdersFromApi(noFlyZonesEndpoint, NamedRegion[].class);

        String centralAreaEndpoint = baseurl + URLEndpoints.CENTRAL_AREA;
        NamedRegion centralArea = getDataFromServer.getOrdersFromApi(centralAreaEndpoint, NamedRegion.class);


        LngLat at = new LngLat(-3.186874, 55.944494);
        try {
        OrderProcessors orderProcessor = new OrderProcessors(at, noFlyZones, centralArea);
        List<Deliveries> validOrderRecords = orderProcessor.processOrders(orders, restaurants);

        saveObjectsToFile(validOrderRecords, "resultfiles/deliveries-" + date + ".json");
        saveObjectsToFile(orderProcessor.getFlightPaths(), "resultfiles/flightpaths-" + date + ".json");
        GeoJsonConverter.generateGeoJson(orderProcessor.getFlightPaths(), "resultfiles/drone-" + date + ".geojson");


        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to process orders and save files", e);
        }
    }
    /**
     * Saves a list of objects to a file in JSON format.
     *
     * @param objects the objects to save
     * @param fileName the name of the file to which to save the objects
     * @throws IOException if an I/O error occurs
     */
        private <T> void saveObjectsToFile(List<T> objects, String fileName) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(objects);

        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
        }
    }
}

