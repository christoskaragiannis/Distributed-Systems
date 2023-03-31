import java.io.*;
import java.net.*;

public class Master {

    //Creates a TCP Server
    public static void main(String[] args) throws IOException {

        //Creates a Server Socket
        ServerSocket serverSocket = new ServerSocket(8000);
        System.out.println("Server started. Listening on port 8000...");

        while (true) {

            //Accepts the connection
            Socket clientSocket = serverSocket.accept();
            System.out.println("New connection from " + clientSocket.getInetAddress().getHostAddress());

            //Handles the request
            Thread thread = new Thread(new ClientHandler(clientSocket));
            thread.start();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received message from " + clientSocket.getInetAddress().getHostAddress() + ": " + inputLine);
                    out.println(inputLine);
                }

                clientSocket.close();
                System.out.println("Connection closed from " + clientSocket.getInetAddress().getHostAddress());
            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            }
        }
    }
}