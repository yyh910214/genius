/**
 * 2015. 8. 26.
 * Copyright by yyh / Hubigo AIAL
 * GameRoom.java
 */
package com.apolloners.genius.room;

import com.apolloners.genius.client.Client;

public class GameRoom implements Room {
	
	private String title;
	private String masterId;
	
	private Client master;
	private Client guest;
	
	public GameRoom(String title, Client master)	{
		this.title = title;
		this.master = master;
		
		masterId = master.getUserId();
		master.setRoom(this);
	}
	/* (non-Javadoc)
	 * @see genius.room.Room#enterRoom(genius.client.Client)
	 */
	@Override
	public int enterRoom(Client client) {
		this.guest = client;
		return 1;
	}
	/* (non-Javadoc)
	 * @see genius.room.Room#exitRoom(genius.client.Client)
	 */
	@Override
	public int exitRoom(Client client) {
		if(client == this.master)	{
			this.master = this.guest;	
		}
		
		this.guest = null;
		
		return 1;
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
	
	

}
