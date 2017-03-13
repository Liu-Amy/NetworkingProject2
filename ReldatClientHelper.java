package reldat;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import reldat.*;
import static reldat.ReldatClientState.*;
import java.util.Scanner;

public class ReldatClientHelper {
  public static DatagramSocket clientSocket;
  public static ReldatClientState state = CLOSED;

  public static void connect(String ip, int portNum, int windowSize) throws Exception {
    clientSocket = new DatagramSocket();

    InetAddress address = InetAddress.getByName(ip);
    clientSocket.setSoTimeout(ReldatConstants.TIMEOUT);

    String input = "";

    while (true) {

      try {
        switch (state) {
          case CLOSED:
            // sends syn
            System.out.println("Sending SYN");
            byte[] syn = new byte[ReldatConstants.HEADER_SIZE];
            syn[ReldatConstants.HEADER_SIZE - 1] = (byte) 0b10000000;
            DatagramPacket sendPacket = new DatagramPacket(syn, ReldatConstants.HEADER_SIZE, address, portNum);
            clientSocket.send(sendPacket);
            state = SYN_SENT;
            break;
          case SYN_SENT:
            // waits for syn ack
            byte[] potentialSynAck = ReldatHelper.readPacket(clientSocket, ReldatConstants.HEADER_SIZE);

            if (ReldatHelper.checkSynAck(potentialSynAck)) {
              ReldatHelper.sendAck(clientSocket, address, portNum, ReldatConstants.HEADER_SIZE);
              state = ACCEPT_INPUT;
            } else {
              // resend syn if synack not received by timeout
              state = CLOSED;
            }
            System.out.println("SYN_SENT STATE");
            System.out.println("CONNECTION ESTABLISHED");
            break;
          case ACCEPT_INPUT:
            System.out.println("Enter input: ");
            Scanner scan = new Scanner(System.in);
            input = scan.nextLine();
            if (input.equals("disconnect")) {
              disconnect(ip, portNum);
            } else if (input.matches("(transform).*(.txt)")) {
              state = FILE_TRANSFER;
            } else {
              System.out.println("Invalid input. Please try again.");
            }
            break;
          case FILE_TRANSFER:
            String filePath = input.split(" ")[1];
            ReldatSRSender sender = new ReldatSRSender(clientSocket, 4, address, portNum);
            sender.send(filePath);
            break;
        }
      } catch (SocketTimeoutException e) {
        System.out.println("Timeout reached. Communication with server lost.");
        state = CLOSED;
      }
    }
  }

  public static void disconnect(String ip, int portNum) throws Exception {
    state = FIN;

    InetAddress address = InetAddress.getByName(ip);
    while (true) {
      switch (state) {
        case FIN:
          // send fin
          ReldatHelper.sendFin(clientSocket, address, portNum, ReldatConstants.HEADER_SIZE);

          state = FIN_WAIT_1;
          System.out.println("FIN");
          break;
        case FIN_WAIT_1:
          // wait for ack from server
          byte[] potentialAck = new byte[ReldatConstants.HEADER_SIZE];
          potentialAck = ReldatHelper.readPacket(clientSocket, ReldatConstants.HEADER_SIZE);

          if (ReldatHelper.checkFinAck(potentialAck)) {
            ReldatHelper.sendAck(clientSocket, address, portNum, ReldatConstants.HEADER_SIZE);
            state = TIME_WAIT;
          } else if (ReldatHelper.checkReset(potentialAck)) {
            throw new SocketTimeoutException();
          }
          System.out.println("FIN_WAIT_1");
          break;
        case TIME_WAIT:
          // wait for 30 seconds and
          System.out.println("TIME_WAIT");
          TimeUnit.SECONDS.sleep(5);
          clientSocket.close();
          System.exit(0);
      }
    }
  }
}
