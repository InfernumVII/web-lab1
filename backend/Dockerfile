FROM gradle:jdk21


COPY fcgi/build.gradle /fcgi/
WORKDIR /fcgi
RUN gradle dependencies --no-daemon
COPY fcgi/src /fcgi/src
COPY fcgi/lib /fcgi/lib
RUN mkdir logs
RUN touch ./logs/log.txt
RUN gradle shadowJar --no-daemon --parallel
RUN mv /fcgi/build/libs/fcgi-all.jar /fcgi/fcgi.jar
COPY entrypoint.sh /fcgi/
RUN chmod +x entrypoint.sh