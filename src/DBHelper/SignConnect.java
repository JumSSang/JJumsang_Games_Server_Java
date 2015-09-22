package DBHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import Server.ClientThread;

public class SignConnect {

	boolean state=true;
	private ResultSet rs;
	
	//���õ� Ŭ���̾�Ʈ���� ���� ��������� �޼����� ������� �� �Լ��� �����Ѵ�.
	
	public void getMap(ClientThread a)
	{
		try
		{
			PreparedStatement stat = null;
			stat = DBManager.conn.prepareStatement("select * from km_map where stage=?");
			stat.setInt(1,a.m_single_mapnumber);
			rs=stat.executeQuery();
			if(true==rs.next())
			{
				a.m_Singlemap=rs.getString(2);
			}
			
		}
		catch(Exception e)
		{
			System.out.println("DB���̺� �˻�����" + e);
		}
	}
	
	public void setMap(ClientThread a)
	{
		
		String sql ="update km_map set map=" +"'"+a.m_ClientSettingMap+"'"+"where stage=1";
		//String sql=new String("update km_map set map = "+a.m_ClientSettingMap+"'where stage =' 1");
		try
		{
			System.out.println("���� ��"+a.m_ClientSettingMap);
			int rss = DBManager.stmt.executeUpdate(sql);		
			System.out.println("�ʼ��� �Ϸ�");
			
		}
		catch(Exception e)
		{
			System.out.println("DB���Խ���" + e);
		}
	}
	
	//���� �������� ���ӽ� ���Ǵ� �Լ��̴�.
	public void getGoogleUserInformation(ClientThread a)
	{
		try
		{
			//java.sql.Statement stmt=conn.createStatement();
			PreparedStatement stat = null; //���� ���� ������ �������⿡ �ռ� �ʱ�ȭ���� �Ѵ�.
			stat = DBManager.conn.prepareStatement("select * from km_user where google_ID=?"); //userID���� ����.
			stat.setString(1, a.googleID); //�ʵ� ��ġ�� ���̵� �����Ѵ�.
			rs=stat.executeQuery(); //���� �ҷ���
			if(true==rs.next())
			{				
				//a.id=rs.getString(1);
				a.sum_number=rs.getString(6);
				a.victory=rs.getString(5);
				a.cash=rs.getString(4);
				a.gold=rs.getString(3);
				a.u_id=rs.getString(8);
				a.level=rs.getString(11);
				a.guild=rs.getString(7);	
				a.user_uid=rs.getString(10);
				//�����,�¸� ,ĳ��,���,�г���,����,���,��¥ uid
			}
			stat = null; //���� ���� ������ �������⿡ �ռ� �ʱ�ȭ���� �Ѵ�.
			stat=DBManager.conn.prepareStatement("select *from km_inven where USER_UID=?"); //uid �� ã�ư���.
			stat.setString(1, a.user_uid); //�˻����� �ڷᰪ
			rs=stat.executeQuery();//���� �ҷ���
			if(true==rs.next())
			{
				a.mycards.archer=rs.getString(2);
				a.mycards.worrior=rs.getString(3);
				a.mycards.maigician=rs.getString(4);
				a.mycards.boom=rs.getString(5);
				a.mycards.jumping=rs.getString(6);
				a.mycards.archertower=rs.getString(7);
				a.mycards.magictower=rs.getString(8);
				a.mycards.knight=rs.getString(9);
				a.mycards.orc=rs.getString(10);
				a.mycards.anna=rs.getString(11);
				
			}		
			
			
		}
		catch(Exception e)
		{
			System.out.println("DB���̺� �˻�����" + e);
		}
	}
	//���������� �����ö� ȣ���Ѵ�.
	public void getUserInfomation(ClientThread a)
	{
		try
		{
			//java.sql.Statement stmt=conn.createStatement();
			PreparedStatement stat = null; //���� ���� ������ �������⿡ �ռ� �ʱ�ȭ���� �Ѵ�.
			stat = DBManager.conn.prepareStatement("select * from km_user where USER_ID=?"); //userID���� ����.
			stat.setString(1, a.id); //�ʵ� ��ġ�� ���̵� �����Ѵ�.
			rs=stat.executeQuery(); //���� �ҷ���
			if(true==rs.next())
			{				
				a.sum_number=rs.getString(6);
				a.victory=rs.getString(5);
				a.cash=rs.getString(4);
				a.gold=rs.getString(3);
				a.u_id=rs.getString(8);
				a.level=rs.getString(11);
				a.guild=rs.getString(7);	
				a.user_uid=rs.getString(10);
				//�����,�¸� ,ĳ��,���,�г���,����,���			
			}
			stat = null; //���� ���� ������ �������⿡ �ռ� �ʱ�ȭ���� �Ѵ�.
			stat=DBManager.conn.prepareStatement("select *from km_inven where USER_UID=?"); //uid �� ã�ư���.
			stat.setString(1, a.user_uid); //�˻����� �ڷᰪ
			rs=stat.executeQuery();//���� �ҷ���
			if(true==rs.next())
			{
				a.mycards.archer=rs.getString(2);
				a.mycards.worrior=rs.getString(3);
				a.mycards.maigician=rs.getString(4);
				a.mycards.boom=rs.getString(5);
				a.mycards.jumping=rs.getString(6);
				a.mycards.archertower=rs.getString(7);
				a.mycards.magictower=rs.getString(8);
				a.mycards.knight=rs.getString(9);
				a.mycards.orc=rs.getString(10);
				a.mycards.anna=rs.getString(11);
				
			}		
			
		}
		catch(Exception e)
		{
			System.out.println("DB���̺� �˻�����" + e);
		}
		
	}
	
	public String get_nikname(String id)
	{
		try
		{
			//java.sql.Statement stmt=conn.createStatement();
			PreparedStatement stat = null;
			stat = DBManager.conn.prepareStatement("select * from km_user where USER_ID=?");
			stat.setString(1, id);
			rs=stat.executeQuery();
			if(true==rs.next())
			{
				String a=rs.getString(8);
				return rs.getString(8);
			}
		}
		catch(Exception e)
		{
			System.out.println("DB���̺� �˻�����" + e);
		}
		return null;
	}
	//���� ���̵�� �г����� ã�ƿ´�.
	public String get_nikname_google(String google_id)
	{
		try
		{
			//java.sql.Statement stmt=conn.createStatement();
			PreparedStatement stat = null;
			stat = DBManager.conn.prepareStatement("select * from km_user where google_id=?");
			stat.setString(1, google_id);
			rs=stat.executeQuery();
			if(true==rs.next())
			{
				String a=rs.getString(8);
				return rs.getString(8);
			}
		}
		catch(Exception e)
		{
			System.out.println("DB���̺� �˻�����" + e);
		}
		return null;
	}
	
	

	
	//���� �α��� ó�� �κ��̴�.
		public boolean GoogleConnections(String google) 
		{	
			try
			{
				//java.sql.Statement stmt=conn.createStatement();
				PreparedStatement stat = null;
				//���̵� �ʵ带 ã�´�.
				stat = DBManager.conn.prepareStatement("select * from km_user where google_id=?");
				//id�� 1��° 
				stat.setString(1, google);
				rs=stat.executeQuery();
				if(true==rs.next())
				{
					return true;
				}			
							
					System.out.println("--------------");			
					//conn.close();	
			}
			catch(Exception e)
			{
				System.out.println("DB���̺� �˻�����" + e);
			}
			return false;	
		
			
		}
	//�α��� ó�� �κ��̴�.
	public boolean Connections(String id,String pass) 
	{	
		try
		{
			//java.sql.Statement stmt=conn.createStatement();
			PreparedStatement stat = null;
			//���̵� �ʵ带 ã�´�.
			stat = DBManager.conn.prepareStatement("select * from km_user where USER_ID=?");
			//id�� 1��° 
			stat.setString(1, id);
			rs=stat.executeQuery();
			if(true==rs.next())
			{
				//���̵� ����
				//��й�ȣ �� �Ѵ�. ��й�ȣ ������ true Ʋ���� false;
				String temp=rs.getString(2);
				if(temp.equals(pass))
				{
					return true;
				}
				else
				{
					return false;
				}
			}			
						
				System.out.println("--------------");			
				//conn.close();	
		}
		catch(Exception e)
		{
			System.out.println("DB���̺� �˻�����" + e);
		}
		return false;	
	
		
	}
	public SignConnect() {

		try {
			DBManager.conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/game", "root", "apmsetup");
			System.out.println("������ ���̽� ���� ����");
			//DB�� ��ɾ �����Ҽ� �ִ� ��ü�� �����Ǿ�����. (java.sql.Statement)
			DBManager.stmt=(Statement)DBManager.conn.createStatement();
			
			
		//	conn.close();
		}

		catch (Exception e) {
			System.out.println("���ӽ���" + e);
		}
		
		
	}

}
