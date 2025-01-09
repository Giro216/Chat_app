import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class ChatServer {
    private static final int port = 8000;

    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer();
    }

    ChatServer() throws IOException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server is running and waiting for connections...");

        while (true){
            Socket client_socket = server.accept();
            System.out.println("New client connected: " + client_socket);
            ClientHandler client_handler = new ClientHandler(client_socket);
            new Thread(client_handler).start();
        }

    }

    private static class ClientHandler implements Runnable{
        private PrintWriter client_output;
        private Scanner client_input;
        private Scanner servers_sender;
        private Socket client_socket;
        private String username;

        ClientHandler(Socket clientsocket){
            try {
                this.client_socket = clientsocket;
                client_output = new PrintWriter(client_socket.getOutputStream());
                client_input = new Scanner(client_socket.getInputStream());
                servers_sender = new Scanner(System.in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                client_output.println("Enter your name");
                username = client_input.nextLine();
                System.out.println("User " + username + " connected");
                client_output.println("Welcome " + username + "\nType your message");

                String client_message;
                while (client_input.hasNext()){
                    client_message = client_input.nextLine();
                    System.out.println("[" + username + "] " + client_message);
                }

                while (true){
                    String server_message = servers_sender.nextLine();
                    client_output.println(server_message);
                    client_output.flush();
                }
//                if (client_input.hasNext()){
//                    String client_message = client_input.nextLine();
//                    System.out.println("[client] " + client_message);
//                }
//
//                System.out.println("Print message to client");
//                client_output.println(servers_sender.nextLine());
//                client_output.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    client_output.close();
                    client_input.close();
                    client_socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

//    private static void ServerHandler(Socket client_socket, ServerSocket server_socket) throws IOException {
//        System.out.println("client has been connected");
//
//        PrintWriter client_output = new PrintWriter(client_socket.getOutputStream());
//        Scanner client_input = new Scanner(client_socket.getInputStream());
//        Scanner servers_sender = new Scanner(System.in);
//
//        if (client_input.hasNext()){
//            String client_message = client_input.nextLine();
//            System.out.println("[client] " + client_message);
//        }
//
//        System.out.println("Print message to client");
//        client_output.println(servers_sender.nextLine());
//        client_output.flush();
//
//        client_output.close();
//        client_socket.close();
//        server_socket.close();
//    }
