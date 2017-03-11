package reldat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ReldatFileReceiver {

  public static void readPacket (byte[] packet) {

    try {
      while (true) {
        byte[] data = new byte[ReldatConstants.PAYLOAD_SIZE];
        data = Arrays.copyOfRange(packet, 13, packet.length);

        String message = new String(data, "UTF-8");

        message = message.toUpperCase();

        System.out.println("UPPERCASE: " + message);

        // TODO: convert byte[] data to string
        // TODO: convert string from lowercase to uppercase
        // TODO: convert uppercase string to byte[]
        // TODO: break up byte[] into packets
        // TODO: send packets to client
      }
    } catch(Exception e) {
      System.out.println(e.getClass().getCanonicalName());
    }


  }

}
