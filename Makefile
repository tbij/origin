
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
	@read -p 'VPC ID: ' vpc ;\
	read -p 'Access key: ' accesskey ;\
	read -p 'Secret key: ' secretkey ;\
	groupid=$$(aws ec2 create-security-group \
		--group-name 'origin' \
		--description 'Origin' \
		--vpc-id $$vpc \
		--query 'GroupId' \
		--output text) ;\
	aws ec2 authorize-security-group-ingress \
		--group-id $$groupid \
		--protocol 'tcp' \
		--port 80 \
		--cidr '0.0.0.0/0' ;\
	docker-machine create \
		--driver amazonec2 \
		--amazonec2-access-key $$accesskey \
		--amazonec2-secret-key $$secretkey \
		--amazonec2-vpc-id $$vpc \
		--amazonec2-region 'eu-west-1' \
		--amazonec2-instance-type 't2.micro' \
		--amazonec2-security-group 'origin' \
		origin-aws || true
# todo: needs to deal with cname

aws.build: compile aws.machine
	@eval `docker-machine env origin-aws`; docker build -t origin .

aws.run: aws.build aws.stop
	@eval `docker-machine env origin-aws`; docker run -itdp 80:8000 origin

aws.stop:
	@eval `docker-machine env origin-aws`; docker stop $$(docker ps -q) || true

aws.delete:
	@read -p 'This will delete the AWS machine. Continue? (y/n) ' a && test $$a == 'y' || exit
	@docker-machine rm -f origin-aws
