import java.io.*;
import java.net.*;

public class smsclientTCP {
  public static void main(String[] args) throws IOException {
    // does not have the correct number of arguments
    if (args.length != 3) {
      System.out.println("Usage: java smsclientTCP <server IP address> <port number> <msg.txt>");
      System.exit(1);
    }

    // did not provided a text file of message
    if (!args[2].endsWith(".txt")) {
      System.out.println("The message you have provided is not in a .txt file");
      System.exit(1);
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
      System.exit(1);
    }

    // sending message to server and receivng a reply
    try (
      Socket clientSocket = new Socket(ip, portNumber);
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    ) {
      out.println(wholeMessage);
      System.out.println("Response from server:");
      System.out.println(in.readLine());
    } catch (UnknownHostException e) {
      System.out.println("Unknown host " + ip);
      System.exit(1);
    } catch (IOException e) {
      System.out.println("Could not get I/O for the connection to " + ip);
      System.exit(1);
    }
  }
}
