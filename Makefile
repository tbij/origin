
.PHONY: jar jar.run docker docker.run local-machine

jar:
	@sbt assembly

jar.run: jar
	@java -jar target/origin.jar

docker: jar
	@docker build -t origin .

docker.run: docker
	@docker run -p 8000:8000 -it origin

local-machine:
	@docker-machine create --driver virtualbox origin-dev
	@VBoxManage controlvm origin-dev --natpf1 'tcp-port8000,tcp,,8000,,8000'
