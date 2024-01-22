package uk.ac.ed.inf;
import uk.ac.ed.inf.Service.RestSeverClient;
import uk.ac.ed.inf.Constant.URLEndpoints;
import java.io.IOException;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Properties;

public class App {
    /**
     * The main method that runs the application based on command line arguments or settings in the configuration file.
     *
     * @param args The command line arguments, which should contain the date and base URL.
     * @throws IOException If an I/O error occurs.
     */
    public static void main(String[] args) throws IOException {

        if (args.length >= 2) {
            String date = args[0];
            String baseurl = args[1];
            runWithDate(date, baseurl);
        } else {
            String dateFromConfig = getDateFromConfig("date");
            String baseurlFromConfig = getDateFromConfig("baseurl");
            if (dateFromConfig != null && baseurlFromConfig != null) {
                runWithDate(dateFromConfig, baseurlFromConfig);
            } else {
                System.err.println("Please re-enter a baseurl and date.");
            }
        }
    }
    /**
     * Runs the application for a specific date and base URL.
     *
     * @param date The date to process.
     * @param baseurl The base URL of the REST server.
     * @throws IOException If an I/O error occurs.
     */
    private static void runWithDate(String date, String baseurl) throws IOException {
        if (!isRestServerReachable(baseurl)) {
            System.err.println("please provide a url that is reachable to the rest service");
            System.exit(2);
        }
        if (!isValidDate(date)) {
            System.err.println("Invalid date");
            System.exit(2);
        }
        RestSeverClient restSeverClient = new RestSeverClient(baseurl);
        restSeverClient.run(date);
    }
    /**
     * Checks if the REST server is reachable.
     *
     * @param baseUrl The base URL of the REST server.
     * @return true if the REST server is reachable, false otherwise.
     */
    static boolean isRestServerReachable(String baseUrl) {
        try {
            var temp = new URL(baseUrl + URLEndpoints.IS_ALIVE);
            HttpURLConnection httpURLConnection = (HttpURLConnection) temp.openConnection();
            httpURLConnection.setRequestMethod("HEAD");
            int responseCode = httpURLConnection.getResponseCode();
            return responseCode == 200;
        } catch (IOException e) {
            System.err.println("The URL is invalid: " + e);
            return false;
        }
    }
    /**
     * Validates the date.
     *
     * @param date The date to validate.
     * @return true if the date is valid, false otherwise.
     */
    static boolean isValidDate(String date) {
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate.parse(date, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            System.err.println("The Date is invalid " + e);
            return false;
        }
    }
    /**
     * Gets the date from the configuration file.
     *
     * @param key The key in the configuration file.
     * @return The date from the configuration file, or null if an I/O error occurs.
     */
    private static String getDateFromConfig(String key) {
        Properties properties = new Properties();
        try (var input = new FileInputStream("config.properties")) {
            properties.load(input);
            return properties.getProperty(key);
        } catch (IOException e) {
            return null;
        }
    }
}
