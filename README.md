# Supercomputer Resources Scheduler

## Description of project

This is a computer job scheduling system. A university has a new supercomputer that must be available to all employees to use in their research and work for computing and processing. The system has admins (sysadmins), employees and faculty managers. Employees submit resource requests that indicate how many resources they need and the deadline of their request. Different faculties have different amounts of resources. An employee can submit requests only to their faculty. Each faculty has a manager. The system has both automatic and manual approval and rejection of requests. In some cases when an optimal scheduling is available, the request gets scheduled automatically, while in other cases the faculty manager has to decide whether to approve the request or reject it. If employees have computers that they do not use for prolonged periods of time, they can make a request to add their computer to the system as a processing unit. The system can then utilize that node for future requests.

The backend is implemented using a Microservice Architecture. We have 5 microservices including authentication. All users are authenticated and authorized. The Facade and Chain of Responsibility design patterns are utilized.

## How to run it
After cloning the repository, all the microservices should be run, by starting the Spring applications. Then, since only the backend is implemented, *Postman* can be used to perform the different requests. All requests can be made by calling *localhost:8086/*, followed by the url stated before the explanation of the request.

## Requests
Here is the list of all requests that can be made to the system.

### Faculties and their resources

#### POST: /createFaculty
This request allows a user to request the creation of a new faculty. It is only accessible to users with the SYSADMIN role.

It expects a request body with a request object, which is the faculty creation request. It should be formatted as follows:
{
    "name": the name of the faculty that needs to be created,

    "managerNetId": the netId of the account that needs to be matched to the faculty and needs to be promoted to a faculty account
}

The endpoint returns a message indicating whether the request was successful.

#### POST: /availableResourcesForTomorrow
This request allows a user to request the available resources for the next day for a particular faculty. 

It expects a request body with a request object, containing a long which is the facultyId the user wants to see the available resources for.

The endpoint returns a string of the available resources for tomorrow.

#### GET: /distribution/current
This requests returns a string with the current distribution of the resources in the system. It is only accessible to users with the SYSADMIN role.

#### POST: /distribution/add
This requests adds a distribution for a faculty to the queue. It is only accessible to users with the SYSADMIN role.

The endpoint expects a parameter distribution which is the wanted percentage of resources for a faculty. 
It should be formatted as follows: 
{   
    "name": the faculty name,

    "cpu": the percentage of cpu, given as a double,

    "gpu": the percentage of gpu, given as a double,

    "memory": the percentage of memory, given as a double
}

It returns a 200 OK status code if the adding is successful.

#### GET: /distribution/status
This request returns a string with the current distributions in the queue. It is only accessible to users with the SYSADMIN role. The endpoint returns a ResponseEntity containing a string with the current distributions in the queue.

#### POST: /distribution/save
This request saves all the current faculty distributions in the queue to the full system. It is only accessible to users with the SYSADMIN role. The endpoint returns a 200 OK status code if the saving is successful.

#### POST: /distribution/clear
This request clears the queue with all the current faculty distributions. It is only accessible to users with the SYSADMIN role. The endpoint returns a 200 OK status code if the clearing is successful.

### Requests

#### POST: /request/status
This request allows the user to retrieve the status of a request. It expects a request parameter requestID, which needs to be a long, to be passed in the request body.

The endpoint returns the status of the request found in the database with the given id.

#### POST: /request/register
This request allows a user to register a resource request.

 It expects a request body with a request object, which is a model of the request containing all the important information. It should be formatted as follows:
 {
    "description": a String containing the description of the request,

    "cpu": an int containg the requested amount of cpu,

    "gpu": an int containg the requested amount of gpu,

    "memory": an int containg the requested amount of memory,

    "facultyName": the faculty name for which the request is made,

    "deadline": the deadline, formatted as follows: "DD-MM-YYYY"
 }

The endpoint returns a message to the user that tells them whether their request was successfully submitted.

#### GET /pendingRequests
This request returns a string with the pending requests for a faculty. It is only accessible to users with a Faculty account.

The endpoint returns a ResponseEntity containing a string with the pending request for that faculty.

### Nodes

#### POST /contributeNode
This request allows an EMPLOYEE to contribute a node to a faculty. 

The input should be formatted as follows:
{
    "name": the name of the node,

    "url": the url of the node,

    "facultyId": the id of the faculty that the node has to be contributed to,

    "token": the token of the node,

    "cpu": an int containg the contributed amount of cpu,

    "gpu": an int containg the contributed amount of gpu,

    "memory": an int containg the contributed amount of memory,
}

The endpoint returns a 200 OK status code if the contribution was successful.

#### POST /deleteNode
This request allows an EMPLOYEE to delete a node from a faculty. It expects a request parameter nodeID, which needs to be a long, to be passed in the request body.

The endpoint returns a 200 OK status code if the deletion was successful.

### Schedules and scheduling

#### GET: /schedules/viewSchedules
This request allows a user to view the schedules they are authorized to view. Depending on the user's role, the following rules apply:

SYSADMIN: All schedules for all available days per faculty are returned.
Faculty Manager: All schedules for all available days of their faculty are returned.
Employee: No schedules are returned.
The endpoint returns the schedules of the authorized faculties.

#### POST: /request/manualSchedule
This request allows a Faculty Manager to manually approve or reject a request. 

It expects a request body that should formatted as followed:
{
    "approved": boolean, true when the request needs to be approved, false otherwise,

    "requestId": long of the requestId,

    "dayOfExecution": the day of execution, formatted as follows: "DD-MM-YYYY"
}

The endpoint returns a message to the user informing them the request is successfully approved or rejected.

#### POST: /releaseResources
This request allows a user to release resources for a particular faculty on a particular day. 

It expects a request body formatted as follows:
{
    "facultyId": a long which is the facultyId that the resources need to be released for,

    "day": the day for which the resources need to be released, formatted as follows: "DD-MM-YYYY"
}

The endpoint returns a string which indicates whether the request was successful.
