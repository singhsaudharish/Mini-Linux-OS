import java.awt.*;
import javax.swing.*;

public class LoginScreen extends JFrame {

    public LoginScreen() {

        setTitle("Mini OS Login");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(loginBtn);
        panel.add(registerBtn);

        add(panel);
        setVisible(true);

        ImageIcon icon = new ImageIcon("resources/icon.png");
        setIconImage(icon.getImage());

        // LOGIN LOGIC
        loginBtn.addActionListener(e -> {

            String username = userField.getText();
            String password = new String(passField.getPassword());

            User user = UserManager.authenticate(username, password);

            if (user != null) {
                dispose(); // close login
                new MiniLinuxTerminal(user); // open terminal
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid Credentials");
            }
        });

        // REGISTER LOGIC
        registerBtn.addActionListener(e -> {

            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (!username.isEmpty() && !password.isEmpty()) {
                UserManager.registerUser(username, password);
                JOptionPane.showMessageDialog(this,
                        "User Registered Successfully!");
            }
        });
    }

    public static void main(String[] args) {
        new LoginScreen();
    }
}