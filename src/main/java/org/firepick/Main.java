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
    sb.append("  -t --timeout\n");
    sb.append("\tChange timeout from 1500ms to given number of milliseconds.\n");
    sb.append("\n");
    sb.append("See Also:\n");
    sb.append("\thttp://github.com/firepick1/firerest-cmd\n");
    System.out.println(sb);
  }

  private static int listServices(int msTimeout) {
    System.out.println("Scanning local network for FireREST services (@" + msTimeout + "ms)...");
    Collection<ServiceResolver> resolvers = ServiceResolver.discover(null, 255, msTimeout);
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

  private static int listHosts(int msTimeout) {
    System.out.println("Scanning local network (@" + msTimeout + "ms)...");
    int count = 0;
    for (InetAddress host: IPv4Scanner.scanRange(null, 255, msTimeout)) {
      count++;
      System.out.println("HOST " + count + ": " + host.getHostAddress() + " " + host.getCanonicalHostName());
    }
    return 1;
  }

  public static void main(String [] args) {
    int argsProcessed = 0;
    int msTimeout = 1500;

    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if (arg.equals("-t") || arg.equals("--timeout")) {
	msTimeout = Integer.parseInt(args[++i]);
	argsProcessed += 2;
      } else if (arg.equals("-s") || arg.equals("--services")) {
	argsProcessed += listServices(msTimeout);
      } else if (arg.equals("-h") || arg.equals("--hosts")) {
	argsProcessed += listHosts(msTimeout); } else if (arg.equals("--help")) {
        // no action
      } else {
	break;
      }
    }
    if (args.length == 0 || argsProcessed != args.length) {
      help();
    }
  }
}
