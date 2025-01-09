import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
//TODO Дописать работу 2 польователей между собой с прослушкой на сервере, также надо добавить бродкаст с сервера.
// Потом надо обернуть в докер и поднять сервер. Затем проделать это же на 2 раных контейнерах/виртуалках.
// Еще добавить нейронку в беседу.
// Также надо будет добавить веб интерфейс и прописать бекенд через spring

public class ChatClient {
    private Socket ClientSocket;
    private PrintWriter SocketOutput;
    private Scanner SocketInput;
    private Scanner ClientSender;

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient();
    }

    ChatClient() throws IOException {
        try {
            ClientSocket = new Socket("localhost", 8000);
            SocketOutput = new PrintWriter(ClientSocket.getOutputStream());
            SocketInput = new Scanner(ClientSocket.getInputStream());
            ClientSender = new Scanner(System.in);


            new Thread(() -> {
                try {
                    String server_massage;
                    while (SocketInput.hasNext()){
                        server_massage = SocketInput.nextLine();
                        System.out.println("[server] " + server_massage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

//            System.out.println("Enter a massage to server:");
            while (true){
                String client_message = ClientSender.nextLine();
                SocketOutput.println(client_message);
                SocketOutput.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
