package reldat;

import java.io.*;
import java.net.*;
import reldat.*;

public class ReldatClient {
  public final int HEADER_SIZE = 20;
  public final int PAYLOAD_SIZE = 1000;
  public final int PACKET_SIZE = HEADER_SIZE + PAYLOAD_SIZE;

  public static void main(String[] args) throws IOException {
    if (args.length != 2 || !args[0].contains(":")) {
      System.out.println("Usage: java reldat.ReldatClient Host:PortNumber WindowSize");
      System.exit(1);
    }

    String ip = args[0].split(":")[0];
    int portNumber = Integer.parseInt(args[0].split(":")[1]);
    int windowSize = Integer.parseInt(args[1]);

    try {
      ReldatClientHelper.connect(ip, portNumber, windowSize);
    } catch(Exception e) {
      System.out.println(e.getMessage());
      System.out.println(e.getClass().getCanonicalName());
    }
  }
}
