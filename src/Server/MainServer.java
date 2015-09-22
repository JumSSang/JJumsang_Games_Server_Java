package Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import DBHelper.DBManager;


public class MainServer {
	 
	public synchronized void removeClient(ClientThread cs) 
	{
		//vClients.remove(cs);
	}
	public synchronized void broadcast(String msg) 
	{
		
	}
	
	
	public synchronized void CreateUser()
	{
		
	}
	
	
	public synchronized void CheckLogin()
	{
		
	}
	
	
	 
	 
	 
	public static void main(String[] aa) throws IOException
	{
		@SuppressWarnings("resource")
		//SignConnect connect_db;
		ServerSocket server = new ServerSocket(7800);		
		// 접속한 클라이언트 목록을 저장한다.
		DBManager.getInstance().Init();			
		
		while(true)
		{
			Socket client = server.accept();				
			new ClientThread(client).start();				
		}

		
		
		
//		int count=0;
//		ServerSocketChannel server = ServerSocketChannel.open();
//	    server.configureBlocking(false);
//		server.bind(new InetSocketAddress(7801));
//		
//		ByteBuffer buffer = ByteBuffer.wrap(new byte[1024]);  
//		
//		System.out.println("꺄르륵");
//		
//		Selector selector = Selector.open();
//	    server.register(selector, SelectionKey.OP_ACCEPT);
//		
//		//소켓 채널 (클래스이름)
//		
//		while(true)
//		{
//			selector.select();
//			Set<SelectionKey> readyKeys = selector.selectedKeys(); //이벤트 가져온다.
//			
//			for(SelectionKey key : readyKeys)
//			{
//				if (key.isAcceptable())  //접속되었을때
//				{
//					SocketChannel client = server.accept();
//					count+=1;
//					System.out.println("누군가가 접속되었습니다");
//					System.out.println(""+client.socket());
//					System.out.println("현재 접속 인원은: "+	count);
//				
//					client.configureBlocking(false);					
//					SelectionKey clientKey = client.register(selector,SelectionKey.OP_WRITE | SelectionKey.OP_READ);
//					Client c = new Client(client);
//					clientKey.attach(c);
//				}
//				else if (key.isReadable()) //클라이언트에게 받을 준비가 되어있는거
//				{
//					Client c = (Client)key.attachment();
//					((SocketChannel)key.channel()).read(buffer);
//					c.pos.write(buffer.array(), 0, buffer.position());
//					buffer.clear();
//					c.read();
//					
//				}
//				else if (key.isWritable()) //클라이언트에게 보낼준비가 되어있는거
//				{
//					Client c = (Client)key.attachment();
//					SocketChannel client = (SocketChannel) key.channel();	
//					
//					//c.SendMsg("바보들");
//					
//				}
//			}
//			readyKeys.clear();
//			
//		}
		
	}

}
