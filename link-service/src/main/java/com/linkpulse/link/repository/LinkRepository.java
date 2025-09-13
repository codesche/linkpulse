package com.linkpulse.link.repository;

import com.linkpulse.link.domain.Link;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepository extends JpaRepository<Link, UUID> {
    Optional<Link> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
}
