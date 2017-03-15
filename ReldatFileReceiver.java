package reldat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ReldatFileReceiver {

  public static void handlePacket (DatagramSocket serverSocket, InetAddress clientAddress, int clientPort) throws IOException {
    while (true) {
      // get packet from client
      byte[] packet = ReldatHelper.readPacket(serverSocket, ReldatConstants.PACKET_SIZE);

      // get header from packet received
      byte[] header = Arrays.copyOfRange(packet, 0, ReldatConstants.HEADER_SIZE);

      // fin check
      if (ReldatHelper.checkFin(header)) {
        return;
      }

      // remove header from packet received
      byte[] data = new byte[ReldatConstants.PAYLOAD_SIZE];
      data = Arrays.copyOfRange(packet, ReldatConstants.HEADER_SIZE, packet.length);

      // implement checksum
      byte[] checksum = Arrays.copyOfRange(header, 0, 16);
      byte[] other = ReldatHelper.calculateChecksum(data);

      // if checksums are equal continue, if not do nothing
      if (Arrays.equals(ReldatHelper.calculateChecksum(data), checksum)) {

        // convert byte array to upper case char array
        char[] upperCharArrayData = ReldatHelper.byteArrayToUpperCharArray(data);

        //System.out.println("UPPERCASE: " + String.valueOf(upperCharArrayData));

        // convert uppercase char array to byte array
        byte[] upperByteArrayData = ReldatHelper.charArraytoByteArray(upperCharArrayData);

        // set ack number to sequence number from client
        int ackNum = ReldatHelper.byteArrToInt(Arrays.copyOfRange(header, 18, 22));
        System.out.println(ackNum);

        // send byte array to client
        ReldatHelper.sendPacketWithHeader(serverSocket, upperByteArrayData, clientAddress, clientPort, 0, ackNum);

      } else {
        System.out.println("Packet was corrupted");
      }
    }
  }
}

