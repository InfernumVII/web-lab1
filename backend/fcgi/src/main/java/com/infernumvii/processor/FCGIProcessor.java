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
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fastcgi.FCGIInterface;
import com.infernumvii.Main;
import com.infernumvii.annotation.Request;
import com.infernumvii.annotation.MethodByRequestComparator;
// import com.infernumvii.annotation.model.Method;
import com.infernumvii.listener.RequestListener;

public class FCGIProcessor {
    private static final Map<Request, Method> REQUESTS = new LinkedHashMap<>();

    private static final RequestListener LISTENER = new RequestListener();

    static {
        Stream.of(LISTENER.getClass().getDeclaredMethods())
            .filter(m -> m.isAnnotationPresent(Request.class))
            .sorted(new MethodByRequestComparator())
            .forEachOrdered((m) -> {
                Request cmd = m.getAnnotation(Request.class);
                REQUESTS.put(cmd, m);
            });

        

    }

    private static String getBody() throws IOException {
        String CONTENT_LENGTH = FCGIInterface.request.params.getProperty("CONTENT_LENGTH");
        if (CONTENT_LENGTH == null) {
            return "";
        }
        int contentLength = Integer.parseInt(CONTENT_LENGTH);
        byte[] bytes = FCGIInterface.request.inStream.readNBytes(contentLength);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(byteArrayInputStream));
        return bufferedReader.lines().collect(Collectors.joining("\n"));
    }

    public static boolean process() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException{
        String method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
        String uri = FCGIInterface.request.params.getProperty("REQUEST_URI");
        Main.getFilePrinter().getPrintWriter().println(String.format("Method: %s\nURI: %s", method, uri));


        Main.getFilePrinter().getPrintWriter().println(generateUniqueUserID());
        for (Entry<Request, Method> entry : REQUESTS.entrySet()) {
            if ((entry.getKey().method().getName().equals(method) || entry.getKey().method().getName().equals("*")) &&
                 (entry.getKey().uri().equals(uri) || entry.getKey().uri().equals("*"))) {
                    Method methodActually = entry.getValue();
                    String body = getBody();
                    Main.getFilePrinter().getPrintWriter().println(String.format("Body: %s", body));
                    methodActually.invoke(LISTENER, body);
                    return true;
            }
        }
        return false;
    }

    public static String generateUniqueUserID(){
        //REMOTE_ADDR
        //HTTP_USER_AGENT
        //HTTP_ACCEPT_LANGUAGE
        //HTTP_SEC_CH_UA_PLATFORM
        String REMOTE_ADDR = System.getProperty("REMOTE_ADDR");
        String HTTP_USER_AGENT = System.getProperty("HTTP_USER_AGENT");
        String HTTP_ACCEPT_LANGUAGE = System.getProperty("HTTP_ACCEPT_LANGUAGE");
        String HTTP_SEC_CH_UA_PLATFORM = System.getProperty("HTTP_SEC_CH_UA_PLATFORM");
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


}
