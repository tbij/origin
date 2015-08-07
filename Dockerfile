FROM java:8

COPY target/origin.jar origin.jar
COPY config.json config.json

CMD java -jar origin.jar

EXPOSE 8000
