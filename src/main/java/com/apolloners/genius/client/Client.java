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
import java.net.SocketException;

import org.apache.commons.io.IOUtils;
import org.junit.runners.ParentRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apolloners.blackandwhite.Server;
import com.apolloners.genius.common.CommonCode;
import com.apolloners.genius.common.Protocol;
import com.apolloners.genius.room.GameRoom;
import com.apolloners.genius.room.Room;
import com.apolloners.genius.room.WaitingRoom;

public class Client extends Thread {
	protected static Logger logger = LoggerFactory.getLogger(Client.class);
	private Room room;
	private Socket socket;
	private DataOutputStream os;
	private DataInputStream is;

	private boolean isPlaying;

	private String userId;

	public Client() {
	}

	public Client(Socket socket) {
		this.socket = socket;
		try {
			this.is = new DataInputStream(socket.getInputStream());
			this.os = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		logger.info("Client Start");
		write(CommonCode.SUCCESS);
		this.userId = read();
		while (!socket.isClosed()) {
			action(read());
		}
		logger.debug(this.userId + "'s thread is End");
	}

	protected void action(String message) {
		String[] messages = message.split("\\|");
		switch (Protocol.valueOf(messages[0])) {
		case CREATE:
			createGameRoom(messages[1]);
			break;
		case JOIN:
			Room gameRoom = ((WaitingRoom) room).getGameRoom(Integer
					.parseInt(messages[1]));
			enterRoom(gameRoom);
			break;
		case REFRESH:
			refreshWaitingList();
			break;
		case EXIT:
			exitRoom();
			break;
		default:
			if (room instanceof GameRoom) {
				((GameRoom) room).doInput(this, messages[1]);
			}
			break;
		}
	}

	public void enterRoom(Room room) {
		if (room == null) {
			write(Protocol.JOIN + CommonCode.DELIMITER + CommonCode.NOT_EXIST);
			logger.info(userId + " fail to enter room(room is not exist)");
		}

		int result = room.enterRoom(this);
		if (result == 1) {
			this.room = room;
			write(Protocol.JOIN + CommonCode.DELIMITER + CommonCode.SUCCESS);
			if (room instanceof GameRoom) {
				this.isPlaying = true;
			}
			logger.info(userId + " is entered room");
		} else if (result == -1) {
			write(Protocol.JOIN + CommonCode.DELIMITER
					+ CommonCode.EXCEED_PERSON);
			logger.info(userId + " fail to enter room(exceed person)");
		}
	}

	public void refreshWaitingList() {
		if (room instanceof WaitingRoom) {
			String roomList = ((WaitingRoom) this.room).getRefreshJsonString();
			write(Protocol.REFRESH + CommonCode.DELIMITER + roomList);
		}
	}

	public void createGameRoom(String title) {
		if (room instanceof WaitingRoom) {
			WaitingRoom waitingRoom = (WaitingRoom) room;
			GameRoom gameRoom = waitingRoom.createGameRoom(title, this);

			if (gameRoom != null) {
				this.room = gameRoom;
				this.isPlaying = true;
				write(Protocol.CREATE + CommonCode.DELIMITER
						+ CommonCode.SUCCESS);
				logger.info(this.userId + " create Gameroom");
			} else {
				write(Protocol.CREATE + CommonCode.DELIMITER
						+ CommonCode.EXCEED_ROOM);
				logger.info(this.userId + "fail to create Gameroom");
			}
		}
	}

	public void exitRoom() {
		Room parentRoom = room.exitRoom(this);
		if (parentRoom == null) {
			logger.info(this.userId + " leave the waiting room.");
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(socket);
			logger.info(this.userId + "'s connection is closed.");
		} else {
			this.room = parentRoom;
			this.isPlaying = false;
			logger.info(this.userId + " exit the game room.");
		}
		write(Protocol.EXIT + CommonCode.DELIMITER + CommonCode.SUCCESS);
	}

	public void write(String message) {
		try {
			os.writeUTF(message);
			logger.info("Send message " + this.userId + " : " + message);
		} catch (IOException e) {
			logger.error("Send message to " + this.userId + " is failed.");
			logger.error(e.getMessage());
		}
	}

	public String read() {
		String input = "";
		try {
			input = is.readUTF();
		} catch (IOException e) {
			logger.error("Socket read IO Exception");
			logger.error(e.getMessage());
			removeClient();
		}
		logger.debug("Receive message from " + userId + " : " + input);
		return input;
	}

	protected void removeClient() {
		logger.debug("Remove Client");
		exitRoom();	// If, this client is in gameroom, exit one more
		exitRoom();
	}

	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * @param socket
	 *            the socket to set
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
	 * @param room
	 *            the room to set
	 */
	public void setRoom(Room room) {
		this.room = room;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	/**
	 * @return the isPlaying
	 */
	public boolean isPlaying() {
		return isPlaying;
	}

	/**
	 * @param isPlaying
	 *            the isPlaying to set
	 */
	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

}
