package com.linkpulse.repository;

import com.linkpulse.auth.domain.Member;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByUsername(String username);
    boolean existsByUsername(String username);

}
