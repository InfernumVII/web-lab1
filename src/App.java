import java.io.IOException;
import com.fastcgi.*;

public class App {
    public static void main(String[] args) throws IOException {
        System.setProperty("FCGI_PORT", "25501");
        var fcgiInterface = new FCGIInterface();
        while (fcgiInterface.FCGIaccept() >= 0) {
            var method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
    
            if (method.equals("GET")) {
                System.out.println("Content-Type: text/plain\n");
                System.out.println("Hello from java");
            }
        }
    }
}
