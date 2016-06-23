package cn.withzz.Internet;
import java.net.InetAddress;

import cn.withzz.game.Partner;

/**
 * ����������
 * ��ȡ�����ù�������������
 * 
 * 
 */
public class Host {
	private Partner p;
	private int num = 0;

	/**
	 * ��ø����������������Ϣ
	 * 
	 * @return
	 */
	public Partner getP() {
		return p;
	}

	public void setP(Partner p) {
		this.p = p;
	}

	/**
	 * �����������
	 * 
	 * @return
	 */
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public Host(Partner p, int num) {
		this.p = p;
		this.num = num;
	}

	/**
	 * ��ȡ����IP
	 * 
	 * @return
	 */
	public InetAddress getIp() {
		return p.getIp();
	}

	/**
	 * ��ȡ����������̶˿�
	 * 
	 * @return
	 */
	public int getPort() {
		return p.getPort();
	}
}
