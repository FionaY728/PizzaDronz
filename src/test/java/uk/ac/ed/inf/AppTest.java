package uk.ac.ed.inf;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Assert;
import uk.ac.ed.inf.Constant.URLEndpoints;
import uk.ac.ed.inf.Service.RestSeverClient;
import uk.ac.ed.inf.Service.getDataFromServer;
import uk.ac.ed.inf.ilp.data.NamedRegion;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.io.PrintStream;



/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{


    public void testIsRestServerReachable() {
        String baseUrl = "https://ilp-rest.azurewebsites.net";
        Assert.assertTrue(App.isRestServerReachable(baseUrl));
    }

    public void testIsRestServerNotReachable() {
        String baseUrl = "https://invalid-url";
        Assert.assertFalse(App.isRestServerReachable(baseUrl));
    }
    public void testIsValidDate() {
        String date = "2023-12-03";
        Assert.assertTrue(App.isValidDate(date));
    }
    public void testIsInvalidDate() {
        String date = "2023-12-036";
        Assert.assertFalse(App.isValidDate(date));
    }

    public void testNoFlyZoneServiceUnavailable() throws IOException {
        String baseUrl = "https://ilp-ret.azurewebsites.net"; // if this is a invalid url
        RestSeverClient restClient = new RestSeverClient(baseUrl);

        NamedRegion[] noFlyZones = getDataFromServer.getOrdersFromApi(baseUrl + URLEndpoints.NO_FLY_ZONES, NamedRegion[].class);

        assertNull("No-fly zones should be null when service is unreachable", noFlyZones);
    }
    public void testNoFlyZoneServiceAvailable() throws IOException {
        String baseUrl = "https://ilp-rest.azurewebsites.net";
        RestSeverClient restClient = new RestSeverClient(baseUrl);

        NamedRegion[] noFlyZones = getDataFromServer.getOrdersFromApi(baseUrl + URLEndpoints.NO_FLY_ZONES, NamedRegion[].class);

        assertNotNull("No-fly zones should not be null when service is reachable", noFlyZones);
    }


    public void testCentralAreaServiceUnavailable() throws IOException {
        String baseUrl = "https://ilp-ret.azurewebsites.net"; // if this is a invalid url
        RestSeverClient restClient = new RestSeverClient(baseUrl);

        NamedRegion centralArea = getDataFromServer.getOrdersFromApi(baseUrl + URLEndpoints.CENTRAL_AREA, NamedRegion.class);

        assertNull("Central area should be null when service is unreachable", centralArea);
    }


    // system test to check if the program runs within 60 seconds

    public void testRuntime() throws IOException {

        String date = "2023-12-03";
        String url = "https://ilp-rest.azurewebsites.net";
        long maxRunTime = 60000;


        long startTime = System.currentTimeMillis();


        App.main(new String[]{date, url});

        long endTime = System.currentTimeMillis();


        long runTime = endTime - startTime;

        System.out.println("runtime: " + runTime + " ms");


        assertTrue("run time should less than 60s", runTime < maxRunTime);
    }
    //check if three files are generated after running the program
    public void testMainTestSuccess() throws IOException {
        String[] args = {"2023-12-10", "https://ilp-rest.azurewebsites.net"};
        App.main(args);

        File deliveriesFile = new File("C:\\PizzaDronz\\resultfiles\\deliveries-2023-12-10.json");
        File flightPathFile = new File("C:\\PizzaDronz\\resultfiles\\flightpaths-2023-12-10.json");
        File droneFile = new File("C:\\PizzaDronz\\resultfiles\\drone-2023-12-10.geojson");

        assertTrue("Deliveries file should exist", deliveriesFile.exists());
        assertTrue("Flightpath file should exist", flightPathFile.exists());
        assertTrue("Drone GeoJSON file should exist", droneFile.exists());

        // Cleanup: delete the files after test
        deliveriesFile.delete();
        flightPathFile.delete();
        droneFile.delete();
    }


    public void testConfigFileMissingInfo() throws IOException {
        // Set up a new ByteArrayOutputStream to capture the System.err output.
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream newErr = new PrintStream(errContent);
        PrintStream originalErr = System.err;

        try {

            System.setErr(newErr);


            String[] args = {}; // no arguments
            App.main(args);

            // Check that the expected error message is printed to System.err
            String expectedErrorMessage = "Please re-enter a baseurl and date.";
            assertTrue("An error message should be printed when necessary information is missing from the configuration file",
                    errContent.toString().trim().contains(expectedErrorMessage));
        } finally {
            // After the test is complete, restore System.err to its original stream
            System.setErr(originalErr);
        }
    }






    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
