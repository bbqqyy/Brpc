package com.bqy.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer{
    @Override
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();

        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        server.requestHandler(request -> {
            System.out.println(request.method()+":"+request.uri());

            request.response()
                    .putHeader("content-type","text/plain")
                    .end("hello");
        });
        server.requestHandler(new HttpServerHandler());
        server.listen(port,result -> {
            if(result.succeeded()){
                System.out.println("succeed");
            }else {
                System.out.println("failed");
            }
        });
    }
}
