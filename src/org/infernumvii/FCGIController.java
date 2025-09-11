package org.infernumvii;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fastcgi.FCGIInterface;
import com.google.gson.Gson;

public class FCGIController {
    private String port = "9000";
    private FCGIInterface fcgiInterface = new FCGIInterface();
    private Gson gson = new Gson();

    public FCGIController(String port){
        this.port = port;
        System.setProperty("FCGI_PORT", port);
    }

    private String getBody() throws IOException {
        int contentLength = Integer.parseInt(FCGIInterface.request.params.getProperty("CONTENT_LENGTH"));
        byte[] bytes = FCGIInterface.request.inStream.readNBytes(contentLength);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(byteArrayInputStream));
        return bufferedReader.lines().collect(Collectors.joining("\n"));
    }


    public void start(Function<String, String> handler) throws IOException{
        Main.getFilePrinter().getPrintWriter().println("Started");
        while (fcgiInterface.FCGIaccept() >= 0) {
            String method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
            Main.getFilePrinter().getPrintWriter().println(String.format("Request: %s", method));
            if (method.equals("POST")){
                String body = getBody();
                Main.getFilePrinter().getPrintWriter().println(String.format("Body: %s", body));
                
                String answer = (String) handler.apply(body);
                Main.getFilePrinter().getPrintWriter().println(String.format("Answer: %s", answer));
                
                System.out.println(
                    new Response.Builder()
                    .withStatusCode(StatusCode.C_200)
                    .withContentType(ContentType.APPLICATION_JSON)
                    .withBody(answer)
                    .build()
                    .toString()
                );
                continue;
            }
            
            System.out.println(
                new Response.Builder()
                .withStatusCode(StatusCode.C_405)
                .withContentType(ContentType.TEXT_PLAIN)
                .withBody("Method is not allowed")
                .build()
                .toString()
            );
        }
    }

}
