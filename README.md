# **TRE**

## **Objectives**

A major European rail infrastructure manager received an idea for a unified rail service scheduling and ticketing system, and has chosen us to prototype a trial run, over multiple regions in their network.

Thus, the project provides a platform that covers the needs of both passengers and train companies, as well as an underlying management system of any regional-sized rail network, which supports network managers in its administration.
It provides a set of functionalities which support the planning of train travel. The system lets passengers discover paths, view scheduling and manage tick-
ets. On the other side, it lets train companies introduce train services and network managers administer the network.
The project coordinates the functionalities for each stakeholder into a solution that has been identified in an application accessible for the aforesaid users.

The main objectives of the project are:

### **Ticketing system**
Subsystem which deals with tickets, from the search to the possible actions after the purchase. It includes basic operations that the passenger can per-
form, such as:
- Viewing tickets details.
- Purchase.
- Accessing tickets history.
- Cancellation.

### **Route-finding**
The system shall let passengers select a starting station, a destination station, and either a departure time or arrival time.
It computes and ranks possible routes that best fit the user’s request and permissions (for example, not permitting cargo trains to passengers).
The user must be able to compare the options based on:
- Departure and arrival times.
- Number of transfers.
- Possible transfer locations.

### **Virtual board**
The system provides passengers access to departure and arrival boards for any station. It also shows intermediate stations and scheduled times for each individual train service.

### **Rail system modeling**
The system provides a data-driven approach to rail network modelling, supporting an efficient administration of the infrastructure.
It shall support:
- Management of the network by the network manager.
- Management of rolling stock by the network manager.
- Submission of a new service request by train companies.
- Review of a service request by the network manager.

## **Stakeholders**

### **Internal**

**Human**
*Network managers*:, the product owner. They manage the rail network combining the availability of train companies with passengers’ needs.
They are the primary stakeholders for making functional decisions.

**Non-Human**
*Database*, where all the information used by the application are stored.

### **External**

**Human**
*Passengers*, the main end users. They want to streamline their interactions with the train transport system, including:
- Searching for routes and schedules.
- Analyzing departure/arrival boards as well as train information.
- Purchasing tickets.
*Train companies*, who provide the rolling stock. They request the introduction of services. They are also expected to provide information of trains they ask to be managed.
*Anonymous users*, who have not yet authenticated. They are considered as passengers with restricted access to functionalities that require authentication.

**Non-human**
Payment gateway, which provides the underlying payment methods.
