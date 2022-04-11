package com.rps.game.server;

import com.rps.game.server.telnet.TelnetServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpsGameServerApplication {

	private final static int PORT = 3000;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(RpsGameServerApplication.class, args);
		TelnetServer.initServer(PORT);
	}
}