package reldat;

public class ReldatConstants {
  public static final int HEADER_SIZE = 27;
  public static final int PAYLOAD_SIZE = 1000;
  public static final int PACKET_SIZE = HEADER_SIZE + PAYLOAD_SIZE;
  public static final int CHECKSUM_SIZE = 16;
  public static final int BUFFER_SIZE = 2;
  public static final int SEQ_SIZE = 4;
  public static final int ACK_SIZE = 4;
  public static final int FLAGS_SIZE = 1;
}
