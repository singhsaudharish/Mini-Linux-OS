import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import javax.swing.*;

public class MiniLinuxTerminal extends JFrame {

    private final Color bgColor = new Color(18, 18, 18);
    private final Color textColor = new Color(0, 255, 70);

    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private File currentDir = new File(System.getProperty("user.dir"));

    private java.util.List<String> history = new java.util.ArrayList<>();
    private int historyIndex = -1;


    private User currentUser;

public MiniLinuxTerminal(User user) {

    this.currentUser = user;
        setTitle("Mini Linux Terminal v1.0");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(bgColor);

        // Add Vertical Scroll  bar
        scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane);
        addInputLine();
        setVisible(true);

        
    }

    private void addInputLine() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.setBackground(bgColor);
    JLabel prompt = createLabel(currentUser.getUsername() + "@miniOS:" + currentDir.getName() + "$ ");
    JTextField input = new JTextField(40);

    input.setFont(new Font("Monospaced", Font.PLAIN, 16));
    input.setForeground(Color.WHITE);
    input.setBackground(bgColor);
    input.setCaretColor(Color.WHITE);
    input.setBorder(null);

    panel.add(prompt);
    panel.add(input);
    mainPanel.add(panel);
    mainPanel.revalidate();

    input.requestFocus();

    // Works when Enter key press
    input.addActionListener(e -> {
        String command = input.getText().trim();
        input.setEditable(false);

        if (!command.isEmpty()) {
            history.add(command);
            historyIndex = history.size();
        }

        executeCommand(command);
        addInputLine();
        scrollToBottom();
    });

    // UP / DOWN ARROW SUPPORT
    input.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyPressed(java.awt.event.KeyEvent e) {

            if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
                if (historyIndex > 0) {
                    historyIndex--;
                    input.setText(history.get(historyIndex));
                }
            }

            if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
                if (historyIndex < history.size() - 1) {
                    historyIndex++;
                    input.setText(history.get(historyIndex));
                } else {
                    historyIndex = history.size();
                    input.setText("");
                }
            }
        }
    });
}


    //Main method to select and call commands
    private void executeCommand(String command) {

     if (command.isEmpty()) return;

     String[] parts = command.trim().split("\\s+");
     String cmd = parts[0].toLowerCase();

     try {
        switch(cmd) {

            case "help":
                help();
                break;

            case "date":
                LocalDate today = LocalDate.now();
                output(today.toString());
                break;

            case "time":
                LocalTime now = LocalTime.now();
                output(now.toString());
                break;
            
            case "datetime":
                ZonedDateTime t = ZonedDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");
                output(t.format(formatter));
                break;

            case "pwd":
                output(currentDir.getAbsolutePath());
                break;
            
            case "cd":
                changeDirectory(parts);
                break;

            case "ls":
                listFiles();
                break;

            case "mkdir":
                makeDir(parts);
                break;

            case "rmdir":
                removeDir(parts);
                break;

            case "touch":
                createFile(parts);
                break;

            case "rm":
                deleteFile(parts);
                break;

            case "cat":
                readFile(parts);
                break;

            case "echo":
                echo(command);
                break;

            case "random":
                Random random = new Random();
                String ran = Integer.toString(random.nextInt(100));
                output("Random number = "+ran);
                break;

            case "os":  
                String osName = System.getProperty("os.name");
                output("Operating System: " + osName);
                break; 

            case "user":
                output("User Name : "+System.getProperty("user.name"));
                break;

            case "memory":
                Runtime r = Runtime.getRuntime();
                output("Free Memory: " + r.freeMemory());
                break;

            case "java":
                output("Java Version : "+System.getProperty("java.version"));
                break;

            case "clear":
                mainPanel.removeAll();
                mainPanel.revalidate();
                mainPanel.repaint();
                break;

            case "exit":
                output("Closing Terminal...");
                //mainPanel.add(output());
                System.exit(0);
                return;

            default:
                output("Command not found.");
        }

    } catch (Exception ex) {
        JLabel error = new JLabel("Error: " + ex.getMessage());
        error.setForeground(Color.RED);
        mainPanel.add(error);
      }
    }


    private void addOutputLine(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(textColor);
        label.setFont(new Font("Monospaced", Font.PLAIN, 16));

        JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linePanel.setBackground(bgColor);
        linePanel.add(label);

        mainPanel.add(linePanel);
        mainPanel.revalidate();
    }

    private void help(){
        addOutputLine("pwd, "+"datetime, "+"ls, "+"mkdir, "+"cat, "+"touch, "+"rm, "+"rmdir, "+"exit, "+"clear, "+"java, "+"user,"+"os, "+"random, "+"echo, "+"cd, "+"date, "+"time");
    }


    // Provides all the files name inside the current Directory
    private void listFiles(){
        File[] files = currentDir.listFiles();
        if (files != null) {
            for (File f : files)
                output(f.getName());
        }
    }  

    //Create a new Directory
    private void makeDir(String[] args) {
        if (args.length < 2) {
            addOutputLine("mkdir: missing directory name\n");
            return;
        }

        File dir = new File(currentDir,args[1]);
        if (dir.mkdir()) {
            output("Directory created\n");
        } 
        else {
            output("Failed to create directory\n");
        }
    }

    // Change the current directory with where the user want to work(User input Directory)
    private void changeDirectory(String[] parts) {
        if (parts.length < 2) {
            output("cd: missing directory name");
            return;
        }

        File dir = new File(currentDir, parts[1]);
        if (dir.exists() && dir.isDirectory())
            currentDir = dir;
        else
            output("Directory not found");
    }

    //Delete the Directory which user input
    private void removeDir(String[] args) {
        if (args.length < 2) {
            output("rmdir: missing directory name\n");
            return;
        }

        File dir = new File(currentDir,args[1]);
        if (dir.delete()) {
            output("Directory removed\n");
        }   
        else {
            output("Failed to remove directory\n");
        }
    }

    // Create a new file inside the working Directory
    private void createFile(String[] args) {
        if (args.length < 2) {
            output("touch: missing file name\n");
            return;
        }

        try {
            File file = new File(currentDir,args[1]);
            if (file.createNewFile()) {
                output("File created\n");
            } 
            else {
                output("File already exists\n");
            }
        } 
        catch (IOException e) {
            output("Error creating file\n");
        }
    }

    // Delete any file from the working Directory which user input
    private void deleteFile(String[] args) {
        if (args.length < 2) {
            output("rm: missing file name\n");
            return;
        }

        File file = new File(currentDir,args[1]);
        if (file.delete()) {
            output("File deleted\n");
        } 
        else {
            output("Failed to delete file\n");
        }
    }

    private void readFile(String[] parts) {
        if (parts.length < 2) {
          output("cat: missing file name");
         return;
        }

        File file = new File(currentDir, parts[1]);

        // Check if file exists
        if (!file.exists()) {
            output("cat: file not found");
            return;
        }

        // Check if it's a directory
        if (file.isDirectory()) {
           output("cat: cannot read directory");
           return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                output(line);
            }
        }   
        catch (IOException e) {
            output("Error: " + e.getMessage(), Color.RED);
        }
    }

    // Write the content inside the file 
    private void echo(String command) {

        if (!command.contains(">")) {
           output(command.substring(5)); // remove "echo "
           return;
        }

        String[] parts = command.split(">");
        String text = parts[0].substring(5).trim();
        String fileName = parts[1].trim();

        File file = new File(currentDir, fileName);

        try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
            writer.write(text + System.lineSeparator());
            output("Written to " + fileName);
        }    
        catch (IOException e) {
            output("Error writing file");
        }
    }



    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(textColor);
        label.setFont(new Font("Monospaced", Font.PLAIN, 16));
        return label;
    }

    private void output(String text) {
        output(text, textColor);
    }

    private void output(String text, Color color) {
        JLabel label = createLabel(text);
        label.setForeground(color);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(bgColor);
        panel.add(label);

        mainPanel.add(panel);
        mainPanel.revalidate();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public static void main(String[] args) {
    User testUser = new User("test", "1234");
    new MiniLinuxTerminal(testUser);
}
}
