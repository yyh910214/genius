/**
 * 2015. 8. 26.
 * Copyright by yyh / Hubigo AIAL
 * GameRoom.java
 */
package com.apolloners.genius.room;

import java.util.concurrent.atomic.AtomicInteger;

import com.apolloners.genius.client.Client;

public class GameRoom implements Room {
	
	private static AtomicInteger roomIndex = new AtomicInteger(1);
	
	private WaitingRoom waitingRoom;
	
	private int roomNo;
	private String title;
	private String masterId;
	
	private Client master;
	private Client guest;
	
	public GameRoom(WaitingRoom waitingRoom, String title, Client master)	{
		this.waitingRoom = waitingRoom;
		this.title = title;
		this.master = master;
		this.roomNo = roomIndex.getAndIncrement();
		masterId = master.getUserId();
	}
	/* (non-Javadoc)
	 * @see genius.room.Room#enterRoom(genius.client.Client)
	 */
	@Override
	public int enterRoom(Client client) {
		if(this.master == null)	{
			this.master = client;
		} else	{
			this.guest = client;			
		}
		return 1;
	}
	/* (non-Javadoc)
	 * @see genius.room.Room#exitRoom(genius.client.Client)
	 */
	@Override
	public Room exitRoom(Client client) {
		if(client == this.master)	{
			this.master = this.guest;
			if(this.master == null)	{
				waitingRoom.removeGameRoom(this);
			}
		}
		
		this.guest = null;
		
		return this.waitingRoom;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @return the masterId
	 */
	public String getMasterId() {
		return masterId;
	}
	/**
	 * @return the roomNo
	 */
	public int getRoomNo() {
		return roomNo;
	}

}
