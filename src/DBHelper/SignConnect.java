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
	
	//선택된 클라이언트에서 맵을 가져오라는 메세지가 날라오면 이 함수를 실행한다.
	
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
			System.out.println("DB테이블 검색실패" + e);
		}
	}
	
	public void setMap(ClientThread a)
	{
		
		String sql ="update km_map set map=" +"'"+a.m_ClientSettingMap+"'"+"where stage=1";
		//String sql=new String("update km_map set map = "+a.m_ClientSettingMap+"'where stage =' 1");
		try
		{
			System.out.println("넣을 값"+a.m_ClientSettingMap);
			int rss = DBManager.stmt.executeUpdate(sql);		
			System.out.println("맵수정 완료");
			
		}
		catch(Exception e)
		{
			System.out.println("DB삽입실패" + e);
		}
	}
	
	//구글 계정으로 접속시 사용되는 함수이다.
	public void getGoogleUserInformation(ClientThread a)
	{
		try
		{
			//java.sql.Statement stmt=conn.createStatement();
			PreparedStatement stat = null; //먼저 유저 정보를 가져오기에 앞서 초기화부터 한다.
			stat = DBManager.conn.prepareStatement("select * from km_user where google_ID=?"); //userID쪽을 들어간다.
			stat.setString(1, a.googleID); //필드 위치와 아이디를 셋팅한다.
			rs=stat.executeQuery(); //쿼리 불러옴
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
				//썸네일,승리 ,캐시,골드,닉네임,레벨,길드,진짜 uid
			}
			stat = null; //먼저 유저 정보를 가져오기에 앞서 초기화부터 한다.
			stat=DBManager.conn.prepareStatement("select *from km_inven where USER_UID=?"); //uid 로 찾아간다.
			stat.setString(1, a.user_uid); //검색해줄 자료값
			rs=stat.executeQuery();//쿼리 불러옴
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
			System.out.println("DB테이블 검색실패" + e);
		}
	}
	//유저정보를 가져올때 호출한다.
	public void getUserInfomation(ClientThread a)
	{
		try
		{
			//java.sql.Statement stmt=conn.createStatement();
			PreparedStatement stat = null; //먼저 유저 정보를 가져오기에 앞서 초기화부터 한다.
			stat = DBManager.conn.prepareStatement("select * from km_user where USER_ID=?"); //userID쪽을 들어간다.
			stat.setString(1, a.id); //필드 위치와 아이디를 셋팅한다.
			rs=stat.executeQuery(); //쿼리 불러옴
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
				//썸네일,승리 ,캐시,골드,닉네임,레벨,길드			
			}
			stat = null; //먼저 유저 정보를 가져오기에 앞서 초기화부터 한다.
			stat=DBManager.conn.prepareStatement("select *from km_inven where USER_UID=?"); //uid 로 찾아간다.
			stat.setString(1, a.user_uid); //검색해줄 자료값
			rs=stat.executeQuery();//쿼리 불러옴
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
			System.out.println("DB테이블 검색실패" + e);
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
			System.out.println("DB테이블 검색실패" + e);
		}
		return null;
	}
	//구글 아이디로 닉네임을 찾아온다.
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
			System.out.println("DB테이블 검색실패" + e);
		}
		return null;
	}
	
	

	
	//구글 로그인 처리 부분이다.
		public boolean GoogleConnections(String google) 
		{	
			try
			{
				//java.sql.Statement stmt=conn.createStatement();
				PreparedStatement stat = null;
				//아이디 필드를 찾는다.
				stat = DBManager.conn.prepareStatement("select * from km_user where google_id=?");
				//id를 1번째 
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
				System.out.println("DB테이블 검색실패" + e);
			}
			return false;	
		
			
		}
	//로그인 처리 부분이다.
	public boolean Connections(String id,String pass) 
	{	
		try
		{
			//java.sql.Statement stmt=conn.createStatement();
			PreparedStatement stat = null;
			//아이디 필드를 찾는다.
			stat = DBManager.conn.prepareStatement("select * from km_user where USER_ID=?");
			//id를 1번째 
			stat.setString(1, id);
			rs=stat.executeQuery();
			if(true==rs.next())
			{
				//아이디 있음
				//비밀번호 비교 한다. 비밀번호 맞으면 true 틀리면 false;
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
			System.out.println("DB테이블 검색실패" + e);
		}
		return false;	
	
		
	}
	public SignConnect() {

		try {
			DBManager.conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/game", "root", "apmsetup");
			System.out.println("데이터 베이스 접속 성공");
			//DB에 명령어를 전달할수 있는 객체가 생성되어진다. (java.sql.Statement)
			DBManager.stmt=(Statement)DBManager.conn.createStatement();
			
			
		//	conn.close();
		}

		catch (Exception e) {
			System.out.println("접속실패" + e);
		}
		
		
	}

}
