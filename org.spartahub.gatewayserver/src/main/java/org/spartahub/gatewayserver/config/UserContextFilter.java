package org.spartahub.gatewayserver.config;

import lombok.extern.slf4j.Slf4j;
import org.spartahub.gatewayserver.response.User;
import org.spartahub.gatewayserver.response.UserResponse;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Component
public class UserContextFilter implements GlobalFilter, Ordered {

    private final WebClient webClient;
    private static final String HEADER_TRACE_ID = "X-Trace-Id";
    private static final String HEADER_USER_UUID = "X-User-UUID";
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_ROLES = "X-User-Roles";
    private static final String HEADER_EMAIL = "X-User-Email";
    private static final String HEADER_SLACK_ID = "X-User-Slack-Id";
    private static final String HEADER_USER_NAME = "X-User-Name";
    private static final String HEADER_ENABLED = "X-User-Enabled";

    public UserContextFilter(WebClient.Builder webClientBuilder) {
        // 생성자에서 미리 빌드 (LoadBalancer 가 설정된 빌더여야 함)
        this.webClient = webClientBuilder.baseUrl("http://user-service").build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 진입 시점에 Trace-Id 생성
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();

        log.info("[Trace-ID: {}] Incoming Request: {} {}", traceId, method, path);

        // 필터 진입 시점에 외부에서 유입된 위조 가능성 있는 헤더를 먼저 제거
        ServerHttpRequest cleanedRequest = exchange.getRequest().mutate()
                .headers(httpHeaders -> {
                    httpHeaders.remove(HEADER_TRACE_ID);
                    httpHeaders.remove(HEADER_USER_UUID);
                    httpHeaders.remove(HEADER_USER_ID);
                    httpHeaders.remove(HEADER_USER_NAME);
                    httpHeaders.remove(HEADER_EMAIL);
                    httpHeaders.remove(HEADER_ROLES);
                    httpHeaders.remove(HEADER_SLACK_ID);
                    httpHeaders.remove(HEADER_ENABLED);
                })
                .header(HEADER_TRACE_ID, traceId)
                .build();
        ServerWebExchange mutatedExchange = exchange.mutate().request(cleanedRequest).build();

        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .flatMap(auth -> {
                    // JWT에서 UUID 추출(Subject)
                    String userId = auth.getToken().getSubject();
                    return webClient
                            .get()
                            .uri("/me")
                            .header(HEADER_USER_UUID, userId)
                            .retrieve()
                            .bodyToMono(UserResponse.class)
                            .timeout(Duration.ofMillis(500)) // 500ms 타임아웃 설정(장애 전파 방지)
                            .onErrorResume(e -> {
                                log.error("User Service 연동 실패 (ID: {}): {}", userId, e.getMessage());
                                return Mono.empty();
                            })
                            .flatMap(res -> {
                                if (res == null || !res.success() || res.data() == null) {
                                    log.warn("User 정보 누락 ID: {}", userId);
                                    return chain.filter(mutatedExchange);
                                }

                                User user = res.data();

                                // 조회된 정보를 요청 헤더에 실어서 전송
                                ServerHttpRequest request = mutatedExchange.getRequest().mutate()
                                        .header(HEADER_USER_ID, user.id().toString())
                                        .header(HEADER_USER_NAME, StringUtils.hasText(user.name()) ?
                                                URLEncoder.encode(user.name(), StandardCharsets.UTF_8) : "")
                                        .header(HEADER_EMAIL, user.email())
                                        .header(HEADER_ROLES, StringUtils.hasText(user.type()) ? "ROLE_" + user.type() : "")
                                        .header(HEADER_SLACK_ID, user.slackId())
                                        .header(HEADER_ENABLED, String.valueOf(user.enabled()))
                                        .build();

                                return chain.filter(mutatedExchange.mutate().request(request).build());

                            });

                })
                .switchIfEmpty(chain.filter(mutatedExchange)); // 토큰이 없는 경우(익명 사용자)는 통과 또는 에러 처리
    }

    @Override
    public int getOrder() {
        // SecurityFilter 이후에 적용되어야 함
        return SecurityWebFiltersOrder.AUTHORIZATION.getOrder() + 1;
    }
}
