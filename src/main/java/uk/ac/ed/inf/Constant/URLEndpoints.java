package uk.ac.ed.inf.Constant;
    /**
    * Constants class representing various RESTful API endpoints.
    * It provides static final strings for common API endpoints.
     */
public class URLEndpoints {
    /**
     * API endpoint for retrieving information about restaurants.
     */
    public static final String RESTAURANTS = "/restaurants";

    /**
     * API endpoint for retrieving information about no-fly zones.
     */
    public static final String NO_FLY_ZONES =  "/noFlyZones";
    /**
     * API endpoint for retrieving information about the central area.
     */
    public static final String CENTRAL_AREA =  "/centralArea";
    /**
     * API endpoint for retrieving information about all orders.
     */
    public static final String ALL_ORDERS =  "/orders";
    /**
     * API endpoint for retrieving information about orders on a specific date.
     * The placeholder "{date}" should be replaced with the actual date.
     */
    public static final String ORDERS_DATE = ALL_ORDERS + "/{date}";
    /**
     * API endpoint for checking if the server is alive.
     */
    public static final String IS_ALIVE = "/isAlive";


}

