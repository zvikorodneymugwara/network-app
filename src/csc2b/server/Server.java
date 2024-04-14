package csc2b.server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

	private ServerSocket ss;	//the server socket
	private boolean running = false;
	
	//initialize the server socket
	public Server() {
		try {
			ss = new ServerSocket(2021);
			System.out.println("Waiting for Connections...");
			running = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//run the threaded server
	public void runServer() {
		while(running) {			
			try {
				Thread thread = new Thread(new ZEDEMHandler(ss.accept()));
				thread.start();
				System.out.println("Connected");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Server server = new Server();	//create a server instance
		server.runServer();	//run the server instance
	}
	
}
