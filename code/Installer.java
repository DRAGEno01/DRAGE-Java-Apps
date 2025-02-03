import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.zip.*;

public class Installer extends JFrame {
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private static final String MAVEN_URL = "https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip";
    private static final String JAVAFX_SDK_URL = "https://download2.gluonhq.com/openjfx/17.0.2/openjfx-17.0.2_windows-x64_bin-sdk.zip";
    private static final String DASHBOARD_URL = "https://raw.githubusercontent.com/DRAGEno01/DRAGE-Java-Apps/refs/heads/main/code/DRAGE%20Java%20Apps.java";

    public Installer() {
        setTitle("DRAGE Java Apps Installer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 150);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statusLabel = new JLabel("Starting installation...");
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        mainPanel.add(statusLabel, BorderLayout.NORTH);
        mainPanel.add(progressBar, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
        
        startInstallation();
    }

    private void startInstallation() {
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Create directories
                    updateStatus("Creating directories...", 5);
                    createDirectories();

                    // Download and extract Maven
                    updateStatus("Downloading Maven...", 15);
                    downloadAndExtract(MAVEN_URL, "tools/maven.zip", "tools/maven");

                    // Download and extract JavaFX SDK
                    updateStatus("Downloading JavaFX SDK...", 35);
                    downloadAndExtract(JAVAFX_SDK_URL, "tools/javafx.zip", "tools/javafx");

                    // Create pom.xml
                    updateStatus("Creating Maven configuration...", 60);
                    createPomXml();

                    // Download dashboard source
                    updateStatus("Downloading Dashboard...", 75);
                    downloadDashboard();

                    // Build project
                    updateStatus("Building project...", 85);
                    buildProject();

                    // Cleanup
                    updateStatus("Cleaning up...", 95);
                    cleanup();

                    updateStatus("Installation complete!", 100);
                    JOptionPane.showMessageDialog(null, "Installation completed successfully!");
                    launchDashboard();
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
        });
    }

    private void createDirectories() {
        createDir("tools");
        createDir("src/main/java");
    }

    private void createDir(String path) {
        new File(path).mkdirs();
    }

    private void downloadAndExtract(String url, String saveAs, String extractTo) throws Exception {
        // Download
        downloadFile(url, saveAs);

        // Extract
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(saveAs))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(extractTo, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    file.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
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

    private void createPomXml() throws IOException {
        String pomContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "    <groupId>com.drage</groupId>\n" +
                "    <artifactId>dja</artifactId>\n" +
                "    <version>1.0-SNAPSHOT</version>\n" +
                "    <properties>\n" +
                "        <maven.compiler.source>11</maven.compiler.source>\n" +
                "        <maven.compiler.target>11</maven.compiler.target>\n" +
                "        <javafx.version>17.0.2</javafx.version>\n" +
                "    </properties>\n" +
                "    <dependencies>\n" +
                "        <dependency>\n" +
                "            <groupId>org.openjfx</groupId>\n" +
                "            <artifactId>javafx-controls</artifactId>\n" +
                "            <version>${javafx.version}</version>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.json</groupId>\n" +
                "            <artifactId>json</artifactId>\n" +
                "            <version>20231013</version>\n" +
                "        </dependency>\n" +
                "    </dependencies>\n" +
                "</project>";
        Files.write(Paths.get("pom.xml"), pomContent.getBytes());
    }

    private void downloadDashboard() throws Exception {
        downloadFile(DASHBOARD_URL, "src/main/java/DJA.java");
    }

    private void buildProject() throws Exception {
        String mvnCmd = System.getProperty("os.name").toLowerCase().contains("windows") 
            ? "tools/maven/bin/mvn.cmd" 
            : "tools/maven/bin/mvn";
        
        ProcessBuilder pb = new ProcessBuilder(mvnCmd, "clean", "package");
        pb.inheritIO();
        Process p = pb.start();
        p.waitFor();
    }

    private void cleanup() {
        new File("tools/maven.zip").delete();
        new File("tools/javafx.zip").delete();
    }

    private void launchDashboard() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("java", 
            "--module-path", "tools/javafx/lib", 
            "--add-modules", "javafx.controls,javafx.fxml",
            "-jar", "target/dja-1.0-SNAPSHOT.jar");
        pb.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Installer());
    }
}
