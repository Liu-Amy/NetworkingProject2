package reldat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ReldatFileReceiver {

  public static void handlePacket (DatagramSocket serverSocket, InetAddress clientAddress, int clientPort) throws Exception {
    while (true) {
      // get packet from client
      byte[] packet = ReldatHelper.readPacket(serverSocket, ReldatConstants.PACKET_SIZE);

      // get header from packet received
      byte[] header = Arrays.copyOfRange(packet, 0, ReldatConstants.HEADER_SIZE);

      if (ReldatConstants.DEBUG_MODE) {
        Random rand = new Random();
        int randomNum = rand.nextInt(100 + 1);
        if (randomNum < ReldatConstants.LOSS_RATE) {
          System.out.print("PACKET LOST: ");
          if (ReldatHelper.checkSyn(header)) {
            System.out.println("SYN");
          } else if (ReldatHelper.checkAck(header)) {
            System.out.println("ACK");
          } else if (ReldatHelper.checkFin(header)) {
            System.out.println("FIN");
          } else {
            int seqNum = ReldatHelper.byteArrToInt(Arrays.copyOfRange(header, 16, 20));
            System.out.println(seqNum);
          }
          continue;
        }
        randomNum = rand.nextInt(100 + 1);
        if (randomNum < ReldatConstants.CORRUPT_RATE) {
          System.out.print("PACKET CORRUPTED: ");
          if (ReldatHelper.checkSyn(header)) {
            System.out.println("SYN");
          } else if (ReldatHelper.checkAck(header)) {
            System.out.println("ACK");
          } else if (ReldatHelper.checkFin(header)) {
            System.out.println("FIN");
          } else {
            int seqNum = ReldatHelper.byteArrToInt(Arrays.copyOfRange(header, 16, 20));
            System.out.println(seqNum);
          }
          header[0] = (byte) 0;
        }
      }

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

      // if checksum is empty do nothing
      if (ReldatHelper.byteArrToInt(checksum) == 0) {
        continue;
      }

      // if checksums are equal continue, if not do nothing
      if (Arrays.equals(ReldatHelper.calculateChecksum(data), checksum)) {

        // convert byte array to upper case char array
        char[] upperCharArrayData = ReldatHelper.byteArrayToUpperCharArray(data);

        //System.out.println("UPPERCASE: " + String.valueOf(upperCharArrayData));

        // convert uppercase char array to byte array
        byte[] upperByteArrayData = ReldatHelper.charArraytoByteArray(upperCharArrayData);

        // set ack number to sequence number from client
        int ackNum = ReldatHelper.byteArrToInt(Arrays.copyOfRange(header, 16, 20));
        System.out.println(ackNum);

        // send byte array to client
        ReldatHelper.sendPacketWithHeader(serverSocket, upperByteArrayData, clientAddress, clientPort, 0, ackNum);

      } else {
        System.out.println("Packet was corrupted");
      }
    }
  }
}
