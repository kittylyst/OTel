# Animal vs. Animal

## Docker Details

## Description

This project provides a simple structure to demonstrate distributed traces.

Simulates a battle between two animals chosen from several different clades of animal. Call `GET /battle` to get a 
battle that looks like this:

Output:

{
"good": <animal1>,
"evil": <animal1>
}

Each low-level service contains a simple `GET /getAnimal` route, and top-level service calls one of the low-level services
application's `GET /getAnimal` route for each side (chosen randomly).

The routes are as follows:


## Building the project


## Running the project

```shell
# Run each app in a separate shell
```