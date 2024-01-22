package uk.ac.ed.inf.Domain;
import uk.ac.ed.inf.Constant.CompassDirection;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import java.util.*;
public class FlightPathFinder {
    private final LngLat position;
    private final LngLat destination;
    private List<Cell> cameFrom;
    private final LngLatImplemenation lngLatImplementation;
    public FlightPathFinder(LngLat position, LngLat destination) {
        this.position = position;
        this.destination = destination;
        this.lngLatImplementation = new LngLatImplemenation();
        this.cameFrom = new ArrayList<>();
    }
    public List<Cell> getCameFrom() {
        return cameFrom;
    }
    /**
     * Performs pathfinding using the A* algorithm to find the optimal flight path.
     *
     * @param lngLatImplementation The implementation for geographical calculations.
     * @param noFlyZones           An array of named regions representing no-fly zones.
     * @param centralArea          The named region representing the central area to be avoided.
     */
    public void pathSearching(LngLatImplemenation lngLatImplementation, NamedRegion[] noFlyZones, NamedRegion centralArea) {
        Set<Cell> closedSet = new HashSet<>();
        PriorityQueue<Cell> openSet = new PriorityQueue<>(Comparator.comparingDouble(cell -> cell.f));
        Cell startCell = new Cell(position);
        startCell.g = 0.0;
        startCell.f = lngLatImplementation.distanceTo(position, destination);
        openSet.add(startCell);
        while (!openSet.isEmpty()) {
            Cell currentCell = openSet.poll();
            closedSet.add(currentCell);
            assert currentCell != null;
            if (lngLatImplementation.isCloseTo(currentCell.position, destination)) {
                this.cameFrom = reconstructPath(currentCell);
                return;
            }
            for (CompassDirection direction : CompassDirection.values()) {
                LngLat nextLat = lngLatImplementation.nextPosition(currentCell.position, direction.getVal());
                double weight = weight(currentCell.position, destination);
                double gTemp = currentCell.g + weight* lngLatImplementation.distanceTo(currentCell.position, nextLat);
                if (isNeighborValid(nextLat, closedSet, noFlyZones, centralArea, currentCell.position)) {
                    Cell nextCell = new Cell(nextLat);
                    boolean isInOpenSet = openSet.contains(nextCell);
                    if (gTemp < nextCell.g || !isInOpenSet) {
                        nextCell.parent = currentCell;
                        nextCell.g = gTemp;
                        nextCell.h = lngLatImplementation.distanceTo(nextLat, destination);
                        nextCell.f = nextCell.g + nextCell.h;
                        nextCell.angle = (float) direction.getVal();
                        if (isInOpenSet) {
                            openSet.remove(nextCell);  // Remove the old neighbor from the open set
                        }
                        openSet.add(nextCell);  // Add the updated neighbor to the open set
                    }
                }
            }
        }
    }
    /**
     * Checks if a neighboring cell is valid for exploration in the A* algorithm.
     *
     * @param nextLat       The geographical position of the neighboring cell.
     * @param closedSet     The set of closed cells that have already been explored.
     * @param noFlyZones    An array of named regions representing no-fly zones.
     * @param centralArea   The named region representing the central area to be avoided.
     * @param currentLat    The geographical position of the current cell.
     * @return {@code true} if the neighbor is valid, {@code false} otherwise.
     */
    private boolean isNeighborValid(LngLat nextLat, Set<Cell> closedSet, NamedRegion[] noFlyZones, NamedRegion centralArea, LngLat currentLat) {

            Cell neighborCell = new Cell(nextLat);
            if (closedSet.contains(neighborCell)) {
                return false;
            }

            if (lngLatImplementation.isInRegion(currentLat, centralArea) &&
                    !lngLatImplementation.isInRegion(nextLat, centralArea) &&
                    lngLatImplementation.isInRegion(destination, centralArea)) {
                return false;
            }

            // Check if the path between currentLat and nextLat intersects with any no-fly zone
            for (NamedRegion noFlyZone : noFlyZones) {
                if (isPathIntersectingNoFlyZone(currentLat, nextLat, noFlyZone)) {
                    return false;
                }
            }

            return true;
        }
    /**
     * Checks if the path between two geographical points intersects with a given no-fly zone.
     *
     * @param start       The starting position of the path.
     * @param end         The ending position of the path.
     * @param noFlyZone   The named region representing the no-fly zone.
     * @return {@code true} if the path intersects with the no-fly zone, {@code false} otherwise.
     */


        private boolean isPathIntersectingNoFlyZone(LngLat start, LngLat end, NamedRegion noFlyZone) {
            // Use the line segment intersection method to check if the path intersects with the no-fly zone
            return isLineIntersectingPolygon(start, end, noFlyZone);
        }
    /**
     * Checks if a line segment defined by two points intersects with a polygon defined by its vertices.
     *
     * @param start     The starting point of the line segment.
     * @param end       The ending point of the line segment.
     * @param polygon   The named region representing the polygon.
     * @return {@code true} if the line segment intersects with the polygon, {@code false} otherwise.
     */

        private boolean isLineIntersectingPolygon(LngLat start, LngLat end, NamedRegion polygon) {
            LngLat[] vertices = polygon.vertices();

            for (int i = 0; i < vertices.length - 1; i++) {
                if (GeometryUtils.doLineSegmentsIntersect(start, end, vertices[i], vertices[i + 1])) {
                    return true;
                }
            }

            // Check the last line segment (closing the polygon)
            return GeometryUtils.doLineSegmentsIntersect(start, end, vertices[vertices.length - 1], vertices[0]);
        }
    /**
     * Calculates the weight of traversing from the current position to the goal position.
     *
     * @param current   The current geographical position.
     * @param goal      The goal geographical position.
     * @return The weight of traversing from the current position to the goal position.
     */

        private static double weight(LngLat current, LngLat goal){
            double dist = new LngLatImplemenation().distanceTo(current, goal);
            return 1.0/(dist + 1.5);
        }
    /**
     * Reconstructs the path from the destination cell to the starting cell.
     *
     * @param current The current cell representing the destination.
     * @return The list of cells representing the reconstructed path.
     */
        public List<Cell> reconstructPath(Cell current) {

        while (current != null) {
            cameFrom.add(current);
            current = current.parent;
        }
        Collections.reverse(cameFrom);
        return cameFrom;
    }
    /**
     * Reverses the order of cells in the given path.
     *
     * @param path The original path to be reversed.
     * @return The reversed path.
     */
    public List<Cell> reverseReconstructedPath(List<Cell> path) {
        List<Cell> reversedPath = new ArrayList<>(path);
        Collections.reverse(reversedPath);
        return reversedPath;
    }
    /**
     * The {@code Cell} class represents a cell in the grid used by the A* algorithm for pathfinding.
     */
    public static class Cell {
        public LngLat position;
        double f;
        double g;
        double h;
        public float angle;
        Cell parent;
        public Cell(LngLat position) {
            this.position = position;
            this.parent = null;
            this.f = 0.0;
            this.g = 0.0;
            this.h = 0.0;
            this.angle = 0.0F;
        }
        @Override
        public int hashCode() {
            return Objects.hash(position);
        }
        /**
         * Checks if the cell is equal to another object based on its position.
         *
         * @param obj The object to compare with the cell.
         * @return {@code true} if the cell is equal to the object, {@code false} otherwise.
         */

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Cell other = (Cell) obj;
            return Objects.equals(position, other.position);
        }
    }
}
