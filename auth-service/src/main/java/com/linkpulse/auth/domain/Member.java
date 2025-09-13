package com.linkpulse.auth.domain;

import com.linkpulse.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 인덱스 유연성: 트래픽/쿼리 패턴 따라 (username), (createdAt), (username, createdAt) 등 조합을 릴리즈 전에 실측 기반으로 조정.
 */

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "member", indexes = {
    @Index(name = "uk_member_username", columnList = "username", unique = true)
})
public class Member extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, length = 200)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String nickName;

}
