# Animal vs. Animal

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

To build the project, use:

```shell
mvn clean package
```

This will generate a shaded JAR that can be picked up by the following steps:

The project is deployed using Docker. Each separate subcomponent needs a separate container, they are built like this:

```shell
docker build -t animals_demo -f src/main/docker/animal/Dockerfile target/
```

The tag name should match the contents of `docker-compose.yml`

Currently, you need to build:

```
animals_demo
fish_demo
mustelid_demo
feline_demo
mammal_demo
```


## Running the project

In the deploy directory are a docker-compose YAML file and a collector config.

```shell
docker-compose up
```