package uk.ac.ed.inf.Controller;
import uk.ac.ed.inf.Domain.*;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * This class processes orders.
 */
public class OrderProcessors {
    private final LngLat appletonTower;
    private final NamedRegion[] noFlyZones;
    private final NamedRegion centralArea;
    private final List<DroneMovement> flightPaths;
    private final Map<Restaurant,List<DroneMovement>> flightPathCache = new HashMap<>();
    /**
     * Constructs a new OrderProcessor with the specified parameters.
     *
     * @param appletonTower the location of Appleton Tower
     * @param noFlyZones the regions where drones are not allowed to fly
     * @param centralArea the central area of the city
     */
    public OrderProcessors(LngLat appletonTower, NamedRegion[] noFlyZones, NamedRegion centralArea) {
        this.appletonTower = appletonTower;
        this.noFlyZones = noFlyZones;
        this.centralArea = centralArea;
        this.flightPaths = new ArrayList<>();
    }
    /**
     * Processes the orders for the specified restaurants.
     *
     * @param orders the orders to process
     * @param restaurants the restaurants for which to process orders
     * @return a list of valid order records
     */

    public List<Deliveries> processOrders(Order[] orders, Restaurant[] restaurants) {
        List<Deliveries> validOrderRecords = new ArrayList<>();
        OrderValidationImplementation orderValidator = new OrderValidationImplementation();

        for (Order order : orders) {
            Order validatedOrder = orderValidator.validateOrder(order, restaurants);
            if (validatedOrder == null) {
                continue;
            }
            Deliveries record = createJsonOrderRecord(validatedOrder);

            if (validatedOrder.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED
                    && validatedOrder.getOrderValidationCode() == OrderValidationCode.NO_ERROR) {
                Restaurant restaurant = orderValidator.getRestaurant();

                if(flightPathCache.containsKey(restaurant)){
                    List<DroneMovement> cacheMovement = flightPathCache.get(restaurant);
                    flightPaths.addAll(cacheMovement);
                }else{
                    FlightPathFinder flightPathFinder = new FlightPathFinder(appletonTower, restaurant.location());
                    List<DroneMovement> droneMovements = allDroneMoves(validatedOrder, flightPathFinder, noFlyZones, centralArea);
                    flightPaths.addAll(droneMovements);
                    flightPathCache.put(restaurant,droneMovements);
                }

                order.setOrderStatus(OrderStatus.DELIVERED);
                record = createJsonOrderRecord(validatedOrder);
            }

            validOrderRecords.add(record);
        }

        return validOrderRecords;
    }
    /**
     * Returns the flight paths of the drones.
     *
     * @return a list of drone movements
     */
    public List<DroneMovement> getFlightPaths() {
        return flightPaths;
    }
    /**
     * Creates a JSON order record for the specified order.
     *
     * @param order the order for which to create a record
     * @return a Deliveries object representing the order record
     */

    private Deliveries createJsonOrderRecord(Order order) {
        return new Deliveries(
                order.getOrderNo(),
                order.getOrderStatus().name(),
                order.getOrderValidationCode().name(),
                order.getPriceTotalInPence()
        );
    }
    /**
     * Calculates all drone movements for the specified order.
     *
     * @param order the order for which to calculate movements
     * @param flightPathFinder the flight pathfinder to use
     * @param noFlyZones the regions where drones are not allowed to fly
     * @param centralArea the central area of the city
     * @return a list of drone movements
     */

    private List<DroneMovement> allDroneMoves(Order order, FlightPathFinder flightPathFinder, NamedRegion[] noFlyZones, NamedRegion centralArea) {
        List<DroneMovement> droneMovements = new ArrayList<>();
        flightPathFinder.pathSearching(new LngLatImplemenation(), noFlyZones, centralArea);
        List<DroneMovement> oneWay = addDroneMoves(flightPathFinder.getCameFrom(), order);
        List<DroneMovement> backWay = addDroneMoves(flightPathFinder.reverseReconstructedPath(flightPathFinder.getCameFrom()), order);
        droneMovements.addAll(oneWay);
        droneMovements.addAll(backWay);
        return droneMovements;
    }
    /**
     * Adds drone movements for the specified nodes and order.
     *
     * @param nodes the nodes for which to add movements
     * @param order the order for which to add movements
     * @return a list of drone movements
     */

    private List<DroneMovement> addDroneMoves(List<FlightPathFinder.Cell> nodes, Order order) {
        List<DroneMovement> droneMovements = new ArrayList<>();
        for (int i = 0; i < nodes.size() - 1; i++) {
            droneMovements.add(new DroneMovement(order.getOrderNo(),nodes.get(i).position.lng(), nodes.get(i).position.lat(), nodes.get(i).angle, nodes.get(i + 1).position.lng(),  nodes.get(i + 1).position.lat()));
        }
        droneMovements.add(new DroneMovement(order.getOrderNo(),  nodes.get(nodes.size() - 1).position.lng(), nodes.get(nodes.size() - 1).position.lat(), 999.0, nodes.get(nodes.size() - 1).position.lng(),  nodes.get(nodes.size() - 1).position.lat()));
        return droneMovements;
    }
}
