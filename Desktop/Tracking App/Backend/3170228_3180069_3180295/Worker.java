import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import net.NetworkUtils;

public class Worker implements Runnable, Serializable {
    private int port;
    private String ipAddress;
    private transient ServerSocket serverSocket;

    public Worker() throws IOException {
        this.serverSocket = new ServerSocket(0); // 0 to let the system choose an available port
        this.port = serverSocket.getLocalPort();
        this.ipAddress = InetAddress.getLocalHost().getHostAddress();
    }

    public int getPort() {
        return port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected to worker: " + clientSocket);
    
                // Create a workerHandler for processing
                WorkerHandler workerHandler = new WorkerHandler(clientSocket);
                new Thread(workerHandler).start();
    
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String masterIpAddress;
        // Get the IP address of the master server from the user
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter the IP address of the master server: ");
            masterIpAddress = scanner.nextLine();
        }
    
        // Connect to the server
        Socket socket = new Socket(masterIpAddress, 1234);
    
        // Create a new Worker object
        Worker worker = new Worker();
    
        // Send a WorkerRequest to the server with the Worker object
        NetworkUtils.sendWorkerRequest(socket, worker);
    
        // Close the socket
        socket.close();
    
        // Start the Worker's server functionality
        new Thread(worker).start();
    }
}