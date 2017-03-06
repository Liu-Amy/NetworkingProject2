package reldat;

import java.io.*;
import java.net.*;
import java.util.*;
import reldat.*;

public class ReldatServer {
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

  public static void main(String[] args) throws IOException {
    ReldatServerHelper reldatServer = new ReldatServerHelper();
    try {
      reldatServer.listen(8088);
    } catch(Exception e) {
      System.out.println(e.getMessage());
      System.out.println(e.getClass().getCanonicalName());
    }
  }
}

