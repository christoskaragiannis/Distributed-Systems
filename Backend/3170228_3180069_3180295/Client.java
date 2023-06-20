import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import net.NetworkUtils;
import net.GPXFile;

public class Client {
    private static final int SERVER_PORT = 1234;

    public static void main(String[] args) throws ClassNotFoundException {
        String userId;
        String serverAddress;

        // Create a Scanner for User's inputs
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter Server IP address: ");
            serverAddress = scanner.nextLine();

            System.out.print("Enter user ID: ");
            userId = scanner.nextLine();

            while (true) {
                System.out.println("1. Send a GPX file");
                System.out.println("2. Request user stats comparison");
                System.out.print("Enter your choice (or 'exit' to quit): ");
                String choice = scanner.nextLine();

                if (choice.equalsIgnoreCase("exit")) {
                    break;
                }

                try (Socket clientSocket = new Socket(serverAddress, SERVER_PORT)) {
                    System.out.println("Connected to the server at " + serverAddress + ":" + SERVER_PORT);

                    switch (choice) {
                        case "1":
                            System.out.print("Enter file path: ");
                            String filePath = scanner.nextLine();

                            File file = new File(filePath);
                            if (!file.exists()) {
                                System.out.println("Error: File not found, Try again");
                                continue; 
                            }

                            // Send a ClientRequest to the server
                            NetworkUtils.sendClientRequest(clientSocket);

                            // Create a GPXFile object and send it to the server
                            GPXFile gpxFile = new GPXFile(file, userId);
                            NetworkUtils.sendObject(clientSocket, gpxFile);
                            System.out.println("Sent GPXFile to the MasterServer");

                            // Receive the GPXFile object from the server
                            GPXFile receivedGpxFile = (GPXFile) NetworkUtils.receiveObject(clientSocket);
                            System.out.println(
                                    "Received GPXFile from the server with userId: " + receivedGpxFile.getUserId());
                            //...
                            break;

                        case "2":
                            // Send a StatsRequest to the server
                            NetworkUtils.sendStatsRequest(clientSocket, userId);
                            System.out.println("Sent stats request to the MasterServer");

                            // Receive the comparison data from the server
                            @SuppressWarnings("unchecked")
                            Map<String, Map.Entry<Double, Double>> comparison = 
                                    (Map<String, Map.Entry<Double, Double>>) NetworkUtils.receiveObject(clientSocket);
                            System.out.println("Received comparison data from the server");

                            // Print the comparison data
                            for (Map.Entry<String, Map.Entry<Double, Double>> entry : comparison.entrySet()) {
                                String key = entry.getKey();
                                double userValue = entry.getValue().getKey();
                                double averageValue = entry.getValue().getValue();
                                System.out.println(key + ": User value = " + userValue + ", Average value = " + averageValue);
                            }
                            break;

                        default:
                            System.out.println("Invalid choice");
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}