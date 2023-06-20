import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.io.Serializable;
import java.util.ArrayList;
import net.GPXFile;




public class StatManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private HashMap<String, List<GPXFile>> userStats;
    private List<GPXFile> allUsersStat;
    private int totalUsers = 0;

    public StatManager() {
        this.userStats = new HashMap<>();
        this.allUsersStat = new ArrayList<>();
    }

    public synchronized void addUserStat(GPXFile gpxFile) {
        String userId = gpxFile.getUserId();

        // Add statistics for the user
        List<GPXFile> userStat = userStats.getOrDefault(userId, new ArrayList<>());
        userStat.add(gpxFile);
        this.userStats.put(userId, userStat);

        // Add statistics for all users
        this.allUsersStat.add(gpxFile);

        this.totalUsers++;
    }

    public synchronized GPXFile reduceUserStats(String userId) {
        List<GPXFile> userStat = this.userStats.get(userId);
        if (userStat == null) {
            return null;
        } else {
            return GPXFile.reduce(userStat);
        }
    }

    public synchronized GPXFile reduceAllUsersStats() {
        return GPXFile.reduce(this.allUsersStat);
    }

    public synchronized GPXFile getAverageUserReducedStats(String userId) {
        GPXFile reducedStats = reduceUserStats(userId);
        
        if (reducedStats == null) {
            return null;
        } else {
            for (String key : reducedStats.getStatistics().keySet()) { // Corrected here
                if (!key.equals("averageSpeed")) {
                    reducedStats.setStatistic(key, reducedStats.getStatistic(key) / this.userStats.get(userId).size());
                }
            }
            return reducedStats;
        }
    }
    
    public synchronized GPXFile getAverageAllUsersReducedStats() {
        GPXFile reducedStats = reduceAllUsersStats();
        for (String key : reducedStats.getStatistics().keySet()) { // Corrected here
            if (!key.equals("averageSpeed")) {
                reducedStats.setStatistic(key, reducedStats.getStatistic(key) / this.totalUsers);
            }
        }
        return reducedStats;
    }
    
    public synchronized Map<String, Map.Entry<Double, Double>> compareUserStatsToAverage(String userId) {
        GPXFile userAverage = getAverageUserReducedStats(userId);
        GPXFile allUsersAverage = getAverageAllUsersReducedStats();
    
        Map<String, Map.Entry<Double, Double>> comparison = new HashMap<>();
    
        for (String key : userAverage.getStatistics().keySet()) {
            if (!key.equals("averageSpeed")) {
                double userValue = userAverage.getStatistic(key);
                double averageValue = allUsersAverage.getStatistic(key);
                comparison.put(key, new SimpleEntry<>(userValue, averageValue));
            }
        }
    
        return comparison;
    }
}

