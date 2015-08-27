/**
 * 2015. 8. 26.
 * Copyright by yyh / Hubigo AIAL
 * WaitingRoom.java
 */
package com.apolloners.genius.room;

import com.apolloners.genius.client.Client;

public interface Room {
	public int enterRoom(Client client);
	public int exitRoom(Client client);
}
