
.PHONY: compile run local.machine local.build local.run local.kill local.delete

compile:
	@sbt assembly

run: compile
	@java -jar target/origin.jar

local.machine:
	@docker-machine create --driver virtualbox origin-local || true
	@VBoxManage controlvm origin-local natpf1 'local,tcp,,8000,,8000' || true

local.build: compile local.machine
	@eval `docker-machine env origin-local`; docker build -t origin .

local.run: local.build local.kill
	@eval `docker-machine env origin-local`; docker run -itdp 8000:8000 origin

local.kill:
	@eval `docker-machine env origin-local`; docker stop $$(docker ps -q) || true

local.delete:
	@docker-machine rm -f origin-local
