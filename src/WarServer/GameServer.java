package WarServer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import cn.withzz.Internet.HostTool;
import cn.withzz.game.Map;
import cn.withzz.game.Panel;
import cn.withzz.game.Partner;
import cn.withzz.game.ReadResource;
/*
 * ��Ϸ�����
 * ����JFrame  ������Ϸ��ڲ˵�  ����������ʵ�ֲ˵�����
 * ����ҪͼƬ��Դ���ؽ��ڴ�
 */
public class GameServer {
	BufferedImage background;
	BufferedImage chose1[] = new BufferedImage[2],
			chose2[] = new BufferedImage[2], chose3[] = new BufferedImage[2];
	BufferedImage chose11[] = new BufferedImage[2],
			chose12[] = new BufferedImage[2], chose13[] = new BufferedImage[2];
	BufferedImage chose21[] = new BufferedImage[2],
			chose22[] = new BufferedImage[2];
	BufferedImage gameList;
	HostTool ht;
	JFrame f = new JFrame();
	int chose = 0;
	int mode = 3;
	final JPanel j = new JPanel() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
/*
 * (non-Javadoc)
 * @see javax.swing.JComponent#paint(java.awt.Graphics)
 * ���ƽ�������
 */
		@Override
		public void paint(Graphics g) {
			g.drawImage(background, 0, -10, 800, 500, this);

				int[] order11 = { 0, 0, 0 };
				if (chose != 0)
					order11[chose - 1] = 1;
				g.drawImage(chose21[order11[0]], 0, 100, 300, 100, this);
				g.drawImage(chose22[order11[1]], 0, 200, 300, 100, this);
				g.drawImage(chose13[order11[2]], 0, 300, 300, 100, this);
				g.drawImage(gameList, 280, 100, 500, 300, this);
				int i1 = 0;
				if(ht!=null)
				for (Partner p : ht.users) {
					g.drawString(p.getIp().toString(), 300, 120 + (i1 * 10));
					i1++;
				}

			
			g.dispose();//�ͷ���Դ
		}
	};

	GameServer() {

		MouseMotionListener mml = new MouseMotionListener() {// ���λ�ü���

			@Override
			public void mouseDragged(MouseEvent e) {
			}
			@Override
			public void mouseMoved(MouseEvent e) {
				chose = getBox(e.getX(), e.getY());
			}

		};
		
	/*
	 * ��Ҫ����������¼� 
	 */
		MouseListener ml = new MouseListener() {// ��궯������
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				int x, y;
				x = e.getX();
				y = e.getY();
				hostWork(getBox(x, y));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		};
		// new MusicPlayer("RanMa.mid");//ˮ�ݱ�ը��Ч

		// new MainMenu();
		/*
		 * ����ͼƬ��Դ
		 * ����ͼƬ ����֡ͼƬ ����ͼƬ ש��ذ�ֲ��ͼƬ �˵���ǩͼƬ
		 */
		try {

			background = ImageIO.read(new File("images/menu/background.jpg"));
			chose1[0] = ImageIO.read(new File("images/menu/1.png"));
			chose2[0] = ImageIO.read(new File("images/menu/2.png"));
			chose3[0] = ImageIO.read(new File("images/menu/3.png"));
			chose1[1] = ImageIO.read(new File("images/menu/10.png"));
			chose2[1] = ImageIO.read(new File("images/menu/20.png"));
			chose3[1] = ImageIO.read(new File("images/menu/30.png"));

			chose11[0] = ImageIO.read(new File("images/menu/joinHouse.png"));
			chose12[0] = ImageIO.read(new File("images/menu/newHouse.png"));
			chose13[0] = ImageIO.read(new File("images/menu/backMenu.png"));
			chose11[1] = ImageIO.read(new File("images/menu/joinHouse1.png"));
			chose12[1] = ImageIO.read(new File("images/menu/newHouse1.png"));
			chose13[1] = ImageIO.read(new File("images/menu/backMenu1.png"));

			chose21[0] = ImageIO.read(new File("images/menu/startGame.png"));
			chose21[1] = ImageIO.read(new File("images/menu/startGame1.png"));
			chose22[0] = ImageIO.read(new File("images/menu/kickOut.png"));
			chose22[1] = ImageIO.read(new File("images/menu/kickOut1.png"));
			gameList = ImageIO.read(new File("images/menu/gameList.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //��Ӹ������������JFrame��������
		j.addMouseListener(ml);
		j.addMouseMotionListener(mml);
		j.setSize(400, 400);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		f.setLocation(width / 2 - 400, height / 2 - 250);
		f.setTitle("������");
		f.setSize(800, 500);
		f.add(j);
		f.setResizable(false);
		f.setVisible(true);
		f.setFocusable(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		thread.start();
		ht = new HostTool();
		System.out.println("\n��������");
		ht.startWork();
	}
 //ͨ��һ���̲߳���ˢ��ҳ������ ����sleep30��ʹ��������
	Thread thread = new Thread(new Runnable() {// ˢ�½���
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while (true) {
						j.repaint();
						try {
							Thread.sleep(30);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});

	private int getBox(int x, int y) {// �ж����λ�������ĸ�����
		if (x > 40 && x < 260 && y > 130 && y < 370) {
			if ((y - 130) % 100 < 40)
				return (y - 130) / 100 + 1;
		}
		return 0;
	}


	private void hostWork(int type) {//������������
		switch (type) {
		case 1:// ��ʼ��Ϸ
			startGame();
			break;
		case 2:// �߳����
			System.out.println("�߳����");
			break;
		case 3:// �ص��˵�
			if (ht != null)
				ht.stopWork();
			mode = 2;
			break;
		}
	}

	private void startGame() {//������Ϸ
		ReadResource r = new ReadResource();
		r.init();
		Panel p = new Panel(r, Map.map2, Map.bothPlace2,2);
		ht.startGame(p);
		this.f.dispose();
		JFrame F = new JFrame();
		p.setPeople(ht.getMans());
		F.setSize(p.map.m2 * 60 + 5, p.map.m1 * 60 + 20);
		F.add(p);
		F.setBackground(Color.white);
		F.setLocationRelativeTo(null);
		F.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		F.setVisible(true);

	}
/*
 * ���
 */
	public static void main(String[] args) {
		org.json.JSONArray ja=new org.json.JSONArray();

		new GameServer();

	}

}
