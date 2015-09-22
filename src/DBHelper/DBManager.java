package DBHelper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;

import Server.ClientThread;

public class DBManager {
	
	public static Connection conn;
	public static Statement stmt;	
	private static final HashMap<String, ClientThread> clients = new HashMap<String, ClientThread>();

	public synchronized static ClientThread findClient(String id) {
		return clients.get(id);
	}

	public synchronized static void loadMap(ClientThread client)
	{
		
				
	}
	//�ٸ� ��븦 ã�� �Լ��̴�.
	public synchronized static void serachOther(ClientThread client)
			throws IOException {
		if (client.getOther() != null) {
			System.out.println("�̹� ������� �˻� �Ǿ����ϴ�.");
			return;
		}
		//���� ���˻��� �ȵǾ������� ��ġ �����Ѵ�.
		System.out.println("��ġ ����");
		//Ŭ���̾�Ʈ�� ��� ���´� true�� �ȴ�.
		client.ready=true;		
		Collection<ClientThread> values = clients.values();
		for (ClientThread other : values) {
			if(other == client || other.ready==false) continue;
			if (other.getOther() != null) continue; //������� �˻����� �ʾҴ����
			client.joinother(other); //Ŭ���̾�Ʈ�� ��������� �־��ش�.
			client.sendMessage(other.u_id+":"+other.sum_number+":"+client.team); //����� ����� ������ �Ѱ��ش�. (���ӿ� �ʿ��� �⺻����)
			client.oos.flush(); //����
			other.sendMessage(client.u_id+":"+client.sum_number+":"+other.team); //���濡�Ե� ���� ����� ������ �Ѱ��ش�. (������ �ʿ��� �⺻����)
			other.oos.flush(); //����
			System.out.println("�˻���� ��ġ �Ϸ�"); //������ ���� ���� �޼��� �˻� ��ٴ°� �˷���		
			
			return;
		}
		System.out.println("���� ��븦 ã���� �����ϴ�.");
	}

	
	public synchronized static boolean putClientGoogle(ClientThread client) {
		if (clients.get(client.googleID) != null)
			return false;
		clients.put(client.googleID, client);
		// new ClientThread(client).start();
		System.out.println("���ο� ������ �����Ͽ����ϴ�." + client);
		System.out.println("Ŭ���ȣ." + client.getNumber());
		System.out.println("���� �������� �ο���." + clients.size());
		return true;
	}
	//������ �����ϰ� �Ǹ� Ŭ���̾�Ʈ�� �߰����ش�. 
	public synchronized static boolean putClient(ClientThread client) {
		if (clients.get(client.id) != null)
			return false;
		clients.put(client.id, client);
		// new ClientThread(client).start();
		System.out.println("���ο� ������ �����Ͽ����ϴ�." + client);
		System.out.println("Ŭ���ȣ." + client.getNumber());
		System.out.println("���� �������� �ο���." + clients.size());
		return true;
	}
	//Ŭ���̾�Ʈ�� ������ �Ǹ� �����忡�� �����ִ� ������ �Ѵ�.
	public synchronized static void removeClient(ClientThread client) {
		clients.remove(client.id);
		System.out.println("���ο� ������ �������ϴ�." + client);
		System.out.println("Ŭ���ȣ." + client.getNumber());
		System.out.println("���� �������� �ο���." + clients.size());
	}
	private static DBManager s_instance;
	public UserInfo m_User;
	public boolean Sign = false;
	public SignConnect DB;

	public void Init() {
		m_User = new UserInfo("", "");
		DB = new SignConnect();
	}

	public static DBManager getInstance() {
		if (s_instance == null) {
			s_instance = new DBManager();
		}
		return s_instance;
	}

}
