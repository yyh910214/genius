/**
 * 2015. 8. 26.
 * Copyright by yyh / Hubigo AIAL
 * GameRoom.java
 */
package com.apolloners.genius.room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apolloners.genius.client.Client;
import com.apolloners.genius.common.CommonCode;
import com.apolloners.genius.common.Protocol;

public class GameRoom implements Room {
	
	protected static final Logger logger = LoggerFactory.getLogger(GameRoom.class);
	private static final int MAX_ROUND = 10;
	
	private static AtomicInteger roomIndex = new AtomicInteger(1);
	
	private WaitingRoom waitingRoom;
	
	private int roomNo;
	private String title;
	private String masterId;
	
	private Client master;
	private Client guest;
	
	/**
	 * make A Game Class, later
	 */
	
	private Client first, second;
	
	private boolean playing;
	private boolean playOrder;	// true : master first, false : guest first
	private boolean attackOrder;	// true : Attack, false : Defense

	private int firstPoint, secondPoint;	// Turn point of the clients
	
	private List<Integer> masterPoints, guestPoints;
	
	private int round;
	
	public GameRoom(WaitingRoom waitingRoom, String title, Client master)	{
		this.waitingRoom = waitingRoom;
		this.title = title;
		this.master = master;
		this.guest = null;
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
			this.master.write(Protocol.JOIN + CommonCode.DELIMITER + CommonCode.ID + 
					CommonCode.DELIMITER + this.guest.getUserId());
		}
		return 1;
	}
	
	public void startGame()	{
		logger.info("Start Game Room No." + this.roomNo);
		this.playing = true;
		this.guest.write(Protocol.START.name());
		this.master.write(Protocol.START.name());
	
		this.playOrder = (Math.random() < 0.5);
		this.round = 0;
		this.masterPoints = new ArrayList<Integer>();
		this.guestPoints = new ArrayList<Integer>();
		doTurnStart();
	}
	
	protected void doTurnStart()	{
		round++;
		if(playOrder)	{
			first = this.master;
			second = this.guest;
		} else	{
			first = this.guest;
			second = this.master;
		}
		
		attackOrder = true;
		first.write(Protocol.ATTACK.name());
	}
	
	
	/**
	 * Progress Input Data
	 * Reduce the point from a client.
	 */
	public void doInput(Client client, String pointString)	{
		int point = Integer.parseInt(pointString);
		
		if(master == client)	{
			this.masterPoints.add(point);
		} else if(guest == client)	{
			this.guestPoints.add(point);
		}
		
		if(attackOrder)	{	// attack Protocol case
			firstPoint = point;
			second.write(Protocol.DEFENSE.name() + CommonCode.DELIMITER + pointString);
			attackOrder = false;
			
			logger.debug(first.getUserId() + "'s Attack point is " + firstPoint);
			// 점수 기록 필요함.
		} else	{	// Defense Protocol case
			secondPoint = point;
			StringBuilder firstMsg = new StringBuilder();
			StringBuilder secondMsg = new StringBuilder();
			firstMsg.append(Protocol.FINISH).append(CommonCode.DELIMITER);
			secondMsg.append(Protocol.FINISH).append(CommonCode.DELIMITER);
			
			logger.debug(second.getUserId() + "'s Defense point is " + secondPoint);		 
			
			if(secondPoint < firstPoint)	{
				firstMsg.append(CommonCode.WIN);
				secondMsg.append(CommonCode.LOSE);
			} else if(secondPoint > firstPoint)	{
				firstMsg.append(CommonCode.LOSE);
				secondMsg.append(CommonCode.WIN);
				
				playOrder = !playOrder;	// change the play order
			} else	{
				firstMsg.append(CommonCode.DRAW);
				secondMsg.append(CommonCode.DRAW);
				
				playOrder = (Math.random() < 0.5);	// If the game result is draw, get random order.
			}
			
			firstMsg.append(CommonCode.DELIMITER).append(secondPoint);
			secondMsg.append(CommonCode.DELIMITER).append(firstPoint);
			
			first.write(firstMsg.toString());
			second.write(secondMsg.toString());
			
			logger.debug(firstMsg.toString());
			logger.debug(secondMsg.toString());
			
			if(round != MAX_ROUND)	{
				doTurnStart();	
			} else	{
				doGameEnd();
			}
		}
	}
	
	/**
	 * Calculate winner
	 * Send the End Protocol
	 */
	protected void doGameEnd()	{
		int masterWin = 0, guestWin = 0;
		for(int i = 0; i < MAX_ROUND; ++i)	{
			if(masterPoints.get(i) < guestPoints.get(i))	{
				guestWin++;
			} else if(guestPoints.get(i) < masterPoints.get(i))	{
				masterWin++;
			}
		}
		
		if(masterWin > guestWin)	{
			master.write(Protocol.END.name() + CommonCode.DELIMITER + CommonCode.WIN);
			guest.write(Protocol.END.name() + CommonCode.DELIMITER + CommonCode.LOSE);
		} else if(guestWin > masterWin)	{
			master.write(Protocol.END.name() + CommonCode.DELIMITER + CommonCode.LOSE);
			guest.write(Protocol.END.name() + CommonCode.DELIMITER + CommonCode.WIN);
		} else	{
			master.write(Protocol.END.name() + CommonCode.DELIMITER + CommonCode.DRAW);
			guest.write(Protocol.END.name() + CommonCode.DELIMITER + CommonCode.DRAW);
		}
		
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
			} else	{
				this.masterId = this.master.getUserId();
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
	/**
	 * @return the playing
	 */
	public boolean isPlaying() {
		return playing;
	}
	
}
