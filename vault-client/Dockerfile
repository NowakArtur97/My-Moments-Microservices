FROM ubuntu:20.04
RUN apt-get update && apt-get install -y software-properties-common curl gnupg2 && \
  curl -fsSL https://apt.releases.hashicorp.com/gpg | apt-key add - && \
  apt-add-repository "deb [arch=amd64] https://apt.releases.hashicorp.com $(lsb_release -cs) main" && \
  apt-get update && apt-get install -y \
  vault && \
  setcap cap_ipc_lock= /usr/bin/vault

COPY file /vault/file
COPY entrypoint/vault-init.sh ./
COPY entrypoint/token-swap.sh ./
COPY entrypoint/commands-wrapper.sh ./

CMD ./commands-wrapper.sh
