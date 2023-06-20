import java.net.Socket;
import java.util.Map;

import net.NetworkUtils;

public class StatsHandler implements Runnable {
    private Socket socket;
    private StatManager statManager;
    private String userId;

    public StatsHandler(Socket socket, StatManager statManager, String userId) {
        this.socket = socket;
        this.statManager = statManager;
        this.userId = userId;
    }

    @Override
    public void run() {
        try {

            Map<String, Map.Entry<Double, Double>> comparison = statManager.compareUserStatsToAverage(userId);
            NetworkUtils.sendObject(socket, comparison);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
