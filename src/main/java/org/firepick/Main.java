package org.firepick;

import java.net.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  static Logger logger = LoggerFactory.getLogger(Main.class);


  private static void help() {
    StringBuilder sb = new StringBuilder();

    sb.append("------------------------------------------\n");
    sb.append("FireREST\u2122 command line utility " + Version.VERSION + "\n");
    sb.append("------------------------------------------\n");
    sb.append("Discovers hosts and FireREST services on local area network.\n");
    sb.append("\n");
    sb.append("Examples:\n");
    sb.append("  ./firerest -h\n");
    sb.append("  ./firerest -h -s\n");
    sb.append("\n");
    sb.append("Options:\n");
    sb.append("  --help\n");
    sb.append("\tPrint this text\n");
    sb.append("\n");
    sb.append("  -h --hosts\n");
    sb.append("\tList hosts on local area network assuming 24-bit subnet mask\n");
    sb.append("\n");
    sb.append("  -s --services\n");
    sb.append("\tList FireREST services on local area network assuming 24-bit subnet mask\n");
    sb.append("\n");
    sb.append("See Also:\n");
    sb.append("\thttp://github.com/firepick1/firerest-cmd\n");
    System.out.println(sb);
  }

  private static int listServices() {
    System.out.println("Scanning local network for FireREST services...");
    Collection<ServiceResolver> resolvers = ServiceResolver.discover(null, 255, 500);
    int count = 0;
    for (ServiceResolver resolver: resolvers) {
      count++;
      StringBuilder sb = new StringBuilder();
      sb.append("SERVICE ");
      sb.append(count);
      sb.append(": ");
      sb.append(resolver.getURL());
      sb.append(" ");
      sb.append(resolver.getConfig().get("FireREST").getString());
      System.out.println(sb);
    }
    return 1;
  }

  private static int listHosts() {
    System.out.println("Scanning local network...");
    int count = 0;
    for (InetAddress host: IPv4Scanner.scanRange(null, 255, 500)) {
      count++;
      System.out.println("HOST " + count + ": " + host.getHostAddress() + " " + host.getCanonicalHostName());
    }
    return 1;
  }

  public static void main(String [] args) {
    int count = 0;

    for (String arg: args) {
      if (arg.equals("-s") || arg.equals("--services")) {
	count += listServices();
      } else if (arg.equals("-h") || arg.equals("--hosts")) {
	count += listHosts();
      } else if (arg.equals("--help")) {
        // no action
      } else {
	break;
      }
    }
    if (args.length == 0 || count != args.length) {
      help();
    }
  }
}
