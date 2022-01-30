# Rock Paper Scissors
A Spring Boot application for the game of Rock Paper Scissors. The application can be played in two ways by starting it using PlayerVsPlayer profile or PlayerVsComputer profile. The default active profile is PlayerVsComputer.

### Project details
- Spring-Boot was chosen instead of building just a console app because it comes with a lot of preconfigured infrastructure beans and also it comes with the dependency injection mechanism
- The application could run into a cloud infrastructure environment such as a VM and two players were able to play from different locations, or a player could play against the computer
- Use the power of Spring MVC & Java 17 coupled with Spring Data Jpa for saving the data into an embedded H2 database
- Use Spring aspect oriented programming, and a Java custom annotation for a cross-cutting concern such as logging
- Utility of aspect oriented programming is also present in handling errors mechanism using controller advice
- Tested using Mockito framework & Junit5, and also MockMvc 
- I attached the postman collections (PlayerVsPlayer & PlayerVsComputer) into the project structure


## How to play
### PlayerVsComputer

- The default active profile is PlayerVsComputer
- The first thing needed is to create a player
```
POST http://localhost:8080/player/Ronaldo
```
- After creating a player, a new game should be crated using that player name
```
POST http://localhost:8080/create-game/Ronaldo
```
- The next thing to do is setting the player into ready state (at start he/she is in state waiting)
```
PUT   http://localhost:8080/ready-player/Ronaldo
```
- The player should play his move. The sessionCode received when the game was previously created should be used in requests.
```
POST   http://localhost:8080/play

body: 
{
    "playerName":"Ronaldo",
    "sessionCode":"1642784675716",
    "move":"rock"
}
```
- The result could be retrieved by using the sessionCode and making a get request to the result endpoint.
```
GET   http://localhost:8080/result/1642784675716
```
- Deleting a player would cascade the deletion to the other rows created by the player resulting in deleting the previous games/rounds/turns.
```
DELETE   http://localhost:8080/player/Ronaldo
```

### PlayerVsPlayer
- The default active profile is PlayerVsComputer. In order to play PlayerVsPlayer you need to change the active profile to PlayerVsPlayer. 
- The first thing needed is to create the first player
```
POST http://localhost:8080/player/Ronaldo
```
- A second player should be crated
```
POST http://localhost:8080/player/Messi
```
- After creating the players, a new game should be crated using one of the players name.
```
POST http://localhost:8080/create-game/Ronaldo
```
- The second player should use the sessionCode (invitation) and accept the invitation
```
PUT http://localhost:8080/accept-invite/1644211398711/Messi
```
- The next thing to do is setting the players into ready state
```
PUT   http://localhost:8080/ready-player/Ronaldo
```
```
PUT   http://localhost:8080/ready-player/Messi
```
- One of the players should play his move. The sessionCode received when the game was previously created should be used in requests.
```
POST   http://localhost:8080/play

body: 
{
    "playerName":"Ronaldo",
    "sessionCode":"1642784675716",
    "move":"rock"
}
```
- The other player should play his move. The sessionCode received when the game was previously created should be used in requests.
```
POST   http://localhost:8080/play

body: 
{
    "playerName":"Messi",
    "sessionCode":"1642784675716",
    "move":"paper"
}
```
- The result could be retrieved by using the sessionCode and making a get request to the result endpoint.
```
GET   http://localhost:8080/result/1642784675716
```
- Deleting a player would cascade the deletion to the other rows created by the player resulting in deleting the previous games/rounds/turns.
```
DELETE   http://localhost:8080/player/Ronaldo
```


## Built With
* [Java 17](https://docs.oracle.com/en/java/javase/17/) - Programming language
* [Spring-Boot](http://spring.io/projects/spring-boot) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management


## Versioning
* [Git](https://git-scm.com/)


## Authors
* **Manaila Catalin-Stefan** - https://github.com/CatalinStefan11/RockPapperScissors

