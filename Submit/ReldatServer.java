package reldat;

import java.io.*;
import java.net.*;
import java.util.*;
import reldat.*;

public class ReldatServer {
  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.out.println("Usage: java reldat.ReldatServer PortNumber WindowSize");
      System.exit(1);
    }

    int portNumber = Integer.parseInt(args[0]);

    ReldatServerHelper reldatServer = new ReldatServerHelper();
    try {
      reldatServer.listen(portNumber);
    } catch(Exception e) {
      System.out.println(e.getMessage());
      System.out.println(e.getClass().getCanonicalName());
    }
  }
}
