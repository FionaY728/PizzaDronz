package uk.ac.ed.inf.Domain;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;



import static java.lang.Math.sqrt;


public class LngLatImplemenation implements LngLatHandling {
    /**
     * This method calculates the Euclidean distance between two geographical points.
     * The points are represented by their longitude (lng) and latitude (lat).
     *
     * @param startPosition The starting position as a LngLat object.
     * @param endPosition The ending position as a LngLat object.
     * @return The Euclidean distance between the start and end positions.
     */

    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {

        return sqrt(Math.pow(startPosition.lng() - endPosition.lng(), 2) + Math.pow(startPosition.lat() - endPosition.lat(), 2));

    }
    /**
     * This method checks if the distance between two geographical points is less than or equal to a specified limit.
     * The points are represented by their longitude (lng) and latitude (lat).
     *
     * @param startPosition The starting position as a LngLat object.
     * @param otherPosition The other position as a LngLat object.
     * @return true if the distance between the start and other positions is less than or equal to the limit, false otherwise.
     */

    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        return distanceTo(startPosition, otherPosition) <= SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }
    /**
     * Checks if a given position is within a specified region.
     *
     * @param position The position to check, represented as a LngLat object.
     * @param region The region within which to check the position, represented as a NamedRegion object.
     * @return true if the position is within the region or on its boundary, false otherwise.
     * This method uses the ray casting algorithm to determine whether the point is inside the polygon.
     * It iterates over each edge of the polygon and checks if a horizontal ray from the point to the right crosses this edge.
     * If the point is directly on an edge or vertex of the polygon, it is considered inside.
     */
    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {

            if (position == null) {
                return false;
            }

            boolean oddEdgesCrossedOnTheLeft = false;  // 重置 oddEdgesCrossedOnTheLeft
            LngLat[] coordinates = region.vertices();
            for (int i = 0; i < coordinates.length; i++) {
                double x1 = coordinates[i].lng();
                double y1 = coordinates[i].lat();
                LngLat lngLat1 = new LngLat(x1, y1);

                double x2 = coordinates[(i + 1) % coordinates.length].lng();
                double y2 = coordinates[(i + 1) % coordinates.length].lat();
                LngLat lngLat2 = new LngLat(x2, y2);

        /*
          If the point is on the edge of the polygon, it is considered to be inside it.
          The point being on the edge means the distance from one vertex to the other vertex
          equals the sum of the distances from each of the vertices to the point.
         */
                if (distanceTo(lngLat1, position) + distanceTo(lngLat2, position) == distanceTo(lngLat1, lngLat2)) {
                    return true;
                }

        /*
          The point is inside the polygon if there is an odd number of edges crossed by
          the line y = this.lat
         */
                if ((y1 < position.lat() && position.lat() <= y2) || (y2 < position.lat() && position.lat() <= y1)) {
                    if (x1 + (position.lat() - y1) / (y2 - y1) * (x2 - x1) < position.lng()) {
                        oddEdgesCrossedOnTheLeft = !oddEdgesCrossedOnTheLeft;
                    }
                }
            }

            return oddEdgesCrossedOnTheLeft;
        }




    /**
     * Calculates the next position based on the given start position and angle.
     *
     * @param startPosition The starting position as a LngLat object.
     * @param angle The angle in degrees. If the angle is 999, the move length is set to 0.0.
     * @return A new LngLat object representing the next position.
     */
    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {
        double angleRadians = Math.toRadians(angle);
        double moveLength = (angle == 999) ? 0.0 : SystemConstants.DRONE_MOVE_DISTANCE;
        double startLat = startPosition.lat();
        double startLng = startPosition.lng();
        double newLat = startLat + moveLength * Math.sin(angleRadians);
        double newLng = startLng + moveLength * Math.cos(angleRadians);
        return new LngLat(newLng, newLat);
    }
}



