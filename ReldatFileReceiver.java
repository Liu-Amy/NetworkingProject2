package reldat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ReldatFileReceiver {

  public static void handlePacket (DatagramSocket serverSocket, InetAddress clientAddress, int clientPort) {
    try {
      while (true) {
        // get packet from client
        byte[] packet = ReldatHelper.readPacket(serverSocket, ReldatConstants.PACKET_SIZE);

        // remove header from packet received
        byte[] data = new byte[ReldatConstants.PAYLOAD_SIZE];
        data = Arrays.copyOfRange(packet, ReldatConstants.HEADER_SIZE, packet.length);

        // convert byte array to upper case char array
        char[] upperCharArrayData = ReldatHelper.byteArrayToUpperCharArray(data);

        System.out.println("UPPERCASE: " + String.valueOf(upperCharArrayData));

        // convert uppercase char array to byte array
        byte[] upperByteArrayData = ReldatHelper.charArraytoByteArray(upperCharArrayData);

        // send byte array to client
        ReldatHelper.sendPacketWithHeader(serverSocket, upperByteArrayData, clientAddress, clientPort, 0, 1);

      }
    } catch(Exception e) {
      System.out.println(e.getClass().getCanonicalName());
    }
  }

}
