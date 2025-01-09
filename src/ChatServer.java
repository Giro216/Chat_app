import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class ChatServer {
    private static final int port = 8000;

    ChatServer() throws IOException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server is start");

        Socket client = server.accept();
        ServerHandler(client, server);
    }

    private static void ServerHandler(Socket client, ServerSocket server) throws IOException {
        System.out.println("client has been connected");
        PrintWriter out = new PrintWriter(client.getOutputStream());
        Scanner servers_send = new Scanner(System.in);
        System.out.println("Print message to client");
        out.println(servers_send.nextLine());
        out.flush();

        out.close();
        client.close();
        server.close();
    }
}
