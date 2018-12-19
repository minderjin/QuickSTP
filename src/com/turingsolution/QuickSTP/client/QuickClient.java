package com.turingsolution.QuickSTP.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

public class QuickClient {

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getRemoteDir() {
		return remoteDir;
	}

	public void setRemoteDir(String remoteDir) {
		this.remoteDir = remoteDir;
	}

	public String getLocalDir() {
		return localDir;
	}

	public void setLocalDir(String localDir) {
		this.localDir = localDir;
	}

	private String serverIp;
	private int serverPort;
	private String remoteDir;
	private String localDir = "c:\\temp\\get";

	public static void main(String[] args){
		QuickClient client = new QuickClient();
		int argLeng = args.length;
		if( argLeng == 0 ) {
			client.setServerIp("127.0.0.1");
			client.setServerPort(10002);
			client.setRemoteDir("c:\\temp\\");
			client.run();
		}
		else if( argLeng == 2 ) {
			client.setServerIp(args[0]);
			client.setServerPort(Integer.parseInt(args[1]));
			client.run();
		}
		else if( argLeng == 1 ) {

			String configFileName = args[0];
			File file = new File(configFileName);
			if( !file.exists() ) {
				System.out.println(configFileName+" not found.");
				System.exit(1);
			}
			
			
			Properties prop = new Properties();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(configFileName);
				prop.load(fis);
				
				String serverIp = prop.getProperty("serverip");
				String serverPort = prop.getProperty("serverport");
				String method = prop.getProperty("method");
				String remoteDir = prop.getProperty("remotedir");
				String localDir = prop.getProperty("localdir");
				String fileName = prop.getProperty("filename");

				if( serverIp==null || "".equals(serverIp) ) {
					System.out.println("serverip not setting");
					System.exit(1);
				}
				if( serverPort==null || "".equals(serverPort) ) {
					System.out.println("serverport not setting");
					System.exit(1);
				}
				if( method==null || "".equals(method) ) {
					System.out.println("method not setting");
					System.exit(1);
				}
				if( remoteDir==null || "".equals(remoteDir) ) {
					System.out.println("remotedir not setting");
					System.exit(1);
				}
				if( localDir==null || "".equals(localDir) ) {
					System.out.println("localdir not setting");
					System.exit(1);
				}
				if( fileName==null || "".equals(fileName) ) {
					System.out.println("filename not setting");
					System.exit(1);
				}
				
				
				//세팅 
				client.setServerIp(serverIp);
				client.setServerPort(Integer.parseInt(serverPort));
				client.setRemoteDir(remoteDir);
				client.setLocalDir(localDir);

				method = method.toLowerCase();

				//
				if( !"get".equals(method) && !"put".equals(method) ) {
					showProgUsage();
					System.exit(1);
				} else if( "put".equals(method) ) {
					try {
						client.putFileToServer("put "+client.getRemoteDir()+"/"+fileName, fileName);
						
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if( "get".equals(method) ) {
					try {
						client.getFileFromServer("get "+client.getRemoteDir()+"/"+fileName, fileName);
						
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			} catch (IOException e) {
			          e.printStackTrace();
			} finally {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		else if( argLeng == 6 ) {
			client.setServerIp(args[0]);
			client.setServerPort(Integer.parseInt(args[1]));
			client.setRemoteDir(args[3]);
			client.setLocalDir(args[4]);
			
			String method = args[2].toLowerCase();
			String fileName = args[5];

//			System.out.println("serverIp: "+client.getServerIp());
//			System.out.println("serverPort: "+client.getServerPort());
//			System.out.println("method: "+method);
//			System.out.println("remoteDir: "+client.getRemoteDir());
//			System.out.println("localDir: "+client.getLocalDir());
//			System.out.println("fileName: "+fileName);

			if( !"get".equals(method) && !"put".equals(method) ) {
				showProgUsage();
				System.exit(1);
			} else if( "put".equals(method) ) {
				try {
					client.putFileToServer("put "+client.getRemoteDir()+"/"+fileName, fileName);
					
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if( "get".equals(method) ) {
				try {
					client.getFileFromServer("get "+client.getRemoteDir()+"/"+fileName, fileName);
					
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
		} else {
			// 잘못된 파라메터값 입력
			showProgUsage();
		}
	
	}

	private static void showProgUsage() {
		System.out.println("Usage:  java com.turingsolution.QuickSTP.client.QuickClient {serverip} {serverport} {GET|PUT} {remotedir} {localdir} {filename}");
		System.out.println("        java com.turingsolution.QuickSTP.client.QuickClient {serverip} {serverport}");
	}

	
	private void run() {
		try{
			showMessage("Welcome to the QuickSTP b0.1");

			BufferedReader keyboard =
					new BufferedReader(new InputStreamReader(System.in));
			
			String line = null;
			while((line = keyboard.readLine()) != null){
				line = line.trim();
				if(line.equals("quit") || line.equals("exit")) 
					break;
				
				if("".equals(line)) {
					showPrompt();
					continue;
				}
				if("help".equals(line) || "?".equals(line)) {
					showCommandList();
					continue;
				}
				if(line.equals("pwd")) {
					showMessage(getLocalDir());
					continue;
				}
				if(line.equals("!pwd")) {
					showMessage(getRemoteDir());
					continue;
				}
				if(line.startsWith("cd")) {
					String[] lines = line.split(" ");
					if(lines.length!=2) {
						showMessage("입력값이 잘못되었습니다.");
						continue;
					}
					setLocalDir(lines[1]);
					showMessage("경로가 "+getLocalDir()+" 으로 변경되었습니다.");
					continue;
				}
				if(line.startsWith("!cd")) {
					String[] lines = line.split(" ");
					if(lines.length!=2) {
						showMessage("입력값이 잘못되었습니다.");
						continue;
					}
					setRemoteDir(lines[1]);
					showMessage("원격 대상경로가 "+getRemoteDir()+" 으로 변경되었습니다.");
					continue;
				}
				if(line.equals("ls") || line.equals("dir")) {
					File f = new File(localDir);
					if(f.exists() && f.isDirectory()){
						String[] flist = f.list();
						for(int i=0; i<flist.length; i++) 
							System.out.println(flist[i]);
					} else {
						showMessage("경로가 옳지 않습니다. cd 명령으로 변경해주세요.\n"
								+"현재 설정된 경로는 "+localDir+" 입니다.");
					}
					showPrompt();
					continue;
				}
				if(line.startsWith("put")) {
					String[] lines = line.split(" ");
					if(lines.length!=2) {
						showMessage("입력값이 잘못되었습니다.");
						continue;
					}
					String fileName = lines[1];
					String cmd = "put "+getRemoteDir()+"/"+fileName;
					putFileToServer(cmd,fileName);
//					putFileToServer(line,fileName);
					
					continue;
				}
				if(line.equals("!ls") || line.equals("!dir") ) {
					String cmd = line+" "+getRemoteDir();
					getServerFileList(cmd);
					continue;
				}
				if(line.startsWith("get")) {
					String[] lines = line.split(" ");
					if(lines.length!=2) {
						showMessage("입력값이 잘못되었습니다.");
						continue;
					}
					String fileName = lines[1];
					String cmd = "get "+getRemoteDir()+"/"+fileName;
					getFileFromServer(cmd,fileName);
//					putFileToServer(line,fileName);

					continue;
				}
				
				showMessage("명령어가 틀렸습니다. 아래 리스트를 확인해주세요.");
				showCommandList();
			}


		}catch(Exception e){
			System.out.println(e);
		}
	}

	/**
	 * 파일 가져오기 
	 * 
	 * @param line
	 */
	private void getFileFromServer(String line, String fileName) throws UnknownHostException, IOException {
		String serverIp = getServerIp();
		int serverPort = getServerPort();
		
		Socket sock = new Socket(serverIp, serverPort);

		OutputStream out = sock.getOutputStream();
		InputStream in = sock.getInputStream();

		DataOutputStream dos = new DataOutputStream(out);
		DataInputStream dis = new DataInputStream(in);
		
		// 명령 전송
		dos.writeUTF(line);

		String existFile = dis.readUTF();
		if( "yes".equals(existFile) ) {

			FileOutputStream fout = new FileOutputStream(new File(localDir,fileName));

			int BUFFER_SIZE = 8192;	//TODO properties
			byte[] bytes = new byte[BUFFER_SIZE];
	        long left = dis.readLong();
			long sum = 0;
			System.out.println("file size is "+left+" bytes.");

			// 파일 받기 
			int bytesRead;
	        while(left > 0 && (bytesRead = in.read(bytes, 0, (int)Math.min((long)left, bytes.length))) > 0) {
	        	fout.write(bytes, 0, bytesRead);
				fout.flush();
				
	            left -= bytesRead;
				sum += bytesRead;
				
				System.out.print("#");
				//System.out.println("fileSize:"+fSize+", sum:"+sum+", bytesRead:"+bytesRead);
	        }
	        System.out.println("");
			fout.close();
			
			
			System.out.println("received total size is "+sum+" bytes.");
			showMessage("Download completed.");
			
		} else {//no
			showMessage("No file found from remote");
		}

		// 자원 해제 
		dis.close();
		dos.close();
		sock.close();
		
	}

	/**
	 * 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	private void getServerFileList(String line) throws UnknownHostException, IOException {

		String serverIp = getServerIp();
		int serverPort = getServerPort();
		
		Socket sock = new Socket(serverIp, serverPort);

		OutputStream out = sock.getOutputStream();
		InputStream in = sock.getInputStream();

		DataOutputStream dos = new DataOutputStream(out);
		DataInputStream dis = new DataInputStream(in);
		
		// 명령 서버에 전달 
		dos.writeUTF(line);
		
		// 결과 받기
//		String remotePath = dis.readUTF();
		int fileCnt = dis.readInt();
		

		System.out.println("remote path  "+getRemoteDir());
		System.out.println("");

		for(int i=0; i<fileCnt; i++)
			System.out.println(dis.readUTF());

		showMessage("total "+fileCnt);

		// 자원 해제 
		dos.close();
		dis.close();
		sock.close();
		
	}

	private void putFileToServer(String line, String fileName)
			throws UnknownHostException, IOException, FileNotFoundException {

		String serverIp = getServerIp();
		int serverPort = getServerPort();
		
		
		File localFile = new File(localDir,fileName);
		if( !localFile.exists() ) {
			showMessage("No file found.");		
		} else {
			
			long fSize = localFile.length();
			Socket sock = new Socket(serverIp, serverPort);
			OutputStream out = sock.getOutputStream();
			DataOutputStream bout = new DataOutputStream(out);
			
			// 서버에 명령 전달 
			bout.writeUTF(line+" "+fSize);
			System.out.println(localFile.getPath()+". Start transfering to Server");

			// 파일 읽기 
			FileInputStream fin = new FileInputStream(localFile);
			int BUFFER_SIZE = 8192;	//TODO properties
			byte[] buff = new byte[BUFFER_SIZE];
			int bytesRead = 0;
			long left = (long) fin.getChannel().size();
			System.out.println("file size is "+left+" bytes.");
			
			// 파일 size 서버에 알려주기 
			bout.writeLong(left);
			
			// 파일 전송 
			while((bytesRead = fin.read(buff)) > 0) {
				bout.write(buff, 0, bytesRead);
				bout.flush();
				System.out.print("#");
			}
			
			// 자원 해제 
			fin.close();
			bout.close();
			sock.close();

			showMessage("Upload Completed.");
		}
	}

	/**
	 * 
	 */
	private void showPrompt() {
		System.out.print("QuickSTP> ");
	}

	private void showMessage(String msg) {
		System.out.println("");
		System.out.println(msg);
	}

	/**
	 *  
	 */
	private void showCommandList() {
		System.out.println("Command list");
		System.out.println("              quit | exit");
		System.out.println("              pwd");
		System.out.println("              cd {filepath}");
		System.out.println("              ls | dir");
		System.out.println("              !ls | !dir");
		System.out.println("              put {filename}");
		System.out.println("              get {filename}");
		showPrompt();
	}
}
