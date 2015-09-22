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
	//다른 상대를 찾는 함수이다.
	public synchronized static void serachOther(ClientThread client)
			throws IOException {
		if (client.getOther() != null) {
			System.out.println("이미 상대편이 검색 되었습니다.");
			return;
		}
		//만약 상대검색이 안되어있으면 서치 시작한다.
		System.out.println("서치 시작");
		//클리이언트의 대기 상태는 true가 된다.
		client.ready=true;		
		Collection<ClientThread> values = clients.values();
		for (ClientThread other : values) {
			if(other == client || other.ready==false) continue;
			if (other.getOther() != null) continue; //상대편이 검색되지 않았더라면
			client.joinother(other); //클라이언트의 상대정보를 넣어준다.
			client.sendMessage(other.u_id+":"+other.sum_number+":"+client.team); //상대의 썸네일 정보를 넘겨준다. (게임에 필요한 기본정보)
			client.oos.flush(); //갱신
			other.sendMessage(client.u_id+":"+client.sum_number+":"+other.team); //상대방에게도 나의 썸네일 정보를 넘겨준다. (게임의 필요한 기본정보)
			other.oos.flush(); //갱신
			System.out.println("검색상대 서치 완료"); //서버에 띄우기 위한 메세지 검색 됬다는걸 알려줌		
			
			return;
		}
		System.out.println("접속 상대를 찾을수 없습니다.");
	}

	
	public synchronized static boolean putClientGoogle(ClientThread client) {
		if (clients.get(client.googleID) != null)
			return false;
		clients.put(client.googleID, client);
		// new ClientThread(client).start();
		System.out.println("새로운 유저가 접속하였습니다." + client);
		System.out.println("클라번호." + client.getNumber());
		System.out.println("현재 접속중인 인원은." + clients.size());
		return true;
	}
	//유저가 접속하게 되면 클라이언트를 추가해준다. 
	public synchronized static boolean putClient(ClientThread client) {
		if (clients.get(client.id) != null)
			return false;
		clients.put(client.id, client);
		// new ClientThread(client).start();
		System.out.println("새로운 유저가 접속하였습니다." + client);
		System.out.println("클라번호." + client.getNumber());
		System.out.println("현재 접속중인 인원은." + clients.size());
		return true;
	}
	//클라이언트가 나가게 되면 쓰레드에서 지워주는 역할을 한다.
	public synchronized static void removeClient(ClientThread client) {
		clients.remove(client.id);
		System.out.println("새로운 유저가 나갔습니다." + client);
		System.out.println("클라번호." + client.getNumber());
		System.out.println("현재 접속중인 인원은." + clients.size());
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
