# Powergrab Simulation Framework
Informatics Large Practical Powergrab game simulation framework.

The simulation plays the drone game around the University of Edinuburgh central campus. The aim is for the drone to collect as many coins as possible from the map for the specified date within 250 moves and without running out of power. There is an easier drone (stateless) and a harder drone (stateful).

The stateful drone remembers its past positions and moves around the map in a smarter way. 

The output of the application is a log file, which you can upload here: https://homepages.inf.ed.ac.uk/stg/ilp/ or in any other GeoJSON visualiser to view the path of the drone. 

## Build Instructions
To build the project using Maven, run
`mvn package`
from the root directory of the project.

## Run instructions
The above creates a target directory with the compiled class files. To run the simulation, run
`java -jar target/powergrab-0.0.1-SNAPSHOT.jar {day} {month} {year} {latitude} {longitude} {random seed} {drone state}`

## Logs
A directory called log is created in the root directory with a geojson file showing the drone's path and a txt file showing every move and the drone's power and coins at every move.
