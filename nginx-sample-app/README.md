- git clone
$ git clone https://github.com/heuns2/heuns2-pcf-spring-sample-apps-docs.git

- move app director
$ cd /heuns2-pcf-spring-sample-apps-docs/nginx-sample-app

- cf push nginx sample app
$ cf push my-app -b nginx_buildpack -p .
