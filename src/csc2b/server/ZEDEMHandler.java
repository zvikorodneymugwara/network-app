package csc2b.server;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ZEDEMHandler implements Runnable {

	private Socket socket;	//the socket connection
	
	/**
	 * set the socket connection from the server
	 * @param connection
	 */
	public ZEDEMHandler(Socket connection) {
		socket = connection;
	}
	
	@Override
	public void run() {
		try {//initialize streams
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			boolean running = true;	//run the server
			while(running) {
				String message = br.readLine();	//read a request
				String[] messages = message.split(" ");	//get all words in request
				System.out.println("Command: "+messages[0]);
				//check first request
				switch(messages[0]) {
					case "BONJOUR":{	//login
						if(matchUser(messages[1], messages[2])) {
							pw.println("1");
							pw.println("JA");
							pw.println("Login Successful");
						}
						else {
							pw.println("NEE");
							pw.println("Invalid User Credentials");
						}
						break;
					}
					case "PLAYLIST":{	//sending a file list
						System.out.println("Sending file list");
						String s = "";
						ArrayList<String> strings = getFileList();
						for(String str : strings) {
							s+=(str+"@");
						}
						System.out.println("File List: " + s);
						if(strings!=null) {						
							pw.println(s);
							pw.println("JA");
							pw.println("File List Sent Successfully");
							System.out.println("File List Sent Successfully");
						}
						else{						
							pw.println("NEE");
							pw.println("NEE");
							pw.println("An Error Occured");
						}
						break;				
					}
					case "ZEDEMGET":{	//sending a file
						System.out.println("Sending File Name");
						//get and send file name
						String filename = idToFileName(messages[1]);
						pw.println(filename);
						System.out.println("File Name Sent!");
						//if the file has been sent
						if(sendFile(filename, pw, dos)) {
							pw.println("JA");
							pw.println("File Sent Successfully!");
							System.out.println("File Sent!");
						}
						else {
							pw.println("NEE");
							pw.println("Could Not Send the File");	
							System.out.println("File Not Sent!");					
						}
						break;
					}
					case "ZEDEMBYE":{	//log off
						pw.println("JA");
						pw.println("Goodbye!");
						pw.close();
						br.close();
						running = false;
						break;				
					}
					default :{	//unknown request
						pw.println("NEE");
						pw.println("Unknonwn Request");
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * method to match a user's name and password
	 * @param userN
	 * @param passW
	 * @return	true if there's a match
	 */
	private boolean matchUser(String userN, String passW)
	{
		boolean found = false;
		//Code to search users.txt file for match with userN and passW.
		File userFile = new File("data/server/users.txt");
		try
		{
		    Scanner scan = new Scanner(userFile);
		    while(scan.hasNextLine()&&!found)
		    {
				String line = scan.nextLine();
				String lineSec[] = line.split("\\s");
				if(lineSec[0].equals(userN) && lineSec[1].equals(passW)) {
					found = true;
				}		
		    }
		    scan.close();
		}
		catch(IOException ex)
		{
		    ex.printStackTrace();
		}
		return found;
	}
	
	/**
	 * method to get the file list
	 * @return	the ArrayList of file tokens
	 */
	private ArrayList<String> getFileList()
	{
		ArrayList<String> result = new ArrayList<String>();
		
		//Code to add list text file contents to the ArrayList.
		File lstFile = new File("data/server/List.txt");
		try
		{
			Scanner scan = new Scanner(lstFile);
			while(scan.hasNext()) {
				result.add(scan.nextLine());
			}
			scan.close();
		}	    
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		return result;
	}
	
	/**
	 * method to match the name of the file to the provided id
	 * @param strID
	 * @return the name of the file if theres a match
	 */
	private String idToFileName(String strID)
	{
		String result ="";
		//Code to find the file name that matches strID
		File lstFile = new File("data/server/List.txt");
    	try
    	{
    		Scanner scan = new Scanner(lstFile);
    		String line = "";
    		while(scan.hasNext()) {
    			line = scan.nextLine();
    			String[] arr = line.split(" ");
    			if(arr[0].equals(strID)) {
    				result = arr[1];
    				break;
    			}
    		}
    		scan.close();
    	}
    	catch(IOException ex)
    	{
    		ex.printStackTrace();
    	}
		return result;
	}
	
	/**
	 * method to send the file over the network
	 * @param fname
	 * @param pw
	 * @return true if the file has been sent successfully
	 */
	private boolean sendFile(String fname, PrintWriter pw, DataOutputStream dos) {
		try {
			File file = new File("data/server/"+fname);
			pw.println(file.length());
			int i = 0;
			byte[] buffer = new byte[1024];
			FileInputStream fis = new FileInputStream(file);
			while((i = fis.read(buffer))> 0) {
				dos.write(buffer, 0, i);
				dos.flush();
			}
			fis.close();
			return true;
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
