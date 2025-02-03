import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Installer {
    private static final String PROGRAM_URL = "https://raw.githubusercontent.com/DRAGEno01/DRAGE-Java-Apps/refs/heads/main/code/DJA.java";
    private static final String OUTPUT_FILE = "DJA.java";
    private static JProgressBar progressBar;
    private static JLabel statusLabel;
    private static JFrame frame;

    public static void main(String[] args) {
        // Create GUI on EDT
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        // Create and setup the window
        frame = new JFrame("DJA Installer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 150);
        frame.setLocationRelativeTo(null);
        
        // Create panel with padding
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create status label
        statusLabel = new JLabel("Downloading...");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Create progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(350, 25));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to panel
        panel.add(statusLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(progressBar);

        frame.add(panel);
        frame.setVisible(true);

        // Start the download process in a separate thread
        new Thread(() -> downloadAndInstall()).start();
    }

    private static void downloadAndInstall() {
        try {
            // Create URL object and open connection
            URL url = new URL(PROGRAM_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int fileSize = connection.getContentLength();

            // Create directory if it doesn't exist
            Path directory = Paths.get("downloaded");
            if (!Files.exists(directory)) {
                Files.createDirectory(directory);
            }

            // Setup the output file path
            String outputPath = directory.resolve(OUTPUT_FILE).toString();

            // Download with progress tracking
            try (BufferedInputStream in = new BufferedInputStream(url.openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(outputPath)) {

                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                int totalBytesRead = 0;

                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    
                    // Update progress bar
                    if (fileSize > 0) {
                        final int progress = (int) ((totalBytesRead * 100.0) / fileSize);
                        SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                    }
                }

                // Update status for compilation
                SwingUtilities.invokeLater(() -> statusLabel.setText("Compiling..."));
                
                // Compile the downloaded Java file
                Process compile = Runtime.getRuntime().exec("javac " + outputPath);
                int compileResult = compile.waitFor();

                if (compileResult == 0) {
                    // Delete the source file
                    Files.delete(Paths.get(outputPath));
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Installation Complete!");
                        progressBar.setValue(100);
                        
                        // Show completion message and close after 2 seconds
                        Timer timer = new Timer(2000, e -> frame.dispose());
                        timer.setRepeats(false);
                        timer.start();
                    });
                } else {
                    showError("Compilation failed!");
                }

            } catch (IOException | InterruptedException e) {
                showError("Error during installation: " + e.getMessage());
            }

        } catch (Exception e) {
            showError("Failed to download file: " + e.getMessage());
        }
    }

    private static void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Error!");
            JOptionPane.showMessageDialog(frame, message, "Installation Error", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
        });
    }
}
