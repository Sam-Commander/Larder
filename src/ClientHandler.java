import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    //declare variables
    Socket clientSocket;
    int clientNum;
    Database database;

    //Constructor
    public ClientHandler (Socket socket, int clientId, Database db)
    {
        clientSocket = socket;
        clientNum = clientId;
        database = db;
    }

    public void run() {

        try{
            System.out.println("ClientHandler started...");

            // Create I/O streams to read/write data, PrintWriter and BufferedReader
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream(), true);
            String clientMessage;

            // Receive messages from the client and send replies, until the user types "stop"
            while(!(clientMessage = inFromClient.readLine()).equals("stop")) {

                if (clientMessage.equals("List")){
                    String reply = database.executeQuery(clientMessage);
                    outToClient.println(reply);
                }else if (clientMessage.contains("|")){
                    String[] clientMessageArray = clientMessage.split("\\|");

                    System.out.print("Client's recipe name is " + clientMessageArray[0]);
                    System.out.println(" with ingredients " + clientMessageArray[1]);

                    String reply = database.executeQuery(clientMessage); // get rid of String reply = ?

                    // Send reply to Client:
                    outToClient.println("Recipe added to DB");
                } else if (clientMessage.contains(", ")){
                    String reply = database.executeQuery(clientMessage);
                    outToClient.println(reply);
                }
            }

            System.out.println("Client " + clientNum + " has disconnected");
            outToClient.println("Connection closed, Goodbye!");

            // Close I/O streams and socket
            inFromClient.close();
            outToClient.close();
            clientSocket.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
