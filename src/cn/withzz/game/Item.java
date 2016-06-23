package cn.withzz.game;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.json.JSONObject;

/**
 * 
 * ������
 * ը����Ϸ�ڿ��ƻ�ש����Ա�������
 * ��Ҵ�������ʰȡ���������������
 */
public class Item {
Panel p;
int ID=1;
Boolean working=true;
int gx,gy;
BufferedImage img;
int size;
/*
 * ���߱�ʰȡ����
 */
private void BePick(Man m){
	System.out.println(m.ID+"����"+ID+"�ŵ���");
	working=false;
	switch(ID){
	case 1:
	case 2:
		m.setSkill(this.ID);
		break;
	case 3:
		m.setMax(m.getMax()+1);
		break;
	case 4:
		m.setSpeed(m.getSpeed()-1);
		break;
	case 5:
		m.setPower(m.getPower()+1);
		break;
	}
	p.allItem.remove(this);
}
/*
 * �����๹�캯��
 * �����������ʾ��Panel
 */
public Item(Panel p,int x,int y){
	if(p.TerminalMode==1)
		return;
	ID=(int) (Math.random()*5+1);
	this.img=p.RR.item[ID-1];
	this.p=p;
	this.gx=x;
	this.gy=y;
	this.size=p.map.size;
	p.allItem.add(this);
	t.start();
	sendToClient();
}
public void sendToClient(){
	JSONObject jo=new JSONObject();
	jo.put("op","item");
	jo.put("x",gx);
	jo.put("y",gy);
	jo.put("id",ID);
	//p.ic.sendMessage("set:item-"+x+"/"+y+"/"+ID);
	p.ic.sendMessage(jo.toString());
}
public Item(Panel p,int x,int y,int type){
	ID=type;
	this.img=p.RR.item[ID-1];
	this.p=p;
	this.gx=x;
	this.gy=y;
	this.size=p.map.size;
	p.allItem.add(this);
	t.start();
}
/*
 * ʵ�ֻ���
 */
public void drawItem(Graphics g){
	if(working)
	g.drawImage(img, gy*size, gx*size, size, size,p);
}
/*
 * �����Ƿ�ʰȡ������Ҵ�������ʰȡ��
 */
Thread t=new Thread(new Runnable(){
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(working){
			try {
    			Thread.sleep(100);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
			for(Man m:p.people){
				if(!m.isDead&&m.gx==gy&&m.gy==gx){
					BePick(m);
				}
			}
		}
	}
});	

}
