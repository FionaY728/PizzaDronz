package uk.ac.ed.inf.Domain;
/**
 * The {@code DroneMovement} record represents the movement details of a drone for a specific order.
 * It includes information such as order number, starting longitude and latitude, angle, and ending longitude and latitude.
 *
 * <p>This record is immutable, and its fields are set during the object's creation.</p>
 *
 * @param orderNo        The unique identifier for the order associated with the drone movement.
 * @param fromLongitude  The starting longitude of the drone movement.
 * @param fromLatitude   The starting latitude of the drone movement.
 * @param angle          The angle at which the drone moves.
 * @param toLongitude    The ending longitude of the drone movement.
 * @param toLatitude     The ending latitude of the drone movement.
 */
public record DroneMovement(
        String orderNo,
        double fromLongitude,
        double fromLatitude,
        double angle,
        double toLongitude,
        double toLatitude
) {
}