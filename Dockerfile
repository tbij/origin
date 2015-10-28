FROM java:8

RUN apt-get update
RUN apt-get install -y build-essential ruby-dev rubygems
RUN gem install jekyll

COPY target/origin.jar origin.jar
COPY config.json config.json

CMD java -jar origin.jar

EXPOSE 8000
