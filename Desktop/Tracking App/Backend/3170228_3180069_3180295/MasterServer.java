import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.NetworkUtils;

public class MasterServer {
    private static final int PORT = 1234;

    // Create synchronized list of workers
    private static List<Worker> workers = Collections.synchronizedList(new ArrayList<>());

    // Initialize StatManager
    private static StatManager statManager = new StatManager();

    public static void main(String[] args) {

        // Create Server Socket that listens for incoming connections
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Master server is running and listening on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();

                try {
                    NetworkUtils.RequestType requestType = NetworkUtils.receiveRequestType(socket);

                    switch (requestType) {
                        // Create a new UserHandler object to handle the client's requests
                        case CLIENT:
                            System.out.println("Client connected: " + socket);
                            UserHandler userHandler = new UserHandler(socket, workers, statManager);
                            new Thread(userHandler).start();
                            break;
                        // Register the workers to the list
                        case WORKER:
                            Worker worker = (Worker) NetworkUtils.receiveObject(socket);
                            workers.add(worker);
                            System.out.println("Worker connected: IP = " + worker.getIpAddress() + ", Port = " + worker.getPort());
                            break;
                        case STATS:
                            System.out.println("Stats request received: " + socket);
                            String userId = (String) NetworkUtils.receiveObject(socket);
                            System.out.println("User ID: " + userId);
                            StatsHandler statsHandler = new StatsHandler(socket, statManager, userId);
                            new Thread(statsHandler).start();
                        default:
                            System.out.println("Unknown request type");
                            break;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}