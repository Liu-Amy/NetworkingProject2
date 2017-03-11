package reldat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ReldatFileSender {
  public static int seqNum = 0;

  public static void sendFile(DatagramSocket socket, String filePath,
      InetAddress ip, int portNum, int windowSize) {
    try {
      byte[] packetData = new byte[ReldatConstants.PACKET_SIZE];
      BufferedInputStream in = new BufferedInputStream(new FileInputStream(filePath));
      while ((in.read(packetData, 0, ReldatConstants.PACKET_SIZE)) != -1) {
        // sends packet of data
        ReldatHelper.sendPacketWithHeader(socket, packetData, ip, portNum, seqNum, 0);
        // increment sequence number
        seqNum += packetData.length;
        // wait for ack (no selective repeat for now)
        packetData = new byte[ReldatConstants.PACKET_SIZE];

        // receive packet from server
        byte[] potentialReply = new byte[ReldatConstants.PACKET_SIZE];
        potentialReply = ReldatHelper.readPacket(socket, ReldatConstants.PACKET_SIZE);

        // remove header from packet received
        byte[] potentialByteUpperCase = new byte[ReldatConstants.PAYLOAD_SIZE];
        potentialByteUpperCase = Arrays.copyOfRange(potentialReply, ReldatConstants.HEADER_SIZE, potentialByteUpperCase.length);

        // convert byte array to char array
        char[] potentialCharUpperCase = new char[ReldatConstants.PAYLOAD_SIZE];
        potentialCharUpperCase = ReldatHelper.byteArrayToUpperCharArray(potentialByteUpperCase);

        System.out.println("UPPERCASE: " + String.valueOf(potentialCharUpperCase));
      }
    } catch(FileNotFoundException e) {
      System.out.println("File not found");
    } catch(IOException e) {
      System.out.println("IO Exception");
    } catch(Exception e) {
      System.out.println(e.getClass().getCanonicalName());
    }
  }
}
