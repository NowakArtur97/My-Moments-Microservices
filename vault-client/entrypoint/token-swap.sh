#!/bin/bash

echo "Replacing placeholder to token from environment variable..."

sed -i 's/VAULT_TOKEN/'"$VAULT_DEV_ROOT_TOKEN_ID"'/g' vault-init.sh
