package reldat;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import reldat.*;
import static reldat.ReldatClientState.*;

public class ReldatClientHelper {
  public final int HEADER_SIZE = 15;
  public final int PAYLOAD_SIZE = 1000;
  public final int PACKET_SIZE = HEADER_SIZE + PAYLOAD_SIZE;
  public final int TIMEOUT = 10000;

  public DatagramSocket clientSocket;
  public ReldatClientState state = CLOSED;

  public void connect(String ip, int portNum, int windowSize) throws Exception {
    clientSocket = new DatagramSocket();

    InetAddress address = InetAddress.getByName(ip);
    clientSocket.setSoTimeout(TIMEOUT);

    while (true) {

      try {
        switch (state) {
          case CLOSED:
            // sends syn
            byte[] syn = new byte[HEADER_SIZE];
            syn[HEADER_SIZE - 1] = (byte) 0b10000000;
            DatagramPacket sendPacket = new DatagramPacket(syn, HEADER_SIZE, address, portNum);
            clientSocket.send(sendPacket);
            state = SYN_SENT;
            System.out.println("CLOSED STATE");
            break;
          case SYN_SENT:
            // waits for syn ack
            byte[] potentialSynAck = ReldatHelper.readPacket(clientSocket, HEADER_SIZE);

            if (ReldatHelper.checkSynAck(potentialSynAck)) {
              byte[] ack = new byte[HEADER_SIZE];
              ack[HEADER_SIZE - 1] = (byte) 0b00100000;
              DatagramPacket ackPacket = new DatagramPacket(ack, HEADER_SIZE, address, portNum);
              clientSocket.send(ackPacket);
              state = ESTABLISHED;
            } else {
              // resend syn if synack not received by timeout
              state = CLOSED;
            }
            System.out.println("SYN_SENT STATE");
            break;
          case ESTABLISHED:
            System.out.println("CONNECTION ESTABLISHED");
            state = FIN;
            disconnect(ip, portNum);
        }
      } catch (SocketTimeoutException e) {
        state = CLOSED;
      }
    }
  }

  public void disconnect(String ip, int portNum) throws Exception {
    InetAddress address = InetAddress.getByName(ip);
    clientSocket.setSoTimeout(TIMEOUT);

    try {
      while (true) {
        switch (state) {
          case FIN:
            // send fin
            ReldatHelper.sendFin(clientSocket, address, portNum, HEADER_SIZE);

            // byte[] potentialAck2 = new byte[HEADER_SIZE];
            // potentialAck2 = ReldatHelper.readPacket(clientSocket, HEADER_SIZE);

            state = FIN_WAIT_1;
            System.out.println("FIN");
            break;
          case FIN_WAIT_1:
            // wait for ack from server
            byte[] potentialAck = new byte[HEADER_SIZE];
            potentialAck = ReldatHelper.readPacket(clientSocket, HEADER_SIZE);

            if (ReldatHelper.checkAck(potentialAck)) {
              state = FIN_WAIT_2;
            } else if (ReldatHelper.checkFin(potentialAck)) {
              System.out.println("WHYYYYYYYYYYYYY");
            } else if (ReldatHelper.checkReset(potentialAck)) {
              throw new SocketTimeoutException();
            } else {
              System.out.println("potentialAck");
            }
            System.out.println("FIN_WAIT_1");
            break;
          case FIN_WAIT_2:
            // wait for fin from server
            byte[] potentialFin = new byte[HEADER_SIZE];
            potentialFin = ReldatHelper.readPacket(clientSocket, HEADER_SIZE);
            if (ReldatHelper.checkFin(potentialFin)) {
              ReldatHelper.sendAck(clientSocket, address, portNum, HEADER_SIZE);
              state = TIME_WAIT;
            } else if (ReldatHelper.checkReset(potentialFin)) {
              throw new SocketTimeoutException();
            }
            System.out.println("FIN_WAIT_2");
            break;
          case TIME_WAIT:
            // wait for 30 seconds and
            System.out.println("TIME_WAIT");
            TimeUnit.SECONDS.sleep(5);
            clientSocket.close();
            System.exit(0);
        }
      }
    } catch (SocketTimeoutException e) {
      state = CLOSED;
      System.out.println("Error disconnecting. Returning back to LISTEN");
      return;
    }
  }

  /*public void run(String ip, String hostname, int windowSize) throws IOException {
    String wholeMessage = "";
    try {
      BufferedReader reader = new BufferedReader(new FileReader(msg));
      String message;
      while ((message = reader.readLine()) != null) {
        wholeMessage = wholeMessage + message + " ";
      }
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("Could not find file " + msg);
      return;
    }

    // create a datagram socket
    DatagramSocket clientSocket = new DatagramSocket();

    boolean gotResponse = false;
    int tries = 0;

    // wait for 2 seconds for a reply
    clientSocket.setSoTimeout(2000);

    // if the client has not received a response within 2 seconds, retry the query 3 times
    while (!gotResponse && tries < 3) {
      // send request from server
      tries++;
      try {
        InetAddress ipAdress = InetAddress.getByName(ip);
        byte[] sendData = new byte[PACKET_SIZE];
        byte[] receiveData = new byte[1024];
        sendData = wholeMessage.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAdress, portNumber);
        clientSocket.send(sendPacket);

        // get response from server
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

        // print response without the extra whitespace
        String response = new String(receivePacket.getData());
        String responseTrim = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("Response from server: " + responseTrim);
        clientSocket.close();
        gotResponse = true;
      } catch (SocketTimeoutException e) {
        if (tries <= 3) {
          System.out.println("Timed out. Try number: " + tries + ". Will try again!");
        }
      }
    }

    if (gotResponse == false) {
      System.out.println("0 -1 Error");
    }

  }*/
}
