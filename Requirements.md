# Delft Blue Requirements

## Non functional
- The system shall make use of APIs handle user interactions and to facilitate easy integration with other systems and extra functionalities.
- The system shall use a microservices architecture to ensure that services remain scalable and that the demand for a given service is met.
- The system shall be written is version 11 of the Java programming language.
- The system shall be built with the Spring Boot framework and Gradle.
- The system shall make use of Spring Security to implement security related features.

## Must have
- The system shall require users to authenticate themselves using a NetID and a password.
- The system shall store the hashed password safely.
- The system shall handle authorisation.
- The system shall require new nodes to have a name, url, token and resource allocation plan.
- The system shall require new nodes to provide at least as many CPU resources as GPU or memory resources.

### Request
- The system shall require requests to contain a name, description, resource allocation plan and preferred day of execution or before.
- The system shall execute all requests prior to or on their respective execution day.
- The system shall require requests concerning memory or GPU resources to have an equal or greater number of CPU resources.

### Sysadmin
- The system shall allow users to have sysadmin accounts.
- The system shall allow the sysadmin to add faculties to a list of available faculties.
- The system shall allow the sysadmin to remove faculties from a list of available faculties.
- The system shall allow the sysadmin to create faculties.
- The system shall allow sysadmins to view schedules from all days, current capacity of cluster per node and reserved/available resources for all days and all faculties.
- The system shall allow the distribution resources between faculties to change.

### Faculty
- The system shall allow faculties to have only one faculty account, which is a seperate account and not linked to a user.
- The system shall allow faculties to assign users to their faculties.
- The system shall allow faculty accounts to approve or reject requests made by regular users to the faculty in question.
- The system shall allow the faculty account to decide the day the request will be handled.
- The system shall distribute resources between all available faculties.
- The system shall allow faculties to release their resources for one or multiple days.
- The system shall allocate released resources to a free resource pool which can be used by other faculties if they exceed their daily limit.
- The system shall put the unused resources into the free resource pool 6 hours before the start of the day.
- The system shall exempt requests from faculty approval if they are made 6 hours before the start of the day.
- The system shall automatically accept requests that did not require approval on a first come first serve basis until no more resources are available.
- The system shall reject last minute requests that are made 5 minutes before the start of the day.
- The system shall pick up dropped jobs once sufficient resources become available.
- The system shall notify users who's jobs have been dropped.
- The system shall notify users who's jobs have been picked up again.
- The system shall drop jobs that can no longer be processed on a given day.

### Regular
- The system shall allow users to have regular accounts.
- The system shall allow a user to be assigned to one or more faculties.
- The system shall allow a user to make multiple simultaneous resource requests.
- The system shall allow users to request multiple resources simultaneously.
- The system shall allow regular users to only see available resources for tomorrow.
- The system shall allow users to request CPU resources.
- The system shall allow users to request GPU resources.
- The system shall allow users to request memory resources.
- The system shall allow users to see the status of their requests.
- The system shall allow the user who submited a node to take it down starting from the next day.
- The system shall allow users to contribute their own nodes to the cluster.

## Should have
- The system should only provide specific services to users for which they have rights to access.
- The system should allow sysadmins to promote other sysadmins.

## Could have
- The system could moderate user requests to ensure that users remain sensible and do not spam the system.
- The system could block access to certain users who have misused the system in the past.

## Won't have
- The system shall not have a GUI.
- The system shall not allow users to change their login credentials (this is managed by TUDelft SSO).

## Questions
- Should the base line amount of resources, so without extra nodes, distributed to each faculty be the same every day?
- What are the permissions of floating users? Can they provide nodes?
- How do you manage the free pool of resources and the preferred days? If you apply for a day in the future and there is a free pool of resources available for tomorrow, will you get added to that queue?
- "A faculty can decide to release their resources for one or multiple days so others can schedule them in." How does releasing resources work? What does it mean?
- How should requests that require low CPU, GPU and memory but high time be handled? What if a request takes more than a day to execute? Is there a maximum time for which a process can run?
- "Jobs should be dropped and their requesters notified". How should the requester be notified since there will not be a GUI present? 
