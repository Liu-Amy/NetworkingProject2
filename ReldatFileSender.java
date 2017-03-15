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
      System.out.println("THIS IS THE startIndex: " + startIndex);

      // read from file at offset
      in.skip(startIndex*ReldatConstants.PAYLOAD_SIZE);

      while ((in.read(packetData, 0, ReldatConstants.PAYLOAD_SIZE)) != -1
          && numPacketsToSend > 0) {
        numPacketsToSend--;
        ReldatHelper.sendPacketWithHeader(socket, packetData, ip, portNum, seqNum, seqNum);
        System.out.println("seqNum: " + seqNum);
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
