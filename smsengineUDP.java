import java.io.*;
import java.net.*;
import java.util.*;

public class smsengineUDP {
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
    // wrong number of arguments
    if (args.length != 2) {
      System.out.println("Usage: java smsengineUDP <port number> <suspicious-words.txt>");
      System.exit(1);
    }

    // did not provided a .txt file
    if (!args[1].endsWith(".txt")) {
      System.out.println("The list of suspicious words you have provided is not in a .txt file");
      System.exit(1);
    }

    int portNumber = Integer.parseInt(args[0]);
    String susWordList = args[1];
    Set<String> susWordSet = new HashSet<String>();
    int susWordNum = 0;

    // add suspicious words to set
    try {
      BufferedReader reader = new BufferedReader(new FileReader(susWordList));
      String word;
      while ((word = reader.readLine()) != null) {
        susWordSet.add(word);
        susWordNum++;
      }
      reader.close();
    } catch (FileNotFoundException e) {
      System.out.println("Could not find file " + susWordList);
      System.exit(1);
    }

    // create server socketes
    // packets with size 1024
    DatagramSocket serverSocket = new DatagramSocket(portNumber);
    byte[] receiveData = new byte[1024];
    byte[] sendData = new byte[1024];

    while(true) {
      int messageWordCount = 0;
      int numCaughtWords = 0;
      String caughtWords =  "";

      // receive request from client
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      serverSocket.receive(receivePacket);
      String message = new String(receivePacket.getData());

      // removes white space
      String messageTrim = new String(receivePacket.getData(), 0, receivePacket.getLength());

      // gets the length of the message
      byte[] data = new byte[receivePacket.getLength()];
      // System.out.println("getlength: " + data.length);

      // client address and port
      InetAddress ipAdress = receivePacket.getAddress();
      int port = receivePacket.getPort();

      String errorMessage = "0 -1 Error";

      // message provided by client is empty
      if (data.length <= 0) {
        System.out.println("The message is empty");
        sendData = errorMessage.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAdress, port);
        serverSocket.send(sendPacket);
      }

      // message provided by client exceeds character limit
      if (data.length > 1000) {
        System.out.println("The message is longer than 1000 characters");
        sendData = errorMessage.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAdress, port);
        serverSocket.send(sendPacket);
      }

      // message provided by client has at one or more non-ascii characters
      if (data.length > 0 && !isStringAscii(messageTrim)) {
        System.out.println("The message contains one or more non-ascii characters");
        sendData = errorMessage.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAdress, port);
        serverSocket.send(sendPacket);
      }

      // split message by space, commas, !, ?, and m
      String[] messageWords = messageTrim.split("[\\s,!?.]+");
      Set<String> caughtWordsSet = new HashSet<String>();

      for (int i = 0; i < messageWords.length; i++) {
        // if message has suspicious word, increment numCaughtWords
        if (susWordSet.contains(messageWords[i])) {
          numCaughtWords++;
        }

        // if message has suspicious word and it has not appeared before, add it to caughtWords
        if (susWordSet.contains(messageWords[i]) && !caughtWordsSet.contains(messageWords[i])) {
          caughtWords = caughtWords + messageWords[i] + " ";
        }
        caughtWordsSet.add(messageWords[i]);
        messageWordCount++;
      }


      // send response to client
      float spamScore = (float)numCaughtWords / messageWordCount;
      String response = susWordNum + " " + spamScore + " " + caughtWords;

      sendData = response.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAdress, port);
      serverSocket.send(sendPacket);
      System.out.println("Response sent");
    }
  }
}
