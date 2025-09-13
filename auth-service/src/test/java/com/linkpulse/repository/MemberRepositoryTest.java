package com.linkpulse.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.linkpulse.auth.domain.Member;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17")
        .withDatabaseName("linkpulse_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private MemberRepository memberRepository;

    private Member newMember(String username, String nick) {
        return Member.builder()
            .username(username)
            .passwordHash("ENC")
            .nickName(nick)
            .build();
    }

    @Test
    @DisplayName("findByUsername: 존재할 때 Optional.of")
    public void findByUsername_exists() {
        memberRepository.save(newMember("hgd", "홍길동"));
        Optional<Member> found = memberRepository.findByUsername("hgd");
        assertThat(found).isPresent();
        assertThat(found.get().getNickName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("유니크 제약 위반: username 중복 저장 시 예외")
    public void unique_username_violation() {
        memberRepository.save(newMember("hgd", "홍길동"));
        Member testMember = newMember("hgd2", "홍길동2");
        assertThatThrownBy(() -> memberRepository.saveAndFlush(testMember))
            .isInstanceOfAny(RuntimeException.class);
    }


}