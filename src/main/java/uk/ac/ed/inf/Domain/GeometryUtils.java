package uk.ac.ed.inf.Domain;

import uk.ac.ed.inf.ilp.data.LngLat;

public class GeometryUtils {
    /**
     * Checks if two line segments intersect.
     *
     * @param p1 First point of the first line segment.
     * @param q1 Second point of the first line segment.
     * @param p2 First point of the second line segment.
     * @param q2 Second point of the second line segment.
     * @return true if the line segments intersect, false otherwise.
     */
    protected static boolean doLineSegmentsIntersect(LngLat p1, LngLat q1, LngLat p2, LngLat q2) {
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);
        if (o1 != o2 && o3 != o4) {
            return true;
        }
        if (o1 == 0 && onSegment(p1, p2, q1)) {
            return true;
        }
        if (o2 == 0 && onSegment(p1, q2, q1)) {
            return true;
        }
        if (o3 == 0 && onSegment(p2, p1, q2)) {
            return true;
        }
        return o4 == 0 && onSegment(p2, q1, q2);
    }
    /**
     * Determines the orientation of ordered triplet (p, q, r).
     *
     * @param p First point.
     * @param q Second point.
     * @param r Third point.
     * @return 0 if p, q, and r are colinear, 1 if they are clockwise, 2 if counterclockwise.
     */
    private static int orientation(LngLat p, LngLat q, LngLat r) {
        double val = (q.lat() - p.lat()) * (r.lng() - q.lng()) -
                (q.lng() - p.lng()) * (r.lat() - q.lat());

        if (val == 0) return 0;
        return (val > 0) ? 1 : 2;
    }
    /**
     * Checks if point q lies on line segment pr.
     *
     * @param p First point of the line segment.
     * @param q Point to check.
     * @param r Second point of the line segment.
     * @return true if q lies on the line segment pr.
     */
    private static boolean onSegment(LngLat p, LngLat q, LngLat r) {
        return q.lng() <= Math.max(p.lng(), r.lng()) && q.lng() >= Math.min(p.lng(), r.lng()) &&
                q.lat() <= Math.max(p.lat(), r.lat()) && q.lat() >= Math.min(p.lat(), r.lat());
    }
}
