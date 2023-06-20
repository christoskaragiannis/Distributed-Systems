package net;

import java.io.*;
import java.net.Socket;

public class NetworkUtils {
    public enum RequestType {
        CLIENT, WORKER, STATS
    }

    // Send Object over the socket
    public static void sendObject(Socket socket, Object object) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
    }

    // Receive Object from the Socket
    public static Object receiveObject(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        return objectInputStream.readObject();
    }
    //Send Stats request
    public static void sendStatsRequest(Socket socket, String userId) throws IOException {
        sendObject(socket, RequestType.STATS);
        sendObject(socket, userId);
    }

    // Send Worker request
    public static <Worker> void sendWorkerRequest(Socket socket, Worker worker) throws IOException {
        sendObject(socket, RequestType.WORKER);
        sendObject(socket, worker);
    }

    // Send Client request
    public static void sendClientRequest(Socket socket) throws IOException {
        sendObject(socket, RequestType.CLIENT);
    }

    // Read the object from the Socket- Cast it to request type- Return the request type object
    public static RequestType receiveRequestType(Socket socket) throws IOException, ClassNotFoundException {
        return (RequestType) receiveObject(socket);
    }
}