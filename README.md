# SEC-Assignment-1
[COMP3003] Software Engineering Concepts: Assignment 1

This program is a simple multi-threaded game displayed in a JavaFX GUI where a 9x9 grid exists and robots spawn randomly after 'x' seconds in one of the four corners of the grid map. 
Robots move randomly but try to move 'towards' the centre of the grid map.
The game is over when the robots move to the centre. 
The player can delay this by building walls on the map to delay the robots.
When robots move into a wall they damage the wall.
When robots move into a damaged wall they destory the wall.
Every time a robot moves into any wall they destroy themselves.
Robots cannot move into each other.
Only one robot may move into a particular wall.
The game has a score tally.
The score increases by 10 every second.
The score increases by 100 for every robot destroyed.
