package cn.withzz.Internet;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import cn.withzz.game.Controller;
import cn.withzz.game.Partner;

/**
 * 
 * ����UDPͨѶ�Ķ��� ��ʵ�ֽ��շ��� ����Ϣ���ݸ�Controller����
 * 
 */
public class IManager {
	List<Partner> partner = new ArrayList<Partner>();
	Controller ctr;
	DC d;

	public IManager(Controller ctr, int dcPort) {
		this.ctr = ctr;
		d = new DC(this, dcPort);
	}

	public void send(String word, Partner p) {
		d.sendMessage(word, p);
	}


	public void startRecive() {
		d.startRecive();
	}
	public void stopRecive() {
		d.stopWoking();
	}
}

/**
 * ��������
 * 
 * @author Administrator
 * 
 */
/*
 * class CC { ServerSocket s; Socket socket; int port=8888; IManager im; public
 * CC(IManager im,int port){ this.im=im; this.port=port; init(); } void init(){
 * try{ s = new ServerSocket(port); } catch(IOException e){
 * System.out.println("�˿�����ʧ�ܣ�"); e.printStackTrace(); } } public void recive(){
 * Socket ss=null; try { ss=s.accept(); InputStream op=ss.getInputStream();
 * BufferedReader is=new BufferedReader(new InputStreamReader(op));
 * is.readLine(); } catch (IOException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); } } }
 */
/**
 * ��������
 * 
 * @author Administrator
 * 
 */
class DC {
	int myPort;
	IManager im;
	Boolean isWoking = true;
	private DatagramSocket dss;
	public DC(IManager im, int port) {
		this.im = im;
		myPort = port;
		try {
			System.out.println("��������˿�" +myPort);
			dss = new DatagramSocket(myPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("���������׽��ִ���ʧ�ܣ�");
		}
	}

	public void stopWoking() {
		isWoking = false;
	}


	public void sendMessage(final String word, final Partner p) {// ����UDP����
		try {
			byte[] buf = word.getBytes();
			InetAddress address = p.getIp();
			DatagramPacket dp = new DatagramPacket(buf, buf.length, address,
					p.getPort());
			
			/*
			 * System.out.println("���ݰ�Ŀ���ַ��"+dp.getAddress()+"\n");
			 * System.out.println("���ݰ�Ŀ��˿ںţ�"+dp.getPort()+"\n");
			 * System.out.println("���ݰ����ȣ�"+dp.getLength()+"\n");
			 */
			System.out.println("�������ݰ����ȣ�"+dp.getLength()+"\n");
			dss.send(dp);
			System.out.println("���ض˿ڣ�"+dss.getLocalPort());
		} catch (Exception e) {
			System.out.println("����ʧ��");

		}

	}

	public void startRecive() {// ��ʼ���յ���
		if(myPort==0)
			return;
		isWoking = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte[] buf = new byte[1024*10];
				DatagramPacket dp = new DatagramPacket(buf, buf.length);	
				System.out.println("��ʼ�����˿�" +myPort);
				while (isWoking) {
					try {
						// synchronized (isWoking) {
						dss.receive(dp);
						int length = dp.getLength();
						String message = new String(dp.getData(), 0, length);
						System.out.println("\n�յ������ǣ�" + message + "  ��С��"+length);
						im.ctr.recive(message, dp);
					} catch (Exception e) {
						System.out.println("��������ʧ�ܣ�");
						e.printStackTrace();
						//isWoking = false;
					}
				}
				while (isWoking && !dss.isClosed())
					dss.close();// �ر��׽���
				System.out.println("�ɹ��ر��׽���");
			}

		}).start();

	}
}
