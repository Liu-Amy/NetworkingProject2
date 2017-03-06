package reldat;

import java.io.*;
import java.net.*;
import java.util.*;
import reldat.*;
import static reldat.ReldatServerState.*;

public class ReldatServerHelper {
  public final int HEADER_SIZE = 15;
  public final int PAYLOAD_SIZE = 1000;
  public final int PACKET_SIZE = HEADER_SIZE + PAYLOAD_SIZE;
  public final int TIMEOUT = 10000;

  public ReldatServerState state = LISTEN;
  public int clientPort;
  public InetAddress clientAddress;

  DatagramSocket serverSocket;

  public void listen(int portNum) throws Exception {
    serverSocket = new DatagramSocket(portNum);
    serverSocket.setSoTimeout(TIMEOUT);

    while (true) {
      switch (state) {
        case LISTEN:
          byte[] potentialSyn = new byte[HEADER_SIZE];
          // wait for syn
          DatagramPacket receivePacket = new DatagramPacket(potentialSyn, potentialSyn.length);
          serverSocket.receive(receivePacket);
          clientPort = receivePacket.getPort();
          clientAddress = receivePacket.getAddress();

          // syn check, do nothing if syn
          if (potentialSyn[HEADER_SIZE - 1] == (byte) 0b10000000) {
            ReldatHelper.sendSynAck(serverSocket, clientAddress, clientPort, HEADER_SIZE);
            state = SYN_RCVD;
          }
          break;
        case SYN_RCVD:
          System.out.println("SYN RECEIVED");
          // wait for ack from client
          if (ReldatHelper.checkAck(serverSocket, HEADER_SIZE)) {
            // establish connection
            state = ESTABLISHED;
          } else {
            // if ack is lost, client might assume connection is
            // established so we need to send a RST packet to
            // reset connection
            ReldatHelper.sendReset(serverSocket, clientAddress, clientPort, HEADER_SIZE);
          }
          break;
        case ESTABLISHED:
          // a bunch of code
          System.out.println("CONNECTION ESTABLISHED");
      }
    }
  }

  // method to check if all characters in a string are ascii characters
  public static boolean isStringAscii(String string) {
    if (string == null) {
      return false;
    }
    if (string.matches("[\\x00-\\x7F]+")) {
      return true;
    }
    return false;
  }
}
