import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Enumeration;

public class ShafiClientGUI extends JFrame {
    private static final int PORT = 5000;
    private static final String CLIENT_DIRECTORY = "client_files/";

    private JTextField ipField;
    private JLabel statusLabel;
    private DefaultListModel<String> sentModel;
    private DefaultListModel<String> serverModel;
    private JList<String> serverList;

    // Professional Shafi Color Palette
    private final Color PINK_BACKGROUND = Color.decode("#FCE4EC");
    private final Color PINK_BUTTON = Color.decode("#EC407A");
    private final Color LIST_BACKGROUND = Color.decode("#F8BBD0");
    private final Color WHITE_TEXT = Color.WHITE;

    public ShafiClientGUI() {
        setTitle("Shafi");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(PINK_BACKGROUND);

        // Ensures the local folder exists for default operations
        File dir = new File(CLIENT_DIRECTORY);
        if (!dir.exists()) dir.mkdirs();

        buildInterface();
        requestFileList(); // Attempts to connect and load the list on startup
    }

    private void buildInterface() {
        // --- TOP: IP Configuration and Connection Status ---
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(PINK_BACKGROUND);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        inputPanel.setBackground(PINK_BACKGROUND);
        inputPanel.add(new JLabel("Server IP:"));

        ipField = new JTextField("127.0.0.1", 12);
        ipField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputPanel.add(ipField);

        JButton btnRefresh = createButton("Connect / Refresh");
        btnRefresh.addActionListener(e -> requestFileList());
        inputPanel.add(btnRefresh);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setBackground(PINK_BACKGROUND);
        statusLabel = new JLabel("Status: Checking connection...");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        statusPanel.add(statusLabel);

        topPanel.add(inputPanel);
        topPanel.add(statusPanel);

        // Adds top margin
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(topPanel, BorderLayout.NORTH);

        // --- CENTER: Split Lists ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setBackground(PINK_BACKGROUND);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Left: Files Sent by the User
        sentModel = new DefaultListModel<>();
        JList<String> sentList = new JList<>(sentModel);
        sentList.setBackground(LIST_BACKGROUND);
        JScrollPane sentScroll = new JScrollPane(sentList);
        sentScroll.setBorder(createBorder("My Sent Files"));
        centerPanel.add(sentScroll);

        // Right: Files Available on the Server
        serverModel = new DefaultListModel<>();
        serverList = new JList<>(serverModel);
        serverList.setBackground(LIST_BACKGROUND);
        JScrollPane serverScroll = new JScrollPane(serverList);
        serverScroll.setBorder(createBorder("Files Available for Download"));
        centerPanel.add(serverScroll);

        add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM: Action Buttons ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(PINK_BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        JButton btnUpload = createButton("Choose and Send File");
        btnUpload.addActionListener(e -> openFileManagerAndSend());
        bottomPanel.add(btnUpload, BorderLayout.WEST);

        JPanel downloadsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        downloadsPanel.setBackground(PINK_BACKGROUND);

        JButton btnDownloadSel = createButton("Download Selected");
        btnDownloadSel.addActionListener(e -> downloadSelectedFile());

        JButton btnDownloadAll = createButton("Download All");
        btnDownloadAll.addActionListener(e -> downloadAllFiles());

        downloadsPanel.add(btnDownloadSel);
        downloadsPanel.add(btnDownloadAll);
        bottomPanel.add(downloadsPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(PINK_BUTTON);
        btn.setForeground(WHITE_TEXT);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private TitledBorder createBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleFont(new Font("SansSerif", Font.BOLD, 14));
        border.setTitleColor(Color.DARK_GRAY);
        return border;
    }

    private String getIP() {
        return ipField.getText().trim();
    }

    private void updateStatus(boolean connected) {
        SwingUtilities.invokeLater(() -> {
            if (connected) {
                statusLabel.setText("Status: Connected to Server ✅");
                statusLabel.setForeground(Color.decode("#2E7D32")); // Dark Green
            } else {
                statusLabel.setText("Status: Disconnected (IP not found) ❌");
                statusLabel.setForeground(Color.decode("#C62828")); // Dark Red
                serverModel.clear();
            }
        });
    }

    private void openFileManagerAndSend() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a file to send to the Server");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File chosenFile = fileChooser.getSelectedFile();
            performUpload(chosenFile);
        }
    }

    private void performUpload(File file) {
        new Thread(() -> {
            try (Socket socket = new Socket(getIP(), PORT);
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 FileInputStream fis = new FileInputStream(file)) {

                out.writeUTF("UPLOAD");
                out.writeUTF(file.getName());
                out.writeLong(file.length());

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                SwingUtilities.invokeLater(() -> {
                    sentModel.addElement(file.getName());
                    JOptionPane.showMessageDialog(this, "File sent successfully!");
                    requestFileList();
                });

            } catch (IOException ex) {
                updateStatus(false);
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Connection error. Check the IP.", "Error", JOptionPane.ERROR_MESSAGE)
                );
            }
        }).start();
    }

    private void requestFileList() {
        new Thread(() -> {
            try (Socket socket = new Socket(getIP(), PORT);
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream())) {

                out.writeUTF("LIST");
                int count = in.readInt();

                // Reads everything from the network before sending to GUI to prevent "Socket closed"
                java.util.List<String> receivedFiles = new java.util.ArrayList<>();
                for (int i = 0; i < count; i++) {
                    receivedFiles.add(in.readUTF());
                }

                updateStatus(true); // Connection and reading successful

                SwingUtilities.invokeLater(() -> {
                    serverModel.clear();
                    for (String fileName : receivedFiles) {
                        serverModel.addElement(fileName);
                    }
                });

            } catch (IOException ex) {
                updateStatus(false); // Connection failed
            }
        }).start();
    }

    // --- MÉTODOS NOVOS PARA ESCOLHA DE DIRETÓRIO ---

    private File chooseDestinationFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Destination Folder");
        // Limita o gerenciador de arquivos para selecionar apenas pastas
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    private void downloadSelectedFile() {
        String selected = serverList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a file from the right list first.");
            return;
        }

        File destFolder = chooseDestinationFolder();
        if (destFolder != null) {
            processDownload(selected, true, destFolder);
        }
    }

    private void downloadAllFiles() {
        if (serverModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No files on the server to download.");
            return;
        }

        File destFolder = chooseDestinationFolder();
        if (destFolder != null) {
            Enumeration<String> elements = serverModel.elements();
            while (elements.hasMoreElements()) {
                processDownload(elements.nextElement(), false, destFolder);
            }
            JOptionPane.showMessageDialog(this, "Starting download of all files to:\n" + destFolder.getAbsolutePath());
        }
    }

    private void processDownload(String fileName, boolean showSingleWarning, File destFolder) {
        new Thread(() -> {
            try (Socket socket = new Socket(getIP(), PORT);
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream())) {

                out.writeUTF("DOWNLOAD");
                out.writeUTF(fileName);

                if (!in.readBoolean()) return;

                long size = in.readLong();
                // Usa a pasta selecionada pelo usuário como destino
                File dest = new File(destFolder, "download_" + fileName);

                try (FileOutputStream fos = new FileOutputStream(dest)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long totalRead = 0;
                    while (totalRead < size && (bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, size - totalRead))) != -1) {
                        fos.write(buffer, 0, bytesRead);
                        totalRead += bytesRead;
                    }
                }

                if (showSingleWarning) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Download complete!\nSaved at: " + dest.getAbsolutePath())
                    );
                }
            } catch (IOException ex) {
                updateStatus(false);
            }
        }).start();
    }

    public static void main(String[] args) {
        // Strictly starts the Client's Graphical User Interface
        SwingUtilities.invokeLater(() -> new ShafiClientGUI().setVisible(true));
    }
}