# Women Coding Community Platform Backend Service

<!-- TOC -->

* [Women Coding Community Platform Backend Service](#women-coding-community-platform-backend-service)
    * [Setup](#setup)
        * [JAVA 21 with SDKMAN](#java-21-with-sdkman)
        * [Setup IntelliJ](#setup-intellij)
    * [Run Locally](#run-locally)
    * [Open API Documentation](#open-api-documentation)

<!-- TOC -->

## Setup

### JAVA 21 with SDKMAN

This project uses Java 21, you can run in 21.0.2 or 21.0.3. If you have installed a different
version on your machine and don't want to remove it, you can use **SDKMAN** development tool.
It will allow you to switch based on the Java version you want to use.
Here is the [link](https://sdkman.io).

* Install SDKMAN

Open your terminal and run the following command:

```shell
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
```

* Check the list of available Java versions:

```shell
sdk list java
```

* Install the desired Java version

```shell
sdk install java 21.0.2-open 
```

* Use the specific java version in the current session on your terminal

```shell
sdk use java 21.0.2-open
```

Set the default Java version for your system:

* To set the newly installed Java version as the default:

```shell
sdk default java 21.0.2-open
```

To verify if the java version is correct use:
```shell
java -version
```

### Setup IntelliJ

#### Lombok

Install lombok plugin and enable Annotation Processing, as the image below: 

![image](docs/images/annotation-procession.png)

#### Enable Save Actions

 ![image](docs/images/save-actions.png)

#### Enable Checkstyle Warnings

Install checkstyle plugin and the configuration will be enabled

## Run Locally

* Build and run tests

```shell
./gradlew clean build
```

* Create Jar

```shell
./gradlew clean bootJar
```

* Start Spring Boot Application:

```shell
./gradlew bootRun
```

* Access application on http://localhost:8080/api/cms/v1/team

## Open API Documentation

* Access swagger at http://localhost:8080/swagger-ui/index.html
  and corresponding openAPI docs at http://localhost:8080/api-docs
