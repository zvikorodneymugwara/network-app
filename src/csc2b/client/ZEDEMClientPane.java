package csc2b.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ZEDEMClientPane extends Pane //You may change the JavaFX pane layout
{
	private VBox vbox;	//main vbox of application
	
	//network related variables
	private Socket socket;
	DataInputStream dis;
	private PrintWriter pr;
	private BufferedReader br;
	private boolean loggedIn = false;
	
	//buttons
	private Button loginBtn;
	private Button getPlaylistBtn;
	private Button getAudioFileBtn;
	private Button logOffBtn;
	private Button connectBtn;
	
	//text boxes
	private TextField usernameTxt;
	private TextField passwordTxt;
	private TextField fileIdTxt;
	private TextArea audioFilesListTxt;
	
	//labels
	private Label usernameLbl;
	private Label passwordLbl;
	private Label fileIdLbl;
	private Label fileListLbl;
	
	//constructor
	public ZEDEMClientPane() {
		//initialize VBox and network variables
		vbox = new VBox();
		try {
			socket = new Socket("localhost", 2021);
			pr = new PrintWriter(socket.getOutputStream(), true);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			dis = new DataInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//initialize GUI elements
		loginBtn = new Button("Login");
		getPlaylistBtn = new Button("Get Playlist");
		getAudioFileBtn = new Button("Download File");
		logOffBtn = new Button("Log Off");
		connectBtn = new Button("Connect to Server");
		
		usernameTxt = new TextField();
		passwordTxt = new TextField();
		fileIdTxt = new TextField();
		audioFilesListTxt = new TextArea();
		
		usernameLbl = new Label("Username");
		passwordLbl = new Label("Password");
		fileIdLbl = new Label("Enter File ID to Download");
		fileListLbl = new Label("List of Available Audio Files");

		//button actions
		//connect button
		connectBtn.setOnAction(e ->{
			if(socket == null) {
				//create socket connection and alert user
				try {
					socket = new Socket("localhsot", 2021);
					alertUser("Connected", "Connection made successfully");	
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
					alertUser("Error", "And Error Occured");
				} catch (IOException e1) {
					e1.printStackTrace();
					alertUser("Error", "And Error Occured");
				}
			}
			else {
				alertUser("Connected", "Connection made successfully");				
			}
		});
		
		//login button
		loginBtn.setOnAction(e ->{
			if(!loggedIn) {
				//send the request
				pr.println("BONJOUR " + usernameTxt.getText() + " " + passwordTxt.getText());
				try {
					//read the response. Could either be 1 or 0
					String response = br.readLine();
					if(response.equals("1")) {
						//read server responses and prompt user
						String msg1 = br.readLine();
						String msg2 = br.readLine();
						alertUser(msg1, msg2);
						loggedIn = true;
					}
					else {
						//read server responses and prompt user
						String msg1 = br.readLine();
						String msg2 = br.readLine();
						alertUser(msg1, msg2);
						
					}
				} catch (IOException e1) {
					e1.printStackTrace();
					alertUser("Login Error", "And Error Occured");
				}
			}
			else {
				alertUser("Login Error", "You are already logged in!");				
			}
		});
		
		//get playlist button
		getPlaylistBtn.setOnAction(e ->{
			if(loggedIn) {
				System.out.println("Sending request");
				pr.println("PLAYLIST");	//send the request
				try {
					//read in server responses
					String fileList = br.readLine();
					String msg1 = br.readLine();
					String msg2 = br.readLine();

					//read server responses and prompt user
					System.out.println("Server Responses:");
					System.out.println(fileList);
					System.out.println(msg1);
					System.out.println(msg2);
					
					//set text of textbox
					for(String s :  fileList.split("@")) {
						audioFilesListTxt.appendText(s+"\n");
					}
					alertUser(msg1, msg2);
				} catch (IOException e1) {
					e1.printStackTrace();
					alertUser("Error", "And Error Occured");
				}		
			}
			else {
				alertUser("Error", "You are not logged in!");
			}
		});
		
		//get audio file button
		getAudioFileBtn.setOnAction(e ->{
			if(loggedIn) {				
				try {				
					//request the file
					pr.println("ZEDEMGET " + fileIdTxt.getText());
					downloadFile();	//method to download file
					//read server responses and prompt user
					String msg1 = br.readLine();
					String msg2 = br.readLine();
					alertUser(msg1, msg2);	
				}
				catch(IOException ex) {
					ex.printStackTrace();
					alertUser("Error", "And Error Occured");
				}
			}
			else {
				alertUser("Login Error", "You are not logged in!");
			}
		});
		
		//log off button
		logOffBtn.setOnAction(e ->{
			if(loggedIn) {
				pr.println("ZEDEMBYE");
				try {
					//read server responses and prompt user
					String msg1 = br.readLine();
					String msg2 = br.readLine();
					alertUser(msg1, msg2);
					//close the connection
					if(socket != null) {
						socket.close();
						loggedIn = false;
					}
				} catch (IOException e1) {
					e1.printStackTrace();
					alertUser("Error", "And Error Occured");
				}
			}
			else {
				alertUser("Login Error", "You are not logged in!");
			}
		});
		
		//add all nodes to the VBox
		vbox.getChildren().addAll(
			connectBtn,
			usernameLbl,
			usernameTxt,
			passwordLbl,
			passwordTxt,
			loginBtn,
			fileListLbl,
			audioFilesListTxt,
			getPlaylistBtn,
			fileIdLbl,
			fileIdTxt,
			getAudioFileBtn,
			logOffBtn
			);
		//set VBox padding
		vbox.setPadding(new Insets(10,10,10,10));
	}
	
	/**
	 * get the client's VBox
	 * @return
	 */
	public VBox getVbox() {
		return this.vbox;
	}
	
	/**
	 * prompts the user
	 * @param messageHeader
	 * @param messageContent
	 */
	private void alertUser(String messageHeader, String messageContent) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(messageHeader);
        alert.setHeaderText(messageHeader);
        alert.setContentText(messageContent);
        alert.showAndWait();
	}
	
	//method to download a file
	private void downloadFile() {
		try {
			//read in the file name, size and initialize fos
			String fileName = br.readLine();
			System.out.println("Recieving File: "+fileName);
			FileOutputStream fos = new FileOutputStream(new File("data/client/"+fileName));
			
			int fileSize = Integer.parseInt(br.readLine());
			System.out.println("File Size: "+fileSize);
			
			//create a counter, byte array and totalBytes counter
			int counter = 0;
			int totalBytes = 0;
			byte[] buffer = new byte[1024];
			
			//read in the file
			while(totalBytes!=fileSize) {
				counter = dis.read(buffer, 0, buffer.length);
				fos.write(buffer, 0, counter);
				fos.flush();
				totalBytes+=counter;
			}
			fos.close();
			System.out.println("File Recieved!");
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
		
	}

}
