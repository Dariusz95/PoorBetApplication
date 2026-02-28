package com.poorbet.users.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class RsaKeyProvider {

    private final PrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final String keyId;

    public RsaKeyProvider(
            @Value("${jwt.private-key-location:classpath:keys/private.pem}") Resource privateKeyResource,
            @Value("${jwt.public-key-location:classpath:keys/public.pem}") Resource publicKeyResource,
            @Value("${jwt.kid:poorbet-key-1}") String keyId) {
        this.privateKey = loadPrivateKey(privateKeyResource);
        this.publicKey = (RSAPublicKey) loadPublicKey(publicKeyResource);
        this.keyId = keyId;
    }

    public PrivateKey privateKey() {
        return privateKey;
    }

    public RSAPublicKey publicKey() {
        return publicKey;
    }

    public String keyId() {
        return keyId;
    }

    private PrivateKey loadPrivateKey(Resource resource) {
        try {
            String pem = readPem(resource);
            String normalized = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(normalized);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Unable to load RSA private key", e);
        }
    }

    private PublicKey loadPublicKey(Resource resource) {
        try {
            String pem = readPem(resource);
            String normalized = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(normalized);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Unable to load RSA public key", e);
        }
    }

    private String readPem(Resource resource) throws IOException {
        try (var inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
