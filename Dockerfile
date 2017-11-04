FROM registry.ap-southeast-1.aliyuncs.com/wiki0/java:8
VOLUME /tmp
ADD ./docker-weixin-0.0.1-SNAPSHOT.jar app.jar
#RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

EXPOSE 8080