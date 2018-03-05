# job-queues 2.0.0

This is the second exercise of functional programming provided by Nubank.
To see the enunciation of the proposed exercise click [here](/resources/queues-2.txt).

The second exercise is a continuation of the first exercise, click [here](/resources/queues-1.tx) to check the first one.

## Pre requirements

- Java 1.8.0
- Leiningen 2.8.1
- MongoDB v3.4.10

## How to build

Clone the repository and enter in the project folder
```
git clone git@gitlab.com:cirochang/job-queues.git
cd job-queues
```

Build the project
```
lein ring uberjar
```

## How to run

```
java -jar target/uberjar/job-queues-2.0.0-standalone.jar
```

OR
```
lein ring server-headless
```

## How to run the tests
```
lein test
```

## The solution

When the software runs, it creates a webserver on port 3000 and creates a database called "job-queues" with some properties.

The application creates the following endpoints:

#### POST /agents

Add an agent to the database.

Example input:
```json
{
  "id": "8ab86c18-3fae-4804-bfd9-c3d6e8f66260",
  "name": "BoJack Horseman",
  "primary_skillset": ["bills-questions"],
  "secondary_skillset": []
}
```

Example output:
```
ok
```

#### POST /jobs

Add a job to the database.

Example input:
```json
{
  "id": "f26e890b-df8e-422e-a39c-7762aa0bac36",
  "type": "rewards_question",
  "urgent": false
}
```

Example output:
```
ok
```

#### POST /request_jobs

Complete the last job assigned by agent if this job exists.
Assign an agent-id to a best job match if this match exists.
And return the id of this job or indicate the lack of one (null).

Example input:
```json
{ "agent_id": "8ab86c18-3fae-4804-bfd9-c3d6e8f66260" }
```

Example output:
```json
{
  "job_id": "c0033410-981c-428a-954a-35dec05ef1d2",
  "agent_id": "8ab86c18-3fae-4804-bfd9-c3d6e8f66260",
}
```


#### GET /queue_state

Output a breakdown of the job queue.
Consisting of all being done, completed, and waiting jobs.

Example output:
```json
{
	"completed": [
		{
			"_id": "5a9c796bc9cca95e8bc04c71",
			"id": "f26e890b-df8e-422e-a39c-7762aa0bac36",
			"type": "rewards-question",
			"urgent": false,
			"status": "completed",
			"created_at": "2018-03-04T22:55:39Z",
			"assigned_by_agent": "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88"
		}
	],
	"being done": [
		{
			"_id": "5a9c7971c9cca95e8bc04c74",
			"id": "c0033410-981c-428a-954a-35dec05ef1d2",
			"type": "bills-questions",
			"urgent": true,
			"status": "being done",
			"created_at": "2018-03-04T22:55:45Z",
			"assigned_by_agent": "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"
		}
	],
	"waiting": [
		{
			"_id": "5a9c796fc9cca95e8bc04c73",
			"id": "690de6bc-163c-4345-bf6f-25dd0c58e864",
			"type": "bills-questions",
			"urgent": false,
			"status": "being done",
			"created_at": "2018-03-04T22:55:43Z",
			"assigned_by_agent": "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88"
		}
	]
}
```

#### GET /agent_stats/:agent-id

Given an agent, output how many jobs of each type this agent has performed.

Example output:
```json
{
	"bills-questions": 1,
	"rewards-question": 1
}
```
