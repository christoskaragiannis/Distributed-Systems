package net;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class GPXFile implements Serializable {
    private static final long serialVersionUID = 1L;

    private byte[] data;
    private String userId;
    Map<String, Double> statistics;

    public GPXFile(File file, String userId) throws IOException {
        this.data = Files.readAllBytes(file.toPath());
        this.userId = userId;
        this.statistics = new HashMap<>();
    }

    public GPXFile(byte[] data, String userId) {
        this.data = data;
        this.userId = userId;
        this.statistics = new HashMap<>();
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getStatistic(String key) {
        return statistics.get(key);
    }
    public Map<String, Double> getStatistics() {
        return this.statistics;
    }

    public void setStatistic(String key, Double value) {
        statistics.put(key, value);
    }

    public static List<GPXFile> chunkGPXFile(GPXFile gpxFile, int chunkSize)
            throws ParserConfigurationException, IOException, TransformerException, SAXException {
        List<GPXFile> gpxChunks = new ArrayList<>();

        // Parse the GPX data
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(gpxFile.getData());
        Document doc = builder.parse(inputStream);

        // Find the wpt elements
        NodeList wptList = doc.getElementsByTagName("wpt");

        // Split wpts into chunks and create new GPXFile objects
        for (int i = 0; i < wptList.getLength(); i += chunkSize) {
            int remainingWaypoints = wptList.getLength() - i;

            // Merge the last waypoints with the previous chunk if less than 2 waypoints
            // remain
            if (remainingWaypoints < 2 && !gpxChunks.isEmpty()) {
                gpxChunks.remove(gpxChunks.size() - 1);
                chunkSize += remainingWaypoints;
                i -= chunkSize;
                continue;
            }

            Document chunkDoc = builder.newDocument();
            Node root = chunkDoc.importNode(doc.getDocumentElement(), true);
            chunkDoc.appendChild(root);

            for (int j = i; j < i + chunkSize && j < wptList.getLength(); j++) {
                Node wpt = chunkDoc.importNode(wptList.item(j), true);
                root.appendChild(wpt);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(chunkDoc), new StreamResult(outputStream));

            GPXFile chunk = new GPXFile(outputStream.toByteArray(), gpxFile.getUserId());
            gpxChunks.add(chunk);
        }

        return gpxChunks;
    }

    public static GPXFile map(GPXFile gpxFile) throws ParserConfigurationException, IOException, SAXException {
        Map<String, Double> stats = calculateGPXStatistics(gpxFile);

        // Create a new GPXFile object with the updated values
        GPXFile mappedGPXFile = new GPXFile(gpxFile.getData(), gpxFile.getUserId());
        mappedGPXFile.statistics.putAll(stats);

        return mappedGPXFile;
    }

    public static GPXFile reduce(List<GPXFile> gpxFiles) {
        if (gpxFiles.isEmpty()) {
            throw new IllegalArgumentException("The input list of GPXFile objects must not be empty");
        }

        String userId = null;
        byte[] data = null;
        Map<String, Double> combinedStats = new HashMap<>();

        for (GPXFile gpxFile : gpxFiles) {
            if (gpxFile == null) {
                throw new IllegalArgumentException("The input list of GPXFile objects must not contain null values");
            }

            if (userId == null) {
                userId = gpxFile.getUserId();
                data = gpxFile.getData();
            }

            for (Map.Entry<String, Double> entry : gpxFile.statistics.entrySet()) {
                combinedStats.put(entry.getKey(), combinedStats.getOrDefault(entry.getKey(), 0.0) + entry.getValue());
            }
        }

        if (combinedStats.get("totalTime") > 0) {
            combinedStats.put("averageSpeed", combinedStats.get("totalDistance") / combinedStats.get("totalTime"));
        }

        GPXFile reducedGPXFile = new GPXFile(data, userId);
        reducedGPXFile.statistics.putAll(combinedStats);

        return reducedGPXFile;
    }

    private static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371e3; // Earth's radius in meters
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2 - lat1);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2)
                        * Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private static Map<String, Double> calculateGPXStatistics(GPXFile gpxFile)
            throws ParserConfigurationException, IOException, SAXException {
        Map<String, Double> stats = new HashMap<>();

        double totalDistance = 0;
        double totalAscent = 0;
        double totalTime = 0;

        // Parse the GPX data
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(gpxFile.getData());
        Document doc = builder.parse(inputStream);

        // Find the wpt elements
        NodeList wptList = doc.getElementsByTagName("wpt");
        if (wptList.getLength() < 2) {
            throw new IllegalArgumentException("At least two waypoints are required to calculate values");
        }

        Element prevWpt = null;
        for (int i = 0; i < wptList.getLength(); i++) {
            Element currentWpt = (Element) wptList.item(i);

            if (prevWpt != null) {
                double lat1 = Double.parseDouble(prevWpt.getAttribute("lat"));
                double lon1 = Double.parseDouble(prevWpt.getAttribute("lon"));
                double ele1 = Double.parseDouble(prevWpt.getElementsByTagName("ele").item(0).getTextContent());
                double time1 = ZonedDateTime.parse(
                        prevWpt.getElementsByTagName("time").item(0).getTextContent(),
                        DateTimeFormatter.ISO_DATE_TIME).toInstant().toEpochMilli();

                double lat2 = Double.parseDouble(currentWpt.getAttribute("lat"));
                double lon2 = Double.parseDouble(currentWpt.getAttribute("lon"));
                double ele2 = Double.parseDouble(currentWpt.getElementsByTagName("ele").item(0).getTextContent());
                double time2 = ZonedDateTime.parse(
                        currentWpt.getElementsByTagName("time").item(0).getTextContent(),
                        DateTimeFormatter.ISO_DATE_TIME).toInstant().toEpochMilli();

                totalDistance += haversineDistance(lat1, lon1, lat2, lon2);
                totalTime += (time2 - time1) / 1000; // Convert milliseconds to seconds
                totalAscent += Math.max(0, ele2 - ele1);
            }

            prevWpt = currentWpt;
        }

        double averageSpeed = totalTime > 0 ? totalDistance / totalTime : 0;

        stats.put("averageSpeed", averageSpeed);
        stats.put("totalAscent", totalAscent);
        stats.put("totalDistance", totalDistance);
        stats.put("totalTime", totalTime);

        return stats;
    }

    /*
     * public static void main(String[] args) {
     * try {
     * File file = new File(args[0]);
     * String userId = "myUserId";
     * GPXFile gpxFile = new GPXFile(file, userId);
     * 
     * // Chunk the GPX file
     * List<GPXFile> gpxChunks = chunkGPXFile(gpxFile, 3);
     * System.out.println("Number of chunks: " + gpxChunks.size());
     * 
     * // Map each chunk
     * List<GPXFile> mappedGPXFiles = new ArrayList<>();
     * for (GPXFile chunk : gpxChunks) {
     * mappedGPXFiles.add(map(chunk));
     * }
     * 
     * // Reduce the mapped GPX files
     * GPXFile reducedGPXFile = reduce(mappedGPXFiles);
     * 
     * // Output the results
     * System.out.println("Average speed: " +
     * reducedGPXFile.getStatistic("averageSpeed"));
     * System.out.println("Total ascent: " +
     * reducedGPXFile.getStatistic("totalAscent"));
     * System.out.println("Total distance: " +
     * reducedGPXFile.getStatistic("totalDistance"));
     * System.out.println("Total time: " +
     * reducedGPXFile.getStatistic("totalTime"));
     * 
     * } catch (IOException | ParserConfigurationException | TransformerException |
     * SAXException e) {
     * e.printStackTrace();
     * }
     * }
     */
}
