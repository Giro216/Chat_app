package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class BroadcastClient implements Runnable {

  private volatile InetAddress serverIp;
  private final InetAddress clientIp;
  private DatagramSocket socket;
  private static final int UDP_PORT = 8001;
  private static final int TCP_PORT = 8002;
  private DatagramPacket packet;
  private volatile boolean running = true;

  {
    try {
      clientIp = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }

  BroadcastClient() {
    try {
      socket = new DatagramSocket();
      socket.setBroadcast(true);
      String message = clientIp.getHostAddress() + ": is searching the server";
      byte[] buffer = message.getBytes();
      InetAddress broadcastIp = new BroadcastAddressFinder().getBroadcastIp();
      packet = new DatagramPacket(buffer, buffer.length, broadcastIp, 8001);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public InetAddress getServerIp() {
    while (true) {
      if (serverIp != null) {
        break;
      }
      Thread.onSpinWait();
    }
    return serverIp;
  }

  @Override
  public void run() {
    // Запуск потоков отправки и приема
    new Thread(this::sendBroadcast).start();
    new Thread(this::receiveResponse).start();
  }

  private void sendBroadcast() {
    try {
      while (running) {
        socket.send(packet);
        System.out.println("Send packet [" + packet.toString() + "] to broadcast");
        Thread.sleep(5000);  // Ждём перед следующим отправлением
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void receiveResponse() {
    // Listen for TCP connection from the server
    try (ServerSocket clientServerSocket = new ServerSocket(TCP_PORT);
        Socket serverConnection = clientServerSocket.accept();
        BufferedReader input = new BufferedReader(
            new InputStreamReader(serverConnection.getInputStream()))) {
      running = false;
      String serverMessage = input.readLine();
      System.out.println("Received from server: " + serverMessage);

      // Parse and use the server IP as needed
      serverIp = InetAddress.getByName(serverMessage);
      System.out.println("Server IP is: " + serverIp);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static class BroadcastAddressFinder {

    private InetAddress broadcastIp;

    public BroadcastAddressFinder() throws SocketException {
      try {
        InetAddress localAddress = InetAddress.getLocalHost();
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localAddress);

        if (networkInterface != null) {
          for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
            if (address.getAddress() instanceof Inet4Address) {
              broadcastIp = address.getBroadcast();
              if (broadcastIp != null) {
                System.out.println("Broadcast address: " + broadcastIp.getHostAddress());
              }
            }
          }
        }
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
    }

    public InetAddress getBroadcastIp() {
      return broadcastIp;
    }
  }

}
