package user_management;

public class Main {
    public static void main(String[] args) {
        NetworkManager networkManager = new NetworkManager("admin", "admin@example.com", "passwordsicura");

        System.out.println("Username: " + networkManager.getUsername());
        System.out.println("Email: " + networkManager.getEmail());
        System.out.println("User Type: " + networkManager.getUserType());
    }
}
