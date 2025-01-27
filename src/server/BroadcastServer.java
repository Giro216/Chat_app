package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

public class BroadcastServer implements Runnable {

  private InetAddress serverIp;
  private InetAddress clientIp;
  private DatagramSocket broadcastSocket;
  private static final int UDP_PORT = 8001;
  private static final int TCP_PORT = 8002;

  public BroadcastServer() {
    try {
      broadcastSocket = new DatagramSocket(UDP_PORT);
      //broadcastSocket.setBroadcast(true);
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    // Запуск потоков отправки и приема
    while (true) {
      receiveResponse();
    }
  }

  private void receiveResponse() {
    try {
      byte[] buffer = new byte[1024];
      DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
      broadcastSocket.receive(responsePacket);

      String received = new String(responsePacket.getData(), 0, responsePacket.getLength());
      System.out.println("Received broadcast response: " + received);

      // Обработка сообщения и установка соединения
      this.clientIp = responsePacket.getAddress();
      sendPersonal();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void sendPersonal() {
    try (Socket socket = new Socket(clientIp, TCP_PORT)) {
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      serverIp = socket.getLocalAddress();
      out.println(serverIp.getHostAddress());
      System.out.println("Sent server IP to client: " + serverIp.getHostAddress());

      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}