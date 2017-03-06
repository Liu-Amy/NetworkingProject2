import java.util.*;
import java.net.*;
import java.io.*;

public class smsengineTCP {
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
    while (true) {
      if (args.length != 2) {
        System.out.println("Usage: java smsengineTCP <port number> <suspicious-words.txt>");
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

      int messageWordCount = 0;
      int numCaughtWords = 0;
      String caughtWords =  "";

      // create socket, accept connection, get input from client
      try (
        ServerSocket serverSocket = new ServerSocket(portNumber);
        Socket clientSocket = serverSocket.accept();
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      ) {
        String inputLine = in.readLine();

        // message provided by client is empty
        if (inputLine.length() <= 0) {
          System.out.println("The message is empty");
          out.println("0 -1 Error");
        }

        // message provided by client exceeds character count
        if (inputLine.length() > 1000) {
          System.out.println("The message is longer than 1000 characters");
          out.println("0 -1 Error");
        }

        // message provided by client has at one or more non-ascii characters
        if (inputLine.length() > 0 && !isStringAscii(inputLine)) {
          System.out.println("The message contains one or more non-ascii characters");
          out.println("0 -1 Error");
        }

        // split message by space, commas, !, ?, and m
        String[] messageWords = inputLine.split("[\\s,!?.]+");
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
        out.println(susWordNum + " " + spamScore + " " + caughtWords);
        System.out.println("Response sent");
      } catch (IOException e) {
        System.out.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
        System.out.println(e.getMessage());
      }
    }
  }
}
