package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class ChatClient {
  //TODO сделать проверку работоспособности сервера и кидать исключение при необходимости

  private Socket ClientSocket;
  private PrintWriter SocketOutput;
  private Scanner SocketInput;
  private Scanner ClientSender;

  public static void main(String[] args) throws IOException {
    ChatClient client = new ChatClient();
  }

  ChatClient() throws IOException {
    try {
      // Получаю IP сервера
      BroadcastClient broadcastClient = new BroadcastClient();
      new Thread(broadcastClient).start();
      String serverIp = broadcastClient.getServerIp().getHostAddress();

      // Прокидываю сокет и in/out интерфейсы
      if (Objects.equals(serverIp, InetAddress.getLocalHost().getHostAddress())) {
        serverIp = "localhost";
      }
      ClientSocket = new Socket(serverIp, 8000); // надо вводить ip сервера
      SocketOutput = new PrintWriter(ClientSocket.getOutputStream());
      SocketInput = new Scanner(ClientSocket.getInputStream());
      ClientSender = new Scanner(System.in);

      // В отдельном потоке получаю сообщения от пользователей с сервера
      new Thread(() -> {
        try {
          String server_massage;
          while (SocketInput.hasNext()) {
            server_massage = SocketInput.nextLine();
            System.out.println(server_massage);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }).start();

      // В текущем потоке обрабатываю сообщение юзера к серверу
      while (true) {
        String client_message = ClientSender.nextLine();
        SocketOutput.println(client_message);
        SocketOutput.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
