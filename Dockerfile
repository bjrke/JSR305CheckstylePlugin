FROM java:8
MAINTAINER Jan Burkhardt <github@bjrke.de>
RUN useradd -d /home/theuser -m theuser
USER theuser
VOLUME /dist
WORKDIR /dist
RUN java -version
