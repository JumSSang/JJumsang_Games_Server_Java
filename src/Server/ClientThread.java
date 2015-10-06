package Server;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import Value.NetState;
import Value.UnitValue;
import DBHelper.DBManager;
import DBHelper.MyCard;

class UserInfo {
	public String id;
	public String u_id; //uid 를 잘 모를때 만든 변수
	public String level; //레벨
	public String gold;
	public String victory;
	public String cash;
	public String sum_number;
	public String guild;
	public String user_uid; //진짜  uid 이다
	UserInfo() {

	}
}


public class ClientThread extends Thread {
	private static int numberCounter = 0;
	public int myFrame = 0;
	public final ObjectOutputStream oos;
	public final ObjectInputStream ois;
	public String googleID;
	public String clientAct = null;
	public MyCard mycards;
	
	
	private static Connection con; 
	 private static Statement stmt; 
	
	public String id;
	public String u_id;
	public String level;
	public String gold;
	public String victory;
	public String cash;
	public String sum_number;
	public String user_uid; 
	public String guild;
	
	
	public int m_single_mapnumber = 0;
	public String m_Singlemap;
	public String m_ClientSettingMap;
	private int state = NetState.LOGIN;
	public int team = 0;
	private boolean game_start = false;

	
	public float myFrameCount = 0;
	private final Socket socket;
	private ClientThread other = null;
	private int channel = 0;
	private final int number;
	public boolean ready = false;
	// /////////
	PrintWriter out = null;
	BufferedReader in = null;


	// ///
	public ClientThread getOther() {
		return other;
	}

	public void setClient() {

	}

	public ClientThread(Socket socket) throws IOException {
		super("Client Thread ");
		this.socket = socket;
		this.number = numberCounter++;
		mycards=new MyCard();
		ois = new ObjectInputStream(socket.getInputStream());
		oos = new ObjectOutputStream(socket.getOutputStream());
	
	}

	public synchronized void sendMessage(String msg) throws IOException

	{
		oos.writeUTF(msg);
		oos.flush();
	}

	// 다른 사람이 들어온다면 호출 해라
	public synchronized void joinother(ClientThread a) {
		if (other != null) {

		} else {
			this.team = 1;

			other = a;
			other.team = 2;
			// other.joinother(this);

			this.state = NetState.MUTI_TRUN_READY;
			other.state = NetState.MUTI_TRUN_READY;

		}
	}

	// 맵정보를 불러오는 애이다.
	public void mapLoader(int mapnumber) {

		m_single_mapnumber = mapnumber;
		DBManager.getInstance().DB.getMap(this);
	}

	// 맵정보를 디비에 책임지고 기록해주는 함수이다.
	public void mapSetting(String a) {
		m_ClientSettingMap = a;
		DBManager.getInstance().DB.setMap(this);

	}
	public int getLv(String a)
	{
		String[] temp=null;
		temp=a.split("a");
		return Integer.parseInt(temp[0]);		
	}
	
	//이 함수는 유저가 돈이 충분하지 판단해주고 충분하다면 구매진행하여 골드를 차감해주는 역할을 한다.
	public boolean buyUnit(int a) throws IOException
	{
		int money=Integer.parseInt(gold);
		if(money-a>=0)
		{				
				System.out.println("DB SUCEES");
				int result=money-a;
				this.gold=""+result;
				try {
					DBManager.getInstance().stmt.executeUpdate("update km_user set USER_GOLD="+this.gold+" where USER_UID="+user_uid);			
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			return true;
		}
		else
		{				
				this.sendMessage("0:골드부족:"+this.gold+":"+this.cash);			
				return false;
		}
	}

	public synchronized void ListrenReady() throws IOException,
			InterruptedException, ClassNotFoundException {

		
		switch(state)
		{
		case NetState.LOADING:
			String a = ois.readObject().toString();
			if (a.equals("uid_request")) {
				System.out.println("리퀘스트 요청 승인");
				Thread.sleep(3000);
				sendMessage(u_id + ":" + gold + ":" + cash + ":" + level + ":"
						+ victory + ":" + sum_number + ":" + guild +":"+mycards.anna +":"+mycards.archer+":"+mycards.archertower+":"+mycards.boom+":"+mycards.jumping+":"+
						mycards.knight+":"+mycards.magictower+":"+mycards.maigician+":"+mycards.orc+":"+mycards.worrior);
				// 닉네임 골드 캐시 레벨 승리 횟수,썸네일번호,길드 순서대로 보낸다.

				// 썸네일,승리 ,캐시,골드,닉네임,레벨,길드
				System.out.println("uid_보냄");
				oos.flush();
				state = NetState.READY;
			}
			break;
			
		case NetState.SHOP:
			Shop();
			break;		
			
		case NetState.STORY:
			System.out.println("상태는 싱글!!");
			String d = ois.readObject().toString();

			mapLoader(1);

			if (m_Singlemap != null) {
				System.out.println(m_Singlemap);
				sendMessage(m_Singlemap);
				if (id.equals("go7072")) {
					state = 10; // go7072면 맵 에디터로 이동
				} // else

				state = 10; // 7번이면 게임진행으로 이동
			}

			break;
			
		case NetState.READY:
			
			// 클라이언트가 대기방 상태면 이쪽으로 들어간다.
			System.out.println("로비룸으로 접속");
			String b = ois.readObject().toString();
			if (b.equals("서치모드")) {
				SearchMode();
			}
			if (b.equals("서치취소")) {
				ready = false;
			}
			if (b.equals("에디터모드")) {
				state = 4;
			}
			if (b.equals("싱글게임")) {
				state = 9;
				}
			if(b.equals("상점모드"))
			{
				state=3;
			}

			
			break;
			
		case NetState.MULTIGAMESTART:
			String sendPacket = null;
			// System.out.println(""+id+"의 프레임카운터");
			if (myFrameCount == other.myFrameCount) {

				if (clientAct != null || other.clientAct != null) {
					if (clientAct != null) {
						other.sendMessage("nextFrame:" + clientAct);
						this.sendMessage("nextFrame:" + clientAct);
						clientAct = null;

					}
					if (other.clientAct != null) {
						other.sendMessage("nextFrame:" + other.clientAct);
						this.sendMessage("nextFrame:" + other.clientAct);
						other.clientAct = null;

					}
				} else {
					other.sendMessage("nextFrame:" + null);
					this.sendMessage("nextFrame:" + null);
					// clientAct=null;
					// other.clientAct=null;
				}

			} else if (myFrameCount < other.myFrameCount) {

				System.out.println("기다리고있는거 안보이냐 빨리이동헤 ,,");
				sendMessage("nextFrame:" + null);
			} else if (myFrameCount > other.myFrameCount) {

				System.out.println("기다리고있는거 안보이냐 빨리이동헤 ,,");
				// other.sendMessage("nextFrame:"+null);
			}
			state=NetState.MULTIGAME;

			break;
			
		case NetState.MULTIGAME:
			String stringFrame = ois.readObject().toString();

			String[] result = stringFrame.split(":");
			System.out.println("" + stringFrame);
			String checkSum = result[0];
			myFrameCount = Integer.parseInt(result[1]);
			// System.out.println(""+checkSum);
			if (!result[2].equals("null")) {
				clientAct = result[2];
				// System.out.println(""+clientAct);
			}

			break;
			
		case NetState.MAPEDIT:
			String e = null;
			e = ois.readObject().toString();
			System.out.println("맵정보 불러옴");
			System.out.println(e);
			mapSetting(e);
			if (e != null) {
				state = NetState.ROBBY; // 다시 로비 대기 상태로 변경한다.
				m_Singlemap = null;
			}

			break;
		case NetState.MUTI_TRUN_READY:
			// 턴제 멀티 모드
			String es = ois.readObject().toString();
			System.out.println("dd" + es);
			if (es.equals("START")) {
				sendMessage("30");
				state = 12;
			}
			if (es.equals("승리")) {
				state = 2;
			}
			if (es.equals("패배")) {
				state = 2;
			}
			break;
		case NetState.MUTI_TRUN:
			// 턴제 멀티게임 2번째 상태

			String StringGameData = ois.readObject().toString();
			if (!StringGameData.equals("START")) {
				String[] stain = StringGameData.split(":");
				System.out.println("current packet" + StringGameData);
				String start = stain[0];

				if (this.game_start == true && other.game_start == true) {
					this.sendMessage(other.clientAct);
					other.sendMessage(clientAct);
					System.out.println("" + this.game_start);
					System.out.println("" + clientAct);
					return;
				}
				if (start.equals("ok")) {
					game_start = true;
					clientAct = stain[1];
				}
			}

			// myFrameCount=Integer.parseInt(result[1]);
			// System.out.println(""+checkSum);

			
			break;
			default:
				DBManager.removeClient(this);
				break;
	
		}		
		
		
		
		
		

		
	}
	
	
	public void Shop() throws IOException, ClassNotFoundException
	{
		System.out.println("유저가 상점으로 들어옴");
		String buymode = ois.readObject().toString();
		String[] resultbuy=buymode.split(":");			
		
			if(resultbuy[0].equals("GOLD"))
			{
				switch(Integer.parseInt(resultbuy[1]))
				{					
				case UnitValue.F_ANNA:
				
					if(getLv(mycards.anna)==0)//처음 구매
					{
						
						if(buyUnit(1000))
						{
						try {
							this.mycards.anna="1"+"a0";
							String sql =("update km_inven set anna="+"'"+this.mycards.anna+"'"+" where USER_UID="+user_uid);
							DBManager.getInstance().stmt.executeUpdate(sql);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						this.sendMessage("1:구매성공:"+this.gold+":"+this.cash+":"+this.mycards.anna+":"+UnitValue.F_ANNA);	
						
						}
					}
					else //업글시 호출되는 가격 계산식
					{
						if(buyUnit(getLv(mycards.anna)*500))
						{
							try {															
								this.mycards.anna=(getLv(mycards.anna)+1)+"a0";
								DBManager.getInstance().stmt.executeUpdate("update km_inven set anna="+"'"+this.mycards.anna+"'"+" where USER_UID="+user_uid);
								//call=null;
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							this.sendMessage("1:업글성공:"+this.gold+":"+this.cash+":"+this.mycards.anna+":"+UnitValue.F_ANNA);									}
												
					}
					break;
					
					
					
					
					
					//매직 타워 부분이다.
				case UnitValue.F_ELSATOWER:
					if(getLv(mycards.magictower)==0) //처음 구매시
					{
						if(buyUnit(3500))
						{
							try {
								this.mycards.magictower="1"+"a0";
								String sql =("update km_inven set magictower="+"'"+this.mycards.magictower+"'"+" where USER_UID="+user_uid);
								DBManager.getInstance().stmt.executeUpdate(sql);
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							this.sendMessage("1:구매성공:"+this.gold+":"+this.cash+":"+this.mycards.magictower+":"+UnitValue.F_ELSATOWER);		
						}

					}
					else  //업그레이드 시도시
					{
						if(buyUnit(getLv(mycards.magictower)*1100))
						{
							try {															
								this.mycards.magictower=(getLv(mycards.magictower)+1)+"a0";								
								DBManager.getInstance().stmt.executeUpdate("update km_inven set magictower="+"'"+this.mycards.magictower+"'"+" where USER_UID="+user_uid);
								//call=null;
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							this.sendMessage("1:업글성공:"+this.gold+":"+this.cash+":"+this.mycards.magictower+":"+UnitValue.F_ELSATOWER);	
						}
					}
					break;
					
					
					
					
					// 점핑트랩
				case UnitValue.F_JUMPINGTRAP:
					if(getLv(mycards.jumping)==0)
					{							
						if(buyUnit(200))
						{
							try {
								this.mycards.jumping="1"+"a0";
								String sql =("update km_inven set jumping="+"'"+this.mycards.jumping+"'"+" where USER_UID="+user_uid);
								DBManager.getInstance().stmt.executeUpdate(sql);
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							this.sendMessage("1:구매성공:"+this.gold+":"+this.cash+":"+this.mycards.jumping+UnitValue.F_JUMPINGTRAP);	
						}
					}
					else
					{
						if(buyUnit(getLv(mycards.jumping)*20))
						{
							try {															
								this.mycards.jumping=(getLv(mycards.jumping)+1)+"a0";
								DBManager.getInstance().stmt.executeUpdate("update km_inven set jumping="+"'"+this.mycards.jumping+"'"+" where USER_UID="+user_uid);
								//call=null;
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							this.sendMessage("1:업글성공:"+this.gold+":"+this.cash+":"+this.mycards.jumping+":"+UnitValue.F_JUMPINGTRAP);	
						}
					}						
					break;
					
					
					
					
					
					
					
					
				case UnitValue.F_BOOM:
					if(getLv(mycards.boom)==0)
					{							
						if(buyUnit(300))
						{
							try {
								this.mycards.boom="1"+"a0";
								String sql =("update km_inven set boom="+"'"+this.mycards.boom+"'"+" where USER_UID="+user_uid);
								DBManager.getInstance().stmt.executeUpdate(sql);
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							this.sendMessage("1:업글성공:"+this.gold+":"+this.cash+":"+this.mycards.boom+":"+UnitValue.F_BOOM);	
						}
					}
					else
					{
						if(buyUnit(getLv(mycards.boom)*20))
						{
							try {															
								this.mycards.boom=(getLv(mycards.boom)+1)+"a0";
								DBManager.getInstance().stmt.executeUpdate("update km_inven set boom="+"'"+this.mycards.boom+"'"+" where USER_UID="+user_uid);
								//call=null;
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							this.sendMessage("1:업글성공:"+this.gold+":"+this.cash+":"+this.mycards.boom+":"+UnitValue.F_BOOM);	
						}
					}
					break;
					
					
					
					
					
					
					
				case UnitValue.F_MAGICAIN:
					if(getLv(mycards.maigician)==0)
					{
						if(buyUnit(2500))
						{
							try {
								this.mycards.maigician="1"+"a0";
								String sql =("update km_inven set magician="+"'"+this.mycards.maigician+"'"+" where USER_UID="+user_uid);
								DBManager.getInstance().stmt.executeUpdate(sql);
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							this.sendMessage("1:구매성공:"+this.gold+":"+this.cash+":"+this.mycards.maigician+":"+UnitValue.F_MAGICAIN);	
						}

					}
					else
					{
						if(buyUnit(getLv(mycards.maigician)*1200))
						{
							try {															
								this.mycards.maigician=(getLv(mycards.maigician)+1)+"a0";
								DBManager.getInstance().stmt.executeUpdate("update km_inven set magician="+"'"+this.mycards.maigician+"'"+" where USER_UID="+user_uid);
								//call=null;
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							this.sendMessage("1:업글성공:"+this.gold+":"+this.cash+":"+this.mycards.maigician+":"+UnitValue.F_MAGICAIN);	
						}
					}
					break;
				case UnitValue.F_ARCHER:
					if(getLv(mycards.archer)==0)
					{
						if(buyUnit(200))
						{
							try {
								this.mycards.archer="1"+"a0";
								String sql =("update km_inven set archer="+"'"+this.mycards.archer+"'"+" where USER_UID="+user_uid);
								DBManager.getInstance().stmt.executeUpdate(sql);
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							this.sendMessage("1:구매성공:"+this.gold+":"+this.cash+":"+this.mycards.archer+":"+UnitValue.F_ARCHER);		
						}
					}
					else
					{
						if(buyUnit(getLv(mycards.archer)*80))
						{
							try {															
								this.mycards.archer=(getLv(mycards.archer)+1)+"a0";
								DBManager.getInstance().stmt.executeUpdate("update km_inven set archer="+"'"+this.mycards.archer+"'"+" where USER_UID="+user_uid);
								//call=null;
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							this.sendMessage("1:업글성공:"+this.gold+":"+this.cash+":"+this.mycards.archer+":"+UnitValue.F_ARCHER);	
						}
					
					}
					break;
				case UnitValue.F_TOWER:
					if(getLv(mycards.archertower)==0)
					{
						if(buyUnit(500))
						{
							try {
								this.mycards.archertower="1"+"a0";
								String sql =("update km_inven set archertower="+"'"+this.mycards.archertower+"'"+" where USER_UID="+user_uid);
								DBManager.getInstance().stmt.executeUpdate(sql);
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							this.sendMessage("1:구매성공:"+this.gold+":"+this.cash+":"+this.mycards.archertower+":"+UnitValue.F_TOWER);	
						}
					}
					else
					{
						if(buyUnit(getLv(mycards.archertower)*120))
						{
							try {															
								this.mycards.archertower=(getLv(mycards.archertower)+1)+"a0";
								DBManager.getInstance().stmt.executeUpdate("update km_inven set archertower="+"'"+this.mycards.archertower+"'"+" where USER_UID="+user_uid);
								//call=null;
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							this.sendMessage("1:업글성공:"+this.gold+":"+this.cash+":"+this.mycards.archertower+":"+UnitValue.F_TOWER);	
						}
					

					}
					break;
				case UnitValue.F_WORRIOR:
					if(getLv(mycards.worrior)==0)
					{
						if(buyUnit(150))
						{
							try {
								this.mycards.worrior="1"+"a0";
								String sql =("update km_inven set worrior="+"'"+this.mycards.worrior+"'"+" where USER_UID="+user_uid);
								DBManager.getInstance().stmt.executeUpdate(sql);
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							this.sendMessage("1:구매성공:"+this.gold+":"+this.cash+":"+this.mycards.worrior+":"+UnitValue.F_WORRIOR);	
						}
					}
					else
					{
						if(buyUnit(getLv(mycards.worrior)*60))							
						{
							try {															
								this.mycards.worrior=(getLv(mycards.worrior)+1)+"a0";
								DBManager.getInstance().stmt.executeUpdate("update km_inven set worrior="+"'"+this.mycards.worrior+"'"+" where USER_UID="+user_uid);
								//call=null;
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							this.sendMessage("1:업글성공:"+this.gold+":"+this.cash+":"+this.mycards.worrior+":"+UnitValue.F_WORRIOR);	
						}
					}
					break;					
				}
			}
			else if(resultbuy[0].equals("활성화"))
			{
				int unit_type=5;
				int unit_state=5;
				int unit_level=0;
				try
				{
			    unit_type=Integer.parseInt(resultbuy[1]);
				unit_state=Integer.parseInt(resultbuy[2]);
				unit_level=Integer.parseInt(resultbuy[3]);
				}
				catch(NumberFormatException e)
				{
					
				}
				
				if(unit_state==0)
				{
					if(unit_level>0)
					{
					unit_state=1;						
					this.sendMessage("3:"+unit_type+":"+unit_state);
					try {
						Unit_active(unit_type, unit_state);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.out.println("유닛활성화 시킵니다.");
					}
					return;
				}
				else
				{
					if(unit_level>0)
					{
					unit_state=0;					
					this.sendMessage("3:"+unit_type+":"+unit_state);
					try {
						Unit_active(unit_type, unit_state);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.out.println("유닛을 비활성화 시킵니다.");
					}
					return;
				}
				
				
			}
			else if(resultbuy[0].equals("CASH"))
			{
				
				System.out.println("유닛활성화를 시도합니다");
			}			
			else if(resultbuy[0].equals("EXIT"))
			{
				state=2;
			}
		
		
		
	}
	public void Unit_active(int type,int state) throws SQLException
	{
		String sql;
		switch(type)
		{
		case UnitValue.F_ANNA:
				sql="update km_inven set anna="+"'"+getLv(this.mycards.anna)+"a"+state+"'"+"where USER_UID="+user_uid;
				
			break;
		case UnitValue.F_ARCHER:
			sql="update km_inven set archer="+"'"+getLv(this.mycards.archer)+"a"+state+"'"+" where USER_UID="+user_uid;				
			break;
		case UnitValue.F_BOOM:
			sql="update km_inven set boom="+"'"+getLv(this.mycards.boom)+"a"+state+"'"+" where USER_UID="+user_uid;
			break;
		case UnitValue.F_ELSATOWER:
			sql="update km_inven set magictower="+"'"+getLv(this.mycards.magictower)+"a"+state+"'"+" where USER_UID="+user_uid;
			break;
		case UnitValue.F_JUMPINGTRAP:
			sql="update km_inven set jumping="+"'"+getLv(this.mycards.jumping)+"a"+state+"'"+" where USER_UID="+user_uid;
			break;
		case UnitValue.F_MAGICAIN:
			sql="update km_inven set magician="+"'"+getLv(this.mycards.maigician)+"a"+state+"'"+" where USER_UID="+user_uid;
			break;
		case UnitValue.F_TOWER:
			sql="update km_inven set archertower="+"'"+getLv(this.mycards.archertower)+"a"+state+"'"+" where USER_UID="+user_uid;
			break;
		case UnitValue.F_WORRIOR:
			sql="update km_inven set worrior="+"'"+getLv(this.mycards.worrior)+"a"+state+"'"+" where USER_UID="+user_uid;
			break;
			default:
				sql="update km_inven set worrior="+"'"+getLv(this.mycards.worrior)+"a"+state+"'"+" where USER_UID="+user_uid;
				break;
		}

		DBManager.getInstance().stmt.executeUpdate(sql);
		
		
	}

	public void LoginConnect() throws IOException, ClassNotFoundException {
		while (true) {
			if (ois == null) {
				DBManager.getInstance().removeClient(this);
			}
			String temp = ois.readObject().toString(); // 넘어온 정보를 토대로 구분시작
			String[] result = temp.split(":");

			if (result[0].equals("기존정보")) {
				if (DBManager.getInstance().DB
						.Connections(result[1], result[2])) // 첫번째 // 비밀번호
				{
					id = result[1];
					if (!DBManager.putClient(this)) // 현재 접속한 클라이언트와 다르다면 접속한
													// 클라이언트와 // 예외
					{
						id = null;
						sendMessage("중복로그인");
						oos.flush();
					} else {
						state = 1;
						sendMessage("접속성공");
						u_id = DBManager.getInstance().DB.get_nikname(result[1]);
						DBManager.getInstance().DB.getUserInfomation(this);
						this.id = result[1];
						oos.flush();
						channel = 1;

						return;
					}
				}

				else {
					sendMessage("접속실패");
					oos.flush();
				}
			}
			
			
			else if (result[0].equals("구글")) {
				
				if (DBManager.getInstance().DB
						.GoogleConnections(result[1])) // 첫번째 // 비밀번호
				{
					googleID = result[1];
					if (!DBManager.putClientGoogle((this))) // 현재 접속한 클라이언트와 다르다면 접속한
													// 클라이언트와 // 예외
					{
						googleID = null;
						sendMessage("중복로그인");
						oos.flush();
					} else {
						state = 1;
						sendMessage("접속성공");
						u_id = DBManager.getInstance().DB.get_nikname_google(result[1]);
						DBManager.getInstance().DB.getGoogleUserInformation(this);
						this.id = result[1];
						oos.flush();
						channel = 1;

						return;
					}
				}

				else {
					sendMessage("접속실패");
					oos.flush();
				}
				
				
				

			}

		}
	}

	public void SearchMode() {
		System.out.println("접속상대 검색을 시작합니다");
		try {
			DBManager.serachOther(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			sendMessage("서버접속성공");
			try {

				LoginConnect();

			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			while (true) {
				// System.out.println("재대로 돌고있을까?");
				ListrenReady();

			}
			// DB에 명령어를 전달할수 있는 객체가 생성되어진다. (java.sql.Statement)
		} catch (EOFException e) {
		} catch (Throwable e) {
			e.printStackTrace();
		}

		if (id != null) {
			DBManager.removeClient(this);
		}
		try {
			socket.close();
		} catch (IOException e) {
		}
	}

	public void send(String msg) {
		// TODO Auto-generated method stub

	}

	public int getNumber() {
		return number;
	}

	@Override
	public String toString() {
		if (id != null)
			return "[" + id + "]";
		return "[client " + number + "]";
	}
}

