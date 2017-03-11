package reldat;

import java.io.*;
import java.net.*;
import java.util.*;
import reldat.*;
import static reldat.ReldatServerState.*;

public class ReldatServerHelper {
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
          byte[] potentialSyn = new byte[ReldatConstants.HEADER_SIZE];
          // wait for syn
          DatagramPacket receivePacket = new DatagramPacket(potentialSyn, potentialSyn.length);
          serverSocket.receive(receivePacket);
          clientPort = receivePacket.getPort();
          clientAddress = receivePacket.getAddress();

          // syn check, do nothing if syn
          if (ReldatHelper.checkSyn(potentialSyn)) {
            ReldatHelper.sendSynAck(serverSocket, clientAddress, clientPort, ReldatConstants.HEADER_SIZE);
            state = SYN_RCVD;
          }
          break;
        case SYN_RCVD:
          System.out.println("SYN RECEIVED");
          // wait for ack from client
          byte[] potentialAck = new byte[ReldatConstants.HEADER_SIZE];
          potentialAck = ReldatHelper.readPacket(serverSocket, ReldatConstants.HEADER_SIZE);

          if (ReldatHelper.checkAck(potentialAck)) {
            // establish connection
            state = ESTABLISHED;
          } else {
            // if ack is lost, client might assume connection is
            // established so we need to send a RST packet to
            // reset connection
            ReldatHelper.sendReset(serverSocket, clientAddress, clientPort, ReldatConstants.HEADER_SIZE);
            state = LISTEN;
          }
          break;
        case ESTABLISHED:
          // a bunch of code
          System.out.println("CONNECTION ESTABLISHED");
          // read in packet from client
          ReldatFileReceiver.handlePacket(serverSocket, clientAddress, clientPort);
      }
    }
  }

  public void disconnect (int portNum) throws Exception {
    state = FIN;
    while (true) {
      switch (state) {
        case FIN:
        byte[] potentialFin = new byte[ReldatConstants.HEADER_SIZE];
        potentialFin = ReldatHelper.readPacket(serverSocket, ReldatConstants.HEADER_SIZE);
          if (ReldatHelper.checkFin(potentialFin)) {
            state = CLOSE_WAIT;
          }
          System.out.println("FIN");
          break;
        case CLOSE_WAIT:
          ReldatHelper.sendFinAck(serverSocket, clientAddress, portNum, ReldatConstants.HEADER_SIZE);
          state = LAST_ACK;
          System.out.println("CLOSE_WAIT");
          break;
        case LAST_ACK:
          System.out.println("LAST_ACK");
          byte[] potentialAck = new byte[ReldatConstants.HEADER_SIZE];
          potentialFin = ReldatHelper.readPacket(serverSocket, ReldatConstants.HEADER_SIZE);
          if (ReldatHelper.checkAck(potentialFin)) {
            state = LISTEN;
            return;
          }
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
