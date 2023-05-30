package com.example.demo;


import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class Server {

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }
    public Server() throws CertificateException, IOException {
        https();
    }
    private void https() throws  CertificateException {
        SelfSignedCertificate cert = new SelfSignedCertificate();
        HttpHandler httpHandler = RouterFunctions.toHttpHandler(
                route(GET("/image"), request -> {
                    Path imagePath = Paths.get("/Users/ywz/http2/image2.jpg");
                    byte[] imageBytes = new byte[0];
                    try {
                        imageBytes = Files.readAllBytes(imagePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return ServerResponse.ok()
                                .contentType(MediaType.IMAGE_PNG)
                                .bodyValue(imageBytes);
                })
        );
        HttpServer server = HttpServer.create()
                .secure(spec -> spec.sslContext(SslContextBuilder.forServer(cert.certificate(), cert.privateKey())))
                .port(443)
                .protocol(HttpProtocol.HTTP11).handle( new ReactorHttpHandlerAdapter(httpHandler));
        server.bindNow();
    }
}
