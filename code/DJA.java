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
    private Color primaryColor = new Color(25, 118, 210);
    private Color accentColor = new Color(66, 165, 245);
    private Color backgroundColor = new Color(245, 245, 245);
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    private Font headerFont = new Font("Segoe UI", Font.BOLD, 18);
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
        header.setBackground(primaryColor);
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("DRAGE Java Apps");
        title.setFont(titleFont);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> loadMarketplaceApps());
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
        appPanel.setLayout(new BorderLayout(15, 15));
        appPanel.setBackground(Color.WHITE);
        appPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // App info panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(app.getString("name"));
        nameLabel.setFont(headerFont);
        nameLabel.setForeground(primaryColor);
        
        JLabel descLabel = new JLabel(app.getString("description"));
        descLabel.setFont(normalFont);
        
        JLabel versionLabel = new JLabel("Version: " + app.getString("version"));
        versionLabel.setFont(normalFont);
        
        JLabel authorLabel = new JLabel("By: " + app.getString("author"));
        authorLabel.setFont(normalFont);
        
        infoPanel.add(nameLabel);
        infoPanel.add(descLabel);
        infoPanel.add(versionLabel);
        infoPanel.add(authorLabel);

        JButton installButton = createStyledButton("Install");
        installButton.setBackground(accentColor);
        installButton.setForeground(Color.WHITE);
        installButton.addActionListener(e -> installApp(app));

        appPanel.add(infoPanel, BorderLayout.CENTER);
        appPanel.add(installButton, BorderLayout.SOUTH);

        // Add to marketplace with GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.insets = new Insets(10, 10, 10, 10);

        marketplacePanel.add(appPanel, gbc);
        marketplacePanel.revalidate();
        marketplacePanel.repaint();
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
