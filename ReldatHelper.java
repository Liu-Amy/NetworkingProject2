package reldat;

import java.io.*;
import java.net.*;
import reldat.*;

public class ReldatHelper {
  public static void sendPacket(DatagramSocket socket, byte[] data, InetAddress ip, int port) throws IOException {
    DatagramPacket sendPacket = new DatagramPacket(data, data.length, ip, port);
    socket.send(sendPacket);
  }

  public static void sendAck(DatagramSocket socket, InetAddress ip, int port, int headerSize) throws IOException {
    byte[] ack = new byte[headerSize];
    ack[headerSize - 1] = (byte) 0b00100000;
    sendPacket(socket, ack, ip, port);
  }

  public static void sendSynAck(DatagramSocket socket, InetAddress ip, int port, int headerSize) throws IOException {
    byte[] ack = new byte[headerSize];
    ack[headerSize - 1] = (byte) 0b10100000;
    sendPacket(socket, ack, ip, port);
  }

  public static void sendReset(DatagramSocket socket, InetAddress ip, int port, int headerSize) throws IOException {
    byte[] ack = new byte[headerSize];
    ack[headerSize - 1] = (byte) 0b00010000;
    sendPacket(socket, ack, ip, port);
  }

  public static Boolean checkFlag(DatagramSocket socket, int bufferSize, byte flagByte) throws IOException {
    byte[] buffer = new byte[bufferSize];
    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
    socket.receive(receivePacket);
    return buffer[bufferSize - 1] == flagByte;
  }

  public static Boolean checkSyn(DatagramSocket socket, int bufferSize) throws IOException {
    return checkFlag(socket, bufferSize, (byte) 0b10000000);
  }

  public static Boolean checkSynAck(DatagramSocket socket, int bufferSize) throws IOException {
    return checkFlag(socket, bufferSize, (byte) 0b10100000);
  }

  public static Boolean checkAck(DatagramSocket socket, int bufferSize) throws IOException {
    return checkFlag(socket, bufferSize, (byte) 0b00100000);
  }

  public static Boolean checkFin(DatagramSocket socket, int bufferSize) throws IOException {
    return checkFlag(socket, bufferSize, (byte) 0b01000000);
  }

  public static Boolean checkReset(DatagramSocket socket, int bufferSize) throws IOException {
    return checkFlag(socket, bufferSize, (byte) 0b00010000);
  }
}

