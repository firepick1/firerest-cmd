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
    sb.append("  -l --localhost\n");
    sb.append("\tPrint local host addresses\n");
    sb.append("\n");
    sb.append("  -h --hosts\n");
    sb.append("\tList hosts on local area network assuming 24-bit subnet mask\n");
    sb.append("  -H --HOSTS\n");
    sb.append("\tList hosts continuously on local area network assuming 24-bit subnet mask\n");
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

  private static int listHosts(int msTimeout, boolean continuous) {
    System.out.println("Scanning local network (@" + msTimeout + "ms)...");
    HashSet<InetAddress> hosts = new HashSet<InetAddress>();
    int n = 0;
    for (InetAddress host: IPv4Scanner.scanRange(null, 255, msTimeout)) {
      n++;
      hosts.add(host);
      System.out.println("  " + host.getHostAddress() + " " + host.getCanonicalHostName());
    }
    while (continuous) {
      System.out.println();
      HashSet<InetAddress> newHosts = new HashSet<InetAddress>();
      for (InetAddress host: IPv4Scanner.scanRange(null, 255, msTimeout)) {
        if (hosts.contains(host)) {
	  hosts.remove(host);
	} else {
	  System.out.println("+ " + host.getHostAddress()+" "+host.getCanonicalHostName());
	}
	newHosts.add(host);
      }
      for (InetAddress host: hosts) {
	System.out.println("- "+host.getHostAddress()+" "+host.getCanonicalHostName());
      }
      hosts = newHosts;
    }
    return 1;
  }

  private static int listLocal() {
    int count = 0;
    try {
      for (InetAddress host: IPv4Scanner.localhostNetworkAddresses()) {
	count++;
	System.out.println("LOCALHOST " + count + ": " + host.getHostAddress() + " " + host.getCanonicalHostName());
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
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
      } else if (arg.equals("-l") || arg.equals("--localhost")) {
	argsProcessed += listLocal();
      } else if (arg.equals("-s") || arg.equals("--services")) {
	argsProcessed += listServices(msTimeout);
      } else if (arg.equals("-h") || arg.equals("--hosts")) {
	argsProcessed += listHosts(msTimeout, false); 
      } else if (arg.equals("-H") || arg.equals("--HOSTS")) {
	argsProcessed += listHosts(msTimeout, true); 
      } else if (arg.equals("--help")) {
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
