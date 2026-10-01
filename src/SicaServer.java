import java.io.*;
import java.net.*;

public class SicaServer {
    // Defines the TCP communication port and the directory where server files will be saved
    private static final int PORT = 5000;
    private static final String SERVER_DIRECTORY = "server_files/";

    public static void main(String[] args) {
        // Creates the server folder if it doesn't exist
        File directory = new File(SERVER_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Initializes the TCP ServerSocket
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("SICA Server started on port " + PORT + ". Waiting for connections...");

            while (true) {
                // The accept() method blocks execution until a client connects
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Creates a new Thread to handle the client, allowing multiple simultaneous accesses
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
        }
    }

    /**
     * Inner class that implements Runnable to handle each client in a separate Thread.
     */
    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            // Uses DataInputStream and DataOutputStream to read/write primitive data and formatted strings
            try (DataInputStream in = new DataInputStream(socket.getInputStream());
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

                // Reads the command sent by the client
                String command = in.readUTF();

                switch (command) {
                    case "UPLOAD":
                        receiveFile(in);
                        break;
                    case "LIST":
                        listFiles(out);
                        break;
                    case "DOWNLOAD":
                        sendFile(in, out);
                        break;
                    default:
                        System.out.println("Unknown command.");
                }
            } catch (IOException e) {
                System.err.println("Error in client communication: " + e.getMessage());
            } finally {
                try {
                    socket.close(); // Ensures the socket is closed
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Method to receive a file from the client (Upload).
         */
        private void receiveFile(DataInputStream in) throws IOException {
            String fileName = in.readUTF(); // Reads the sent file name
            long fileSize = in.readLong(); // Reads the total size in bytes

            File file = new File(SERVER_DIRECTORY + fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalRead = 0;

                // Reads bytes from the network stream and writes them to the local file
                while (totalRead < fileSize &&
                        (bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalRead))) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }
                System.out.println("File " + fileName + " received successfully.");
            }
        }

        /**
         * Method to send the list of files present on the server.
         */
        private void listFiles(DataOutputStream out) throws IOException {
            File directory = new File(SERVER_DIRECTORY);
            File[] files = directory.listFiles();

            if (files != null && files.length > 0) {
                out.writeInt(files.length); // Informs the amount of files
                for (File f : files) {
                    if (f.isFile()) {
                        out.writeUTF(f.getName()); // Sends the name of each file
                    }
                }
            } else {
                out.writeInt(0); // Informs that the directory is empty
            }
        }

        /**
         * Method to send a file requested by the client (Download).
         */
        private void sendFile(DataInputStream in, DataOutputStream out) throws IOException {
            String requestedFileName = in.readUTF();
            File file = new File(SERVER_DIRECTORY + requestedFileName);

            if (file.exists() && !file.isDirectory()) {
                out.writeBoolean(true); // Confirms the file exists
                out.writeLong(file.length()); // Informs the size

                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    // Reads bytes from the local file and writes them to the network stream
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("File " + requestedFileName + " sent to the client.");
            } else {
                out.writeBoolean(false); // Informs that the file does not exist
            }
        }
    }
}