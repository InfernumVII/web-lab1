package com.infernumvii.processor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.infernumvii.fastcgi.FCGIInterface;
import com.infernumvii.fastcgi.FCGIRequest;
import com.infernumvii.Main;
import com.infernumvii.annotation.Request;
import com.infernumvii.controller.TableController;
import com.infernumvii.annotation.MethodByRequestComparator;
// import com.infernumvii.annotation.model.Method;
import com.infernumvii.listener.RequestListener;

public class FCGIProcessor {
    private static final Map<Request, Method> REQUESTS = new LinkedHashMap<>();
    private static final RequestListener LISTENER = new RequestListener();
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
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

    

    public static boolean process() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException{
        String method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
        String uri = FCGIInterface.request.params.getProperty("REQUEST_URI");
        System.out.println(String.format("Method: %s\nURI: %s", method, uri));


        for (Entry<Request, Method> entry : REQUESTS.entrySet()) {
            if ((entry.getKey().method().getName().equals(method) || entry.getKey().method().getName().equals("*")) &&
                 (entry.getKey().uri().equals(uri) || entry.getKey().uri().equals("*"))) {
                    Method methodActually = entry.getValue();
                    final FCGIRequest currentRequest = FCGIInterface.request;
                    executor.execute(() -> {
                        try {
                            FCGIRequest request = currentRequest;
                            long startTime = System.nanoTime();
                            TableController tableController = gTableController(request);
                            RequestListener requestListener = new RequestListener(tableController, request, startTime);
                            methodActually.invoke(requestListener);
                            request.outStream.close();
                            request.errStream.close();
                        } catch (IllegalAccessException | InvocationTargetException | IOException e) {
                            e.printStackTrace();
                        }
                        
                    });
                    
                    return true;
            }
        }
        return false;
    }

    private static String getUniqueUserID(FCGIRequest request){
        //REMOTE_ADDR
        //HTTP_USER_AGENT
        //HTTP_ACCEPT_LANGUAGE
        //HTTP_SEC_CH_UA_PLATFORM
        String REMOTE_ADDR = request.params.getProperty("REMOTE_ADDR");
        String HTTP_USER_AGENT = request.params.getProperty("HTTP_USER_AGENT");
        String HTTP_ACCEPT_LANGUAGE = request.params.getProperty("HTTP_ACCEPT_LANGUAGE");
        String HTTP_SEC_CH_UA_PLATFORM = request.params.getProperty("HTTP_SEC_CH_UA_PLATFORM");
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

    private static TableController gTableController(FCGIRequest request){
        String userId = getUniqueUserID(request);
        tableControllers.putIfAbsent(userId, new TableController());
        return tableControllers.get(userId);
    }
}
