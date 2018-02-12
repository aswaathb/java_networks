/*
 * Created on 01-Mar-2016
 */
package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Timer;
import java.io.IOException;
import java.util.*;
import java.io.*;

import common.MessageInfo;

public class UDPServer {

	private DatagramSocket recvSoc;
	private int totalMessages = -1;
	private int msgnum = -1;
	private ArrayList<Integer> receivedMessages = new ArrayList<Integer>();
	private boolean close;
	private int received = 0;

	private void run() {
		int				pacSize;
		byte[]			pacData;
		DatagramPacket 	pac;

		// TO-DO: Receive the messages and process them by calling processMessage(...).
		//        Use a timeout (e.g. 30 secs) to ensure the program doesn't block forever
		try {
			recvSoc.setSoTimeout(300000);
			while (true) {
				pacData = new byte[256];
				pacSize = pacData.length;
				pac = new DatagramPacket(pacData, pacSize);
				try {
					recvSoc.receive(pac);
					String recStr= new String(pac.getData(), 0, pac.getLength());
					processMessage(recStr);
					if (msgnum == totalMessages-1 || received==totalMessages){
						sysstatus();
					}
				}
				catch (SocketTimeoutException e) {
					if (totalMessages!=-1)
						e.printStackTrace();
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sysstatus(){
		System.out.println("Received " + received + " out of " + totalMessages + " messages");
		double efficiency = ((double) received/ (double) totalMessages)*100;
		System.out.println("Efficiency of Server = " + efficiency + "%");
		received = 0;
		totalMessages = -1;
		String s = "Lost packet numbers: ";
		int count = 0;
		for (int i = 0; i < totalMessages; i++) {
			if (receivedMessages.get(i) != 1) {
				count++;
				s = s + " " + (i+1) + ", ";
			}
		}

		if (count == 0) s = s + "None";
		System.out.println(s);
	}


	public void processMessage(String data) {

		MessageInfo msg = null;

		try {
			msg = new MessageInfo(data);
		}
		catch (Exception e) {
			e.printStackTrace();
		}


		msgnum = msg.messageNum;
		totalMessages = msg.totalMessages;

		if (totalMessages > receivedMessages.size()){
			receivedMessages.ensureCapacity(totalMessages);
		}

		if (msgnum < totalMessages && !receivedMessages.contains(msgnum) && received < totalMessages && msgnum!=totalMessages-1) {
			receivedMessages.add(msgnum);
			received++;
		}

		else if (msgnum==totalMessages-1) {
			receivedMessages.add(msgnum);
			received++;
		}

	}


	public UDPServer(int rp) {
		// TO-DO: Initialise UDP socket for receiving data
		try {
			recvSoc = new DatagramSocket(rp);
		}
		catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port " + rp);
			System.out.println(e.getMessage());
		}

	}

	public static void main(String args[]) {
		int	recvPort;

		// Get the parameters from command line
		if (args.length < 1) {
			System.err.println("Arguments required: recv port");
		}
		recvPort = Integer.parseInt(args[0]);

		// TO-DO: Construct Server object and start it by calling run().
		UDPServer udpsrv = new UDPServer(recvPort);
		udpsrv.run();

	}
}