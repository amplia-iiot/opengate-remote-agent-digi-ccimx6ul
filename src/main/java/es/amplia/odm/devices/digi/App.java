package es.amplia.odm.devices.digi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	
	private static Agent agent = null;
	
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				LOGGER.info("Shutdown agent...");
				try {
					if (agent != null) agent.stop();
				} catch (Exception e) {
					
				}
			}
		});
		if (args.length != 1) {
			LOGGER.error("You must use only one arguments: configPath.");
			return;
		}
		try {
			InetAddress inetAddress = null;
			NetworkInterface ni = NetworkInterface.getByName("wlan0");
			for (InterfaceAddress addr : ni.getInterfaceAddresses()) {
				if (addr.getAddress().getAddress().length == 4) { // Is IPv4
					LOGGER.info("Connecting Http Connection through IP address: " + addr.getAddress());
					inetAddress = addr.getAddress();
					break;
				}
			}
			
			InetAddress inetEth0Address = null;
			NetworkInterface niEth0 = NetworkInterface.getByName("eth0");
			for (InterfaceAddress addr : niEth0.getInterfaceAddresses()) {
				if (addr.getAddress().getAddress().length == 4) { // Is IPv4
					LOGGER.info("Connecting IOT Http Connction through IP address: " + addr.getAddress());
					inetEth0Address = addr.getAddress();
					break;
				}
			}
			
			agent = new Agent(args[0], inetAddress, inetEth0Address);
			agent.start();
		} catch (FileNotFoundException  e) {
			LOGGER.error("Error accesing files on path " + args[0], e);
		} catch (IOException e) {
			LOGGER.error("Error accesing files or bad configuration files on path " + args[0], e);
		} catch (SQLException e) {
			LOGGER.error("Error accesing data base configured on path " + args[0], e);
		} catch (ClassNotFoundException e) {
			LOGGER.error("Error on data base configured on path " + args[0], e);
		} catch (URISyntaxException e) {
			LOGGER.error("Bad web socket URI configured on path " + args[0], e);
		} catch (InterruptedException e) {
			LOGGER.error("Error starting HTTP server configured on path " + args[0], e);
		} catch (Throwable e) {
			LOGGER.error("Error starting Agent configured on path " + args[0], e);
		}
	}

}
