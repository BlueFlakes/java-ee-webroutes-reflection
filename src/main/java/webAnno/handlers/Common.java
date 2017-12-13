package webAnno.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Common {
    public static void writeHttpOutputStream(HttpExchange httpExchange, String response) throws IOException {
        final byte[] finalResponseBytes = response.getBytes("UTF-8");
        httpExchange.sendResponseHeaders(200, finalResponseBytes.length);
        OutputStream os = httpExchange.getResponseBody();
        os.write(finalResponseBytes);
        os.close();
    }

    public static void redirect(HttpExchange httpExchange, String destination) throws IOException {
        httpExchange.getResponseHeaders().set("Location", destination);
        httpExchange.sendResponseHeaders(302, -1);
    }

    public static Map<String,String> getFormData(HttpExchange httpExchange) throws IOException {
        Map<String, String> postValues = new HashMap<>();

        String method = httpExchange.getRequestMethod();

        if(method.equals("POST")) {
            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();

            if (formData != null) {
                String[] pairs = formData.split("&");
                for(String pair : pairs){
                    String[] keyValue = pair.split("=");
                    String value = (keyValue.length > 1) ? URLDecoder.decode(keyValue[1], "UTF-8") : "";
                    postValues.put(keyValue[0], value);
                }
            }
        }

        return postValues;
    }
}
