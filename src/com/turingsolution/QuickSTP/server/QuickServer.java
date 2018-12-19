package com.turingsolution.QuickSTP.server;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class QuickServer
		extends Thread {

	private Socket socket;
	
	public QuickServer() {
	}
	
	public QuickServer(Socket socket) {
		this.socket = socket;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}



	public static void main(String[] args) {

		
		ServerSocket server = null;
		Socket sock = null;
		int SERVER_PORT = 10002;
		
		
		if( args.length != 1 ) {
			System.out.println("Usage: java -jar turingsolution-quickstp.jar {port}");
			System.exit(1);
		}
		
		// 포트 설정하기 
		String port = args[0];
		SERVER_PORT = Integer.parseInt(port);
		
		
 		try {
			server = new ServerSocket(SERVER_PORT);
			
			while(true) {
				synchronized(server) {
					System.out.println("QuickServer. Wating New Connect ..");
					sock = server.accept();
					System.out.println("socket accept");
				}
				
				new QuickServer(sock).start();
				System.out.println("thread start");
					
//				new QuickServer(sock).run();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	
	public void run() {
		Socket sock = getSocket();

		DataInputStream dis = null;
		DataOutputStream dos = null;
		
		BufferedOutputStream fout = null;
		
		try {
			InetAddress  inetaddr = sock.getInetAddress();
			System.out.println(inetaddr.getHostAddress()+ " is connected.");

			OutputStream out = sock.getOutputStream();
			InputStream in = sock.getInputStream();

			dis = new DataInputStream(in);
			dos = new DataOutputStream(out);
			
			
			String line = null;
			if((line = dis.readUTF()) != null ) {
				System.out.println("[from "+inetaddr.getHostAddress()+"] "+line);
				String FILE_PATH = "c:\\temp\\";	//TODO properties
				String fileName = "";
//				String fileSize = "";
				
				if(line.startsWith("put")) {
					String[] lines = line.split(" ");
					fileName = lines[1];
//					fileSize = lines[2];
//					long fSize = Long.parseLong(fileSize);
					
					fout = new BufferedOutputStream(
							new FileOutputStream(new File(fileName)));
//							new FileOutputStream(new File(FILE_PATH,fileName)));

					int BUFFER_SIZE = 8192;	//TODO properties
					byte[] bytes = new byte[BUFFER_SIZE];
			        long left = dis.readLong();
			        int bytesRead = 0;
					long sum = 0;

					System.out.println("file size is "+left+" bytes.");
			        while(left > 0 && (bytesRead = dis.read(bytes, 0, (int)Math.min(left, (long)bytes.length))) > 0) {
			        	fout.write(bytes, 0, bytesRead);
			            left -= bytesRead;
			            
						fout.flush();
						
						sum += bytesRead;
//						System.out.print("#");
//						System.out.println("fileSize:"+fSize+", sum:"+sum+", bytesRead:"+bytesRead + " left: " + left);
			        }
			        
					System.out.println("received total size is "+sum+" bytes.");
			        
					fout.close();
				}
				else if(line.startsWith("!ls") || line.startsWith("!dir")) {
					String[] lines = line.split(" ");
					FILE_PATH = lines[1];
					
					File filePath = new File(FILE_PATH);
					if( filePath.exists() && filePath.isDirectory() ) {
						String[] flist = filePath.list();
						int fleng = flist.length;

						System.out.println("remote path is "+filePath.getPath());
						System.out.println("file count is "+fleng);
						//리모트 경로
//						dos.writeUTF(filePath.getPath());
						//파일 개수 
						dos.writeInt(fleng);
						
						System.out.println("flist array is "+flist.length);
						for(int i=0; i<fleng; i++) {
							dos.writeUTF(flist[i]);
						}
						
					} else {
						System.out.println("remote path no exists");
					}
				}
				else if(line.startsWith("get")) {
					String[] lines = line.split(" ");
					fileName = lines[1];
					File localFile = new File(fileName);
//					File localFile = new File(FILE_PATH,fileName);
					
					if( localFile.exists() ) {
						dos.writeUTF("yes");
						
						System.out.println(localFile.getPath()+". Start transfering to Local");

						FileInputStream fin = new FileInputStream(localFile);
						int BUFFER_SIZE = 8192;	//TODO properties
						byte[] buff = new byte[BUFFER_SIZE];
						int bytesRead = 0;
						long left = (long) fin.getChannel().size();
						System.out.println("file size is "+left+" bytes.");
						dos.writeLong(left);
						while((bytesRead = fin.read(buff)) > 0) {
							dos.write(buff, 0, bytesRead);
							dos.flush();
							//System.out.print("#");
						}
						fin.close();
					} else {
						dos.writeUTF("no");
					}
					
				}
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fout.close();
				
				dos.close();
				dis.close();
				sock.close();
			}catch(Exception e) {}
			
			System.out.println("end job");
		}
	}
}
