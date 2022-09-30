import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Database {

    // This method executes a QUERY
    public String executeQuery(String clientMessage) {

        try{
            Class.forName("org.postgresql.Driver");
            Connection con1 = DriverManager.getConnection(Credentials.URL, Credentials.USERNAME, Credentials.PASSWORD);

            if (clientMessage.equals("List")){

                String sql = "SELECT recipe_name FROM recipes;";

                PreparedStatement ps = con1.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                StringBuilder resultText = new StringBuilder();

                while (rs.next()) {
                    resultText.append(rs.getString(1)).append("|");
                }

                rs.close();
                ps.close();
                return resultText.toString();
            }else if (clientMessage.contains("|")){
                String[] clientMessageArray = clientMessage.split("\\|");
                String clientRecipe = clientMessageArray[0];
                String clientIngredients = clientMessageArray[1];

                String sql = "INSERT INTO recipes VALUES ('" + clientRecipe + "', '" + clientIngredients + "');";

                PreparedStatement ps = con1.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                rs.close();
                ps.close();
                con1.close();
            }else if (clientMessage.contains(", ")){

                ArrayList<String> mealPlanAL = new ArrayList<>(Arrays.asList(clientMessage.split(", ")));

                String sql = "SELECT recipe_ingredients FROM recipes WHERE recipe_name = " + mealPlanAL.get(0) + " ";
                mealPlanAL.remove(0);

                StringBuilder sb = new StringBuilder();

                for (String s : mealPlanAL) {
                    sb.append(" OR recipe_name = ").append(s);
                }

                String combo = sql + sb + ";";

                PreparedStatement ps = con1.prepareStatement(combo);
                ResultSet rs = ps.executeQuery();

                StringBuilder output = new StringBuilder();

                while (rs.next()) {
                    output.append(rs.getString(1)).append(", ");
                }

                rs.close();
                ps.close();
                return output.toString();
            }
        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    // This method establishes a DB connection & returns a boolean status
    public boolean establishDBConnection() {

        try{
            Class.forName("org.postgresql.Driver");
            Connection con2 = DriverManager.getConnection(Credentials.URL, Credentials.USERNAME, Credentials.PASSWORD);
            return con2.isValid(5); // 5 sec timeout

        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return false;
    }
}
