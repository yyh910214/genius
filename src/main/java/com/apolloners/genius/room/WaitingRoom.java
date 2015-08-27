/**
 * 2015. 8. 26.
 * Copyright by yyh / Hubigo AIAL
 * WaitingRoom.java
 */
package com.apolloners.genius.room;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apolloners.genius.client.Client;

public class WaitingRoom implements Room {
	
	protected static Logger logger = LoggerFactory.getLogger(WaitingRoom.class);
	
	private List<Client> waitingClients;	// ����� ���
	private List<GameRoom> gameRooms;
	
	private int max = 10;
	
	public WaitingRoom()	{
		this.waitingClients = new ArrayList<Client>();
		this.gameRooms = new ArrayList<GameRoom>();
	}

	/**
	 * ���� ����
	 */
	@Override
	public int enterRoom(Client client) {
		if(waitingClients.size() > max)	{
			return -1;
		}
		waitingClients.add(client);
		
		return 1;
	}
	
	/**
	 * ���ӹ� �����
	 */
	public GameRoom createGameRoom(String title, Client master)	{
		if(gameRooms.size() > max)	{
			return null;
		}
		GameRoom gameRoom = new GameRoom(title, master);
		gameRooms.add(gameRoom);
		
		return gameRoom;
	}

	/* (non-Javadoc)
	 * @see genius.room.Room#exitRoom(genius.client.Client)
	 */
	@Override
	public int exitRoom(Client client) {
		if(waitingClients.remove(client))	{
			return 1;
		}
		return -1;
	}

}
