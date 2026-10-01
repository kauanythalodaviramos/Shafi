# Shafi 🌸

<div align="center">

![Shafi Interface](<img width="789" height="546" alt="5d0f9693-839f-45f8-9633-6327e6bd5ab6" src="https://github.com/user-attachments/assets/87b4f26a-7425-4b7d-a8b5-611ee2ad35f4" />
)

A modern, professional TCP Client-Server File Sharing System (SICA) built with Java and Java Swing, featuring a clean pink UI theme, real-time connection status checking, local file selection via file manager, and custom download path mapping.

[English](#-english) | [Português](README.pt.md)

</div>

---

## 📋 About the Project

**Shafi** is a client-server application developed for computer networks using TCP Sockets. It allows users to:
* Connect to a remote server using its IP address.
* Upload local files using the native file manager (`Choose and Send File`).
* View sent files on the left side and available server files on the right side.
* Download specific selected files or download all available server files at once into a custom destination folder.

---

## 🚀 How to Run and Configure

### 1. Requirements
* Java Development Kit (JDK) 17 or higher installed (recommended: JDK 23).
* An IDE such as IntelliJ IDEA.

### 2. Running the Server (`SicaServer.java`)
1. Place `SicaServer.java` inside your project's `src` folder.
2. Run the `main` method of the server. It will listen on port `5000` and automatically create a `server_files/` directory.

### 3. Running the Client GUI (`ShafiClientGUI.java`)
1. Place `ShafiClientGUI.java` inside your project's `src` folder.
2. Run the `main` method to open the pink graphical interface.

---

## 🕹️ Usage Guide & Screenshots

### Main Interface & Connection Status
When you launch the client, type the server's IP address into the top text box and click **Connect / Refresh**. The status label below will dynamically update to show whether you are successfully connected or disconnected.

> ![Main Interface](<img width="789" height="546" alt="5d0f9693-839f-45f8-9633-6327e6bd5ab6" src="https://github.com/user-attachments/assets/3f71bcbb-3bd2-45a0-bcaa-c67a5357b8e2" />
)

### Uploading Files
Clicking **Choose and Send File** opens your computer's native file manager so you can select any file to transmit to the server. Once uploaded, it appears instantly on the left list (`My Sent Files`).

> ![File Manager Upload](<img width="793" height="549" alt="8ed9b4b5-bb9c-465b-b0e1-fd70f9a67cea" src="https://github.com/user-attachments/assets/2ff47f6f-2c92-42cf-980d-e7b5c9127a9e" />
)

### Choosing a Download Destination
When downloading selected or all files, a directory chooser dialog opens, allowing you to pick precisely where you want to save the downloaded items on your PC.

> ![Select Destination Folder](<img width="784" height="545" alt="59fc83a7-b131-435f-ba1d-6dae9643131e" src="https://github.com/user-attachments/assets/5a9176e5-e471-460e-b1f1-f2a558601089" />
)

---

## 📂 Project Structure
* **`SicaServer.java`**: Handles incoming multi-threaded client connections (`UPLOAD`, `LIST`, and `DOWNLOAD` commands).
* **`ShafiClientGUI.java`**: Provides the Swing-based graphical interface with asynchronous background threads to keep the UI smooth and responsive.
