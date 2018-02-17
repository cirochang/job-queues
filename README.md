# job-queues

This is an exercise of functional programming provided by Nubank.

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

## Solution

The solution was divided in some steps:

#### Parse the data
1. Read the input and parse to sequence format (easy to manipulate the data).
2. Parse and Split the data for jobs, agents, job_request. (In this step I also put the urgent jobs in the beginning of the sequence of jobs).

#### Start to assign jobs.
For each job request I do:
3. Get correct agent of this job request.
4. I get all possible jobs according of skillsets of the agent and put in a sequence (I put the primary skills jobs is in the beginning of the sequence).
5. Remove the jobs of the sequence that already was assigned.
6. Get the first job of this sequence and assign to the agent. (in this moment the first of the sequence is the best match)

#### Show the results
7. Convert the assign jobs to json and output this information.

