#include "user_management.h"
#include <iostream>

/** 
* @brief Constructor for UserManagement class 
*/
UserManagement::UserManagement() {}

/**
 * @brief Destructor for UserManagement class
 */
UserManagement::~UserManagement() {}

/**
 * @brief Get the list of authenticated users in the system.
 * @return A list of authenticated users.
 */
list<AuthenticatedUser> UserManagement::getAuthenticatedUsers() {
    return authenticatedUsers;
}

/**
 * @brief Get the list of anonymous users in the system.
 * @return A list of anonymous users.
 */
list<AnonymousUser> UserManagement::getAnonymousUsers() {
    return anonymousUsers;
}

/**
 * @brief Add an authenticated user to the system.
 * @param user The authenticated user to add.
 */
void UserManagement::addAuthenticatedUser(const AuthenticatedUser& user) {
    authenticatedUsers.push_back(user);
}

/**
 * @brief Add an anonymous user to the system, initialized with a unique 
 * identifier.
 */
void UserManagement::addAnonymousUser() {
    anonymousUsers.push_back(AnonymousUser(anonymousUsers.size() + 1));
}

/**
 * @brief Remove an authenticated user from the system.
 * @param user The authenticated user to remove.
 */
void UserManagement::removeAuthenticatedUser(const AuthenticatedUser& user) {
    authenticatedUsers.remove(user);
}

/**
 * @brief Remove an anonymous user from the system.
 * @param user The anonymous user to remove.
 */
void UserManagement::removeAnonymousUser(const AnonymousUser& user) {
    anonymousUsers.remove(user);
}

/**
* @brief Destructor for AuthenticatedUser class
*/
AuthenticatedUser::~AuthenticatedUser() {}

/**
* @brief Get the username of the authenticated user.
* @return The username.
*/
const string& AuthenticatedUser::getUsername() const { return username; }

/**
* @brief Get the email of the authenticated user.
* @return The email.
*/
const string& AuthenticatedUser::getEmail() const { return email; }

/**
* @brief Get the password of the authenticated user.
* @return The password.
*/
const string& AuthenticatedUser::getPassword() const { return password; }

/**
* @brief Get the type of the authenticated user.
* @return The user type.
*/
UserType AuthenticatedUser::getUserType() const { return userType; }

/**
* @brief Set the username of the authenticated user.
* @param username The username to set.
*/
void AuthenticatedUser::setUsername(const string& username) {
    this->username = username;
}

/**
* @brief Set the email of the authenticated user.
* @param email The email to set.
*/
void AuthenticatedUser::setEmail(const string& email) {
    this->email = email;
}

/**
* @brief Set the password of the authenticated user.
* @param password The password to set.
*/
void AuthenticatedUser::setPassword(const string& password) {
    this->password = password;
}

/**
* @brief Set the type of the authenticated user.
* @param type The user type to set.
*/
void AuthenticatedUser::setUserType(UserType type) {
    this->userType = type;
}

/**
* @brief Log out the authenticated user, moving to the anonymous user state.
*/
void AuthenticatedUser::logout() {
    //TODO
}

/**
* @brief Constructor for NetworkManager class
* @param username The username of the network manager.
* @param email The email of the network manager.
* @param password The password of the network manager.
*/
NetworkManager::NetworkManager(string username, string email, string password) {
    setUsername(username);
    setEmail(email);
    setPassword(password);
    setUserType(UserType::NetworkManager);
}

/**
* @brief Destructor for NetworkManager class
*/
NetworkManager::~NetworkManager() {}

/**
* @brief Destructor for EndUser class
*/
EndUser::~EndUser() {}

/**
* @brief Print the data of the end user, that is username and email.
*/
void EndUser::viewData() {
    cout << "Username: " << getUsername() << endl;
    cout << "Email: " << getEmail() << endl;
}

/**
* @brief Change the data of the end user.
*/
void EndUser::changeData() {
    //TODO
}

/**
* @brief Delete the account of the end user.
*/
void EndUser::deleteAccount() {
    //TODO
}

/**
* @brief Constructor for TrainCompany class
* @param username The username of the train company.
* @param email The email of the train company.
* @param password The password of the train company.
*/
TrainCompany::TrainCompany(string username, string email, string password) {
    setUsername(username);
    setEmail(email);
    setPassword(password);
    setUserType(UserType::TrainCompany);
}

/**
* @brief Destructor for TrainCompany class
*/
TrainCompany::~TrainCompany() {}

/**
* @brief Constructor for Passenger class
* @param username The username of the passenger.
* @param email The email of the passenger.
* @param password The password of the passenger.
*/
Passenger::Passenger(string username, string email, string password) {
    setUsername(username);
    setEmail(email);
    setPassword(password);
    setUserType(UserType::Passenger);
}

/**
* @brief Destructor for Passenger class
*/
Passenger::~Passenger() {}

/**
* @brief Get the list of tickets for the passenger.
* @return A pointer to the list of tickets.
*/
list<Ticket>* Passenger::getTickets() const {
    return const_cast<list<Ticket>*>(&tickets);
}

/**
 * @brief Constructor for AnonymousUser class
 * @param identifier The identifier for the anonymous user session.
 */
AnonymousUser::AnonymousUser(int identifier) : identifier(identifier) {}

/**
 * @brief Destructor for AnonymousUser class
 */
AnonymousUser::~AnonymousUser() {}

/**
 * @brief Get the identifier of the anonymous user session.
 * @return The identifier.
 */
int AnonymousUser::getIdentifier() const {
    return identifier;
}

/**
 * @brief Register a new user account from the anonymous user state.
 */
void AnonymousUser::_register() {
    //TODO
}

/**
 * @brief Reset the password of a user from the anonymous user state.
 */
void AnonymousUser::resetPassword() {
    //TODO
}

/**
 * @brief Log the anonymous user into the authenticated state.
 */
void AnonymousUser::login() {
    //TODO
}

/**
* @brief Overload the << operator for printing user types.
* @param os The output stream.
* @param type The user type to print.
* @return The output stream.
*/
ostream& operator<<(ostream& os, UserType type) {
    switch (type) {
        case UserType::Passenger:
            return os << "Passenger";
        case UserType::TrainCompany:
            return os << "TrainCompany";
        case UserType::NetworkManager:
            return os << "NetworkManager";
        default:
            return os << "Unknown";
    }
}
