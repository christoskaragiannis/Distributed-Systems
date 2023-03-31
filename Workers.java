public class Workers {
    private static final int CHUNK_SIZE = 100; // Number of waypoints per chunk

    public static void map(String key, String value, OutputCollector<String, String> output) {
        // Parse the GPX file
        GpxParser parser = new GpxParser();
        List<Waypoint> waypoints = parser.parse(value);
        
        // Group waypoints into chunks of CHUNK_SIZE
        List<List<Waypoint>> chunks = Lists.partition(waypoints, CHUNK_SIZE);
        
        // Emit a key-value pair for each chunk
        int i = 0;
        for (List<Waypoint> chunk : chunks) {
            String chunkValue = waypointsToString(chunk);
            output.collect(String.valueOf(i), chunkValue);
            i++;
        }
    }
    
    private static String waypointsToString(List<Waypoint> waypoints) {
        // Convert a list of waypoints to a string
        StringBuilder sb = new StringBuilder();
        for (Waypoint waypoint : waypoints) {
            sb.append(waypoint.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}