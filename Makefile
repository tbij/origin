
.PHONY: compile run local.machine local.build local.run local.stop local.delete aws.machine aws.build aws.run aws.stop aws.delete

compile:
	@sbt assembly

run: compile
	@java -jar target/origin.jar

local.machine:
	@docker-machine create --driver virtualbox origin-local || true
	@VBoxManage controlvm origin-local natpf1 'local,tcp,,8000,,8000' || true

local.build: compile local.machine
	@eval `docker-machine env origin-local`; docker build -t origin .

local.run: local.build local.stop
	@eval `docker-machine env origin-local`; docker run -itdp 8000:8000 origin

local.stop:
	@eval `docker-machine env origin-local`; docker stop $$(docker ps -q) || true

local.delete:
	@docker-machine rm -f origin-local

aws.machine:
	@docker-machine create \
		--driver amazonec2 \
		--amazonec2-access-key '' \
		--amazonec2-secret-key '' \
		--amazonec2-vpc-id '' \
		--amazonec2-region 'eu-west-1' \
		--amazonec2-instance-type 't2.micro' \
		origin-aws || true
# todo: needs to deal with cname and adding a inbound port 80 rule

aws.build: compile aws.machine
	@eval `docker-machine env origin-aws`; docker build -t origin .

aws.run: aws.build aws.stop
	@eval `docker-machine env origin-aws`; docker run -itdp 80:8000 origin

aws.stop:
	@eval `docker-machine env origin-aws`; docker stop $$(docker ps -q) || true

aws.delete:
	@docker-machine rm -f origin-aws
