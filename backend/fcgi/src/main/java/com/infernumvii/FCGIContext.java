package com.infernumvii;

import java.io.PrintStream;
import java.lang.reflect.Method;

import com.infernumvii.controller.TableController;
import com.infernumvii.fastcgi.FCGIRequest;

public class FCGIContext {
    private FCGIRequest fcgiRequest;
    private String userId;
    private String method;
    private String uri;
    private Method invokeMethod;
    private String body = "";
    private TableController tableController;
    private PrintStream out;
    
    public FCGIContext(FCGIRequest fcgiRequest) {
        this.fcgiRequest = fcgiRequest;
    }

    public FCGIRequest getFcgiRequest() {
        return fcgiRequest;
    }
    
    
    
    @Override
    public String toString() {
        return "FCGIContext [fcgiRequest=" + fcgiRequest + ", userId=" + userId + ", method=" + method + ", uri=" + uri
                + ", invokeMethod=" + invokeMethod + ", body=" + body + ", tableController=" + tableController
                + ", out=" + out + "]";
    }

    public void setFcgiRequest(FCGIRequest fcgiRequest) {
        this.fcgiRequest = fcgiRequest;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Method getInvokeMethod() {
        return invokeMethod;
    }

    public void setInvokeMethod(Method invokeMethod) {
        this.invokeMethod = invokeMethod;
    }

    public TableController getTableController() {
        return tableController;
    }

    public void setTableController(TableController tableController) {
        this.tableController = tableController;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public PrintStream getOut() {
        return out;
    }

    public void setOut(PrintStream out) {
        this.out = out;
    }

    
}
