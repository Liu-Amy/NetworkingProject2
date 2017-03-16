package reldat;

import java.io.*;
import java.net.*;
import java.util.*;
import reldat.*;
import static reldat.ReldatServerState.*;

public class ReldatServerHelper {
  public ReldatServerState state = LISTEN;
  public int clientPort;
  public InetAddress clientAddress;

  DatagramSocket serverSocket;

  public void listen(int portNum) throws Exception {
    serverSocket = new DatagramSocket(portNum);
    serverSocket.setSoTimeout(ReldatConstants.TIMEOUT);

    while (true) {
      try {
        switch (state) {
          case LISTEN:
            System.out.println("LISTENING ON PORT " + portNum);
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
              System.out.println("Handshake not completed. RESET sent to client. Now waiting for connection from client.");
              ReldatHelper.sendReset(serverSocket, clientAddress, clientPort, ReldatConstants.HEADER_SIZE);
              state = LISTEN;
            }
            break;
          case ESTABLISHED:
            // a bunch of code
            System.out.println("CONNECTION ESTABLISHED");
            // read in packet from client
            ReldatFileReceiver.handlePacket(serverSocket, clientAddress, clientPort);
            disconnect();
        }
      } catch (SocketTimeoutException e) {
        //System.out.println("Timeout reached. Connection to client lost.");
        System.out.println("Timeout reached. Now waiting for connection from client.");
        state = LISTEN;
      }
    }
  }

  public void disconnect() throws Exception {
    state = CLOSE_WAIT;
    while (true) {
      switch (state) {
        case CLOSE_WAIT:
          ReldatHelper.sendFinAck(serverSocket, clientAddress, clientPort, ReldatConstants.HEADER_SIZE);
          state = LAST_ACK;
          System.out.println("CLOSE_WAIT");
          break;
        case LAST_ACK:
          System.out.println("LAST_ACK");
          byte[] potentialAck = ReldatHelper.readPacket(serverSocket, ReldatConstants.HEADER_SIZE);
          if (ReldatHelper.checkAck(potentialAck)) {
            state = LISTEN;
            return;
          }
      }
    }
  }
}
