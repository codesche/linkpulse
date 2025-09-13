package com.linkpulse.link.domain;

import com.linkpulse.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "link", indexes = {
    @Index(name = "uk_link_shortcode", columnList = "shortCode", unique = true),
    @Index(name = "ix_link_owner", columnList = "ownerId")
})
public class Link extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String shortCode;

    @Column(nullable = false, length = 2048)
    private String originUrl;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID ownerId;

}

















