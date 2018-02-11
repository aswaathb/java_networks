/*
 * Created on 01-Mar-2016
 */
package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.io.*;
import common.*;

public class RMIServer extends UnicastRemoteObject implements RMIServerI {
    
    private int totalMessages = -1;
    private int[] receivedMessages;
    private int received;
    
    public RMIServer() throws RemoteException {
    }
    
    public void receiveMessage(MessageInfo msg) throws RemoteException {
        
        // TO-DO: On receipt of first message, initialise the receive buffer
        if(receivedMessages == null) {
            receivedMessages = new int[msg.totalMessages];
            totalMessages=msg.totalMessages+received;
        }
        // TO-DO: Log receipt of the message
        
        receivedMessages[msg.messageNum]=1;
        received++;
        // TO-DO: If this is the last expected message, then identify
        //        any missing messages
        if(msg.messageNum==msg.totalMessages-1){
            System.out.println("Received " + received + " out of " + totalMessages + " messages");
	    double efficiency = ((double) received/ (double) totalMessages)*100;
	    System.out.println("Efficiency of Server = " + efficiency + "%");
		
		for(int i=0; i<msg.totalMessages; i++){		
			if(receivedMessages[i]==0){
				System.out.println("message lost at" + i + "th point");
		}
		} 
		received=0;       
	}
        
    }
    
    
    public static void main(String[] args) {
        
        RMIServer rmis = null;
        
        // TO-DO: Initialise Security Manager
        if(System.getSecurityManager() == null){
            System.setSecurityManager (new SecurityManager ());
        }
        String urlServer = new String("rmi://" + "localhost" + "/RMIServer");
        // TO-DO: Instantiate the server class
        try {
            RMIServer myserver = new RMIServer();
            rebindServer(urlServer, myserver);
            
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // TO-DO: Bind to RMI registry
        
    }
    
    protected static void rebindServer(String serverURL, RMIServer server) {
        
        // TO-DO:
        // Start / find the registry (hint use LocateRegistry.createRegistry(...)
        // If we *know* the registry is running we could skip this (eg run rmiregistry in the start script)
	try {
		LocateRegistry.createRegistry(1099);
	} catch(RemoteException e){
		e.printStackTrace();	
	}

	try {
		Naming.bind(serverURL,server);
	} catch(AlreadyBoundException e){
		e.printStackTrace();
	} catch(RemoteException e){
		e.printStackTrace();
	} catch(MalformedURLException e){
		e.printStackTrace();
	}
        // TO-DO:
        // Now rebind the server to the registry (rebind replaces any existing servers bound to the serverURL)
        // Note - Registry.rebind (as returned by createRegistry / getRegistry) does something similar but
        // expects different things from the URL field.
        
    }
}

