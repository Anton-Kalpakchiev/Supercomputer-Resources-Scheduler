# Delft Blue Requirements

## Non functional
- The system shall make use of APIs, handle user interactions and facilitate easy integration with other systems and extra functionalities.
- The system shall use a microservices architecture to ensure that services remain scalable and that the demand for a given service is met.
- The system shall be written is version 11 of the Java programming language.
- The system shall be built with the Spring Boot framework and Gradle.
- The system shall make use of Spring Security to implement security related features.

## Must have
- The system shall require users to authenticate themselves using a NetID and a password.
- The system shall store the password safely by hashing it.
- The system shall handle authorisation.

### Request
- The system shall require requests to contain a name, description, resource allocation plan and a deadline of execution.
- The system shall either schedule all requests to be executed prior to or on the day of their respective deadline of execution or reject them.
- The system shall execute scheduled requests during their scheduled execution timeslot.

### Sysadmin
- The system shall allow users to have sysadmin accounts.
- The system shall allow the sysadmin to create faculties.
- The system shall allow the sysadmin to add faculties to the list of available faculties.

### Faculty
- The system shall allow faculties to have exactly one faculty account, which is not associated with a specific person.
- The system shall allow faculties to assign users to their faculties.
- The system shall allow faculty accounts to approve or reject requests made by regular users to the faculty in question.
- The system shall allow the faculty account to decide the day the request will be handled.
- The system shall distribute resources between all available faculties.
- The system shall allow faculties to release their resources for one or multiple days so others can schedule them in.
- The system shall allocate released resources to the free resource pool which can be used by other faculties if they exceed their daily limit.
- The system shall put the unused resources into the free resource pool 6 hours before the start of the day.
- The system shall automatically accept requests if they are made less than 6 hours and more than 5 minutes before the start of the day and schedule them for the next day irrelevant of when their deadline is. This is done on a first come first serve basis until no more resources are available in the pool.

### Regular
- The system shall allow users to have regular accounts.
- The system shall allow regular users to be assigned to one or more faculties.
- The system shall allow regular users to only see the available resources for tomorrow.
- The system shall allow regular users to request CPU resources.
- The system shall allow regular users to request GPU resources.
- The system shall allow regular users to request memory resources.

## Should have
- The system should only provide specific services to users for which they have rights to access.
- The system should allow sysadmins to promote other sysadmins.

### Request
- The system should require requests to have an equal or greater number of CPU resources than GPU or memory resources.

### Sysadmin
- The system should allow sysadmins to view schedules from all days, current capacity of cluster per node and reserved/available resources for all days and all faculties.
### Faculty
- The system should reject last minute requests that are made less than 5 minutes before the start of the day.
- The system should drop jobs that can no longer be processed on a given day.
- The system should pick up dropped jobs once sufficient resources become available.
- The system should notify users whose jobs have been dropped by writing the corresponding message to a log the user can access.
- The system should notify users whose jobs have been picked up again by writing the corresponding message to a log the user can access.

### Regular
- The system should allow regular users to see the status of their requests.
- The system should allow regular users to make multiple simultaneous resource requests.

## Could have
- The system could allow regular users to contribute their own nodes to the cluster.
- The system could allow regular users who have submited a node to take it down starting from the next day.
- The system could require new nodes to have a name, url, token and resource allocation plan.
- The system could require new nodes to provide at least as many CPU resources as GPU or memory resources.
- The system could allow users not assigned to a faculty to provide their personal nodes.
- The system could allow the sysadmin to remove faculties from the list of available faculties.
- The system could allow the distribution of resources between faculties to change.
- The system could moderate user requests to ensure that users remain sensible and do not spam the system.
- The system could block access to certain users who have misused the system in the past.
- The system could allow for change in the base amout of resources per faculty every day(nodes excluded)

## Won't have
- The system will not have a GUI.
- The system will not allow users to change their login credentials (this is managed by TUDelft SSO).

## Questions
- "A faculty can decide to release their resources for one or multiple days so others can schedule them in." How does releasing resources work? What does it mean?
- How should requests that require low CPU, GPU and memory but high time be handled? What if a request takes more than a day to execute? Is there a maximum time for which a process can run?
