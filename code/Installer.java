import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class Installer extends JFrame {
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private static final String JSON_LIB_URL = "https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar";
    private static final String DASHBOARD_URL = "https://raw.githubusercontent.com/DRAGEno01/DRAGE-Java-Apps/main/code/DJA.java";

    public Installer() {
        setTitle("DRAGE Java Apps Installer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 250);
        setLocationRelativeTo(null);
        setResizable(false);

        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Installing DRAGE Java Apps");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Status label with custom font
        statusLabel = new JLabel("Starting installation...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(100, 100, 100));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Custom progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(550, 25));
        progressBar.setMaximumSize(new Dimension(550, 25));
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        progressBar.setForeground(new Color(46, 204, 113));
        progressBar.setBackground(new Color(240, 240, 240));
        progressBar.setBorderPainted(false);
        mainPanel.add(progressBar);

        // Add shadow border to main window
        getRootPane().setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        setContentPane(mainPanel);
        setVisible(true);
        
        startInstallation();
    }

    private void startInstallation() {
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Create directories
                    updateStatus("Creating directories...", 10);
                    createDirectories();

                    // Download JSON library
                    updateStatus("Downloading JSON library...", 30);
                    downloadFile(JSON_LIB_URL, "lib/json.jar");

                    // Download and compile dashboard
                    updateStatus("Downloading Dashboard...", 60);
                    downloadFile(DASHBOARD_URL, "src/DJA.java");

                    // Compile
                    updateStatus("Compiling...", 80);
                    compile();

                    // Create shortcut and cleanup
                    updateStatus("Creating shortcut...", 90);
                    createShortcut();
                    cleanup();

                    updateStatus("Installation complete!", 100);
                    JOptionPane.showMessageDialog(null, "Installation completed successfully!");
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Installation failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                return null;
            }
        };
        worker.execute();
    }

    private void updateStatus(String status, int progress) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
            progressBar.setValue(progress);
            // Add animation effect
            progressBar.setString(progress + "%");
        });
    }

    private void createDirectories() {
        new File("lib").mkdirs();
        new File("src").mkdirs();
    }

    private void downloadFile(String url, String saveAs) throws Exception {
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream out = new FileOutputStream(saveAs)) {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = in.read(buffer, 0, 1024)) != -1) {
                out.write(buffer, 0, count);
            }
        }
    }

    private void compile() throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
            "javac", 
            "-cp", "lib/json.jar", 
            "src/DJA.java"
        );
        Process p = pb.start();
        p.waitFor();
    }

    private void cleanup() {
        // Delete the source file after compilation
        new File("src/DJA.java").delete();
    }

    private void createShortcut() throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        String currentDir = new File(".").getAbsolutePath().replace("\\.", "");
        
        if (os.contains("windows")) {
            // Create Windows .bat file in current directory
            String batchContent = "@echo off\n" +
                "cd \"" + currentDir + "\"\n" +
                "java -cp \"src;lib/json.jar\" DJA";
            
            File batchFile = new File("DRAGE Java Apps.bat");
            try (FileOutputStream fos = new FileOutputStream(batchFile)) {
                fos.write(batchContent.getBytes());
            }
            
            JOptionPane.showMessageDialog(null, 
                "Launcher created: " + batchFile.getAbsolutePath() + "\n" +
                "You can move it to your desktop or create a shortcut to it.",
                "Installation Complete",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Create Unix/Linux/Mac launcher script
            String shellContent = "#!/bin/bash\n" +
                "cd \"" + currentDir + "\"\n" +
                "java -cp \"src:lib/json.jar\" DJA";
            
            File shellFile = new File("DRAGE Java Apps");
            try (FileOutputStream fos = new FileOutputStream(shellFile)) {
                fos.write(shellContent.getBytes());
                shellFile.setExecutable(true);
            }
            
            JOptionPane.showMessageDialog(null, 
                "Launcher created: " + shellFile.getAbsolutePath() + "\n" +
                "You can move it to your desktop or create a shortcut to it.",
                "Installation Complete",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Installer());
    }
}
