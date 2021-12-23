package ru.romanow.rsocket.server;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class ApiServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiServerApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> rss() {
        return RouterFunctions.route()
                .GET("/api/v1/generate", this::generate)
                .build();
    }

    @NotNull
    public Mono<ServerResponse> generate(@NotNull ServerRequest serverRequest) {
        return rSocketRequester()
                .route("generate")
                .retrieveMono(String.class)
                .flatMap(text -> ok().body(BodyInserters.fromValue(text)));
    }

    @Bean
    public RSocketRequester rSocketRequester() {
        return RSocketRequester
                .builder()
                .rsocketConnector(connector -> connector.reconnect(Retry.indefinitely()))
                .dataMimeType(MimeTypeUtils.TEXT_PLAIN)
                .tcp("localhost", 7090);
    }
}