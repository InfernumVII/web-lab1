package com.infernumvii;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import com.infernumvii.fastcgi.*;
import com.infernumvii.http.*;
import com.infernumvii.ReactiveFCGIProccessor;

public class ReactiveFCGIServer {
    private final ReactiveFCGIProccessor processor = new ReactiveFCGIProccessor();
    public static void main(String[] args) {
        new ReactiveFCGIServer().startServer().block();
    }
    
    public Mono<Void> startServer() {
        return createServerSocket()
            .flatMapMany(this::acceptConnections)
            .flatMap(this::handleConnection)
            .then();
    }
    
    private Mono<ServerSocket> createServerSocket() {
        return Mono.fromCallable(() -> new ServerSocket(9000))
            .subscribeOn(Schedulers.boundedElastic());
    }
    
    private Flux<Socket> acceptConnections(ServerSocket serverSocket) {
        Sinks.Many<Socket> sink = Sinks.many().multicast().onBackpressureBuffer();
        
        Mono.fromRunnable(() -> {
            try {
                while (!serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();
                    sink.tryEmitNext(socket);
                }
            } catch (IOException e) {
                sink.tryEmitError(e);
            }
        })
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe();
        
        return sink.asFlux();
    }
    
    private Mono<Void> handleConnection(Socket socket) {
        return processRequestInSteps(socket)
            .doFinally(signalType -> closeSocket(socket))
            .subscribeOn(Schedulers.boundedElastic());
    }
    
    public Mono<Void> processRequestInSteps(Socket socket) {
        return createFCGIRequest(socket)
            .flatMap(this::fillInputStream)
            .flatMap(this::setupParameters) 
            .flatMap(this::readFCGIParameters)
            .flatMap(this::setupOutputStreams)
            .flatMap(processor::processRequest)
            .flatMap(this::closeStreams)
            .subscribeOn(Schedulers.boundedElastic());
    }

    
    
    private Mono<FCGIRequest> createFCGIRequest(Socket socket) {
        return Mono.fromCallable(() -> {
            FCGIRequest request = new FCGIRequest();
            request.socket = socket;
            request.inStream = new FCGIInputStream(
                request.socket.getInputStream(),
                8192, 0, request
            );
            return request;
        });
    }
    
    private Mono<FCGIRequest> fillInputStream(FCGIRequest request) {
        return Mono.fromCallable(() -> {
            request.inStream.fill();
            return request;
        });
    }
    
    private Mono<FCGIRequest> setupParameters(FCGIRequest request) {
        return Mono.fromCallable(() -> {
            request.params = new Properties();
            
            switch(request.role) {
                case FCGIGlobalDefs.def_FCGIResponder:
                    request.params.put("ROLE", "RESPONDER");
                    break;
                case FCGIGlobalDefs.def_FCGIAuthorizer:
                    request.params.put("ROLE", "AUTHORIZER");
                    break;
                case FCGIGlobalDefs.def_FCGIFilter:
                    request.params.put("ROLE", "FILTER");
                    break;
            }
            
            return request;
        });
    }
    
    private Mono<FCGIRequest> readFCGIParameters(FCGIRequest request) {
        return Mono.fromCallable(() -> {
            request.inStream.setReaderType(FCGIGlobalDefs.def_FCGIParams);
            new FCGIMessage(request.inStream).readParams(request.params);
            request.inStream.setReaderType(FCGIGlobalDefs.def_FCGIStdin);
            return request;
        });
    }
    
    private Mono<FCGIRequest> setupOutputStreams(FCGIRequest request) {
        return Mono.fromCallable(() -> {
            request.outStream = new FCGIOutputStream(
                request.socket.getOutputStream(), 
                8192,
                FCGIGlobalDefs.def_FCGIStdout, 
                request
            );
            
            request.errStream = new FCGIOutputStream(
                request.socket.getOutputStream(), 
                512,
                FCGIGlobalDefs.def_FCGIStderr, 
                request
            );
            
            request.numWriters = 2;
            return request;
        });
    }
    
    private Mono<Void> closeStreams(FCGIContext context) {
        return closeStreams(context.getFcgiRequest());
    }

    private Mono<Void> closeStreams(FCGIRequest request) {
        return Mono.fromCallable(() -> {
            try {
                if (request.outStream != null) {
                    request.outStream.close();
                }
                if (request.errStream != null) {
                    request.errStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    
    private void closeSocket(Socket socket) {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }
}