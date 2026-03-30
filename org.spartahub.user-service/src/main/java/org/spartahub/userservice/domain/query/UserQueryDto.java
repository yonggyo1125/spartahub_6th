package org.spartahub.userservice.domain.query;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.spartahub.userservice.domain.UserId;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserQueryDto {
    @Getter
    @Builder
    public static class Search {
        private List<UserId> ids; // 회원 아이디 - 단일 또는 복수개 검색 가능
        private List<UUID> hubIds; // 허브 아이디 - 단일 또는 복수개 검색 가능
        private List<UUID> storeIds; // 업체 아이디 - 단일 또는 복수개 검색 가능
        private String name; // 회원명
        private List<String> email; // 이메일
        private String hubName; // 허브명
        private String storeName; // 업체명
        private String keyword; // 키워드(name + email + hubName + storeName 에서 키워드 검색)
    }
}
