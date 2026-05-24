#include "user_management.h"
#include <iostream>

/*
* @brief Destructor for AuthenticatedUser class
*/
AuthenticatedUser::~AuthenticatedUser() {}

/*
* @brief Get the username of the authenticated user.
* @return The username.
*/
const string& AuthenticatedUser::getUsername() const { return username; }

/*
* @brief Get the email of the authenticated user.
* @return The email.
*/
const string& AuthenticatedUser::getEmail() const { return email; }

/*
* @brief Get the password of the authenticated user.
* @return The password.
*/
const string& AuthenticatedUser::getPassword() const { return password; }

/*
* @brief Get the type of the authenticated user.
* @return The user type.
*/
UserType AuthenticatedUser::getUserType() const { return userType; }

/*
* @brief Set the username of the authenticated user.
* @param username The username to set.
*/
void AuthenticatedUser::setUsername(const string& username) {
    this->username = username;
}

/*
* @brief Set the email of the authenticated user.
* @param email The email to set.
*/
void AuthenticatedUser::setEmail(const string& email) {
    this->email = email;
}

/*
* @brief Set the password of the authenticated user.
* @param password The password to set.
*/
void AuthenticatedUser::setPassword(const string& password) {
    this->password = password;
}

/*
* @brief Set the type of the authenticated user.
* @param type The user type to set.
*/
void AuthenticatedUser::setUserType(UserType type) {
    this->userType = type;
}

/*
* @brief Log out the authenticated user, moving to the anonymous user state.
*/
void AuthenticatedUser::logout() {
    //TODO
}

/*
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

/*
* @brief Destructor for NetworkManager class
*/
NetworkManager::~NetworkManager() {}

/*
* @brief Print the network, including stations, junctions, and lines.
*/
void NetworkManager::viewNetwork() {
    //TODO
}

/*
* @brief Create a new station in the network.
*/
void NetworkManager::createStation() {
    //TODO
}

/*
* @brief Create a new junction in the network.
*/
void NetworkManager::createJunction() {
    //TODO
}

/*
* @brief Create a new line in the network.
*/
void NetworkManager::createLine() {
    //TODO
}

/*
* @brief Edit an existing station in the network.
* @param station The station to edit.
*/
void NetworkManager::editStation(Station*&) {
    //TODO
}

/*
* @brief Edit an existing junction in the network.
* @param junction The junction to edit.
*/
void NetworkManager::editJunction(Junction*&) {
    //TODO
}

/*
* @brief Edit an existing line in the network.
* @param line The line to edit.
*/
void NetworkManager::editLine(Line*&) {
    //TODO
}

/*
* @brief Delete an existing station from the network.
* @param station The station to delete.
*/
void NetworkManager::deleteStation(Station*&) {
    //TODO
}

/*
* @brief Delete an existing junction from the network.
* @param junction The junction to delete.
*/
void NetworkManager::deleteJunction(Junction*&) {
    //TODO
}

/*
* @brief Delete an existing line from the network.
* @param line The line to delete.
*/
void NetworkManager::deleteLine(Line*&) {
    //TODO
}

/*
* @brief Review a service request submitted by a train company.
* @param request The service request to review.
*/
void NetworkManager::reviewServiceRequest(ServiceRequest*&) {
    //TODO
}

/*
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
