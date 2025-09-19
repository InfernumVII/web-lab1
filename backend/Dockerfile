FROM gradle:jdk21

COPY fcgi /fcgi
WORKDIR /fcgi
RUN gradle shadowJar
RUN mv /fcgi/build/libs/fcgi-all.jar /fcgi/fcgi.jar
COPY entrypoint.sh /fcgi
RUN chmod +x entrypoint.sh