package reldat;

public class ReldatConstants {
  public static final int HEADER_SIZE = 25;
  public static final int PAYLOAD_SIZE = 1000;
  public static final int PACKET_SIZE = HEADER_SIZE + PAYLOAD_SIZE;
  public static final int CHECKSUM_SIZE = 16;
  public static final int SEQ_SIZE = 4;
  public static final int ACK_SIZE = 4;
  public static final int FLAGS_SIZE = 1;
  public static final int TIMEOUT = 15000;
  public static final int DATA_TIMEOUT = 5000;
  public static final Boolean DEBUG_MODE = false;
  public static final int LOSS_RATE = 5;
  public static final int CORRUPT_RATE = 5;
  public static final int DELAY_MIN = 70;
  public static final int DELAY_MAX = 130;
}
