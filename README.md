# Session Replication Demo with Redis
- @ mkim@pivotal.io

This documentation describe how to build and test application.
HTTP protocol is generally stateless. to store the request state, application usually uses session technology.
but once container process down, Information stored in session should be lost.
so most of legacy application uses session replication/synchronization technology generally supported by application container.

those session replication support on container doesn't supported in PCF yet, but CF support in container level.
this demo shows how to cope with this issue in application level using redis backend for storage.

this documentation address Mac OS environment in client OS.


# Prerequisite

1. Mac OS for client environment with internet connectivity.
1. PCF environment.


## Test step on Local machine

1. download this git repository.

    ```
    git clone [this git repository URL]
    ```

1. prepare local redis server

    ```
    brew install redis
    redis-server /usr/local/etc/redis.conf
    ```

1. run application in local

    install maven if not installed.
    ```
    brew install maven
    ```

    ```
    mvn clean tomcat:run
    ```
    you can optionally specify service name explicitly.

    spring.profiles.active property is keyword in spring cloud library.
    ```
    mvn clean tomcat:run -Dspring.profiles.active=redis
    ```

1. access application
    this will read all session attributes in current application.

    url may change depending on your deployment.

    ```
    http://localhost:8080/spring-session
    ```

1. add a new session key, value:

    type in attribute, value and click on 'Add' button.

    application will save to session and reload all session info you typed in.

1. kill tomcat and restart application.

    check session info again with previous web browser UI:
    session info should be preserved if not expired in redis by session timeout(5 minutes)

    ```
    http://localhost:8080/spring-session
    ```


## Deploy to PCF and Test

1. build war

    ```
    mvn clean package
    ```
1. Install the [Cloud Foundry CLI](https://github.com/cloudfoundry/cli) and login to PCF:

    ```
    cf api --skip-ssl-validation https://api.run.pivotal.io
    cf login
    ```
    Now you are ready to run commands such as `cf help`.


1. prepare redis service instance with name 'redis'

    ```
    cf cs rediscloud 25mb redis
    ```

    make sure service name to be 'redis'; because it is used in application as is is.

1. push application

    ```
    cf push -i 2
    ```

    redis service will be bound automatically as manifest.yml specifies as follows:

    ```
     name: spring-session
      memory: 512M
      instances: 1
      # host: spring-session-${random-word}
      host: spring-session-mkim
      path: target/spring-session.war
      services:
      - redis
    ```

1. access application
    this will read all session attributes in current application.

    url may change depending on your deployment.

    ```
    http://spring-session-mkim.cfapps.io
    ```

1. add a new session key, value:

    type in attribute, value and click on 'Add' button.

    application will save to session and reload all session info you typed in.


1. kill one application instance.

    ```
    cf scale spring-session -i 1
    ```

1. check session info again with previous web browser UI:
    session info should be preserved.
    if not expired in redis by session timeout(5 minutes)

    ```
    http://spring-session-mkim.cfapps.io
    ```

## Limitation and consideration

1. data in redis will expire in 5 minutes.
1. to restore data, client web-browser should use previous session id in HTTP request header(cookie=JSESSIONID)
1. redis service down causes data lost.
1. don't bind more than two redis services to this application; will not work.!!
1. some portion of source code is based on [spring-music](https://github.com/cloudfoundry-samples/spring-music) demo.
1. code on this program is intended for demonstration only.
