package com.infernumvii.processor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map.Entry;
import java.util.stream.Stream;
import com.infernumvii.annotation.Request;
import com.infernumvii.controller.TableController;
import com.infernumvii.fcgi.FCGIContext;
import com.infernumvii.fcgi.FCGIReadContext;
import com.infernumvii.fcgi.FCGIServer;
import com.infernumvii.annotation.MethodByRequestComparator;
// import com.infernumvii.annotation.model.Method;
import com.infernumvii.listener.RequestListener;

public class FCGIProcessor {
    private static final Map<Request, Method> REQUESTS = new LinkedHashMap<>();
    private static final RequestListener LISTENER = new RequestListener();
    private static final ConcurrentMap<String, TableController> tableControllers = new ConcurrentHashMap<>();

    static {
        Stream.of(LISTENER.getClass().getDeclaredMethods())
            .filter(m -> m.isAnnotationPresent(Request.class))
            .sorted(new MethodByRequestComparator())
            .forEachOrdered((m) -> {
                Request cmd = m.getAnnotation(Request.class);
                REQUESTS.put(cmd, m);
            });
    }

    

    public static boolean process(FCGIReadContext fcgiReadContext) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException{
        FCGIContext request = fcgiReadContext.getFcgiContext();
        String method = request.getParams().get("REQUEST_METHOD");
        String uri = request.getParams().get("REQUEST_URI");
        System.out.println(String.format("Method: %s\nURI: %s", method, uri));


        for (Entry<Request, Method> entry : REQUESTS.entrySet()) {
            if ((entry.getKey().method().getName().equals(method) || entry.getKey().method().getName().equals("*")) &&
                 (entry.getKey().uri().equals(uri) || entry.getKey().uri().equals("*"))) {
                    Method methodActually = entry.getValue();
                    
                    FCGIServer.getCpuExecutor().execute(() -> {
                            long startTime = System.nanoTime();
                            TableController tableController = gTableController(request);
                            RequestListener requestListener = new RequestListener(tableController, request, fcgiReadContext.getFcgiWriteContext(), startTime);
                            try {
                                methodActually.invoke(requestListener);
                                fcgiReadContext.getSocketChannel().close();
                            } catch (IllegalAccessException | InvocationTargetException | IOException e) {
                                throw new RuntimeException(e);
                            }
                            
                    });
                    
                    return true;
            }
        }
        return false;
    }

    private static String getUniqueUserID(FCGIContext request){
        //REMOTE_ADDR
        //HTTP_USER_AGENT
        //HTTP_ACCEPT_LANGUAGE
        //HTTP_SEC_CH_UA_PLATFORM
        
        String REMOTE_ADDR = request.getParams().get("REMOTE_ADDR");
        String HTTP_USER_AGENT = request.getParams().get("HTTP_USER_AGENT");
        String HTTP_ACCEPT_LANGUAGE = request.getParams().get("HTTP_ACCEPT_LANGUAGE");
        String HTTP_SEC_CH_UA_PLATFORM = request.getParams().get("HTTP_SEC_CH_UA_PLATFORM");
        String uniqueString = String.format("%s|%s|%s|%s", REMOTE_ADDR, HTTP_USER_AGENT, HTTP_ACCEPT_LANGUAGE, HTTP_SEC_CH_UA_PLATFORM);
        MessageDigest md = null; 
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
        }
        byte[] theMD5digest = md.digest(uniqueString.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : theMD5digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static TableController gTableController(FCGIContext request){
        String userId = getUniqueUserID(request);
        tableControllers.putIfAbsent(userId, new TableController());
        return tableControllers.get(userId);
    }
}
