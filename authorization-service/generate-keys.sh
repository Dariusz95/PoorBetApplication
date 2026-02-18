#!/bin/sh

mkdir -p /app/secrets

if [ ! -f /app/secrets/private.pem ]; then
    openssl genpkey -algorithm RSA \
        -out /app/secrets/private.pem \
        -pkeyopt rsa_keygen_bits:2048

    openssl rsa \
        -pubout \
        -in /app/secrets/private.pem \
        -out /app/secrets/public.pem
fi