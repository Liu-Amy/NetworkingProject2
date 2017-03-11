package reldat;

import java.io.*;
import java.nio.*;
import java.net.*;
import reldat.*;
import java.security.MessageDigest;

public class ReldatHelper {

  public static void sendPacket(DatagramSocket socket, byte[] data, InetAddress ip, int port) throws IOException {
    DatagramPacket sendPacket = new DatagramPacket(data, data.length, ip, port);
    socket.send(sendPacket);
  }

  public static void sendPacketWithHeader(DatagramSocket socket, byte[] data, InetAddress ip, int port, int seqNum, int ackNum) throws IOException {
    byte[] merged = mergeByteArray(createHeader(data, seqNum, ackNum), data);
    DatagramPacket sendPacket = new DatagramPacket(merged, merged.length, ip, port);
    socket.send(sendPacket);
  }

  public static byte[] createHeader(byte[] data, int seqNum, int ackNum) {
    ByteBuffer headerBuffer = ByteBuffer.allocate(ReldatConstants.HEADER_SIZE);
    // checksum 2 bytes
    headerBuffer.put(calculateChecksum(data));
    // buffersize 2 bytes
    int buffersize = data.length;
    headerBuffer.putInt(2, buffersize);
    // sequence number 4 bytes
    headerBuffer.putInt(4, seqNum);
    // ack number 4 bytes
    headerBuffer.putInt(8, ackNum);
    // SYN FIN ACK RST bit
    byte flag;
    if (ackNum != 0) {
      flag = (byte) 0b00100000;
    } else {
      flag = (byte) 0;
    }
    headerBuffer.put(12, flag);
    return headerBuffer.array();
  }

  public static byte[] calculateChecksum(byte[] data) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(data);
      System.out.println("length: " + md.digest().length);
      return md.digest();
    } catch (Exception e) {
      System.out.println("Checksum failed");
      return new byte[2];
    }
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

  public static void sendFinAck(DatagramSocket socket, InetAddress ip, int port, int headerSize) throws IOException {
    byte[] ack = new byte[headerSize];
    ack[headerSize - 1] = (byte) 0b01100000;
    sendPacket(socket, ack, ip, port);
  }
  public static void sendReset(DatagramSocket socket, InetAddress ip, int port, int headerSize) throws IOException {
    byte[] ack = new byte[headerSize];
    ack[headerSize - 1] = (byte) 0b00010000;
    sendPacket(socket, ack, ip, port);
  }

  public static void sendFin(DatagramSocket socket, InetAddress ip, int port, int headerSize) throws IOException {
    byte[] ack = new byte[headerSize];
    ack[headerSize - 1] = (byte) 0b01000000;
    sendPacket(socket, ack, ip, port);
  }

  public static byte[] readPacket(DatagramSocket socket, int bufferSize) throws IOException {
    byte[] buffer = new byte[bufferSize];
    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
    socket.receive(receivePacket);
    return buffer;
  }

  public static Boolean checkSyn(byte[] buffer) throws IOException {
    return buffer[buffer.length - 1] == (byte) 0b10000000;
  }

  public static Boolean checkSynAck(byte[] buffer) throws IOException {
    return buffer[buffer.length - 1] == (byte) 0b10100000;
  }

  public static Boolean checkAck(byte[] buffer) throws IOException {
    return buffer[buffer.length - 1] == (byte) 0b00100000;
  }

  public static Boolean checkFin(byte[] buffer) throws IOException {
    return buffer[buffer.length - 1] == (byte) 0b01000000;
  }

  public static Boolean checkFinAck(byte[] buffer) throws IOException {
    return buffer[buffer.length - 1] == (byte) 0b01100000;
  }

  public static Boolean checkReset(byte[] buffer) throws IOException {
    return buffer[buffer.length - 1] == (byte) 0b00010000;
  }

  public static byte[] mergeByteArray(byte[] a, byte[] b) {
    byte[] combined = new byte[a.length+b.length];
    System.arraycopy(a, 0, combined, 0, a.length);
    System.arraycopy(b, 0, combined, a.length, b.length);
    return combined;
  }

  public static char[] byteArrayToUpperCharArray (byte[] byteArray) {
    char[] charArray = new char[byteArray.length];
    for (int i = 0; i < byteArray.length; i++) {
      charArray[i] = Character.toUpperCase((char) byteArray[i]);
    }
    return charArray;
  }

  public static byte[] charArraytoByteArray (char[] charArray) {
    byte[] byteArray = new byte[charArray.length];
    for (int i = 0; i < charArray.length; i++) {
      byteArray[i] = (byte) charArray[i];
    }
    return byteArray;
  }
}
