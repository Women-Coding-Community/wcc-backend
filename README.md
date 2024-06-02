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


## Deploy to [Fly.io](https://fly.io/)

* Create the Jar
```shell
./gradlew clean bootJar
```

* Install [flyctl](https://fly.io/docs/hands-on/install-flyctl/) 

* Create an account with fly auth signup or login with fly auth login.

```shell
fly auth signup
```
or 
```shell
fly auth login
```

* From inside the project directory
```shell
fly deploy
```

### Other commands: 

* Run `fly apps open` – open your browser and direct it to your app
* Run `fly status` – show the status of the application instances