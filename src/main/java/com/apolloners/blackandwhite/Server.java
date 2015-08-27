/**
 * 2015. 8. 26.
 * Copyright by yyh / Hubigo AIAL
 * Server.java
 */
package com.apolloners.blackandwhite;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apolloners.genius.client.Client;
import com.apolloners.genius.room.WaitingRoom;
import com.apolloners.genius.server.GeniusServer;

public class Server implements GeniusServer {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static WaitingRoom waitingRoom;
	
	public Server(WaitingRoom waitingRoom)	{
		this.waitingRoom = waitingRoom;
	}

	/* (non-Javadoc)
	 * @see blackandwhite.server.GeniusServer#startServer()
	 */
	@Override
	public void startServer(int port) {
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(true)	{
		
				Socket clientSocket;
				try {
					logger.info("클라이언트의 연결을 대기중입니다.....");
					clientSocket = server.accept();
					logger.info(clientSocket.getInetAddress() + "와 연결되었습니다.");
					Client client = new Client(clientSocket);
					client.start();
					
					waitingRoom.enterRoom(client);
					client.setRoom(waitingRoom);
					
				} catch (IOException e) {
					logger.error("Client Connect Error");
					logger.error(e.getMessage());
				}
		}
	}

}