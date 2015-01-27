package com.dcs.project1;

import java.net.*;
import java.io.*;

import static com.dcs.project1.FTPContants.*;

public class MyFtpServer
{
	public static void main(String args[]) throws Exception
	{
		ServerSocket soc=new ServerSocket(5227);
		System.out.println("FTP Server Started on Port Number 5227");
		while(true)
		{
			System.out.println("Waiting for Connection ...");
			transferfile t=new transferfile(soc.accept());

		}
	}
}

class transferfile extends Thread
{
	Socket ClientSoc;

	DataInputStream din;
	DataOutputStream dout;
	static String dir = System.getProperty("user.dir");
	public static final String HARDCODESERVERPATH = dir+"/server_files/";
	transferfile(Socket soc)
	{
		try
		{
			ClientSoc=soc;                        
			din=new DataInputStream(ClientSoc.getInputStream());
			dout=new DataOutputStream(ClientSoc.getOutputStream());
			System.out.println("FTP Client Connected ...");
			start();

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}        
	}

	void SendFile() throws Exception
	{        
		String filename=din.readUTF();
		File f=new File(HARDCODESERVERPATH+filename);
		if(!f.exists())
		{
			dout.writeUTF("File Not Found");
			return;
		}
		else
		{
			dout.writeUTF("READY");
			FileInputStream fin=new FileInputStream(f);
			int ch;
			do
			{
				ch=fin.read();
				dout.writeUTF(String.valueOf(ch));
			}
			while(ch!=-1);    
			fin.close();    
			dout.writeUTF("File received Successfully");                            
		}
	}

	void ReceiveFile() throws Exception
	{
		String filename=din.readUTF();
		if(filename.compareTo("File not found")==0)
		{
			return;
		}
		File f=new File(HARDCODESERVERPATH+filename);
		String option;

		if(f.exists())
		{
			dout.writeUTF("File Already Exists");
			option=din.readUTF();
		}
		else
		{
			dout.writeUTF("SendFile");
			option="Y";
		}

		if(option.compareTo("Y")==0)
		{
			FileOutputStream fout=new FileOutputStream(f);
			int ch;
			String temp;
			do
			{
				temp=din.readUTF();
				ch=Integer.parseInt(temp);
				if(ch!=-1)
				{
					fout.write(ch);                    
				}
			}while(ch!=-1);
			fout.close();
			dout.writeUTF("File Send Successfully");
		}
		else
		{
			return;
		}

	}
	/**
	 * Original @author: Moumita
	 * Edited @author manish for path issues on linux systems 
	 */
	void ListFiles(){
		String file_path;
		String file_name="";
		try {
			file_path = HARDCODESERVERPATH;
			File files= new File(file_path);
			File[] file_list= files.listFiles();
			if(file_list.length==0){
				dout.writeUTF("No files in the current directory..");
				return;
			}
			for(int i=0;i<file_list.length;i++){
				String name= file_list[i].getName();
				file_name+=name+" ";

			}
			dout.writeUTF(file_name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @author manish
	 */
	private void CurrentDir() {
		try {
			String file_path = /*din.readUTF();*/HARDCODESERVERPATH;
			dout.writeUTF(file_path);
		}
		catch(Exception ie){
			ie.printStackTrace();
		}
	}

	/**
	 * @author manish
	 * @throws IOException 
	 */
	private void MakeDir() throws IOException {
		String dirName=din.readUTF();
		File f=new File(HARDCODESERVERPATH+dirName);
		if(f.isDirectory())
		{
			dout.writeUTF("Directory already exhists");
			return;
		}
		f.mkdir();
		dout.writeUTF("Directory Created  successfully!");

	}


	/**
	 * @throws IOException 
	 * @@author manish
	 */
	private void DeleteFile() throws IOException  {    
		String filename=din.readUTF();
		File f=new File(HARDCODESERVERPATH+filename);
		if(!f.exists())
		{
			dout.writeUTF("File Not Found");
			return;
		}
		f.delete();
		dout.writeUTF("File deleted successfully!");
	}

	/**
	 * @throws IOException 
	 * 
	 */

	private void ChangeDir() throws IOException {
		// TODO Auto-generated method stub
		String folderName = din.readUTF();
		if(folderName.equals("..")){
			
		}
		else{
			File f=new File(HARDCODESERVERPATH+folderName);
			if(!f.exists())
			{
				dout.writeUTF("Directory Not Found");
				return;
			}
		// Some code should go there , can not figure out what yet. Leaving for now
		}
	}

	/**
	 * @author manish
	 * Will work as a director method to respective methods 
	 */
	public void run()
	{
		while(true)
		{
			try
			{
				System.out.println("Waiting for Command ...");
				String Command=din.readUTF();
				if(Command.equalsIgnoreCase("GET"))
				{
					System.out.println("\tGET Command Received ...");
					SendFile();
					continue;
				}
				else if(Command.equalsIgnoreCase("PUT"))
				{
					System.out.println("\tPUT Command Receiced ...");                
					ReceiveFile();
					continue;
				}
				else if(Command.equalsIgnoreCase("DISCONNECT"))
				{
					System.out.println("\tDisconnect Command Received ...");
					System.exit(1);
				}
				else if(Command.equalsIgnoreCase("LS"))
				{
					System.out.println("\tLS Command Received ...");
					ListFiles();
					continue;
				}
				else if(Command.equalsIgnoreCase("PWD"))
				{
					System.out.println("\tPWD Command Received ...");
					CurrentDir();
					continue;
				}

				else if(Command.equalsIgnoreCase("MKDIR"))
				{
					System.out.println("\tMKDIR Command Received ...");
					MakeDir();
					continue;
				}

				else if(Command.equalsIgnoreCase("DELETE"))
				{
					System.out.println("\t DELETE Command Received ...");
					DeleteFile();
					continue;
				}
				else if(Command.equalsIgnoreCase("CD"))
				{
					System.out.println("\t CD Command Received ...");
					ChangeDir();
					continue;
				}

			}
			catch(Exception ex)
			{
				System.out.println("In the run loop exception");
				ex.printStackTrace();
			}
		}
	}

}
