package reldat;

import java.io.*;
import java.net.*;
import java.util.*;
import static reldat.ReldatClientState.*;

public class ReldatGBNSender {

  public DatagramSocket socket;
  public int windowSize;
  public InetAddress ip;
  public int portNum;

  public ReldatGBNSender(DatagramSocket socket, int windowSize, InetAddress ip, int portNum) {
    this.socket = socket;
    this.windowSize = windowSize;
    this.ip = ip;
    this.portNum = portNum;
  }

  public void send(String filePath) throws IOException {
    File f = new File(filePath);
    // calculate number of packets to send for receive buffer
    int fileSize = (int) f.length();
    int numPacketsToSend = (int) Math.ceil((double) fileSize / ReldatConstants.PAYLOAD_SIZE);

    if (windowSize > numPacketsToSend) {
      windowSize = numPacketsToSend;
    }

    Boolean running = true;
    String[] srBuffer = new String[numPacketsToSend];
    int windowIndex = 0;

    while (running) {
      // send packets in window
      System.out.println("windowIndex: " + windowIndex);
      System.out.println("windowSize: " + windowSize);

      (new ReldatFileSender(socket, filePath, ip,
        portNum, windowSize, windowIndex)).run();

      for (int i = 0 ; i < windowSize; i++) {
        // receive packet from server
        byte[] potentialReply = new byte[ReldatConstants.PACKET_SIZE];
        potentialReply = ReldatHelper.readPacket(socket, ReldatConstants.PACKET_SIZE);

        // get header of potential reply
        byte[] replyHeader = Arrays.copyOfRange(potentialReply, 0, ReldatConstants.HEADER_SIZE);

        int replyAckNum = ReldatHelper.byteArrToInt(Arrays.copyOfRange(replyHeader, 22, 26));

        // if packet is within the window
        if (replyAckNum >= windowIndex
            && replyAckNum < windowIndex + windowSize) {
          // remove header from packet received
          byte[] potentialByteUpperCase = new byte[ReldatConstants.PAYLOAD_SIZE];
          potentialByteUpperCase = Arrays.copyOfRange(potentialReply, ReldatConstants.HEADER_SIZE, potentialByteUpperCase.length);

          // convert byte array to char array
          char[] potentialCharUpperCase = new char[ReldatConstants.PAYLOAD_SIZE];
          potentialCharUpperCase = ReldatHelper.byteArrayToUpperCharArray(potentialByteUpperCase);

          //System.out.println("UPPERCASE: " + String.valueOf(potentialCharUpperCase));
          System.out.println("replyAckNum: " + replyAckNum);

          // save into buffer
          srBuffer[replyAckNum] = new String(potentialCharUpperCase);
        }
      }
        // move window index
        while (srBuffer[windowIndex] != null
            && windowIndex + windowSize < srBuffer.length) {
          windowIndex++;
          System.out.println("this windowIndex: " + windowIndex);
        }

        // if we're at the last window and
        // everything in the window is saved
        // then terminate
        if (windowIndex + windowSize == srBuffer.length) {
          Boolean full = true;
          for (int i = 0; i < windowSize; i++) {
            if (srBuffer[windowIndex + i] == null) {
              full = false;
            }
          }
          running = !full;
        }

    }

    // Finish everything
    try {
      PrintWriter writer = new PrintWriter(filePath.split("\\.")[0]  + "-received." + filePath.split("\\.")[1], "UTF-8");
      for (int i = 0; i < srBuffer.length; i++) {
        writer.append(srBuffer[i]);
      }
      writer.close();
    } catch (IOException e) {
      System.out.println("Problem writing transformed file");
    }

    // System.out.println("filePath " + filePath);
    // System.out.println("0" + filePath.split("\\.")[0]);
    // System.out.println("1" + filePath.split("\\.")[1]);
    System.out.println("File has finished downloading from server.");

    ReldatClientHelper.state = ACCEPT_INPUT;
  }
}
