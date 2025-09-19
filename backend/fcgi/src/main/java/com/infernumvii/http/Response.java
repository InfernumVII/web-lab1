package com.infernumvii.http;

import java.util.StringJoiner;

public class Response {
    private StatusCode statusCode = null;
    private ContentType contentType = null;
    private String body = "";

    public Response(Builder builder){
        this.statusCode = builder.statusCode;
        this.contentType = builder.contentType;
        this.body = builder.body;
    }

    @Override
    public String toString(){
        StringJoiner stringJoiner = new StringJoiner("\n");
        stringJoiner.add(statusCode.toString());
        stringJoiner.add(contentType.toString());
        stringJoiner.add("\n"+body);
        return stringJoiner.toString();
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
    
    public ContentType getContentType() {
        return contentType;
    }
    
    public String getBody() {
        return body;
    }
    
    public static class Builder {
        private StatusCode statusCode = StatusCode.C_200;
        private ContentType contentType = ContentType.TEXT_PLAIN;
        private String body = "";

        public Builder(){
        }
        
        public Builder withStatusCode(StatusCode statusCode){
            this.statusCode = statusCode;
            return this;
        }

        public Builder withContentType(ContentType contentType){
            this.contentType = contentType;
            return this;
        }

        public Builder withBody(String body){
            this.body = body;
            return this;
        }

        public Response build(){
            return new Response(this);
        }

        public void setStatusCode(StatusCode statusCode) {
            this.statusCode = statusCode;
        }

        public void setContentType(ContentType contentType) {
            this.contentType = contentType;
        }

        public void setBody(String body) {
            this.body = body;
        }
        
    }
}
