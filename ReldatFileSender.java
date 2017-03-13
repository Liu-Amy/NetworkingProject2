package reldat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ReldatFileSender {
  public DatagramSocket socket;
  public String filePath;
  public InetAddress ip;
  public int portNum;
  public int numPacketsToSend;
  public int startIndex;

  public ReldatFileSender(DatagramSocket socket, String filePath,
      InetAddress ip, int portNum, int numPacketsToSend, int startIndex) {
    this.socket = socket;
    this.filePath = filePath;
    this.ip = ip;
    this.portNum = portNum;
    this.numPacketsToSend = numPacketsToSend;
    this.startIndex = startIndex;
  }

  public void run() {
    int seqNum = startIndex;
    try {
      // buffer to store packet
      byte[] packetData = new byte[ReldatConstants.PAYLOAD_SIZE];
      BufferedInputStream in = new BufferedInputStream(new FileInputStream(filePath));

      while ((in.read(packetData, startIndex, ReldatConstants.PAYLOAD_SIZE)) != -1
          && numPacketsToSend > 0) {
        numPacketsToSend--;
        ReldatHelper.sendPacketWithHeader(socket, packetData, ip, portNum, seqNum, 0);
        seqNum++;
        packetData = new byte[ReldatConstants.PAYLOAD_SIZE];
      }
    } catch(FileNotFoundException e) {
      System.out.println("File not found");
    } catch(IOException e) {
      System.out.println("IO Exception in ReldatFileSender");
    }
  }
}
