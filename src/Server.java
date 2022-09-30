import java.io.*;
import java.net.*;

public class Server
{
    public static void main(String[] args) throws IOException {

        // Open the server socket
        ServerSocket serverSocket = new ServerSocket(Credentials.PORT);
        System.out.println("Server is running" );
        int clientID = 0;

        //Create a Database object and check the connection with establishDBConnection():
        Database serverOb = new Database();

        if (!serverOb.establishDBConnection()){
            // If the db connection fails, print:
            System.out.println("DB connection fail, stopping.");
        } else {
            // else, print:
            System.out.println("Server is now connected to DB");
        }

        // Continuously listen for client requests
        while (true) {
            // Accept new connection and create the client socket
            Socket clientSocket = serverSocket.accept();

            // Increment clientId. The clientId is not reassigned once used.
            clientID++;

            // Display clientId and IP address:
            System.out.println("Client " + clientID + " connected with IP " + clientSocket.getInetAddress().getHostAddress());

            // Create a new client handler object and start the thread (call ClientHandler)
            ClientHandler CH = new ClientHandler(clientSocket, clientID, serverOb);
            new Thread(CH).start();
        }
    }
}
