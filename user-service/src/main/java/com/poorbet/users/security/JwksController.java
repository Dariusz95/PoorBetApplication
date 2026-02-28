package com.poorbet.users.security;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwksController {

    private final RsaKeyProvider rsaKeyProvider;

    public JwksController(RsaKeyProvider rsaKeyProvider) {
        this.rsaKeyProvider = rsaKeyProvider;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwks() {
        RSAPublicKey publicKey = rsaKeyProvider.publicKey();
        String n = encode(publicKey.getModulus());
        String e = encode(publicKey.getPublicExponent());

        Map<String, String> jwk = Map.of(
                "kty", "RSA",
                "use", "sig",
                "alg", "RS256",
                "kid", rsaKeyProvider.keyId(),
                "n", n,
                "e", e
        );

        return Map.of("keys", List.of(jwk));
    }

    private String encode(BigInteger value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(toUnsignedBytes(value));
    }

    private byte[] toUnsignedBytes(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes.length > 1 && bytes[0] == 0) {
            byte[] withoutSign = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, withoutSign, 0, withoutSign.length);
            return withoutSign;
        }
        return bytes;
    }
}
