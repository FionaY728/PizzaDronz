package uk.ac.ed.inf.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateDeserializer;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateSerializer;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
/**
 * This class is used to fetch data from a server.
 */
public class getDataFromServer {
    /**
     * Checks if the provided endpoint is a valid URL.
     *
     * @param endpoint The endpoint to be checked.
     * @return true if the endpoint is a valid URL, false otherwise.
     */
    private static boolean isUrlValid(String endpoint) {
        try {
            new URL(endpoint).toURI();
            new URI(endpoint);
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
    /**
     * Fetches orders from the API at the provided endpoint.
     *
     * @param endpoint The endpoint of the API.
     * @param orderClass The class of the orders to be fetched.
     * @return An array of orders fetched from the API.
     * @throws IOException If an I/O error occurs when fetching the orders.
     */
    public static <T> T getOrdersFromApi(String endpoint, Class<T> orderClass) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        if (!isUrlValid(endpoint)) {
            System.err.println("Invalid URL: " + endpoint);
            return null;
        }
        try {
            URL url = new URL(endpoint);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            return gson.fromJson(reader, orderClass);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
