package com.infernumvii;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.infernumvii.fastcgi.FCGIInterface;
import com.infernumvii.fastcgi.FCGIRequest;
import com.infernumvii.annotation.Request;
import com.infernumvii.controller.TableController;
import com.infernumvii.annotation.MethodByRequestComparator;
// import com.infernumvii.annotation.model.Method;
import com.infernumvii.listener.RequestListener;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;


public class ReactiveFCGIProccessor {
    private static final Map<Request, Method> REQUESTS = new LinkedHashMap<>();
    private static final ReactiveFCGIRequestListener LISTENER = new ReactiveFCGIRequestListener();
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

    private Mono<FCGIContext> createFCGIContext(FCGIRequest fcgiRequest) {
        return Mono.fromCallable(() -> {
            return new FCGIContext(fcgiRequest);
        });
    }

    private Mono<FCGIContext> getMethodAndUri(FCGIContext fcgiContext) {
        return Mono.fromCallable(() -> {
            fcgiContext.setMethod(fcgiContext.getFcgiRequest().params.getProperty("REQUEST_METHOD"));
            fcgiContext.setUri(fcgiContext.getFcgiRequest().params.getProperty("REQUEST_URI"));
            return fcgiContext;
        });
    }

    private Mono<FCGIContext> revealMethod(FCGIContext fcgiContext) {
        return Mono.fromCallable(() -> {
            String method = fcgiContext.getMethod();
            String uri = fcgiContext.getUri();
            System.out.println(method);
            System.out.println(uri);
            for (Entry<Request, Method> entry : REQUESTS.entrySet()) {
                if ((entry.getKey().method().getName().equals(method) || entry.getKey().method().getName().equals("*")) &&
                    (entry.getKey().uri().equals(uri) || entry.getKey().uri().equals("*"))) {
                        Method methodActually = entry.getValue();
                        fcgiContext.setInvokeMethod(methodActually);
                        return fcgiContext;
                }
            }
            return fcgiContext;
        });
    }

    private Mono<FCGIContext> setUniqueUserID(FCGIContext context) {
        return Mono.fromCallable(() -> {
            FCGIRequest request = context.getFcgiRequest();
            String uniqueString = String.format("%s|%s|%s|%s", 
                request.params.getProperty("REMOTE_ADDR"), 
                request.params.getProperty("HTTP_ACCEPT_LANGUAGE"),
                request.params.getProperty("HTTP_USER_AGENT"), 
                request.params.getProperty("HTTP_SEC_CH_UA_PLATFORM"));
            MessageDigest md = null; 
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            byte[] theMD5digest = md.digest(uniqueString.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : theMD5digest) {
                sb.append(String.format("%02x", b));
            }
            context.setUserId(sb.toString());
            return context;
        });
    } 

    private Mono<FCGIContext> gTableController(FCGIContext fcgiContext) {
        return Mono.fromCallable(() -> {
            fcgiContext.setTableController(tableControllers.computeIfAbsent(fcgiContext.getUserId(), k -> new TableController()));
            return fcgiContext;
        });
    }

    private Mono<FCGIContext> makeAnswer(FCGIContext fcgiContext) {
        return Mono.fromCallable(() -> {
            try {
                return (Mono<Void>) fcgiContext.getInvokeMethod().invoke(new ReactiveFCGIRequestListener(fcgiContext));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        })
        .flatMap(mono -> mono)
        .then(Mono.just(fcgiContext));
    }
    
    private Mono<FCGIContext> configureOut(FCGIContext fcgiContext){
        return Mono.fromCallable(() -> {
            fcgiContext.setOut(new PrintStream(new BufferedOutputStream(fcgiContext.getFcgiRequest().outStream, 8192), true));
            return fcgiContext;
        });
    }
    
    private Mono<FCGIContext> getBody(FCGIContext fcgiContext) {
        return Mono.fromCallable(() -> {
            try {
                FCGIRequest request = fcgiContext.getFcgiRequest();
                String CONTENT_LENGTH = request.params.getProperty("CONTENT_LENGTH");
                if (CONTENT_LENGTH == null)
                    return fcgiContext;
                int contentLength = Integer.parseInt(CONTENT_LENGTH);
                byte[] bytes;
                bytes = request.inStream.readNBytes(contentLength);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(byteArrayInputStream));
                fcgiContext.setBody(bufferedReader.lines().collect(Collectors.joining("\n")));
                return fcgiContext;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Mono<FCGIContext> processRequest(FCGIRequest fcgiRequest) {
        return createFCGIContext(fcgiRequest)
            .flatMap(this::configureOut)
            .flatMap(this::getMethodAndUri)
            .flatMap(this::revealMethod)
            .flatMap(this::setUniqueUserID)
            .flatMap(this::gTableController)
            .flatMap(this::getBody)
            .flatMap(this::makeAnswer)
            .subscribeOn(Schedulers.boundedElastic()); 
    }
}

