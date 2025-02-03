import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

public class DJA extends JFrame {
    private static final String MARKETPLACE_URL = "https://raw.githubusercontent.com/DRAGEno01/DRAGE-Java-Apps/main/code/apps.json";
    private JPanel installedAppsPanel;
    private JPanel marketplacePanel;

    public DJA() {
        setTitle("DRAGE Java Apps");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create main layout
        setLayout(new BorderLayout());

        // Create header
        JPanel header = createHeader();
        add(header, BorderLayout.NORTH);

        // Create main content
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Installed Apps Panel
        installedAppsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        installedAppsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane installedScroll = new JScrollPane(installedAppsPanel);
        tabbedPane.addTab("Installed Apps", installedScroll);

        // Marketplace Panel
        marketplacePanel = new JPanel(new GridLayout(0, 2, 10, 10));
        marketplacePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane marketplaceScroll = new JScrollPane(marketplacePanel);
        tabbedPane.addTab("Marketplace", marketplaceScroll);

        add(tabbedPane, BorderLayout.CENTER);

        // Load marketplace apps
        loadMarketplaceApps();
        loadInstalledApps();

        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setBackground(new Color(33, 150, 243));
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("DRAGE Java Apps");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadMarketplaceApps());
        header.add(refreshButton, BorderLayout.EAST);

        return header;
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
        appPanel.setLayout(new BorderLayout(10, 10));
        appPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // App info panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        JLabel nameLabel = new JLabel(app.getString("name"));
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel descLabel = new JLabel(app.getString("description"));
        JLabel versionLabel = new JLabel("Version: " + app.getString("version"));
        JLabel authorLabel = new JLabel("By: " + app.getString("author"));
        
        infoPanel.add(nameLabel);
        infoPanel.add(descLabel);
        infoPanel.add(versionLabel);
        infoPanel.add(authorLabel);

        // Install button
        JButton installButton = new JButton("Install");
        installButton.addActionListener(e -> installApp(app));

        appPanel.add(infoPanel, BorderLayout.CENTER);
        appPanel.add(installButton, BorderLayout.SOUTH);

        marketplacePanel.add(appPanel);
    }

    private void loadInstalledApps() {
        installedAppsPanel.removeAll();
        File appsDir = new File("installed_apps");
        
        if (appsDir.exists() && appsDir.isDirectory()) {
            File[] files = appsDir.listFiles((dir, name) -> name.endsWith(".jar"));
            if (files != null) {
                for (File file : files) {
                    addInstalledApp(file);
                }
            }
        }
        
        installedAppsPanel.revalidate();
        installedAppsPanel.repaint();
    }

    private void addInstalledApp(File appFile) {
        JPanel appPanel = new JPanel();
        appPanel.setLayout(new BorderLayout(10, 10));
        appPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel nameLabel = new JLabel(appFile.getName().replace(".jar", ""));
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton launchButton = new JButton("Launch");
        launchButton.addActionListener(e -> launchApp(appFile));

        appPanel.add(nameLabel, BorderLayout.CENTER);
        appPanel.add(launchButton, BorderLayout.SOUTH);

        installedAppsPanel.add(appPanel);
    }

    private void installApp(JSONObject app) {
        try {
            String appUrl = app.getString("url");
            String appName = app.getString("name").replaceAll("\\s+", "") + ".jar";
            
            File appsDir = new File("installed_apps");
            if (!appsDir.exists()) {
                appsDir.mkdirs();
            }

            File outputFile = new File(appsDir, appName);
            
            // Download the app
            try (BufferedInputStream in = new BufferedInputStream(new URL(appUrl).openStream());
                 FileOutputStream out = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                int count;
                while ((count = in.read(buffer, 0, 1024)) != -1) {
                    out.write(buffer, 0, count);
                }
            }

            JOptionPane.showMessageDialog(this, 
                "App installed successfully!",
                "Installation Complete",
                JOptionPane.INFORMATION_MESSAGE);

            loadInstalledApps();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error installing app: " + e.getMessage(),
                "Installation Error",
                JOptionPane.ERROR_MESSAGE);
        }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DJA());
    }
}
