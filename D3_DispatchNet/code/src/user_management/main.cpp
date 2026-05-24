#include "user_management.h"
#include <iostream>
using namespace std;

int main() {
    NetworkManager networkManager("admin", "admin@example.com", "passwordsicura");
    cout << "Username: " << networkManager.getUsername() << endl;
    cout << "Email: " << networkManager.getEmail() << endl;
    cout << "User Type: " << networkManager.getUserType() << endl;

    return 0;
}
