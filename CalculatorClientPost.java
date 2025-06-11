import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class CalculatorClientPost {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Operación (suma, resta, multi, div): ");
        String op = scanner.nextLine();

        System.out.print("Número A: ");
        double a = scanner.nextDouble();

        System.out.print("Número B: ");
        double b = scanner.nextDouble();

        JSONObject requestJson = new JSONObject();
        requestJson.put("op", op);
        requestJson.put("a", a);
        requestJson.put("b", b);

        try {
            URL url = new URL("http://localhost:8080/calc");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.write(requestJson.toString().getBytes("utf-8"));
            os.close();

            int responseCode = con.getResponseCode();
            System.out.println("Código de respuesta: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.has("resultado")) {
                System.out.println("Resultado: " + jsonResponse.getDouble("resultado"));
            } else {
                System.out.println("Error: " + jsonResponse.getString("error"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
