#include <string>
#include <list>
//#include "network_management.h"
using namespace std;

//to be removed when network management and service request are implemented
typedef string Station;
typedef string Junction;
typedef string Line;
typedef string ServiceRequest;
typedef string Ticket;
typedef string GroupPass; //removable
//

/**
* @brief Enum representing the type of authenticated user in the system.
*/
enum class UserType {
    Passenger,
    TrainCompany,
    NetworkManager
};

/**
 * @brief Class managing the user accounts in the system. Other modules will
 * interact with this class to manage user sessions and data.
 */
class UserManagement {
    private:
        list<AuthenticatedUser> authenticatedUsers;
        list<AnonymousUser> anonymousUsers;

    public:
        UserManagement();
        ~UserManagement();

        list<AuthenticatedUser> getAuthenticatedUsers();
        list<AnonymousUser> getAnonymousUsers();
        void addAuthenticatedUser(const AuthenticatedUser& user);
        void addAnonymousUser();
        void removeAuthenticatedUser(const AuthenticatedUser& user);
        void removeAnonymousUser(const AnonymousUser& user);
};

/**
* @brief Abstract class representing an authenticated user in the system,
* containing their credentials and user type.
*/
class AuthenticatedUser {
    private:
        string username;
        string email;
        string password;
        UserType userType;

    public:
        ~AuthenticatedUser();

        const string& getUsername() const;
        const string& getEmail() const;
        const string& getPassword() const;
        UserType getUserType() const;

        void setUsername(const string& username);
        void setEmail(const string& email);
        void setPassword(const string& password);
        void setUserType(UserType type);

        void logout();
};

/**
* @brief Class representing a network manager, a type of authenticated user who 
* can manage the network and review service requests.
*/
class NetworkManager : public AuthenticatedUser {
    private:

    public:
        NetworkManager(string username, string email, string password);
        
        ~NetworkManager();

        void viewNetwork();
        void createStation();
        void createJunction();
        void createLine();
        void editStation(Station*&);
        void editJunction(Junction*&);
        void editLine(Line*&);
        void deleteStation(Station*&);
        void deleteJunction(Junction*&);
        void deleteLine(Line*&);
        void reviewServiceRequest(ServiceRequest*&);
};

/**
* @brief Abstract class representing an end user, a type of authenticated user 
* who can view and manage their own data.
*/
class EndUser : public AuthenticatedUser {
    private:

    public:
        ~EndUser();

        void viewData();
        void changeData();
        void deleteAccount();
};

/**
* @brief Class representing a train company, a type of end user who can create 
* service requests and manage their account.
*/
class TrainCompany : public EndUser {
    private:

    public:
        TrainCompany(string username, string email, string password);

        ~TrainCompany();
        
        void CreateServiceRequest();
        void CreateServiceType();
        void CreateTrainType();
};

class Passenger : public EndUser {
    private:
        list<Ticket> tickets;

    public:
        Passenger(string username, string email, string password);

        ~Passenger();

        list<Ticket>* getTickets() const;
        bool purchaseTicket(Ticket*&);
        bool purchaseTicket(GroupPass*&); //removable
        bool cancelTicket(Ticket*&);
        bool cancelTicket(GroupPass*&); //removable
        void getTicketsHistory();
        void findPath();
        void searchStationTrain();
        void viewMap();
};

/**
 * @brief Class representing an anonymous user, who can only view the map and
 * search for stations and trains.
 */
class AnonymousUser {
    private:
        int identifier; //to be used for session management

    public:
        AnonymousUser(int identifier);

        ~AnonymousUser();

        int getIdentifier() const;
        void _register();
        void resetPassword();
        void login();
};

ostream& operator<<(ostream& os, UserType type);
