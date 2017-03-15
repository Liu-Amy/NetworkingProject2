package reldat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ReldatPacketTimers {
  public static Map<Integer, Timer> packetTimers = new HashMap<>();

  public static void createTimer(int seqNum, DatagramSocket socket,
      byte[] data, InetAddress ip, int portNum) {
    Timer timer = new Timer();
    packetTimers.put(seqNum, timer);
    timer.schedule((new ReldatSendTimerTask(seqNum, socket, data, ip, portNum)),
      ReldatConstants.DATA_TIMEOUT,
      ReldatConstants.DATA_TIMEOUT);
  }

  public static void cancelTimer(int seqNum) {
    Timer timer = packetTimers.remove(seqNum);
    timer.cancel();
  }
}
