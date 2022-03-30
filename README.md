# DataVault with Spring Boot

## Java
Use java 8

## Using Maven Wrapper

#### compile, test and install jars into local m2 repo
```
./mvnw clean install
```

#### Running the WebApp on http://localhost:8080

```
./mvnw spring-boot:run --projects datavault-webapp
```

#### Attempt to access a protected resource

In your browser, navigate to http://localhost:8080/secure, it should redirect you to the 
datavault login page.

You will login as the user 'user'. To get the password look through the console output of the
running webapp for something like...

```
Using generated security password: 4c87495e-f07c-4f29-8278-32c6cc49073c
```

Once you are logged in, you should be back on http://localhost:8080/secure.

The screen should say something like...
* SECURE PAGE
* Logout (link)
* logged in as user

If you click the logout link, you should end up at http://auth/confirmation page. 
The confirmation page should say something like...
* To logout of the DataVault you must close your browser, in order to end the browser session, so that you will be logged out of EASE.

If you navigate now to http://localhost:8080/index - the page should say ...
* INDEX PAGE
* NOT logged in
* LOGIN (link)

If you login again, you should be taken back to the http://localhost:8080/ page which should say...
* INDEX PAGE
* Logout (link)
* logged in as user
