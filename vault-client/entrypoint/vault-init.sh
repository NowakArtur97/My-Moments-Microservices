#!/bin/bash

VAULT_RETRIES=5
echo "Vault is starting..."
until vault status > /dev/null 2>&1 || [ "$VAULT_RETRIES" -eq 0 ]; do
	echo "Waiting for vault to start...: $((VAULT_RETRIES--))"
	sleep 1
done

echo "Authenticating to vault..."
vault login token=vault-plaintext-root-token

echo "Initializing vault..."
vault secrets enable -version=2 -path=configuration-service kv

echo "Adding entries..."
vault kv put -format=json /secret/configuration-service @/vault/file/configuration-service.json

echo "Retrieving entries..."
vault kv get secret/configuration-service

echo "Complete..."
