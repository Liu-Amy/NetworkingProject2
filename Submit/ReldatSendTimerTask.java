package reldat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ReldatSendTimerTask extends TimerTask {

  public int seqNum;
  public DatagramSocket socket;
  public byte[] data;
  public InetAddress ip;
  public int portNum;

  public ReldatSendTimerTask(int seqNum, DatagramSocket socket, byte[] data, InetAddress ip, int portNum) {
    this.seqNum = seqNum;
    this.socket = socket;
    this.data = data;
    this.ip = ip;
    this.portNum = portNum;
  }

  public void run() {
    try {
      System.out.println("Packet "+seqNum+" resent");
      ReldatHelper.sendPacketWithHeader(socket, data, ip, portNum, seqNum, 0);
      //ReldatHelper.sendSynAck(socket, ip, portNum, ReldatConstants.HEADER_SIZE);
    } catch(IOException e) {
      System.out.println("IOException in ReldatSendTimerTask");
    } catch(Exception e) {
      System.out.println("Exception in ReldatSendTimerTask");
    }
  }
}
