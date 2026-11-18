FROM amazonlinux:2023@sha256:8ee95c4096d455555cb208d1dcea7b759235afab02253351d566d22d904f2198

RUN yum update && yum install -y postgresql16 awscli jq

COPY backup.sh /backup.sh

CMD ["/backup.sh"]
