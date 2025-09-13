package com.infernumvii.processor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

import com.fastcgi.FCGIInterface;
import com.infernumvii.Main;
import com.infernumvii.annotation.Request;
// import com.infernumvii.annotation.model.Method;
import com.infernumvii.listener.RequestListener;

public class FCGIProcessor {
    private static final Map<Request, Method> REQUESTS = new LinkedHashMap<>();

    private static final RequestListener LISTENER = new RequestListener();

    static {
        Method[] declaredMethods = LISTENER.getClass().getDeclaredMethods();
        
        for (int i = declaredMethods.length - 1; i >= 0; i--) {
            Method m = declaredMethods[i];
            if (m.isAnnotationPresent(Request.class)) {
                Request cmd = m.getAnnotation(Request.class);
                REQUESTS.put(cmd, m);
            }
        }
        Main.getFilePrinter().getPrintWriter().println(REQUESTS.size());
    }

    private static String getBody() throws IOException {
        int contentLength = Integer.parseInt(FCGIInterface.request.params.getProperty("CONTENT_LENGTH"));
        byte[] bytes = FCGIInterface.request.inStream.readNBytes(contentLength);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(byteArrayInputStream));
        return bufferedReader.lines().collect(Collectors.joining("\n"));
    }

    public static boolean process() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException{
        String method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
        String uri = FCGIInterface.request.params.getProperty("REQUEST_URI");

        
        for (Entry<Request, Method> entry : REQUESTS.entrySet()) {
            Main.getFilePrinter().getPrintWriter().println(entry.getKey().method());
            Main.getFilePrinter().getPrintWriter().println(entry.getKey().uri());
            if ((entry.getKey().method().getName().equals(method) || entry.getKey().method().getName().equals("*")) &&
                 (entry.getKey().uri().equals(uri) || entry.getKey().uri().equals("*"))) {
                    Method methodActually = entry.getValue();
                    methodActually.invoke(LISTENER, getBody());
                    return true;
            }
        }
        return false;
    }
}
