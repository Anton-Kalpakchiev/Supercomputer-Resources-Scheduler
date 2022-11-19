# Delft Blue Requirements

## Must have
- The system shall make use of APIs handle user interactions and to facilitate easy integration with other systems and extra functionalities.
- The system shall use a microservices architecture to ensure that services remain scalable and that the demand for a given service is met.
- The system shall be written is version 11 of the Java programming language.
- The system shall be built with the Spring Boot framework and Gradle.
- The system shall require users to authenticate themselves using a NetID and a password.
- The system shall store the hashed password safely and apply current security standards to ensure correct and consistent user authentication.
- The system shall make use of Spring Security to implement security related features.
- The system shall allow faculties to be added to a list of available faculties.
- The system shall allow faculties to be removed from a list of available faculties.
- The system shall require requests to contain a name, description, resource allocation plan and preferred day of execution.
- The system shall execute all requests prior to or on their respective execution day.
- The system shall allow an employee to be assigned to one or more faculties.
- The system shall allow an employee to make multiple simultaneous resource requests.
- The system shall allow users to have regular accounts.
- The system shall allow users to have faculty accounts.
- The system shall allow users to have sysadmin accounts.
- The system shall allow faculty accounts to approve or reject requests made by regular users to the faculty in question.
- The system shall allow the faculty account to decide the day the request will be handled.
- The system shall allow users to request CPU resources.
- The system shall allow users to request GPU resources.
- The system shall allow users to request memory resources.
- The system shall distribute resources between all available faculties.
- The system shall allow the distribution resources between faculties to change.
- The system shall allow users to request multiple resources simultaneously.
- The system shall require requests concerning memory or GPU resources to have an equal or greater number of CPU resources.
- The system shall allow faculties to release their resources for one or multiple days.
- The system shall allocate released resources to a free resource pool which can be used by other faculties if they exceed their daily limit.
- The system shall exempt requests from faculty approval if they are made 6 hours before the start of the day.
- The system shall automatically accept requests that did not require approval on a first come first serve basis until no more resources are available.
- The system shall reject last minute requests that are made 5 minutes before the start of the day.
- The system shall allow users to contribute their own nodes to the cluster.
- The system shall require new nodes to have a name, url, token and resource allocation plan.
- The system shall require new nodes to provide at least as many CPU resources as GPU or memory resources.
- The system shall allow the user who submited a node to take it down starting from the next day.
- The system shall drop jobs that can no longer be processed on a given day.
- The system shall notify users who's jobs have been dropped
- The system shall pick up dropped jobs once sufficient resources become available
- The system shall notify users who's jobs have been picked up again.
- The system shall allow sysadmins to view schedules from all days, current capacity of each node and reserved/available resources for all days.
- The system shall allow regular users to only see available resources for tomorrow.

## Should have
- The system should only provide specific services to users for which they have rights to access.

## Could have
- The system could moderate user requests to ensure that users remain sensible and do not spam the system.
- The system could block access to certain users who have misused the system in the past.
## Won't have
- The system shall not have a GUI.
- The system shall not allow users to change their login credentials (this is managed by TUDelft SSO).

## Questions
- "All users of the system need to authenticate themselves to determine who they are and what they can do on the platform". Do all regular users (excluding sysadmins) have the same rights? is the only difference between regualr users the faculties they are registered to?
- General questions about faculties: Do all faculties have access to Delft Blue or only a select few? If so, does a list of allowed faculties need to be maintained for the purposes of adding a new faculty or revoking another facultiy's access? How are faculties identifed? Unique IDs or something similar to course codes: CSExxxx?
- "Employees can submit the request to a faculty that they are assigned to". How are users assigned to faculties? Do they assign themselves or is it determined by their TU Delft access rights? Is the sysadmin involved somehow?
- "Employees can submit the request". Can we assume sensible/responsible users? Do requests need to be moderated to ensure they follow a certain policy? Does there need to be a way to block certain users from using the system for the purpose of preventing further system misuse?
- "An employee can be assigned to multiple faculties". Does this mean that an employee is required to be assigned to at least one faculty? Or can a user exist without a faculty and only have access to view tomorrow's available resources?
- General questions about faculty accounts: How is it determined when a user authenticates with NetID and password if they are in control of a faculty account? Are faculty accounts separate from user accounts or can a user account be "upgraded" to a faculty account? Can a single user have multiple faculty accounts?
- General question about start of day: What is the exact start of day? 23:59? 6:00? 8:45?
- "6 Hours before the day starts requests no longer require approval". Do requests that did require approval have priority over requests that did not require approval?
- How should requests that require low CPU, GPU and memory but high time be handled? What if a request takes more than a day to execute? Is there a maximum time for which a process can run?
- Should the system account for Delft Blue down time such as scheduled maintanance periods?
- General questions regarding nodes: What exactly are they and how do they work? Does Delft Blue initially consist of nodes or are they "added on top" for extra resources? What type of token does a node require? For a new node to be added, does it have to provide at least as many CPU resources as GPU or memory resources?
- "Jobs should be dropped". On what criteria should jobs be dropped? Jobs that arrived latest should be dropped first? larger jobs first? Attempt to execute as many jobs as possible by only dropping the minimum amount?
- "Jobs should be dropped and their requesters notified". How should the requester be notified since there will not be a GUI present? TU Delft email address?