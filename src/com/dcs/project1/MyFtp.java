package com.dcs.project1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import static com.dcs.project1.FTPContants.*;


public class MyFtp {
	static Socket ClientSoc;

	static DataInputStream din;
	static DataOutputStream dout;
	static BufferedReader br;
	InputStreamReader in;
	static String dir = System.getProperty("user.dir");
	public static final String HARDCODEPATH = dir+"/client_files/";
	public static void main(String[] args) throws IOException {
		String ipPortparam;
		String[] ipPort = null;
		MyFtp clientObj = new MyFtp();
		System.out.println("Enter IP/hostname of server and port in <IP PORT> format");
		BufferedReader bRead  = new BufferedReader(new InputStreamReader(System.in));
		ipPortparam = bRead.readLine();
		ipPort = ipPortparam.split(DELIMETER);
		String IP = ipPort[0];
		String port = ipPort[1];
		System.out.println("Going to connect Machine:"+IP+ "at port : "+port);
		checkConnection(IP ,port);
		clientObj.displayMenu();
	}

	private static  void checkConnection(String ip , String port) throws UnknownHostException, IOException {

		Socket soc = new Socket(ip,5227);
		ClientSoc = soc;
		din=new DataInputStream(ClientSoc.getInputStream());
		dout=new DataOutputStream(ClientSoc.getOutputStream());
		br=new BufferedReader(new InputStreamReader(System.in));
	}

	private  void displayMenu() throws IOException {
		//dout=new DataOutputStream(ClientSoc.getOutputStream());
		while(true)
		{    
			//System.out.println("[USAGE HELP]myftp> command <file_Name>");
			//System.out.println("*************************************");
			System.out.print("myftp>");
			String[] commandWidArg = null;
			String command;
			br = new BufferedReader(new InputStreamReader(System.in));
			command = br.readLine();
			System.out.println(command);
			
//			if((command.contains("GET")) || (command.contains("PUT")) || (command.contains("DELETE")) || (command.contains("CD")) || (command.contains("MKDIR")))
//			{
				commandWidArg = command.split(DELIMETER);
				command = commandWidArg[0];
			//}

			if(command.equalsIgnoreCase(PUT_FILE))
			{
				dout.writeUTF(PUT_FILE);
				put_Files(commandWidArg[1]);
			}
			else if(command.equalsIgnoreCase(GET_FILE))
			{
				dout.writeUTF(GET_FILE);
				get_Files(commandWidArg[1]);
			}

			else if(command.equalsIgnoreCase(DELETE_FILE))
			{
				dout.writeUTF(DELETE_FILE);
				deleteFile(commandWidArg[1]);
			}

			else if(command.equalsIgnoreCase(LIST_FILES))
			{
				dout.writeUTF(LIST_FILES);
				listFiles();
			}

			else if(command.equalsIgnoreCase(CHANGE_DIR))
			{
				dout.writeUTF(CHANGE_DIR); // may be list here first so user knows where he wants to change to
				changeDir(commandWidArg[1]);
			}

			else if(command.equalsIgnoreCase(CREATE_DIR))
			{ // print the correct path so user knows where is he creating the dir
				dout.writeUTF(CREATE_DIR);
				createDir(commandWidArg[1]);
			}

			else if(command.equalsIgnoreCase(CURRENT_PATH))
			{
				dout.writeUTF(CURRENT_PATH);
				printCurrDir();
			}

			else
			{
				dout.writeUTF("DISCONNECT");
				System.exit(1);
			}
		}

	}




	private static void printCurrDir() {
		try {
			String message=din.readUTF();
			System.out.println(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createDir(String dirName) throws IOException {
		dout.writeUTF(dirName);
		try {
			String message=din.readUTF();
			System.out.println(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void changeDir(String dirName) throws IOException {
		dout.writeUTF(dirName);
		try {
			String message=din.readUTF();
			System.out.println(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void listFiles() {
		// TODO Auto-generated method stub
		try {
			String message=din.readUTF();
			System.out.println(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * @author manish
	 * @param fileName
	 * @throws IOException 
	 */
	private static void deleteFile(String fileName) throws IOException {
		dout.writeUTF(fileName);
		try {
			String message=din.readUTF();
			System.out.println(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static void get_Files(String fileName) {

		try {
			dout.writeUTF(fileName);
			String message=din.readUTF();
			if(message.equalsIgnoreCase("File not found")){
				System.out.println("File not found");
				return;
			}
			else{
				File file=new File(HARDCODEPATH+fileName);
				if(file.exists())
				{
					String user_command;
					System.out.println("File already exists. Do you want to overwrite (Y/N) ?");
					user_command=br.readLine();            
					if(user_command.equalsIgnoreCase("N"))    
					{
						dout.flush();
						return;    
					}                
				}
				FileOutputStream fstream=new FileOutputStream(file);
				int i=0;
				String text;
				while(i!=-1){
					text=din.readUTF();
					i=Integer.parseInt(text);
					if(i!=-1)
					{
						fstream.write(i);                    
					}
				}

				fstream.close();
				System.out.println(din.readUTF());
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private static void put_Files(String fileName){

		try {

			File file=new File(HARDCODEPATH+fileName);
			if(!file.exists())
			{
				System.out.println("File does not exist...");
				dout.writeUTF("File not found");
				return;
			}
			dout.writeUTF(fileName);
			String message=din.readUTF();
			if(message.equalsIgnoreCase("File already exists"))
			{
				String read_command;
				System.out.println("File already exists. Do you want to overwrite (Y/N) ?");
				read_command=br.readLine();            
				if(read_command.equalsIgnoreCase("Y"))    
				{
					dout.writeUTF("Y");
				}
				else
				{
					dout.writeUTF("N");
					return;
				}

			}
			FileInputStream fstream=new FileInputStream(file);
			int i=0;
			while(i!=-1){
				i=fstream.read();
				dout.writeUTF(String.valueOf(i));
			}
			fstream.close();
			System.out.println(din.readUTF());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
