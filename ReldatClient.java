package reldat;

import java.io.*;
import java.net.*;
import reldat.*;

public class ReldatClient {
  public final int HEADER_SIZE = 20;
  public final int PAYLOAD_SIZE = 1000;
  public final int PACKET_SIZE = HEADER_SIZE + PAYLOAD_SIZE;

  public static void main(String[] args) throws IOException {
    ReldatClientHelper reldatClient = new ReldatClientHelper();
    try {
      reldatClient.connect("localhost", 8088, 200);
    } catch(Exception e) {
      System.out.println(e.getMessage());
      System.out.println(e.getClass().getCanonicalName());
    }
    /*
    // does not have the correct number of arguments
    if (args.length != 3) {
      System.out.println("Usage: java smsclientUDP <server IP address> <port number> <msg.txt>");
      return;
    }

    // did not provided a text file of suspicious words
    if (!args[2].endsWith(".txt")) {
      System.out.println("The list of suspicious words you have provided is not in a .txt file");
      return;
    }

    String ip = args[0];
    int portNumber = Integer.parseInt(args[1]);
    String msg = args[2];

    // create string from message
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
        byte[] sendData = new byte[1024];
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
    }*/

  }
}

