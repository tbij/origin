Origin
======

A friendly tool for managing static sites.

Origin is a easy-to-use interface for updating the files used by static-site generators (such as [Jekyll] (http://jekyllrb.com/)) to build a website, including a preview of what pages will look like before they are published.

It should be used in conjunction with an automatic deployment tool that will listen for changes to source files and then regenerate and deploy. The [Jekyll site lists a number of such tools] (http://jekyllrb.com/docs/deployment-methods/#automated-methods).


Running
-------

Origin requires [SBT] (http://www.scala-sbt.org/), [Docker] (https://www.docker.com/docker-engine), [Docker Machine] (https://www.docker.com/docker-machine), and for deployment to AWS the [AWS CLI] (https://aws.amazon.com/cli/).

Before running, you will need to create a `config.json` file which includes details about the site you will be managing. The `config.example.json` shows what fields are required.

The `auth` section sets up how Origin should authenticate users. Origin uses Google to do this -- anyone with an email address using the domain listed in `auth.domain` will be granted access. The `auth.key` is a secure key used to verify requests -- it can be any random secret value, such as the output of `cat /dev/urandom | base64 | head -c 64`. To get the `auth.clientId` and `auth.clientSecret` you need to create a new project in [Google Developers Console] (https://console.developers.google.com/) and [create new OAuth 2 credentials] (https://console.developers.google.com/apis/credentials). This will prompt for 'authorised Javascript origins' which should include `localhost:8000` as well as the domain you want to run Origin on in production. The 'authorised redirect URIs' should include both URIs from the previous section, followed by `/sign-in/authenticate`.

The `git` section describes the site you will be managing. The `git.repository` should be a HTTPS link to the Git repository where your site is stored. The `git.username` and `git.password` should be for a user that has write access to that repository.

The `site` section can be left as-is if you are using Jekyll, otherwise you need to tell Origin what command it should use to build your site, and where the different types of files are located. If you are not using Jekyll you will also need to update the Dockerfile to install your static-site generator of choice before deploying. Note that the `site.builder` command is used for generating the preview, so will probably need to be passed a different configuration file that will need to write URLs that correctly include the `/preview/` prefix.


### Locally

To deploy:

	$ make local.run

The first time you run this it will take a while longer as it creates a new virtual machine. The machine created with this command can be removed with `make local.delete`.

Origin will then be available on [localhost:8000] (http://localhost:8000/).

### On AWS

To deploy:

	$ make aws.run

This will prompt you for details about your AWS account. The first time you run this it will take a while longer as it creates a new EC2 machine. This machine can also be removed with `make aws.delete`.
