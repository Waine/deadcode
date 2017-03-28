# Dead code

Write an algorithm that detects dead code in any public github repository. Expose REST endpoints to:
 
1.	Add a github repository. Adding a repository should automatically trigger your algorithm to detect dead code in that repository
2.	List all repositories that have already been added along with their status
    - All repos should have an ID assigned automatically
    - Their current status should be returned like PROCESSING, COMPLETED, FAILED etc
    -	Timestamps like the time when the repo was added and when the processing finished etc should also be returned
    -	Any other information that you think will be useful
3.	Get dead code occurrences for a given github repository. Your algorithm should detect following:
    - Private functions that are never being used by any part of the codebase
    - Local/Global private variables that are not being used
    - Function parameters that are not being used within the function
    - Anything else that you think should be counted as dead code
4.	Any other endpoint that you think would be useful

Once developed, deploy the application on DevFactory docker swarm. Share the URL for the swagger documentation of the REST endpoints. Also record and share a 5mins demo of the application (shorter if possible).

Use the following tools and technologies:

1.	Springboot
2.	Scitools Understand (for code analysis)
3.	Gradle
4.	Docker
5.	Use any in-memory database (if required)
6.	Follow the technical standards from this document (ignore everything about aLine, CI systems for now)


Please watch the training videos here to know more about Scitools understand and how to use it (Note that these training videos are for a similar project but you don’t have to use all of the tools mentioned in the video like Neo4j for example) 

For deployments and server environments, watch the training video here

Some points to note:

●	Design the REST endpoints while assuming that these endpoints will be used by an external application that needs to highlight all dead code occurrences. Also assume that the external applications already have the source code and you need to provide them enough information so that they can highlight dead code

●	The expectation here is that you’ll deliver something that you feel proud of. Something that can be delivered directly to the customers if required (we need you to keep the quality bar at the same level)

●	After you are done, you’ll need to get a sign off for your work. Your goal should be to get a sign off in as less iterations as possible

●	This assignment is designed to be worked on independently and you don’t need access to any codebase for references

●	For docker swarm deployment, you may have to create a ticket on https://jira.devfactory.com/projects/SAASOPS/issues to gain access to the docker nodes. Please check if you have access. If you don’t have access then create a ticket asap so that you don’t waste any time later on. The contents of the ticket can be something like this: “I need access to 10.224.0.0/16 network, currently I can only access the 10.224.128.0/24 network, because of this I can't access some docker of the nodes that I need.”

●	We use slack for communication so log onto devfactorydev.slack.com using the same credentials that you use to connect to the VPN.

●	Complete this assignment within one week (5 working days)


