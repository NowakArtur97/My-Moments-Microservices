FROM vault

RUN apk update && apk add bash

ADD config /vault/config
ADD file /vault/file

ADD entrypoint/entrypoint.sh /entrypoint.sh
ADD entrypoint/vault.sh /vault.sh

ENTRYPOINT ["/bash/sh", "/entrypoint.sh"]