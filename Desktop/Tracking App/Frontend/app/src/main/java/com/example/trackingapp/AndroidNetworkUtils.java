package com.example.trackingapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingDeque;

import net.NetworkUtils;
import net.GPXFile;






public class AndroidNetworkUtils {
    private static final int SERVER_PORT = 1234;

    public static void sendFileToServer(Context context, String serverAddress, String userId, Uri fileUri, OnFileReceivedListener listener) {
        new Thread(() -> {
            try {
                Socket clientSocket = new Socket(serverAddress, SERVER_PORT);
                NetworkUtils.sendClientRequest(clientSocket);

                InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
                byte[] data = readStreamToByteArray(inputStream);
                GPXFile gpxFile = new GPXFile(data, userId);

                NetworkUtils.sendObject(clientSocket, gpxFile);

                GPXFile receivedGpxFile = (GPXFile) NetworkUtils.receiveObject(clientSocket);

                clientSocket.close();

                // Run UI-related operations on the main thread
                ((Activity) context).runOnUiThread(() -> {
                    // Invoke the listener callback with the received GPXFile
                    listener.onFileReceived(receivedGpxFile);
                });
            } catch (IOException | ClassNotFoundException e) {
                Log.e("AndroidNetworkUtils", "Error sending file to server", e);
            }
        }).start();
    }

    public interface OnFileReceivedListener {
        void onFileReceived(GPXFile gpxFile);
    }

    private static byte[] readStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public static void sendStatsToServer(Context context, String serverAddress, String userId, OnStatsReceivedListener listener) {
        new Thread(() -> {
            try {
                Socket clientSocket = new Socket(serverAddress, SERVER_PORT);


                NetworkUtils.sendStatsRequest(clientSocket, userId);

                // Cast the received object into the expected type
                Map<String, Map.Entry<Double, Double>> receivedStats =
                        (Map<String, Map.Entry<Double, Double>>) NetworkUtils.receiveObject(clientSocket);

                clientSocket.close();

                ((Activity) context).runOnUiThread(() -> {
                    listener.onStatsReceived(receivedStats);
                });
            } catch (IOException | ClassNotFoundException e) {
                Log.e("AndroidNetworkUtils", "Error sending stats to server", e);
            }
        }).start();
    }

    public interface OnStatsReceivedListener {
        void onStatsReceived(Map<String, Map.Entry<Double, Double>> stats);
    }
}