import java.io.IOException;
import java.net.Socket;

import javax.xml.parsers.ParserConfigurationException;
import net.GPXFile;

import org.xml.sax.SAXException;

import net.NetworkUtils;

public class WorkerHandler implements Runnable {
    private final Socket clientSocket;

    public WorkerHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;

    }

    @Override
    public void run() {
        try {
            // Receive GPXFile object from the client
            GPXFile receivedGPXFile = (GPXFile) NetworkUtils.receiveObject(clientSocket);
            System.out.println("WorkerThread received GPXFile from MasterThread: " + clientSocket);
            // System.out.println("Speed of received " + receivedGPXFile.getAverageSpeed());
            // Process the received GPXFile using the map method
            GPXFile processedGPXFile = GPXFile.map(receivedGPXFile);
            // System.out.println("Average speed of processed :" +
            // processedGPXFile.getAverageSpeed());
            // Send the processed GPXFile object back to the client
            NetworkUtils.sendObject(clientSocket, processedGPXFile);
            System.out.println("Processed GPXFile sent from the worker to the MasterThread: " + clientSocket);

            // Close the connection with the client
            clientSocket.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {  
            e.printStackTrace();
        }
    }
}