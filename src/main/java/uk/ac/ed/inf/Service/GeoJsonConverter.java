package uk.ac.ed.inf.Service;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import uk.ac.ed.inf.Domain.DroneMovement;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * This class is used to convert drone movements into GeoJSON format.
 */
public class GeoJsonConverter {
    /**
     * Generates a GeoJSON file from a list of drone movements.
     *
     * @param droneMoves A list of drone movements.
     * @param outputFile The path of the output file.
     */
    public static void generateGeoJson(List<DroneMovement> droneMoves, String outputFile) {
        if (droneMoves == null || droneMoves.isEmpty()) {
            return;
        }
        ArrayList<Point> node = new ArrayList<>();
        for (DroneMovement move : droneMoves) {
            node.add(Point.fromLngLat(move.fromLongitude(), move.fromLatitude()));
        }
        LineString lineString = LineString.fromLngLats(node);
        FeatureCollection featureCollection = FeatureCollection.fromFeature(Feature.fromGeometry(lineString));
        writeGeoJsonToFile(featureCollection, outputFile);
    }
    /**
     * Writes a FeatureCollection to a file in GeoJSON format.
     *
     * @param featureCollection The FeatureCollection to be written.
     * @param outputFile The path of the output file.
     */
    private static void writeGeoJsonToFile(FeatureCollection featureCollection, String outputFile) {
        try (FileWriter fileWriter = new FileWriter(outputFile,false)) {
            fileWriter.write(featureCollection.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
