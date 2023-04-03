package tv.memoryleakdeath.hex.ingest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tv.memoryleakdeath.hex.ingest.rtmp.RtmpServer;

public class HexIngest {
	private static final Logger logger = LoggerFactory.getLogger(HexIngest.class);
	private static final int DEFAULT_RTMP_PORT = 1935;
	private static final int DEFAULT_MAX_CONNECTIONS = 100;
	private static final String RTMP_PORT_OPTION_ARG = "rport";
	private static final String MAX_CONNECTIONS_ARG = "maxcon";

	private static CommandLineParser parser = new DefaultParser();

	public static void main(String[] args) {
		try {
			int rtmpPort = DEFAULT_RTMP_PORT;
			int maxConnections = DEFAULT_MAX_CONNECTIONS;
			CommandLine cli = parser.parse(registerCommandLineOptions(), args);
			if (cli.hasOption(RTMP_PORT_OPTION_ARG)) {
				rtmpPort = (int) cli.getParsedOptionValue(RTMP_PORT_OPTION_ARG);
			}
			if (cli.hasOption(MAX_CONNECTIONS_ARG)) {
				maxConnections = (int) cli.getParsedOptionValue(MAX_CONNECTIONS_ARG);
			}
			new RtmpServer(rtmpPort, maxConnections).run();
		} catch (ParseException e) {
			logger.error("Unknown command line option passed!", e);
			System.out.println("Unknown command line option passed");
		} catch (InterruptedException e) {
			logger.info("Server shutdown signal received.");
			System.out.println("Server shutdown signal received.");
			System.exit(0);
		}
	}

	private static Options registerCommandLineOptions() {
		Option portOption = Option.builder().argName("[port]").desc("rtmp port (default is 1935)").hasArg(true)
				.numberOfArgs(1).option(RTMP_PORT_OPTION_ARG).optionalArg(false).required(false).valueSeparator('=')
				.type(Integer.class).build();
		Option maxConnectionsOption = Option.builder().argName("[max]").desc("max client connections to allow")
				.hasArg(true).numberOfArgs(1).option(MAX_CONNECTIONS_ARG).optionalArg(false).required(false)
				.valueSeparator('=').type(Integer.class).build();
		Option helpOption = Option.builder().desc("Shows this help message").hasArg(false).option("help")
				.numberOfArgs(0).required(false).build();
		Options options = new Options();
		options.addOption(portOption);
		options.addOption(maxConnectionsOption);
		options.addOption(helpOption);
		return options;
	}

}
