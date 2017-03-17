package reldat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ReldatPacketTimers {
  public static Map<Integer, Timer> packetTimers = new HashMap<>();

  public static void createTimer(int seqNum, DatagramSocket socket,
      byte[] data, InetAddress ip, int portNum) {
    if (packetTimers.get(seqNum) == null) {
      Timer timer = new Timer();
      packetTimers.put(seqNum, timer);
      timer.schedule((new ReldatSendTimerTask(seqNum, socket, data, ip, portNum)),
        ReldatConstants.DATA_TIMEOUT,
        ReldatConstants.DATA_TIMEOUT);
    } else {
      System.out.println("Client packet was sent twice");
    }
  }

  public static void cancelTimer(int seqNum) {
    Timer timer = packetTimers.remove(seqNum);
    timer.cancel();
    System.out.println("canceled timer " + seqNum);
  }
}
