package org.firepick;

import java.net.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  static Logger logger = LoggerFactory.getLogger(Main.class);

  private static void help() {
    StringBuilder sb = new StringBuilder();

    sb.append(" -h --hosts\n");
    sb.append("\tLists hosts on local area network assuming 24-bit subnet mask");
    System.out.println(sb);
  }

  private static void listHosts() {
    System.out.println("Scanning local network...");
    int count = 0;
    for (InetAddress host: IPv4Scanner.scanRange(null, 255, 500)) {
      count++;
      System.out.println(count + ": " + host.getHostAddress() + " " + host.getCanonicalHostName());
    }
  }

  public static void main(String [] args) {
    for (String arg: args) {
      if (arg.equals("-h") || arg.equals("--hosts")) {
	listHosts();
      } else {
	help();
	break;
      }
    }
  }
}
