package reldat;

import java.io.*;
import java.nio.*;
import java.net.*;
import reldat.*;
import java.util.*;
import java.security.MessageDigest;
import java.util.concurrent.*;

public class ReldatHelper {

  public static void sendPacket(DatagramSocket socket, byte[] data, InetAddress ip, int port) throws Exception {
    if (ReldatConstants.DEBUG_MODE) {
      // delay
      Random rand = new Random();
      int randomNum = rand.nextInt((
        ReldatConstants.DELAY_MAX - ReldatConstants.DELAY_MIN) + 1)
        + ReldatConstants.DELAY_MIN;
      Thread.sleep(randomNum);
      System.out.print("delayed ");
    }
    DatagramPacket sendPacket = new DatagramPacket(data, data.length, ip, port);
    socket.send(sendPacket);
  }

  public static void sendPacketWithHeader(DatagramSocket socket, byte[] data, InetAddress ip, int port, int seqNum, int ackNum) throws Exception {
    if (ReldatConstants.DEBUG_MODE) {
      // delay
      Random rand = new Random();
      int randomNum = rand.nextInt((
        ReldatConstants.DELAY_MAX - ReldatConstants.DELAY_MIN) + 1)
        + ReldatConstants.DELAY_MIN;
      Thread.sleep(randomNum);
      System.out.print("delayed " + seqNum + " ");
    }
    byte[] merged = mergeByteArray(createHeader(data, seqNum, ackNum), data);
    DatagramPacket sendPacket = new DatagramPacket(merged, merged.length, ip, port);
    socket.send(sendPacket);
    socket.send(sendPacket);
  }

  public static byte[] createHeader(byte[] data, int seqNum, int ackNum) {
    int index = 0;
    ByteBuffer headerBuffer = ByteBuffer.allocate(ReldatConstants.HEADER_SIZE);

    headerBuffer.put(calculateChecksum(data));
    int buffersize = data.length;

    index += ReldatConstants.CHECKSUM_SIZE;
    headerBuffer.putInt(index, seqNum);

    index += ReldatConstants.SEQ_SIZE;
    headerBuffer.putInt(index, ackNum);

    index += ReldatConstants.ACK_SIZE;
    // SYN FIN ACK RST bit
    byte flag;
    if (ackNum != 0) {
      flag = (byte) 0b00100000;
    } else {
      flag = (byte) 0;
    }
    headerBuffer.put(index, flag);
    return headerBuffer.array();
  }

  public static byte[] calculateChecksum(byte[] data) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(data);
      return md.digest();
    } catch (Exception e) {
      System.out.println("Checksum failed");
      return new byte[16];
    }
  }

  public static void sendAck(DatagramSocket socket, InetAddress ip, int port, int headerSize) throws Exception {
    byte[] ack = new byte[headerSize];
    ack[headerSize - 1] = (byte) 0b00100000;
    sendPacket(socket, ack, ip, port);
  }

  public static void sendSynAck(DatagramSocket socket, InetAddress ip, int port, int headerSize) throws Exception {
    byte[] ack = new byte[headerSize];
    ack[headerSize - 1] = (byte) 0b10100000;
    sendPacket(socket, ack, ip, port);
  }

  public static void sendFinAck(DatagramSocket socket, InetAddress ip, int port, int headerSize) throws Exception {
    byte[] ack = new byte[headerSize];
    ack[headerSize - 1] = (byte) 0b01100000;
    sendPacket(socket, ack, ip, port);
  }
  public static void sendReset(DatagramSocket socket, InetAddress ip, int port, int headerSize) throws Exception {
    byte[] ack = new byte[headerSize];
    ack[headerSize - 1] = (byte) 0b00010000;
    sendPacket(socket, ack, ip, port);
  }

  public static void sendFin(DatagramSocket socket, InetAddress ip, int port, int headerSize) throws Exception {
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

  public static byte[] readPacketClient(DatagramSocket socket, int bufferSize) throws IOException {
    byte[] buffer = new byte[bufferSize];
    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
    socket.receive(receivePacket);
    if (checkReset(Arrays.copyOfRange(buffer, 24, 25))) {
      throw new UnsupportedOperationException();
    }
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

  public static void printByteArray (byte[] arr) {
    for (int i = 0; i < arr.length; i++) {
      System.out.print(arr[i] + " ");
    }
  }

  public static int byteArrToInt(byte[] arr) {
    return ByteBuffer.wrap(arr).getInt();
  }
}
