import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {

  private static final int port = 8000;
  private static CopyOnWriteArrayList<ClientHandler> ClientList = new CopyOnWriteArrayList<>();

  public static void main(String[] args) throws IOException {
    new ChatServer();
  }

  public ChatServer() throws IOException {
    ServerSocket serverSocket = new ServerSocket(port);
    System.out.println("Server is running and waiting for connections...");

    // Обработка каждого клиента в новом потоке
    while (true) {
      Socket client_socket = serverSocket.accept();
      System.out.println("New client connected: " + client_socket);
      ClientHandler client_handler = new ClientHandler(client_socket);
      ClientList.add(client_handler);
      new Thread(client_handler).start();
    }
  }

  // Broadcast a message to all clients
  public static void broadcast(String message, ClientHandler sender) {
    for (ClientHandler client : ClientList) {
      if (client != sender) {
        String SenderName = (sender == null) ? "Server" : sender.username;
        client.sendMessage(SenderName, message);
      }
    }
  }

  private static class ClientHandler implements Runnable {

    private PrintWriter clientOutput;
    private Scanner clientInput;
    private final Socket clientSocket;
    private String username;

    ClientHandler(Socket clientSocket) {
      this.clientSocket = clientSocket;
      try {
        clientOutput = new PrintWriter(clientSocket.getOutputStream(), true);
        clientInput = new Scanner(clientSocket.getInputStream());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void run() {
      try {
        sendMessage("Server", "Enter your name");
        //clientOutput.println("Enter your name:");
        if (clientInput.hasNextLine()) {
          username = clientInput.nextLine();
          System.out.println("User " + username + " connected");
          sendMessage("Server", "Welcome " + username + "! Type your messages:");
          //clientOutput.println("Welcome " + username + "! Type your messages:");

          new Thread(() -> {
            Scanner serverInput = new Scanner(System.in);
            while (true) {
              String serverMessage = serverInput.nextLine();
              broadcast(serverMessage, null);
            }
          }).start();

          while (clientInput.hasNextLine()) {
            String clientMessage = clientInput.nextLine();
            broadcast(clientMessage, this);
            System.out.println("[" + username + "] " + clientMessage);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          clientOutput.close();
          clientInput.close();
          clientSocket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    public void sendMessage(String username, String message) {
      clientOutput.println("[" + username + "] " + message);
    }
  }
}
