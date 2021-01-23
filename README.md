# webMapServ
A web app that displays visitors geolocation on a map of the world (OpenStreetMap). 

Written on Java with Spring Boot framework, API integration for IP addresses location, and SQLite as a database.

When a user visits the main page, the app takes the user's data(IP address and other properties from the user's browser) and does an HTTP request to a web server. In response, we receive geolocation, country, city, Internet service provider name, etc. Then all data saves into a database. If the IP address is already in our database, it will not do a new HTTP request, and just update the existing record with new visit details from the user.

Following tools are needed:

1) JRE,JDK v.8

2) Apache Maven 3.3.9

To compile this app you need to type:

`mvn clean`

`mvn compile`

`mvn package`

In order to run the app, go to `target/` and type

`$ java -jar demo-0.0.1-SNAPSHOT.jar`

The app automatically creates a database called `test.db`

If a user visit `http://localhost:8080/username` it saves all data with `username` into the database, otherwise as `anonymous`, for others visits that seems not a real people the data saves as `bot`.

In order to take a lock visitors go to `http://localhost:8080/map`

![example](https://github.com/lubomyrV/webMapServ/blob/master/map1.png)
