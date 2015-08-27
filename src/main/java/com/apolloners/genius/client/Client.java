/**
 * 2015. 8. 26.
 * Copyright by yyh / Hubigo AIAL
 * BlackWhiteClient.java
 */
package com.apolloners.genius.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apolloners.blackandwhite.Server;
import com.apolloners.genius.room.GameRoom;
import com.apolloners.genius.room.Room;
import com.apolloners.genius.room.WaitingRoom;

public class Client extends Thread	{
	protected static Logger logger = LoggerFactory.getLogger(Client.class);
	private Room room;
	private Socket socket;
	private DataOutputStream os;
	private DataInputStream is;
	
	private String userId;
	
	public Client()	{
	}
	
	public Client(Socket socket)	{
		this.socket = socket;
		try {
			this.is = new DataInputStream(socket.getInputStream());
			this.os = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		logger.info("Client Start");
		String input;
		while(socket != null)	{
			try {
				input = is.readUTF();
				System.out.println(input);
				os.writeUTF(input);
			} catch (IOException e) {
				try {
					socket.close();
				} catch (IOException e1) {
					logger.error(e.getMessage());
				}
			}
		}
	}
	
	
	public int createGameRoom(String title)	{
		int result = 0;
		if(room instanceof WaitingRoom)	{
			WaitingRoom waitingRoom = (WaitingRoom)room;
			GameRoom gameRoom = waitingRoom.createGameRoom(title, this);
			
			result = -1;
			
			if(gameRoom != null)	{
				waitingRoom.exitRoom(this);			
				result = 1;
			}
		}
		
		return result;
	}
	
	public int exitRoom()	{
		int result = 0;
		room.exitRoom(this);
		if(room instanceof WaitingRoom)	{
			// ���� ����
			
			result = 2;
		} else	{
			room = Server.waitingRoom;
			result = room.enterRoom(this);
			
			result = 1;
		}
		
		return result;
	}

	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * @param socket the socket to set
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	/**
	 * @return the room
	 */
	public Room getRoom() {
		return room;
	}

	/**
	 * @param room the room to set
	 */
	public void setRoom(Room room) {
		this.room = room;
	}
	
	public void setUserId(String userId)	{
		this.userId = userId;
	}
	
	public String getUserId()	{
		return userId;
	}
	
	
}
