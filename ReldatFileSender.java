package reldat;

import java.io.*;
import java.net.*;

public class ReldatFileSender {
  public static final int PACKET_SIZE = 5;
  public static int seqNum = 0;

  public static void sendFile(DatagramSocket socket, String filePath,
      InetAddress ip, int portNum, int windowSize) {
    try {
      byte[] packetData = new byte[PACKET_SIZE];
      BufferedInputStream in = new BufferedInputStream(new FileInputStream(filePath));
      while ((in.read(packetData, 0, PACKET_SIZE)) != -1) {
        // sends packet of data
        ReldatHelper.sendPacketWithHeader(socket, packetData, ip, portNum, seqNum, 0);
        // increment sequence number
        seqNum += packetData.length;
        // wait for ack (no selective repeat for now)
        packetData = new byte[PACKET_SIZE];
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

