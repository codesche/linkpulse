package com.linkpulse.token;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenStore {

    void save(UUID memberId, String token);
    Optional<String> load(UUID memberId);
    void delete(UUID memberId);

}
