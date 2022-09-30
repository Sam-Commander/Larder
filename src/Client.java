import java.io.*;
import java.net.*;
import java.util.*;

public class Client
{
    public final String MENU_TEMPLATE = " ~ Welcome to Larder (V2.0) ~\n" +
            "Enter the number of the menu item you wish to navigate to.\n" +
            "0. Exit\n" +
            "1. List recipes\n" +
            "2. Add recipe to database\n" +
            "3. Construct a meal plan\n";

    public static void main(String[] args) throws IOException {

        String recipeName;
        String recipeIngredients;
        String serverMessage;
        int numberOfDays;
        boolean check = false;
        Client clientObj = new Client();

        // Open a connection to the server, create the client socket
        Socket clientSocket = new Socket(Credentials.HOST, Credentials.PORT);

        // Create I/O streams to read/write data, PrintWriter and BufferedReader
        PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader inFromServer = new BufferedReader (new InputStreamReader(clientSocket.getInputStream()));
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        String selection;
        final String EXIT_CODE = "0";

        do {
            clientObj.displayMenu();
            selection = inFromUser.readLine();

            switch (selection) {
                case "0":
                    System.out.println("Goodbye"); // end
                    break;

                case "1":
                    String list = "List";
                    outToServer.println(list);
                    serverMessage = inFromServer.readLine();
                    serverMessage = serverMessage.replace("|", "\n");
                    System.out.println("The recipes in the database are:\n\n" + serverMessage);
                    break;

                case "2":
                    System.out.println("Enter the recipe name:");
                    recipeName = inFromUser.readLine();
                    System.out.println("Enter the ingredients. " +
                            "If you require multiples of an ingredient please put ' ~a number~' after it. For e.g. 'Carrot 3'." +
                            "\nType 'stop' when you're done.");

                    ArrayList<String> singleRecipeIngredients = new ArrayList<>();

                    while(true){

                        String ingredient = inFromUser.readLine();
                        if(Objects.equals(ingredient, "stop")){
                            break;
                        }
                        int ingredientAmount;

                        if (ingredient.matches(".*\\d.*")){
                            ingredientAmount = Integer.parseInt(ingredient.replaceAll("\\D+",""));
                            for (int x = 0; x < ingredientAmount; x++){
                                singleRecipeIngredients.add(ingredient.replaceAll("\\d","").trim());
                            }
                        }else{
                            singleRecipeIngredients.add(ingredient);
                        }
                    }

                    recipeIngredients = singleRecipeIngredients.toString()
                            .replaceAll("\\]", "")
                            .replaceAll("\\[", "");

                    String combo = recipeName + "|" + recipeIngredients;

                    // Send message to the server
                    outToServer.println(combo);

                    // Receive response from the server
                    serverMessage = inFromServer.readLine();
                    System.out.println(serverMessage);
                    System.out.println("");
                    break;

                case "3":

                    ArrayList<String> mealPlan = new ArrayList<>();

                    System.out.println("How many days would you like to meal plan for?");
                    numberOfDays = Integer.parseInt(inFromUser.readLine());

                    int count = 1;
                    for (int i = 0; i < numberOfDays; i++){
                        System.out.println("What meal would you like to have for day " + count);
                        mealPlan.add("'" + inFromUser.readLine() + "'");
                        count++;
                    }

                    String mealPlanMod = mealPlan.toString()
                            .replaceAll("\\]", "")
                            .replaceAll("\\[", "");

                    outToServer.println(mealPlanMod);

                    // Receive response from the server
                    serverMessage = inFromServer.readLine();
                    serverMessage = serverMessage.trim().replaceAll(".$", "");

                    ArrayList<String> mealPlanDB = new ArrayList<>(Arrays.asList(serverMessage.split(", ")));

                    outToConsole(mealPlanDB);
                    check = true;
                    break;
            }


        } while (!Objects.equals(selection, EXIT_CODE) && !check); // i.e. selection != EXIT_CODE

        // Close I/O streams and socket

        inFromServer.close();
        inFromUser.close();
        outToServer.close();
        clientSocket.close();
    }

    private static void outToConsole(ArrayList<String> allIngredients)
    {
        ArrayList<String> allIngredientsNumbered = new ArrayList<>();

        // Turns arraylist into hashset
        Set<String> allIngredientsSet = new HashSet<>(allIngredients);

        // Uses Collections to count frequency of dupes
        for(String ingredient: allIngredientsSet){
            allIngredientsNumbered.add(ingredient + " " + Collections.frequency(allIngredients, ingredient) + "\n");
        }

        // Alphabetises arraylist
        Collections.sort(allIngredientsNumbered);

        // Prints final ingredients
        System.out.println("\nYour total ingredients are:\n");
        System.out.println(allIngredientsNumbered.toString()
                .replaceAll("]", "")
                .replaceAll("\\[", "")
                .replaceAll(", ", "")
                .replaceAll(" 1", "")
        );
        System.out.println("Goodbye");
    }

    public void displayMenu() {
        System.out.println(MENU_TEMPLATE);
    }
}
