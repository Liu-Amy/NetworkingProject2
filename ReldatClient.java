package reldat;

import java.io.*;
import java.net.*;
import reldat.*;

public class ReldatClient {
  public final int HEADER_SIZE = 20;
  public final int PAYLOAD_SIZE = 1000;
  public final int PACKET_SIZE = HEADER_SIZE + PAYLOAD_SIZE;

  public static void main(String[] args) throws IOException {
    //ReldatClientHelper reldatClient = new ReldatClientHelper();
    try {
      ReldatClientHelper.connect("localhost", 8088, 200);
    } catch(Exception e) {
      System.out.println(e.getMessage());
      System.out.println(e.getClass().getCanonicalName());
    }
  }
}

