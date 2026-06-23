import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private static List<User> users = new ArrayList<>();

    static {
        // Default users
        users.add(new User("root", "root123"));
        users.add(new User("harish", "1234"));
    }

    public static User authenticate(String username, String password) {

        for (User user : users) {
            if (user.getUsername().equals(username) &&
                user.checkPassword(password)) {
                return user;
            }
        }

        return null;
    }

    public static void registerUser(String username, String password) {
        users.add(new User(username, password));
    }
}