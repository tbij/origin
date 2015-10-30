Origin
======

A friendly tool for managing static sites.

Origin is a easy-to-use interface for updating the static files used by static-site generators (such as [Jekyll] (http://jekyllrb.com/)) to build a website, including a preview of what pages will look like before they are published.


Usage
-----

Origin requires [SBT] (http://www.scala-sbt.org/), [Docker] (https://www.docker.com/docker-engine), [Docker Machine] (https://www.docker.com/docker-machine), and for deployment to AWS the [AWS CLI] (https://aws.amazon.com/cli/). You will also need to have the static-site generator you plan to use installed, and whatever dependencies that has.

Before running, you will need to create a `config.json` file which includes details about your site. An example `config.example.json` shows what is required. Origin uses Google for authentication -- to get the required credentials you need to create a new project in [Google Developers Console] (https://console.developers.google.com/) and [create new OAuth 2 credentials] (https://console.developers.google.com/apis/credentials). This will prompt for 'authorised Javascript origins' which should include `localhost:8000` as well as the domain you want to run Origin on in production. The 'authorised redirect URIs' should include both URIs from the previous section, followed by `/sign-in/authenticate` and by `/sign-in/validate`.

If you are not using Jekyll you will also need to update the Dockerfile to install that tool before deploying.

To run locally:

```bash
$ make run
```

Or, with Docker (this also creates a new local virtual machine):

```bash
$ make local.run
```

The machine created with this command can be removed with `make local.delete`.

To deploy to AWS:

```bash
$ make aws.run
```

This will prompt you for details about your AWS account. To shutdown the machine you can also `make aws.delete`.
