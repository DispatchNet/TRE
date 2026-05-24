#include <string>
//#include "network_management.h"
using namespace std;

//to be removed when network management is implemented
typedef string Station;
typedef string Junction;
typedef string Line;
typedef string ServiceRequest;
//

/*
* @brief Enum representing the type of authenticated user in the system.
*/
enum class UserType {
    Passenger,
    TrainCompany,
    NetworkManager
};

/*
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

/*
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

ostream& operator<<(ostream& os, UserType type);
