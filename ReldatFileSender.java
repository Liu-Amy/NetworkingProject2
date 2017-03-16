package reldat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ReldatFileSender {
  public DatagramSocket socket;
  public String filePath;
  public InetAddress ip;
  public int portNum;
  public int windowSize;
  public int startIndex;
  public int shifts;

  public ReldatFileSender(DatagramSocket socket, String filePath,
      InetAddress ip, int portNum, int windowSize, int startIndex, int shifts) {
    this.socket = socket;
    this.filePath = filePath;
    this.ip = ip;
    this.portNum = portNum;
    this.windowSize = windowSize;
    this.startIndex = startIndex;
    this.shifts = shifts;
  }

  public void run() {
    try {
      // buffer to store packet
      byte[] packetData = new byte[ReldatConstants.PAYLOAD_SIZE];
      BufferedInputStream in = new BufferedInputStream(new FileInputStream(filePath));

      int seqNum = startIndex + windowSize - shifts;

      // read from file at offset
      in.skip(seqNum * ReldatConstants.PAYLOAD_SIZE);

      while ((in.read(packetData, 0, ReldatConstants.PAYLOAD_SIZE)) != -1
          && shifts > 0) {
        shifts--;
        ReldatHelper.sendPacketWithHeader(socket, packetData, ip, portNum, seqNum, 0);
        ReldatPacketTimers.createTimer(seqNum, socket, packetData, ip, portNum);
        System.out.println("seqNum: " + seqNum);
        seqNum++;
        packetData = new byte[ReldatConstants.PAYLOAD_SIZE];
      }
    } catch(FileNotFoundException e) {
      System.out.println("File not found");
    } catch(IOException e) {
      System.out.println("IO Exception in ReldatFileSender");
    } catch(Exception e) {
      System.out.println("Exception in ReldatFileSender");
    }
  }
}
