import java.io.*;
import java.net.*;

public class Users {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 8000);

        // Choose the file to send
        File fileToSend = new File("C:/Users/christos/Desktop/katanemimena/test.txt");

        // Send the file to the server
        try (OutputStream out = socket.getOutputStream()) {
            // Send the filename first
            out.write(fileToSend.getName().getBytes());
            out.write('\n');

            // Send the file contents
            try (BufferedReader in = new BufferedReader(new FileReader(fileToSend))) {
                String line;
                while ((line = in.readLine()) != null) {
                    out.write(line.getBytes());
                    out.write('\n');
                }
            }
        }

        socket.close();
    }
}