# Women Coding Community Platform Backend Service

## Locally Development

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

********************************************************************************************************************

## JAVA 21.0.2
This product is built with Java 21.0.2. If you have installed a different version on your machine and don't want to remove it, you can use **SDKMAN** development tool.
It will allow you to switch based on the Java version you want to use. Here is the link: https://sdkman.io/. 

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
sdk install java 0.0.0-open 
```

### Set the new Java version for your specific project

* Navigate to your project directory 
```shell
cd /path/to/your/project
```

* Use SDKMAN to set the java version for this specific directory:
```shell
sdk use java 0.0.0-open
```

* Or set the newly installed Java version as the default:
```shell
sdk default java 0.0.0.-open
```

* To verify if the java version is correct use:
```shell
java -version
```
