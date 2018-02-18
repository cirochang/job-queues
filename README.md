# job-queues

This is an exercise of functional programming provided by Nubank.
To see the enunciation of the proposed exercise click [Here](/resources/queues-1.txt).

## Pre requirements

- Java 1.8.0
- Leiningen 2.8.1

## How to build

Clone the repository and enter in the project folder
```
git clone git@gitlab.com:cirochang/job-queues.git
cd job-queues
```

Build the project
```
lein uberjar
```

## How to run

```
<json-input> | java -jar target/uberjar/job-queues-1.0.0-standalone.jar
```

OR
```
<json-input> | lein run
```

Where `<json-input>` is the input in json format

Example:
```
cat resources/sample.input.json.txt | java -jar target/uberjar/job-queues-1.0.0-standalone.jar
```

## The solution

The solution was divided in some steps:

To parse the data, I do:
1. Read the input and parse to sequence format (easy to manipulate the data).
2. Parse and split the data for jobs, agents, job_request.

Then, **for each** job request... (In fact I used recursion)
1. Get the correct agent of this job request.
2. Get all jobs that has type equal to the primaryskillset of the agent, and orders the urgent jobs to the beggining.
3. Get all jobs that has type equal to the secundaryskillset of the agent, and orders the urgent jobs to the beggining.
4. Join the results of steps 4 and 5 putting in a sequence (The primaryskillsets jobs is in the beggining of this sequence).
5. Remove the jobs of the sequence that already was assigned.
6. Get the first job of this sequence and assign to the agent. (in this moment the first of the sequence is the best match).

Finally, convert the assign jobs to json and output this information.

