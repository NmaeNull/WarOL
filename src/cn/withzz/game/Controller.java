package cn.withzz.game;
import java.net.DatagramPacket;
/*
 * �������ӿ� ������JPanel��ʵ�ֺ���������������
 */
public abstract class Controller {
	public abstract void recive(String m, DatagramPacket dp);
}
