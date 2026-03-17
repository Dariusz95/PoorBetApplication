package com.poorbet.users.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

@Component
public class RsaKeyProvider {

    @Value("${jwt.rsa.key-id:poorbet-key-1}")
    private String keyId;

    @Value("${jwt.rsa.private-key-location:classpath:keys/private.pem}")
    private String privateKeyLocation;

    @Value("${jwt.rsa.public-key-location:classpath:keys/public.pem}")
    private String publicKeyLocation;

    private final ResourceLoader resourceLoader;

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    public RsaKeyProvider(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    void init() {
        try {
            String privatePem = readPem(privateKeyLocation);
            String publicPem = readPem(publicKeyLocation);

            KeyFactory factory = KeyFactory.getInstance("RSA");
            this.privateKey = (RSAPrivateKey) parsePrivateKey(factory, privatePem);
            this.publicKey = (RSAPublicKey) parsePublicKey(factory, publicPem);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot initialize RSA keys", e);
        }
    }

    private String readPem(String location) {
        try {
            Resource resource = resourceLoader.getResource(location);
            if (!resource.exists()) {
                throw new IllegalStateException("RSA key resource does not exist: " + location);
            }
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read RSA key from location: " + location, e);
        }
    }

    private PrivateKey parsePrivateKey(KeyFactory factory, String pem) throws Exception {
        String normalized = normalizePem(pem);
        byte[] decoded = Base64.getDecoder().decode(normalized);
        return factory.generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    private PublicKey parsePublicKey(KeyFactory factory, String pem) throws Exception {
        String normalized = normalizePem(pem);
        byte[] decoded = Base64.getDecoder().decode(normalized);
        return factory.generatePublic(new X509EncodedKeySpec(decoded));
    }

    private String normalizePem(String pem) {
        return pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
    }

    public RSAPrivateKey privateKey() {
        return privateKey;
    }

    public RSAPublicKey publicKey() {
        return publicKey;
    }

    public String keyId() {
        return keyId;
    }

    public Map<String, Object> jwks() {
        RSAKey jwk = new RSAKey.Builder(publicKey)
                .keyID(keyId)
                .algorithm(com.nimbusds.jose.JWSAlgorithm.RS256)
                .build();
        return new JWKSet(jwk).toJSONObject();
    }
}
