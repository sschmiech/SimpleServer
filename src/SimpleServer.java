/**
 * Created by Sebastian Schmiech on 19.01.2016.
 */
import java.net.*;
import java.io.*;

public class SimpleServer {
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java SimpleServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                new SimpleServerThread(serverSocket.accept()).start();
                System.out.println("Listen now on port " + portNumber);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}