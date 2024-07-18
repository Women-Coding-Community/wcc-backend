# Women Coding Community Platform Backend Service

<!-- TOC -->

* [Women Coding Community Platform Backend Service](#women-coding-community-platform-backend-service)
    * [Setup](#setup)
        * [JAVA 21 with SDKMAN](#java-21-with-sdkman)
        * [Setup IntelliJ](#setup-intellij)
            * [Lombok](#lombok)
            * [Enable Save Actions](#enable-save-actions)
            * [Enable Checkstyle Warnings](#enable-checkstyle-warnings)
            * [Google Format](#google-format)
                * [IntelliJ JRE Config](#intellij-jre-config)
    * [Run Locally](#run-locally)
    * [Open API Documentation](#open-api-documentation)

<!-- TOC -->

## Setup

### JAVA 21 with SDKMAN

This project uses Java 21, you can run in 21.0.2 or 21.0.3. If you have installed a different
version on your machine and don't want to remove it, you can use **SDKMAN** development tool.

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

* To verify if the java version is correct use:

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

#### Google Format

A google-java-format IntelliJ plugin is available from the plugin repository. To install it, go to
your IDE's settings and select the Plugins category. Click the Marketplace tab, search for the
google-java-format plugin, and click the Install button.

The plugin will be disabled by default. To enable it in the current project, go to
File→Settings...→google-java-format Settings (or IntelliJ IDEA→Preferences...→Other
Settings→google-java-format Settings on macOS) and check the Enable google-java-format checkbox. (A
notification will be presented when you first open a project offering to do this for you.)

To enable it by default in new projects, use File→Other Settings→Default Settings....

When enabled, it will replace the normal Reformat Code and Optimize Imports actions.

![image](docs/images/google-format.png)

##### IntelliJ JRE Config

The google-java-format plugin uses some internal classes that aren't available without extra
configuration. To use the plugin, go to Help→Edit Custom VM Options... and paste in these lines:

```
--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
```

Once you've done that, restart the IDE.

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

* [Access swagger api](http://localhost:8080/swagger-ui/index.html) and
  corresponding [openAPI docs here](http://localhost:8080/api-docs)
    
## Quality Checks

### Jacoco

* Generate Test reports and open [coverage report](build/reports/jacoco/test/html/index.html) 
```shell
./gradlew test jacocoTestReport
```

* Check coverage minimum of 70%
```shell
./gradlew clean test jacocoTestCoverageVerification
```


### PMD

* Run pmd for src
```shell
./gradlew pmdMain
```

* Run pmd for test
```shell
./gradlew pmdTest
```

### SONAR

#### Install sonarqube docker image locally
  - Make sure you have docker installed on your machine - 
    Download the installer using the url https://docs.docker.com/get-docker/. ( Prefer Docker Desktop Application )
  - Start the docker application. Double-click Docker.app to start Docker.

Get the “SonarQube” image using the command
```shell
docker pull sonarqube
```
Start the "SonarQube" instance
```shell
docker run -d --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 sonarqube:latest
```

* Access SonarQube dashboard - http://localhost:9000</br>
  (default credentials )
login: admin
password: admin

Ref: https://docs.sonarsource.com/sonarqube/latest/try-out-sonarqube/

#### Set-up wcc-backend project on local sonarQube instance 
#### Step 1
1. Select create a local project
2. "Project display name" = wcc-backend
3. "Project key" = wcc-backend
4. "Main branch name" = *
#### Step 2
1. Choose the baseline for new code for this project</br>
select "Use the global setting"
2. Click "Create Project" at the bottom

#### Step 3
Generate token to replace in the project.
1. Click "Locally" on the main dashboard
2. Generate a token on the next screen ( choose Expires in - No expiration) [ Click Generate]
3. Copy the token = "sqp_XXXXXXX" and replace in the file <b> build.gradle.kts </b><br>
   <b>property("sonar.token", "PLACE_YOUR_TOKEN_HERE")</b>

#### Perform SONAR ANALYSIS
```shell
./gradlew sonarQubeAnalysis -PlocalProfile
```