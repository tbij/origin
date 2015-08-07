
.PHONY: jar jar-run docker docker-run

jar:
	@sbt assembly

jar-run: jar
	@java -jar target/origin.jar

docker: jar
	@docker build -t origin .

docker-run: docker
	@docker run -p 8000:8000 -it origin
