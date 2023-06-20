import java.io.IOException;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;
import net.NetworkUtils;
import net.GPXFile;


public class UserHandler implements Runnable {
    private final Socket clientSocket;
    private final List<Worker> workers;
    private final StatManager statManager;

    public UserHandler(Socket clientSocket, List<Worker> workers, StatManager statManager) {
        this.clientSocket = clientSocket;
        this.workers = workers;
        this.statManager = statManager;
    }
    

    @Override
    public void run() {
        try {

            // Receive GPXFile object from the client
            GPXFile receivedGpxFile = (GPXFile) NetworkUtils.receiveObject(clientSocket);
            System.out.println("Received GPXFile from client with userId: " + receivedGpxFile.getUserId());

            // Chunk the GPX file
            List<GPXFile> gpxChunks = GPXFile.chunkGPXFile(receivedGpxFile, 3);
            System.out.println("Number of GPXFile chunks: " + gpxChunks.size());

            // Send chunks to workers using the Round-Robin method

            List<GPXFile> processedGPXFiles = new ArrayList<>(Collections.nCopies(gpxChunks.size(), null));

            // Create an Object as the monitor and an int variable to keep track of the
            // completed chunks
            Object monitor = new Object();
            int[] completedChunks = new int[] { 0 };

            List<Thread> threads = new ArrayList<>();
            for (int i = 0; i < gpxChunks.size(); i++) {
                final int chunkIndex = i;
                Thread workerThread = new Thread(() -> {
                    Worker worker = workers.get(chunkIndex % workers.size());
                    try (Socket workerSocket = new Socket(worker.getIpAddress(), worker.getPort())) {
                        NetworkUtils.sendObject(workerSocket, gpxChunks.get(chunkIndex));
                        System.out.println(
                                "Sending GPXFile chunk " + chunkIndex + " to worker " + (chunkIndex % workers.size()));

                        // Receive the processed GPXFile object from the worker
                        GPXFile processedGpxFile = (GPXFile) NetworkUtils.receiveObject(workerSocket);
                        if (processedGpxFile == null) {
                            throw new IllegalStateException("Received a null processed GPXFile object from the worker");
                        }
                        System.out.println("Received GPXFile chunk " + chunkIndex + " from worker "
                                + (chunkIndex % workers.size()));

                        // Add the processed GPXFile object to the synchronized list
                        synchronized (monitor) {
                            processedGPXFiles.set(chunkIndex, processedGpxFile);
                            System.out.println("Added GPXFile chunk " + chunkIndex + " to the list");
                            completedChunks[0]++;

                            // Notify the monitor if all chunks are completed
                            if (completedChunks[0] == gpxChunks.size()) {
                                monitor.notify();
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
                threads.add(workerThread);
                workerThread.start();
            }

            // Wait for all chunks to be completed
            synchronized (monitor) {
                while (completedChunks[0] < gpxChunks.size()) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Reduce the processed GPXFile objects
            GPXFile reducedGPXFile = GPXFile.reduce(processedGPXFiles);
            System.out.println("Reduced processed GPXFile objects");

            // Update the user stats in the StatManager
            statManager.addUserStat(reducedGPXFile);
            System.out.println("Updated the stats of the user: " + reducedGPXFile.getUserId());
            
            // Send the reduced GPXFile object back to the client
            NetworkUtils.sendObject(clientSocket, reducedGPXFile);
            System.out.println("Sent reduced GPXFile back to the client");

            // Close the connection with the client
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        } catch (TransformerException e1) {
            e1.printStackTrace();
        } catch (SAXException e1) {
            e1.printStackTrace();
        }
    }
}