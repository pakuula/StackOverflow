FROM golang:alpine

ARG UID

RUN apk add make gcc
RUN apk add musl-dev
RUN apk add sed

RUN test -z "${UID}" && { echo 'Missing UID build parameter'; exit 1; } || true
RUN adduser -D -u ${UID} user