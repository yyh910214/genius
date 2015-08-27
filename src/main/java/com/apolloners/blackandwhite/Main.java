/**
 * 2015. 8. 27.
 * Copyright by yyh / Hubigo AIAL
 * Main.java
 */
package com.apolloners.blackandwhite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apolloners.genius.room.WaitingRoom;

/**
 * BlackAndWhite를 시작하는 부분
 */
public class Main {
	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(Main.class);
		logger.debug("Start Black And White Server");
		Server server = new Server(new WaitingRoom());
		server.startServer(20202);
	}
}
