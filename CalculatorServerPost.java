import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import org.json.JSONObject;

public class CalculatorServerPost {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/calc", new CalculatorHandler());
        server.setExecutor(null);
        System.out.println("Servidor POST escuchando en http://localhost:8080/calc");
        server.start();
    }

    static class CalculatorHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1); // Método no permitido
                return;
            }

            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder body = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                body.append(line);
            }

            JSONObject requestJson = new JSONObject(body.toString());
            String op = requestJson.getString("op");
            double a = requestJson.getDouble("a");
            double b = requestJson.getDouble("b");
            double result;

            switch (op) {
                case "suma": result = a + b; break;
                case "resta": result = a - b; break;
                case "multi": result = a * b; break;
                case "div":
                    if (b == 0) {
                        sendJson(exchange, new JSONObject().put("error", "División entre cero"));
                        return;
                    }
                    result = a / b; break;
                default:
                    sendJson(exchange, new JSONObject().put("error", "Operación no válida"));
                    return;
            }

            JSONObject responseJson = new JSONObject().put("resultado", result);
            sendJson(exchange, responseJson);
        }

        private void sendJson(HttpExchange exchange, JSONObject json) throws IOException {
            byte[] responseBytes = json.toString().getBytes("utf-8");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }
}
