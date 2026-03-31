package org.spartahub.gatewayserver.config;

import lombok.RequiredArgsConstructor;
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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserContextFilter implements GlobalFilter, Ordered {

    private final WebClient.Builder webClient;
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_ROLES = "X-User-Roles";
    private static final String HEADER_EMAIL = "X-User-Email";
    private static final String HEADER_SLACK_ID = "X-User-Slack-Id";
    private static final String HEADER_USER_NAME = "X-User-Name";
    private static final String HEADER_ENABLED = "X-User-Enabled";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // SecurityContext에서 JWT의 Subject(UUID) 추출
        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .flatMap(auth -> {
                    // JWT에서 UUID 추출(Subject)
                    String userId = auth.getToken().getSubject();

                    return webClient.build()
                            .get()
                            .uri("http://user-service/me")
                            .header("X-User-UUID", userId)
                            .retrieve()
                            .bodyToMono(UserResponse.class)
                            .flatMap(res -> {
                                if (!res.success() || res.data() == null) {
                                    return chain.filter(exchange);
                                }

                                User user = res.data();
                                // 조회된 정보를 요청 헤더에 실어서 전송
                                ServerHttpRequest request = exchange.getRequest().mutate()
                                        .header(HEADER_USER_ID, user.id().toString())
                                        .header(HEADER_USER_NAME, user.name())
                                        .header(HEADER_EMAIL, user.email())
                                        .header(HEADER_ROLES, user.type())
                                        .header(HEADER_SLACK_ID, user.slackId())
                                        .build();

                                return chain.filter(exchange.mutate().request(request).build());

                            });

                })
                .switchIfEmpty(chain.filter(exchange)); // 토큰이 없는 경우(익명 사용자)는 통과 또는 에러 처리
    }

    @Override
    public int getOrder() {
        // SecurityFilter 이후에 적용되어야 함
        return SecurityWebFiltersOrder.AUTHORIZATION.getOrder() + 1;
    }
}
