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
	private int[] receivedMessages;
	private boolean close;

	private void run() {
		int				pacSize;
		byte[]			pacData;
		DatagramPacket 	pac;

		// TO-DO: Receive the messages and process them by calling processMessage(...).
		//        Use a timeout (e.g. 30 secs) to ensure the program doesn't block forever
		try{
			recvSoc.setSoTimeout(3000);
			while(true){
				pacData = new byte [256];
				pacSize = pacData.length();
				pac=new DatagramPacket(pacData,pacSize);
				try{
					recvSoc.recieve(pac);
					String rcmsg = new String(pac.getData(), 0, pac.getLength());
				}
			}
		}

	}

	public void processMessage(String data) {

		MessageInfo msg = null;

		// TO-DO: Use the data to construct a new MessageInfo object
		try {
			msg = new MessageInfo(data);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		// TO-DO: On receipt of first message, initialise the receive buffer
		if (receivedMessages == null) {
			totalMessages = msg.totalMessages;
			receivedMessages = new int[totalMessages];
		}
		// TO-DO: Log receipt of the message
		receivedMessages[msg.messageNum] = 1;
		// TO-DO: If this is the last expected message, then identify
		//        any missing messages
		if (msg.messageNum + 1 == msg.totalMessages) {
			close = true;

			String s = "Lost packet numbers: ";
			int count = 0;
			for (int i = 0; i < totalMessages; i++) {
				if (receivedMessages[i] != 1) {
					count++;
					s = s + " " + (i+1) + ", ";
				}
			}

			if (count == 0) s = s + "None";

			System.out.println("Messages processed...");
			System.out.println("Of " + msg.totalMessages + ", " + (msg.totalMessages - count) + " received successfully...");
			System.out.println("Of " + msg.totalMessages + ", " + count + " failed...");
			System.out.println(s);
			System.out.println("Test finished.");
		}

	}


	public UDPServer(int rp) {
		// TO-DO: Initialise UDP socket for receiving data
		try {
			port = rp;
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			System.out.println("Error: Could not create socket on port " + port);
		}
		close = false;
		// Done Initialisation
		System.out.println("UDPServer ready");
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
		try {
			udpsrv.run();
		} catch (SocketTimeoutException e) {}
	}
	}

}
