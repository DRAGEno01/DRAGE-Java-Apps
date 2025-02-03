import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;
import java.nio.file.*;
import javax.swing.border.AbstractBorder;

public class DJA extends JFrame {
    private static final String MARKETPLACE_URL = "https://raw.githubusercontent.com/DRAGEno01/DRAGE-Java-Apps/main/code/apps.json";
    private JPanel installedAppsPanel;
    private JPanel marketplacePanel;
    private Color primaryColor = new Color(63, 81, 181);    // Material Indigo
    private Color accentColor = new Color(92, 107, 192);    // Lighter Indigo
    private Color buttonColor = new Color(48, 63, 159);     // Darker Indigo for buttons
    private Color backgroundColor = new Color(250, 250, 250);
    private Color cardColor = Color.WHITE;
    private Color textColor = new Color(33, 33, 33);
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 28);
    private Font headerFont = new Font("Segoe UI", Font.BOLD, 20);
    private Font normalFont = new Font("Segoe UI", Font.PLAIN, 14);

    public DJA() {
        setTitle("DRAGE Java Apps");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.arc", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Main layout
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(backgroundColor);

        // Create header
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH);

        // Create main content
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(headerFont);
        tabbedPane.setBackground(Color.WHITE);
        
        // Installed Apps Panel
        installedAppsPanel = new JPanel(new GridBagLayout());
        installedAppsPanel.setBackground(Color.WHITE);
        JScrollPane installedScroll = new JScrollPane(installedAppsPanel);
        installedScroll.setBorder(null);
        installedScroll.getViewport().setBackground(Color.WHITE);
        tabbedPane.addTab("Installed Apps", installedScroll);

        // Marketplace Panel
        marketplacePanel = new JPanel(new GridBagLayout());
        marketplacePanel.setBackground(Color.WHITE);
        JScrollPane marketplaceScroll = new JScrollPane(marketplacePanel);
        marketplaceScroll.setBorder(null);
        marketplaceScroll.getViewport().setBackground(Color.WHITE);
        tabbedPane.addTab("Marketplace", marketplaceScroll);

        add(tabbedPane, BorderLayout.CENTER);

        // Load apps
        loadMarketplaceApps();
        loadInstalledApps();

        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        header.setBackground(primaryColor);  // Set background color
        
        // Add logo and title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titlePanel.setBackground(primaryColor);  // Match header background
        
        JLabel logoLabel = new JLabel("⚡");
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        logoLabel.setForeground(Color.WHITE);
        
        JLabel title = new JLabel("DRAGE Java Apps");
        title.setFont(titleFont);
        title.setForeground(Color.WHITE);
        
        titlePanel.add(logoLabel);
        titlePanel.add(title);
        
        // Wider refresh button
        JButton refreshButton = new JGradientButton("⟳ Refresh");
        refreshButton.setPreferredSize(new Dimension(150, 45));  // Set fixed width
        refreshButton.addActionListener(e -> loadMarketplaceApps());
        
        header.add(titlePanel, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);
        
        return header;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(normalFont);
        button.setForeground(primaryColor);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadMarketplaceApps() {
        marketplacePanel.removeAll();
        
        try {
            URL url = new URL(MARKETPLACE_URL);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                StringBuilder jsonContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonContent.append(line);
                }
                
                JSONObject json = new JSONObject(jsonContent.toString());
                JSONArray apps = json.getJSONArray("apps");
                
                for (int i = 0; i < apps.length(); i++) {
                    JSONObject app = apps.getJSONObject(i);
                    addAppToMarketplace(app);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading marketplace: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
        
        marketplacePanel.revalidate();
        marketplacePanel.repaint();
    }

    private void addAppToMarketplace(JSONObject app) {
        JPanel appPanel = new JPanel();
        appPanel.setLayout(new BorderLayout(20, 15));
        appPanel.setBackground(cardColor);
        
        // Enhanced shadow and rounded corners
        appPanel.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(10, 0.2f),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // App header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        headerPanel.setBackground(cardColor);
        
        String cleanName = app.getString("name").replaceAll("\\s+", " ").trim();
        JLabel nameLabel = new JLabel(cleanName);
        nameLabel.setFont(headerFont);
        nameLabel.setForeground(primaryColor);
        
        headerPanel.add(nameLabel);

        // Description panel
        JTextArea descLabel = new JTextArea(app.getString("description").replaceAll("\\[|\\]|\\s+", " ").trim());
        descLabel.setFont(normalFont);
        descLabel.setLineWrap(true);
        descLabel.setWrapStyleWord(true);
        descLabel.setEditable(false);
        descLabel.setBackground(cardColor);
        descLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        descLabel.setForeground(textColor);

        // Details panel
        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        detailsPanel.setBackground(cardColor);
        
        JLabel versionLabel = new JLabel("Version " + app.getString("version").trim());
        versionLabel.setFont(normalFont);
        versionLabel.setForeground(new Color(100, 100, 100));
        
        JLabel authorLabel = new JLabel("By " + app.getString("author").trim());
        authorLabel.setFont(normalFont);
        authorLabel.setForeground(new Color(100, 100, 100));
        
        detailsPanel.add(versionLabel);
        detailsPanel.add(authorLabel);

        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(cardColor);
        contentPanel.add(headerPanel);
        contentPanel.add(descLabel);
        contentPanel.add(detailsPanel);

        // Install button
        JButton installButton = new JGradientButton("INSTALL");
        installButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        installButton.addActionListener(e -> installApp(app));

        appPanel.add(contentPanel, BorderLayout.CENTER);
        appPanel.add(installButton, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(15, 15, 15, 15);

        marketplacePanel.add(appPanel, gbc);
    }

    private void loadInstalledApps() {
        installedAppsPanel.removeAll();
        File appsDir = new File("installed_apps");
        
        // Get online versions first
        Map<String, String> onlineVersions = new HashMap<>();
        try {
            URL url = new URL(MARKETPLACE_URL);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                StringBuilder jsonContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonContent.append(line);
                }
                
                JSONObject json = new JSONObject(jsonContent.toString());
                JSONArray apps = json.getJSONArray("apps");
                for (int i = 0; i < apps.length(); i++) {
                    JSONObject app = apps.getJSONObject(i);
                    onlineVersions.put(
                        app.getString("name").replaceAll("\\s+", ""),
                        app.getString("version")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (appsDir.exists() && appsDir.isDirectory()) {
            File[] files = appsDir.listFiles((dir, name) -> name.endsWith(".jar"));
            if (files != null) {
                for (File file : files) {
                    String appName = file.getName().replace(".jar", "");
                    String onlineVersion = onlineVersions.get(appName);
                    String localVersion = getLocalVersion(file);
                    
                    addInstalledApp(file, onlineVersion, localVersion);
                }
            }
        }
        
        installedAppsPanel.revalidate();
        installedAppsPanel.repaint();
    }

    private String getLocalVersion(File jarFile) {
        // Read version from jar manifest or a version file
        try {
            File versionFile = new File("installed_apps/" + jarFile.getName().replace(".jar", ".version"));
            if (versionFile.exists()) {
                return Files.readString(versionFile.toPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1.0"; // Default version if not found
    }

    private void addInstalledApp(File appFile, String onlineVersion, String localVersion) {
        JPanel appPanel = new JPanel();
        appPanel.setLayout(new BorderLayout(15, 15));
        appPanel.setBackground(Color.WHITE);
        appPanel.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(10, 0.2f),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // App info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        String appName = appFile.getName().replace(".jar", "");
        JLabel nameLabel = new JLabel(appName);
        nameLabel.setFont(headerFont);
        nameLabel.setForeground(primaryColor);

        JLabel versionLabel = new JLabel("Version: " + localVersion);
        versionLabel.setFont(normalFont);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(versionLabel);

        // Action button
        JButton actionButton;
        if (onlineVersion != null && !onlineVersion.equals(localVersion)) {
            // Update available
            actionButton = new JGradientButton("Update Available");
            actionButton.setBackground(accentColor);
            actionButton.addActionListener(e -> {
                int choice = JOptionPane.showConfirmDialog(this,
                    "A new version (" + onlineVersion + ") is available. Would you like to update?",
                    "Update Available",
                    JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    // Trigger update from marketplace
                    loadMarketplaceApps();
                }
            });
            
            // Add update indicator
            JLabel updateLabel = new JLabel("🔄 New version " + onlineVersion + " available");
            updateLabel.setFont(normalFont);
            updateLabel.setForeground(new Color(255, 140, 0));
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(updateLabel);
        } else {
            // Launch button
            actionButton = new JGradientButton("Launch");
            actionButton.addActionListener(e -> launchApp(appFile));
        }

        appPanel.add(infoPanel, BorderLayout.CENTER);
        appPanel.add(actionButton, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(10, 10, 10, 10);

        installedAppsPanel.add(appPanel, gbc);
    }

    private void installApp(JSONObject app) {
        try {
            String appName = app.getString("name").replaceAll("\\s+", " ").trim();
            String appUrl = app.getString("url");
            String version = app.getString("version").trim();
            
            if (appName.equals("DJA")) {
                updateDashboard(appUrl);
            } else {
                installRegularApp(appName, appUrl, version);
            }

            JOptionPane.showMessageDialog(this, 
                "App installed successfully!",
                "Installation Complete",
                JOptionPane.INFORMATION_MESSAGE);

            loadInstalledApps();
        } catch (Exception e) {
            e.printStackTrace();  // Print the full error stack trace
            JOptionPane.showMessageDialog(this, 
                "Error installing app: " + e.getMessage(),
                "Installation Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDashboard(String url) throws Exception {
        try {
            // Create src directory if it doesn't exist
            File srcDir = new File("src");
            if (!srcDir.exists()) {
                srcDir.mkdirs();
            }

            // Download new DJA.java
            File tempFile = new File("src/DJA.java.new");
            downloadFile(url, tempFile);

            // Get absolute paths
            File jsonJar = new File("lib/json.jar").getAbsoluteFile();
            
            // Compile with proper paths
            String[] command = {
                "javac",
                "-cp",
                jsonJar.getAbsolutePath(),
                tempFile.getAbsolutePath()
            };
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            
            // Capture the compiler output
            StringBuilder output = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    output.append(line).append("\n");
                    System.out.println("Compiler output: " + line);
                }
            }

            int result = p.waitFor();
            if (result == 0) {
                File currentFile = new File("src/DJA.java");
                Files.move(tempFile.toPath(), currentFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                restartApplication();
            } else {
                tempFile.delete();
                throw new Exception("Compilation failed:\n" + output.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void installRegularApp(String appName, String url, String version) throws Exception {
        File appsDir = new File("installed_apps");
        if (!appsDir.exists()) {
            appsDir.mkdirs();
        }

        String jarName = appName.replaceAll("\\s+", "") + ".jar";
        File jarFile = new File(appsDir, jarName);
        downloadFile(url, jarFile);

        // Save version info
        File versionFile = new File(appsDir, jarName.replace(".jar", ".version"));
        Files.writeString(versionFile.toPath(), version);
    }

    private void downloadFile(String url, File outputFile) throws Exception {
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream out = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = in.read(buffer, 0, 1024)) != -1) {
                out.write(buffer, 0, count);
            }
        }
    }

    private void restartApplication() throws Exception {
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        File currentJar = new File(".");
        
        ProcessBuilder pb = new ProcessBuilder(
            javaBin, 
            "-cp", 
            "src;lib/json.jar", 
            "DJA"
        );
        pb.directory(currentJar);
        pb.start();
        
        System.exit(0);
    }

    private void launchApp(File appFile) {
        try {
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", appFile.getAbsolutePath());
            pb.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error launching app: " + e.getMessage(),
                "Launch Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Custom gradient button class
    private class JGradientButton extends JButton {
        public JGradientButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(getPreferredSize().width, 45));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isPressed()) {
                g2.setPaint(buttonColor.darker());
            } else if (getModel().isRollover()) {
                g2.setPaint(accentColor);
            } else {
                g2.setPaint(buttonColor);
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    // Custom shadow border class
    private class ShadowBorder extends AbstractBorder {
        private final int shadowSize;
        private final float shadowOpacity;

        public ShadowBorder(int size, float opacity) {
            this.shadowSize = size;
            this.shadowOpacity = opacity;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            for (int i = 0; i < shadowSize; i++) {
                float opacity = shadowOpacity * (shadowSize - i) / shadowSize;
                g2.setColor(new Color(0, 0, 0, (int)(opacity * 255)));
                g2.drawRoundRect(x + i, y + i, width - i * 2 - 1, height - i * 2 - 1, 20, 20);
            }
            
            g2.setColor(cardColor);
            g2.fillRoundRect(x, y, width - 1, height - 1, 20, 20);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(shadowSize, shadowSize, shadowSize, shadowSize);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DJA());
    }
}
