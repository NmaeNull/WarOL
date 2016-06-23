package cn.withzz.Internet;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.withzz.game.Controller;
import cn.withzz.game.Man;
import cn.withzz.game.Panel;
import cn.withzz.game.Partner;

/**
 * 
 * ����ʵ�ַ����������������ݵĽ��շ��ͺʹ�����Ϣ
 * 
 */
public class HostTool implements InetController {
	public List<Partner> users = new ArrayList<Partner>();
	IManager im;

	static enum STATE {
		REDYING, REDY, GAMING, OVER
	};

	STATE state = STATE.REDYING;
	Panel gamePanel;
	IDgroup ig = new IDgroup();
	Partner myself;
	int hostPort = 4103;//�����������˶˿�
	
	Controller ctl = new Controller() {
		@Override
		public void recive(String m, DatagramPacket dp) {
			// TODO Auto-generated method stub
			System.out.println("�����յ�" + m+"  state="+state);
			switch (state) {
			case REDYING:
				switch (m) {
				case "leave":// ���������뿪����
					for (Partner p : users) {
						if (p.getIp().equals(dp.getAddress())) {
							users.remove(p);
							System.out.println(dp.getAddress() + "leave");
						}
					}
					break;
				case "join":// �������˽��뷿��
					boolean add = true;
					for (Partner p : users) {// ��ѯ�Ƿ��Ѿ��ڷ�����
						if (p.getIp().equals(dp.getAddress()))
							add = false;
					}
					if (add) {
						users.add(new Partner(dp.getAddress(), dp.getPort()));
						System.out.println(dp.getAddress()+":"+dp.getPort()+ "join in~");
					}
					break;
				}
				break;
			case REDY:
				break;
			case GAMING:// ��Ϸ��
				String a[] = m.split(":");
				if (a[0].equals("gamestart"))
					break;
				
				shanbo(handle(m));// ������յ�����Ϣ���鲥��������
				break;
			case OVER:
				break;
			}
		}

	};

	public String handle(String m) {// ��Ϸ����ʱ�����ݴ���
		String[] mm = m.split(":");
		for (Man man : getMans()) {
			if ((man.getID() + "").equals(mm[0])) {
				m = m + ":" + man.getX() + ":" + man.getY();
				man.ctl.recive(mm[1], null);
				System.out.println("����" + m);
				JSONObject jo=	new JSONObject();
				jo.put("op","userop");
				jo.put("id",man.getID());
				jo.put("opcode",mm[1]);
				return jo.toString();
			}
		}
		return null;
	}

	public void setState(STATE s) {
		state = s;
	}

	/**
	 * �����������ܵ����������Ϣ�б�
	 * 
	 * @return
	 */
	public List<Partner> getPartners() {
		System.out.println("����б�: ");
		for (Partner p : users) {
			System.out.println(p.getIp() + "~");
		}
		return users;
	}

	public HostTool() {
		im = new IManager(ctl, hostPort);
	}

	public void startWork() {
		state = STATE.REDYING;
		im.startRecive();// ��ʼ���ܾ������е���
	}

	public void stopWork() {
		state = STATE.OVER;
		im.stopRecive();// ֹͣ���ܵ���
	}

	/**
	 * ��֪����С�������б�
	 */

	public void fenpei() {
		String message = "";
		for (Partner p : users) {
			Man m = p.getMan();
			message += m.getID() + ":" + m.getX() + ":" + m.getY() + "/";
		}
		System.out.println("Ⱥ��:" + message);
		shanbo(message);
	}

	/**
	 * ������������������� ������ID
	 */
	public void createMans() {
		for (Partner p : users) {
			int id = ig.getID();
			Man man;
//			if (p.equals(myself))
//				man = new Man(gamePanel.map.bothPlace[id - 1][0] * 60 - 20,
//						gamePanel.map.bothPlace[id - 1][1] * 60 - 30, 2,
//						gamePanel, this);
//			else
				man = new Man(gamePanel.map.bothPlace[id - 1][0] * 60 - 20,
						gamePanel.map.bothPlace[id - 1][1] * 60 - 30, 3,
						gamePanel, this);
			man.setID(id);
			p.setMan(man);
			System.out
					.println("����ID " + p.getMan().getID() + "��ɫ��" + p.getIp());
		}
	}

	/**
	 * ��ô����Ķ�Ӧ�����û��Ľ�ɫ�����б�
	 * 
	 * @return
	 */

	public List<Man> getMans() {
		List<Man> a = new ArrayList<Man>();
		for (Partner p : users) {
			a.add(p.getMan());
		}
		return a;
	}

	/**
	 * ����M�ַ���������С���
	 * 
	 * @param m
	 */
	public void shanbo(String m) {
		for (Partner p : users) {
			System.out.println("����:" + m+"��"+p.getIp());
			im.send(m, p);
		}
		System.out.println("�㲥" + m);
	}
	Thread spreadData =new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(state.equals(STATE.GAMING)){
				try {
					Thread.sleep(300);
					JSONArray ja=new JSONArray();
					for (Partner p : users) {
						JSONObject a=new JSONObject();
						a.put("x",p.getMan().getX());
						a.put("y", p.getMan().getY());
						ja.put(a);
					}
					JSONObject jo=new JSONObject();
					jo.put("op", "seat");
					jo.put("data",ja);
					shanbo(jo.toString());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	});
	/**
	 * ��ʼ��Ϸ
	 */
	public void startGame(Panel pl) {
		gamePanel = pl;
		createMans();
		fenpei();
		setState(STATE.GAMING);
		for (Partner p : users) {
			if (!p.equals(myself))
				im.send("gamestart:" + p.getMan().ID, p);
		}
		
		spreadData.start();
	}

	@Override
	public void sendMessage(String m) {
		// TODO Auto-generated method stub
		//im.send(m, myself);// �������Լ���������
		shanbo(m);
		
	}
}

/**
 * �������η��䷿���û���ΨһID
 * 
 * @author Administrator
 * 
 */
class IDgroup {
	boolean[] used = { false, false, false, false };

	public int getID() {
		for (int i = 0; i < 4; i++)
			if (!used[i]) {
				used[i] = true;
				return i + 1;
			}
		return -1;
	}

	public void logoutID(int id) {
		used[id - 1] = false;
	}

}
