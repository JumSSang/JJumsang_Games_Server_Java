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
		// ������ Ŭ���̾�Ʈ ����� �����Ѵ�.
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
//		System.out.println("������");
//		
//		Selector selector = Selector.open();
//	    server.register(selector, SelectionKey.OP_ACCEPT);
//		
//		//���� ä�� (Ŭ�����̸�)
//		
//		while(true)
//		{
//			selector.select();
//			Set<SelectionKey> readyKeys = selector.selectedKeys(); //�̺�Ʈ �����´�.
//			
//			for(SelectionKey key : readyKeys)
//			{
//				if (key.isAcceptable())  //���ӵǾ�����
//				{
//					SocketChannel client = server.accept();
//					count+=1;
//					System.out.println("�������� ���ӵǾ����ϴ�");
//					System.out.println(""+client.socket());
//					System.out.println("���� ���� �ο���: "+	count);
//				
//					client.configureBlocking(false);					
//					SelectionKey clientKey = client.register(selector,SelectionKey.OP_WRITE | SelectionKey.OP_READ);
//					Client c = new Client(client);
//					clientKey.attach(c);
//				}
//				else if (key.isReadable()) //Ŭ���̾�Ʈ���� ���� �غ� �Ǿ��ִ°�
//				{
//					Client c = (Client)key.attachment();
//					((SocketChannel)key.channel()).read(buffer);
//					c.pos.write(buffer.array(), 0, buffer.position());
//					buffer.clear();
//					c.read();
//					
//				}
//				else if (key.isWritable()) //Ŭ���̾�Ʈ���� �����غ� �Ǿ��ִ°�
//				{
//					Client c = (Client)key.attachment();
//					SocketChannel client = (SocketChannel) key.channel();	
//					
//					//c.SendMsg("�ٺ���");
//					
//				}
//			}
//			readyKeys.clear();
//			
//		}
		
	}

}
