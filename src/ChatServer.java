import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ChatServer {
    private static final int port = 8000;

    public static void main(String[] args) throws IOException {
        new ChatServer();
    }

    public ChatServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is running and waiting for connections...");

        while (true){
            Socket client_socket = serverSocket.accept();
            System.out.println("New client connected: " + client_socket);
            ClientHandler client_handler = new ClientHandler(client_socket);
            new Thread(client_handler).start();
        }
    }

    private static class ClientHandler implements Runnable {
        private PrintWriter clientOutput;
        private Scanner clientInput;
        private Socket clientSocket;
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
                clientOutput.println("Enter your name:");
                if (clientInput.hasNextLine()) {
                    username = clientInput.nextLine();
                    System.out.println("User " + username + " connected");
                    clientOutput.println("Welcome " + username + "! Type your messages:");

                    new Thread(() -> {
                        Scanner serverInput = new Scanner(System.in);
                        while (true) {
                            String serverMessage = serverInput.nextLine();
                            clientOutput.println("Server: " + serverMessage);
                        }
                    }).start();


                    while (clientInput.hasNextLine()) {
                        String clientMessage = clientInput.nextLine();
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
    }
}
