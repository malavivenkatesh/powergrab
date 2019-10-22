# Powergrab Simulation Framework
Informatics Large Practical Powergrab game simulation framework.

The simulation plays the drone game around the University of Edinuburgh central campus. The aim is for the drone to collect as many coins as possible from the map for the specified date. There is an easier drone (stateless) and a harder drone (stateful).

## Build Instructions
To build the project using Maven, run
`mvn clean compile assembly:single`
from the root directory of the project.

## Run instructions
The above creates a target directory with the compiled class files. To run the simulation, run
`java -cp target/powergrab-0.0.1-SNAPSHOT-jar-with-dependencies.jar uk.ac.ed.inf.powergrab.App {day} {month} {year} {latitude} {longitude} {random seed} {drone state}`

## Logs
A directory called log is created in the root directory with a geojson file showing the drone's path and a txt file showing every move and the drone's power and coins at every move.
