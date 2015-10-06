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
	public String u_id; //uid �� �� �𸦶� ���� ����
	public String level; //����
	public String gold;
	public String victory;
	public String cash;
	public String sum_number;
	public String guild;
	public String user_uid; //��¥  uid �̴�
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

	// �ٸ� ����� ���´ٸ� ȣ�� �ض�
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

	// �������� �ҷ����� ���̴�.
	public void mapLoader(int mapnumber) {

		m_single_mapnumber = mapnumber;
		DBManager.getInstance().DB.getMap(this);
	}

	// �������� ��� å������ ������ִ� �Լ��̴�.
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
	
	//�� �Լ��� ������ ���� ������� �Ǵ����ְ� ����ϴٸ� ���������Ͽ� ��带 �������ִ� ������ �Ѵ�.
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
				this.sendMessage("0:������:"+this.gold+":"+this.cash);			
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
				System.out.println("������Ʈ ��û ����");
				Thread.sleep(3000);
				sendMessage(u_id + ":" + gold + ":" + cash + ":" + level + ":"
						+ victory + ":" + sum_number + ":" + guild +":"+mycards.anna +":"+mycards.archer+":"+mycards.archertower+":"+mycards.boom+":"+mycards.jumping+":"+
						mycards.knight+":"+mycards.magictower+":"+mycards.maigician+":"+mycards.orc+":"+mycards.worrior);
				// �г��� ��� ĳ�� ���� �¸� Ƚ��,����Ϲ�ȣ,��� ������� ������.

				// �����,�¸� ,ĳ��,���,�г���,����,���
				System.out.println("uid_����");
				oos.flush();
				state = NetState.READY;
			}
			break;
			
		case NetState.SHOP:
			Shop();
			break;		
			
		case NetState.STORY:
			System.out.println("���´� �̱�!!");
			String d = ois.readObject().toString();

			mapLoader(1);

			if (m_Singlemap != null) {
				System.out.println(m_Singlemap);
				sendMessage(m_Singlemap);
				if (id.equals("go7072")) {
					state = 10; // go7072�� �� �����ͷ� �̵�
				} // else

				state = 10; // 7���̸� ������������ �̵�
			}

			break;
			
		case NetState.READY:
			
			// Ŭ���̾�Ʈ�� ���� ���¸� �������� ����.
			System.out.println("�κ������ ����");
			String b = ois.readObject().toString();
			if (b.equals("��ġ���")) {
				SearchMode();
			}
			if (b.equals("��ġ���")) {
				ready = false;
			}
			if (b.equals("�����͸��")) {
				state = 4;
			}
			if (b.equals("�̱۰���")) {
				state = 9;
				}
			if(b.equals("�������"))
			{
				state=3;
			}

			
			break;
			
		case NetState.MULTIGAMESTART:
			String sendPacket = null;
			// System.out.println(""+id+"�� ������ī����");
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

				System.out.println("��ٸ����ִ°� �Ⱥ��̳� �����̵��� ,,");
				sendMessage("nextFrame:" + null);
			} else if (myFrameCount > other.myFrameCount) {

				System.out.println("��ٸ����ִ°� �Ⱥ��̳� �����̵��� ,,");
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
			System.out.println("������ �ҷ���");
			System.out.println(e);
			mapSetting(e);
			if (e != null) {
				state = NetState.ROBBY; // �ٽ� �κ� ��� ���·� �����Ѵ�.
				m_Singlemap = null;
			}

			break;
		case NetState.MUTI_TRUN_READY:
			// ���� ��Ƽ ���
			String es = ois.readObject().toString();
			System.out.println("dd" + es);
			if (es.equals("START")) {
				sendMessage("30");
				state = 12;
			}
			if (es.equals("�¸�")) {
				state = 2;
			}
			if (es.equals("�й�")) {
				state = 2;
			}
			break;
		case NetState.MUTI_TRUN:
			// ���� ��Ƽ���� 2��° ����

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
		System.out.println("������ �������� ����");
		String buymode = ois.readObject().toString();
		String[] resultbuy=buymode.split(":");			
		
			if(resultbuy[0].equals("GOLD"))
			{
				switch(Integer.parseInt(resultbuy[1]))
				{					
				case UnitValue.F_ANNA:
				
					if(getLv(mycards.anna)==0)//ó�� ����
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
						this.sendMessage("1:���ż���:"+this.gold+":"+this.cash+":"+this.mycards.anna+":"+UnitValue.F_ANNA);	
						
						}
					}
					else //���۽� ȣ��Ǵ� ���� ����
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
							this.sendMessage("1:���ۼ���:"+this.gold+":"+this.cash+":"+this.mycards.anna+":"+UnitValue.F_ANNA);									}
												
					}
					break;
					
					
					
					
					
					//���� Ÿ�� �κ��̴�.
				case UnitValue.F_ELSATOWER:
					if(getLv(mycards.magictower)==0) //ó�� ���Ž�
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
							this.sendMessage("1:���ż���:"+this.gold+":"+this.cash+":"+this.mycards.magictower+":"+UnitValue.F_ELSATOWER);		
						}

					}
					else  //���׷��̵� �õ���
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
							this.sendMessage("1:���ۼ���:"+this.gold+":"+this.cash+":"+this.mycards.magictower+":"+UnitValue.F_ELSATOWER);	
						}
					}
					break;
					
					
					
					
					// ����Ʈ��
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
							this.sendMessage("1:���ż���:"+this.gold+":"+this.cash+":"+this.mycards.jumping+UnitValue.F_JUMPINGTRAP);	
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
							this.sendMessage("1:���ۼ���:"+this.gold+":"+this.cash+":"+this.mycards.jumping+":"+UnitValue.F_JUMPINGTRAP);	
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
							this.sendMessage("1:���ۼ���:"+this.gold+":"+this.cash+":"+this.mycards.boom+":"+UnitValue.F_BOOM);	
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
							this.sendMessage("1:���ۼ���:"+this.gold+":"+this.cash+":"+this.mycards.boom+":"+UnitValue.F_BOOM);	
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
							this.sendMessage("1:���ż���:"+this.gold+":"+this.cash+":"+this.mycards.maigician+":"+UnitValue.F_MAGICAIN);	
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
							this.sendMessage("1:���ۼ���:"+this.gold+":"+this.cash+":"+this.mycards.maigician+":"+UnitValue.F_MAGICAIN);	
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
							this.sendMessage("1:���ż���:"+this.gold+":"+this.cash+":"+this.mycards.archer+":"+UnitValue.F_ARCHER);		
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
							this.sendMessage("1:���ۼ���:"+this.gold+":"+this.cash+":"+this.mycards.archer+":"+UnitValue.F_ARCHER);	
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
							this.sendMessage("1:���ż���:"+this.gold+":"+this.cash+":"+this.mycards.archertower+":"+UnitValue.F_TOWER);	
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
							this.sendMessage("1:���ۼ���:"+this.gold+":"+this.cash+":"+this.mycards.archertower+":"+UnitValue.F_TOWER);	
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
							this.sendMessage("1:���ż���:"+this.gold+":"+this.cash+":"+this.mycards.worrior+":"+UnitValue.F_WORRIOR);	
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
							this.sendMessage("1:���ۼ���:"+this.gold+":"+this.cash+":"+this.mycards.worrior+":"+UnitValue.F_WORRIOR);	
						}
					}
					break;					
				}
			}
			else if(resultbuy[0].equals("Ȱ��ȭ"))
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
					System.out.println("����Ȱ��ȭ ��ŵ�ϴ�.");
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
					System.out.println("������ ��Ȱ��ȭ ��ŵ�ϴ�.");
					}
					return;
				}
				
				
			}
			else if(resultbuy[0].equals("CASH"))
			{
				
				System.out.println("����Ȱ��ȭ�� �õ��մϴ�");
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
			String temp = ois.readObject().toString(); // �Ѿ�� ������ ���� ���н���
			String[] result = temp.split(":");

			if (result[0].equals("��������")) {
				if (DBManager.getInstance().DB
						.Connections(result[1], result[2])) // ù��° // ��й�ȣ
				{
					id = result[1];
					if (!DBManager.putClient(this)) // ���� ������ Ŭ���̾�Ʈ�� �ٸ��ٸ� ������
													// Ŭ���̾�Ʈ�� // ����
					{
						id = null;
						sendMessage("�ߺ��α���");
						oos.flush();
					} else {
						state = 1;
						sendMessage("���Ӽ���");
						u_id = DBManager.getInstance().DB.get_nikname(result[1]);
						DBManager.getInstance().DB.getUserInfomation(this);
						this.id = result[1];
						oos.flush();
						channel = 1;

						return;
					}
				}

				else {
					sendMessage("���ӽ���");
					oos.flush();
				}
			}
			
			
			else if (result[0].equals("����")) {
				
				if (DBManager.getInstance().DB
						.GoogleConnections(result[1])) // ù��° // ��й�ȣ
				{
					googleID = result[1];
					if (!DBManager.putClientGoogle((this))) // ���� ������ Ŭ���̾�Ʈ�� �ٸ��ٸ� ������
													// Ŭ���̾�Ʈ�� // ����
					{
						googleID = null;
						sendMessage("�ߺ��α���");
						oos.flush();
					} else {
						state = 1;
						sendMessage("���Ӽ���");
						u_id = DBManager.getInstance().DB.get_nikname_google(result[1]);
						DBManager.getInstance().DB.getGoogleUserInformation(this);
						this.id = result[1];
						oos.flush();
						channel = 1;

						return;
					}
				}

				else {
					sendMessage("���ӽ���");
					oos.flush();
				}
				
				
				

			}

		}
	}

	public void SearchMode() {
		System.out.println("���ӻ�� �˻��� �����մϴ�");
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
			sendMessage("�������Ӽ���");
			try {

				LoginConnect();

			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			while (true) {
				// System.out.println("���� ����������?");
				ListrenReady();

			}
			// DB�� ��ɾ �����Ҽ� �ִ� ��ü�� �����Ǿ�����. (java.sql.Statement)
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

