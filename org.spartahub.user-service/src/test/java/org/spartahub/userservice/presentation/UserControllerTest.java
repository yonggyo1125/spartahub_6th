package org.spartahub.userservice.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spartahub.userservice.domain.service.HubData;
import org.spartahub.userservice.domain.service.HubProvider;
import org.spartahub.userservice.domain.service.StoreData;
import org.spartahub.userservice.domain.service.StoreProvider;
import org.spartahub.userservice.presentation.dto.TokenRequest;
import org.spartahub.userservice.presentation.dto.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"kafka", "dev", "test"})
@Transactional
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper om;

    @MockitoBean
    HubProvider hubProvider;

    @MockitoBean
    StoreProvider storeProvider;

    UUID hubId =  UUID.randomUUID();
    UUID storeId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        given(hubProvider.get(hubId)).willReturn(new HubData("테스트 허브", "테스트 허브 주소"));
        given(storeProvider.get(storeId)).willReturn(new StoreData("테스트 업체", "테스트 업체 주소"));
    }

    @Test
    void signUp_and_token_issue_test() throws Exception {
        String email = UUID.randomUUID() + "@test.org";
        String password = "_aA123456";

        UserRequest.SignUp request = new UserRequest.SignUp();
        request.setEmail(email);
        request.setPassword(password);
        request.setName("테스트 사용자");
        request.setSlackId("U1234567");
        request.setType("HUB_MANAGER");
        request.setHubId(hubId);
        request.setStoreId(storeId);

        String body = om.writeValueAsString(request);

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
            ).andDo(print())
                .andExpect(status().isCreated());

        // 토큰 발급 테스트
        TokenRequest tokenRequest = new TokenRequest(email, password);
        mockMvc.perform(post("/token")
        .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(tokenRequest)))
                .andDo(print());


    }
}
