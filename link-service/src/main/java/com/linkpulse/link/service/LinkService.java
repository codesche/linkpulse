package com.linkpulse.link.service;

import com.linkpulse.error.BaseException;
import com.linkpulse.error.ErrorCode;
import com.linkpulse.link.domain.Link;
import com.linkpulse.link.repository.LinkRepository;
import com.linkpulse.link.dto.request.CreateLinkRequest;
import com.linkpulse.link.dto.response.CreateLinkResponse;
import com.linkpulse.link.util.ShortCodeGenerator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class LinkService {

    private final LinkRepository linkRepository;
    private final ShortCodeGenerator generator = new ShortCodeGenerator();

    public CreateLinkResponse create(UUID ownerId, CreateLinkRequest req) {
        String code = req.getAlias();
        if (code != null && !code.isBlank()) {
            if (linkRepository.existsByShortCode(code)) {
                throw new BaseException(ErrorCode.CONFLICT, "이미 사용중인 별칭입니다.");
            }
        } else {
            code = nextUniqueCode();
        }

        Link saved = linkRepository.save(Link.builder()
            .shortCode(code)
            .originUrl(req.getOriginalUrl())
            .ownerId(ownerId)
            .build());

        return CreateLinkResponse.builder()
            .id(saved.getId().toString())
            .shortCode(saved.getShortCode())
            .originalUrl(saved.getOriginUrl())
            .build();
    }

    private String nextUniqueCode() {
        String code;
        int tries = 0;
        do {
            code = generator.generate(8);
            if (++tries > 10) {
                // 방어
                code = generator.generate(10);
            }
        } while (linkRepository.existsByShortCode(code));
        return code;
    }

    @Transactional(readOnly = true)
    public Link getByCode(String code) {
        return linkRepository.findByShortCode(code)
            .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND, "단축 링크가 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public void verifyOwnerShip(Link link, UUID ownerId) {
        if (!link.getOwnerId().equals(ownerId)) {
            throw new BaseException(ErrorCode.FORBIDDEN, "소유자만 허용됩니다.");
        }
    }

}
