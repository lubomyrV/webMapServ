# webMapServ
A web app that displays visitor's geolocation on a map of the world (OpenStreetMap). 

Written on Java with Spring Boot framework, API integration for IP addresses location, and SQLite as a database.

When a user visits the main page, the app takes the user's data(IP address and other properties from the user's browser) and does an HTTP request to a third-party web service. In response, we receive geolocation, country, city, Internet service provider name, etc. then all data save into a database. If the IP address is already in our database, it will not do a new request, just update the existing record with new visit details from the user.

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

If a user visits `http://localhost:8080/?name=username` it saves all data with `username` into the database, otherwise as `anonymous` with `http://localhost:8080/` link. If the user seems not a real person - the system will the data save as `bot`.

In order to see the map with all visitors open this link `http://localhost:8080/map`

![example](https://github.com/lubomyrV/webMapServ/blob/master/map1.png)
