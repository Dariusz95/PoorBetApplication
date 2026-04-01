package com.poorbet.walletservice.util;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class UserIdResolver {

    private UserIdResolver() {
    }

    public static UUID fromSubject(String subject) {
        return UUID.nameUUIDFromBytes(subject.getBytes(StandardCharsets.UTF_8));
    }
}
