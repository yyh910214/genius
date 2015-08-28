/**
 * 2015. 8. 26.
 * Copyright by yyh / Hubigo AIAL
 * WaitingRoom.java
 */
package com.apolloners.genius.room;

import com.apolloners.genius.client.Client;

public interface Room {
	/**
	 * 1 : enter success
	 * -1 : exceed person
	 * -2 : not exist
	 */
	public int enterRoom(Client client);
	/**
	 * @return the information of parent room
	 */
	public Room exitRoom(Client client);
}
