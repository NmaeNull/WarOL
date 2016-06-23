package cn.withzz.Internet;
import java.awt.Color;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.withzz.game.Controller;
import cn.withzz.game.Item;
import cn.withzz.game.Man;
import cn.withzz.game.Map;
import cn.withzz.game.Panel;
import cn.withzz.game.Partner;
import cn.withzz.game.ReadResource;

/**
 * 
 * ��ͨ�û��������������û�������������Ϣר����
 * 
 */
public class UserTool implements InetController {
	List<Man> users = new ArrayList<Man>();
	public List<Host> hostGroup = new ArrayList<Host>();
	IManager im;

	static enum STATE {
		REDYING, REDY, GAMING, OVER
	};

	STATE state = STATE.REDYING;
	Panel gamePanel;
	String msg;
	Host host;
	int myID;
	public int  userPort =4003,hostPort = 4103;
	String MulticastIp = "224.0.2.4";
	public void setHost(Host host){
		this.host=host;
	}
	/**
	 * ������Ϣ
	 */
	Controller ctl = new Controller() {

		@Override
		public void recive(String m, DatagramPacket dp) {
			// TODO Auto-generated method stub
			System.out.println("�յ�" + m);

			switch (state) {
			case REDYING:
				String a[] = m.split(":");
				if (a[0].equals("gamestart")) {
					myID = Integer.valueOf(a[1]);
					setState(STATE.GAMING);
					System.out.println("�յ���Ϸ��ʼ!��ȡID" + myID);
					startGame();
				} else {
					msg = m;
				}
				break;
			case REDY:
				break;
			case GAMING:
				
				handle(m);
				
				break;
			case OVER:
				break;
			}
		}

	};

	public UserTool() {
		im = new IManager(ctl, userPort);//�û��˿�δ֪  ��д0��IManger����ѡ��
	}

	/*
	 * ��������������������
	 */
	private void set(String m){
		String[] s=m.split("-");
		String data=s[1];
		switch(s[0]){
		case "map":
			System.out.println("map"+data);
			gamePanel.map.setMapBy(data);
			break;
		case "item":
			System.out.println("item"+data);
			String[] seat=data.split("/");
			int x=Integer.valueOf(seat[0]);
			int y=Integer.valueOf(seat[1]);
			int type=Integer.valueOf(seat[2]);
			System.out.println("�ŵ���!"+x+","+y);
			new Item(gamePanel,x,y,type);
			break;
		case "seat":
			System.out.println("seat"+data);
			String[] seat2=data.split("+");
			for (int i=0;i<seat2.length;i++) {
				String[] ss=seat2[i].split("/");
				users.get(i).setSeat(Integer.valueOf(ss[0]),Integer.valueOf(ss[1]));
			}
			break;
		
		}
	}
	/**
	 * ��ʼ����
	 */
	public void startWork() {
		im.startRecive();// ��ʼ���ܵ�����Ϣ
		this.setState(STATE.REDYING);// ����״̬Ϊ��׼����
	}

	/**
	 * ���뷿��
	 */
	public void joinHouse() {

	}

	/**
	 * ֹͣ����
	 */
	public void stopWork() {
		setState(STATE.OVER);
		im.stopRecive();// ֹͣһ�н��գ��رն˿�
	}

	/**
	 * new�������Man����ŵ�user�б�
	 */
	public void createMan() {
		String[] a = msg.split("/");
		String[][] b = new String[a.length][];
		for (int i = 0; i < a.length; i++) {
			b[i] = a[i].split(":");
		}
		for (int i = 0; i < b.length; i++) {
			int id = Integer.valueOf(b[i][0]);
			Man m;
			if (id == myID)// ���ñ���Man typeΪ2������1p
				m = new Man(Integer.valueOf(b[i][1]), Integer.valueOf(b[i][2]),
						2, gamePanel, this);
			else
				// ���������typeΪ3
				m = new Man(Integer.valueOf(b[i][1]), Integer.valueOf(b[i][2]),
						3, gamePanel, this);
			m.setID(id);
			users.add(m);
		}
	}

	/**
	 * ��ȡ���
	 * 
	 * @return
	 */
	public List<Man> getMans() {
		return users;
	}

	/**
	 * �����������ַ�����ϢM
	 * 
	 * @param m
	 */
	public void sendM(String m) {
		System.out.println("�����ڣ�" + host.getIp() + ":" + host.getPort());
		im.send(m, new Partner(host.getIp(), hostPort));
		System.out.println("����������" + m);
	}

	/**
	 * �����������͵�����
	 * 
	 * @param m
	 */
//	public void handle(String m) {// ��Ϸ����ʱ����Ҳ������ݴ���
//		String[] mm = m.split(":");
//		for (Man man : users) {
//			if ((man.getID() + "").equals(mm[0])) {
//				man.setSeat(Integer.valueOf(mm[2]), Integer.valueOf(mm[3]));
//				man.ctl.recive(mm[1], null);
//				break;
//			}
//		}
//	}
	public void handle(String m) {// ��Ϸ����ʱ����Ҳ������ݴ���
		JSONObject ja=new JSONObject(m);
		switch(ja.getString("op"))
		{
		case "map":
			gamePanel.map.setMapBy(ja.getString("map"));
			break;
		case "item":
			int x=ja.getInt("x");
			int y=ja.getInt("y");
			int type=ja.getInt("id");
			System.out.println("�ŵ���!"+x+","+y);
			new Item(gamePanel,x,y,type);
			break;
		case "seat":
			JSONArray jarray=ja.getJSONArray("data");
			JSONObject jo;
			for (int i=0;i<jarray.length();i++) {
				jo=jarray.getJSONObject(i);
				users.get(i).setSeat(jo.getInt("x"),jo.getInt("y"));
			}
			break;
		case "userop":
			int id=ja.getInt("id");
			String opcode=ja.getString("opcode");
			users.get(id-1).ctl.recive(opcode, null);
			break;
		}
	
	}
	

	/**
	 * ��ʼ������Ϸ
	 */
	public void startGame() {
		System.out.println("��ʼ��");
		ReadResource r = new ReadResource();
		r.init();
		gamePanel = new Panel(r, Map.map2, Map.bothPlace2,1);
		createMan();
		JFrame F = new JFrame();
		gamePanel.setPeople(getMans());
		F.setSize(gamePanel.map.m2 * 60 + 17, gamePanel.map.m1 * 60 + 40);
		F.add(gamePanel);
		F.setBackground(Color.white);
		F.setLocationRelativeTo(null);
		F.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		F.setVisible(true);
		setState(STATE.GAMING);
	}

	public void setState(STATE s) {
		state = s;
	}

	/**
	 * ��дInetController�ķ�����Ϣ�����ڼ��̲���ʱ���÷�����Ϣ������
	 */
	@Override
	public void sendMessage(String m) {
		// TODO Auto-generated method stub
		sendM(m);// �ͻ��˷������ݵ�����
	}
}
