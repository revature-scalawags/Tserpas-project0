# Project0 - WoW Server Tracker
This project uses scala and mongodb to fetch data from blizzard web api about their servers for their game world of warcraft

## Mongo setup with Docker
> docker run -p 27017:27017 -d --name project0db mongo

## Running
you will need a clientId and clientSecret for blizards Oauth https://develop.battle.net/documentation/world-of-warcraft/game-data-apis 

> sbt "run [CLIENTID] [CLIENTSECRET]"