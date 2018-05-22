import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Server extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JTextArea text;

	JButton close;

	ServerSocket server;

	Socket you;

	//ip
	InetAddress youraddress;
	
	//���ݿ����
	Dao dao;
	// ����߳�
	ArrayList<ServerThread> playerlist = new ArrayList<ServerThread>();

	// ��ɫ
	String[] color = { "c", "d", "s", "h" };

	// ������
	String[] num = { "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
			"14", "15" };

	// ����ֳɵ�����
	String[][] player = new String[4][17];
	
	String []pucks;

	Server() {
		setTitle("������");
		setSize(400, 500);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new ExitWindow());
		init();
		validate();
		dao = new Dao();
		startServer(6666);
		
	}

	class ExitWindow extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			dao.setAllState("0");
			System.exit(0);
		}
	}

	public void init() {
		text = new JTextArea();
		String myaddress = null;
		try {
			myaddress = InetAddress.getLocalHost().getHostAddress().toString(); // ������IP��ַ
		} catch (UnknownHostException e) {
		}
		text.setText("����������...(IP��ַ: " + myaddress + ")\n");
		text.setEditable(false);
		add(text, BorderLayout.CENTER);
		JScrollPane scroll = new JScrollPane(text);
		add(scroll, BorderLayout.CENTER);
		close = new JButton("�رշ�����");
		add(close, BorderLayout.SOUTH);
		close.addActionListener(this);
	}

	/*
	 * ����ֳ������Ʒַ������
	 */
	public void Randomization() {
		//System.out.println("Randomization");
		
		
		//��ʼ���˿�
		pucks=new String[52];
		int puck_n=0;
		for (int i = 0; i < 13; i++)
			for (int j = 0; j < 4; j++) {
				pucks[puck_n]="" + color[j] + num[i];
				puck_n++;
			}
		
		/*pucks[puck_n]="s16";
		puck_n++;
		pucks[puck_n]="s17";*/
		
		//ϴ��
        for(int i=51; i>=0; i--) {    
        	int index=new Random().nextInt(i+1);
            String temp = pucks[index];    
            pucks[index] = pucks[i];    
            pucks[i] = temp; 
        }    
     
        //����
        int countPuck=13;
        for (int i = 0; i < 4; i++)
        	for (int j = 0; j < countPuck; j++) {
				player[i][j] = pucks[j+i*(countPuck)];
			}
        
        //С��������
        int n1,n2;
        String temp;
        for (int i = 0,key=0; i < 4; i++){
        	for(int n=1;n<countPuck;n++)
        	{
        		key=n;
        		n1=Integer.parseInt(player[i][key].substring(1,
        				player[i][key].length()));
        		n2=Integer.parseInt(player[i][key-1].substring(1,
        				player[i][key-1].length()));// ����ƵĴ�С
				while (n1<n2&&key>=1)
        		{
        			temp=player[i][key];
        			player[i][key]=player[i][key-1];
        			player[i][key-1]=temp;
        			key--;
        			if(key>=1)
        			{
	        			n1=Integer.parseInt(player[i][key].substring(1,
	            				player[i][key].length()));
	            		n2=Integer.parseInt(player[i][key-1].substring(1,
            				player[i][key-1].length()));
        			}
        			
        		}
        	}
        	
        }
        
        
		
		
		
		
		
//		int[] a = new int[3];
//		for (int i = 0; i < 3; i++)
//			a[i] = 0;
//		for (int i = 0; i < 13; i++)
//			for (int j = 0; j < 3; j++) {
//				
//				int row = (new Random().nextInt(100))%3 ;
//				if (a[row] < 13)
//					player[row][a[row]++] = "" + color[j] + num[i];
//				else {
//					while (a[row] >= 13)
//						row = (row + 1) % 3;
//					player[row][a[row]++] = "" + color[j] + num[i];
//				}
//
//			}
		
		//System.out.println("Randomization_over");
	}

	public void startServer(int port) {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "��������������", "��ʾ",
					JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
		while (true) {
			try {
				you = server.accept();
				youraddress = you.getInetAddress();
			} catch (IOException e) {
			}
			if (you != null) {
				new ServerThread(you, youraddress).start();
			}
		}
	}

	class ServerThread extends Thread {
		Socket socket;

		InetAddress address;

		DataInputStream is = null;

		DataOutputStream os = null;

		String id = null, psw = null; // �û���Ϣ

		int tablenum; // �û������table��

		int seatnum; // table��seat��

		/*
		 * ���̵߳�λ������,"hall"��ʾ�����̣߳�"seat"��ʾseat�߳�,
		 * һ��player�յ�½ʱ����һ���̣߳�����socket���ܹ�������
		 * ����seat����̺߳͸ոս���������̱߳�־�������location��
		 */
		String location = null;

		boolean readyflag = false; // ��Ϸ�Ƿ�׼����

		ServerThread(Socket s, InetAddress addr) {
			tablenum = -1;
			seatnum = -1;
			socket = s;
			location = "hall";
			address = addr;
			try {
				is = new DataInputStream(socket.getInputStream());
				os = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			SimpleDateFormat tempDate = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String nowtime;
			String s = "";
			while (true) {
				try {
					s = is.readUTF();
					/*
					 * ��¼��Ϣ
					 */
					if (s.startsWith("login")) {
						id = is.readUTF();
						psw = is.readUTF();
						String result = dao.Check(id, psw);
						if (result.equals("2")) {
							/*
							 * �ɹ���¼,���͵��ͻ�����Ϣ
							 */
							dao.setState("1", id);
							os.writeUTF("2");
							/*
							 * ��ͻ��˷��͵�ǰ����Щ�������
							 */
							os.writeUTF("otheronlineplayer");
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								if (th != this && th.location.equals("hall"))
									os.writeUTF(th.id);
							}
							os.writeUTF("END1");
							/*
							 * ��ͻ��˷���seat��������
							 */
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								if (th.location.equals("seat")) {
									os.writeUTF((th.tablenum + "").toString());
									os.writeUTF((th.seatnum + "").toString());
									os.writeUTF(dao.getIcon(th.id));
								}
							}
							os.writeUTF("END2");
							os.writeUTF(dao.getScore(this.id) + "");
							os.writeUTF(dao.getIcon(this.id));
							/*
							 * ��½���Լ����뵽playerlist�����У�����������ҷ��ͽ��뷿����Ϣ
							 */
							playerlist.add(this);
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								th.os.writeUTF("Newcomer");
								th.os.writeUTF(id);
							}

							// ��������¼��־
							nowtime = tempDate.format(new java.util.Date());
							text.append(nowtime + "\n�û���" + id + "\nIP��ַ��"
									+ youraddress.toString() + " �����˷���\n");

						} else if (result.equals("1")) {
							os.writeUTF("1"); // �ظ���¼
							break;
						} else {
							os.writeUTF("0"); // ����
							break;
						}
					}
					/*
					 * ע����Ϣ
					 */
					if (s.equals("register")) {
						String name=is.readUTF();
						String psw=is.readUTF();
						if(!dao.Check(name)){
							dao.Adduser(name, psw);
							os.writeUTF("success");
						}else
							os.writeUTF("failed");
					}
					/*
					 * ���������Ϣ
					 */
					if (s.equals("chating")) {
						String toname = is.readUTF();
						String fromname = is.readUTF();
						String text = is.readUTF();
						if (toname.equals("������")) {
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								if (th != this && th.location.equals("hall")) {
									th.os.writeUTF("chating");
									th.os.writeUTF(fromname + "��������˵:" + text);
								}
							}
						} else {
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								if (th.id.equals(toname.trim())
										&& th.location.equals("hall")) {
									th.os.writeUTF("chating");
									th.os.writeUTF(fromname + "��" + toname
											+ "˵(���Ļ�):" + text);
								}
							}
						}
					}

					/*
					 * ������Ϸtable����Ϣ��������������û�֪ͨ
					 */
					if (s.equals("entertable")) {
						String tn = is.readUTF();
						String sn = is.readUTF();
						tablenum = Integer.parseInt(tn);// �õ����Ӻ�
						seatnum = Integer.parseInt(sn);// �õ���λ��
						for (int i = 0; i < playerlist.size(); i++) {
							ServerThread th = playerlist.get(i);
							if (th.location.equals("hall")) {
								th.os.writeUTF("comeoneplayer");
								th.os.writeUTF((tablenum + "").toString());
								th.os.writeUTF((seatnum + "").toString());
								th.os.writeUTF(dao.getIcon(id));
							}
						}
					}
					/*
					 * ������Ϸseat��ʵ��������һ���̵߳��¼���
					 */
					if (s.equals("enterseat")) {
						location = "seat";// �޸�λ�ã�˵������һ���µ��߳�
						id = is.readUTF();
						String tn = is.readUTF();
						String sn = is.readUTF();
						tablenum = Integer.parseInt(tn);// �õ����Ӻ�
						seatnum = Integer.parseInt(sn);// �õ���λ��
						// playerlist������߳�
						playerlist.add(this);

						/*
						 * ����������player������Ϣ
						 */
						for (int i = 0; i < playerlist.size(); i++) {
							ServerThread th = playerlist.get(i);
							if (th != this && th.tablenum == this.tablenum
									&& th.location.equals("seat")) {
								// ��ʼ�����������û���Ϣ
								os.writeUTF("initotherplayer");
								os.writeUTF(th.seatnum + "");
								os.writeUTF(th.readyflag + "");
								os.writeUTF(th.id);
								// -------
								th.os.writeUTF("oneplayercomein");
								th.os.writeUTF(this.seatnum + "");
								th.os.writeUTF(this.id + "");
							}
						}
					}

					/*
					 * �յ�׼������Ϣ,���������û�֪ͨ�����׼������Ϣ
					 */
					if (s.equals("ready")) {	
						this.readyflag = true;
						int count = 0;
					System.out.println("aaa"+playerlist.size());
						for (int i = 0; i < playerlist.size(); i++) {
							ServerThread th = playerlist.get(i);
							if (th != this && th.tablenum == this.tablenum
									&& th.location.equals("seat")) {
								if (th.readyflag == true)
									count++;
								th.os.writeUTF("oneplayerready");
								th.os.writeUTF(this.seatnum + "");
							}
						}
	
						///System.out.println(count);
						
						System.out.println(count);
						if (count == 3) {
							System.out.println("ok1");
							Randomization();// ������ֳ�4��
							System.out.println(playerlist.size());
							//String num = (new Random().nextInt(100) % 2) + "";
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								if (th.tablenum == this.tablenum
										&& th.location.equals("hall")) {
									th.os.writeUTF("Gamestart");
									th.os.writeUTF(tablenum + "");
								}
								if (th.tablenum == this.tablenum
										&& th.location.equals("seat")) {
									th.os.writeUTF("gamestart");
									String cards = "";
									for (int j = 0; j < 13; j++)
										cards += player[th.seatnum][j] + "#";
									th.os.writeUTF(cards);									
									/*th.os.writeUTF(pucks[51]);
									th.os.writeUTF(pucks[52]);
									th.os.writeUTF(pucks[51]);*/
								}
							}
						}
					}
					if(s.equals("qiangdizhu"))
					{
						String num = is.readUTF();
						//System.out.println("okkkkkk");
						for (int i = 0; i < playerlist.size(); i++) {
							ServerThread th = playerlist.get(i);
							if (th.tablenum == this.tablenum
									&& th.location.equals("hall")) {
								th.os.writeUTF("Gamestart");
								th.os.writeUTF(tablenum + "");
							}
							if (th.tablenum == this.tablenum
									&& th.location.equals("seat")) {
								th.os.writeUTF("gamestarta");
								th.os.writeUTF(num);				
								
							}
						}
					}
					/*
					 * ��Ϸ��Ϣ,����Ҫ����Ϣ����
					 */

					if (s.equals("gameinfo")) {
						
						String type = is.readUTF();// ���������ƻ�ʱ
						if (type.equals("buchu")) {
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								if (th != this && th.tablenum == this.tablenum
										&& th.location.equals("seat")) {
									th.os.writeUTF("oneplayerbuchu");
									th.os.writeUTF(this.seatnum + "");
								}
							}
						} else if (type.equals("chaoshi")) {
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								if (th != this && th.tablenum == this.tablenum
										&& th.location.equals("seat")) {
									th.os.writeUTF("oneplayerchaoshi");
									th.os.writeUTF(this.seatnum + "");
								}
							}
						} else if (type.equals("chupai")) {
							String info = is.readUTF();
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								if (th != this && th.tablenum == this.tablenum
										&& th.location.equals("seat")) {
									th.os.writeUTF("oneplayerchupai");
									th.os.writeUTF(this.seatnum + "");
									th.os.writeUTF(info);
								}
							}
						}

					}

					if (s.equals("oneplayerwin")) {
						for (int i = 0; i < playerlist.size(); i++) {
							ServerThread th = playerlist.get(i);
							if (th != this && th.tablenum == this.tablenum
									&& th.location.equals("seat")) {
								dao.updateScore(th.id, -5);
								th.os.writeUTF("youfailed");
								th.os.writeUTF(this.id + "");
							}
							if (th == this) {
								os.writeUTF("youwin");
								dao.updateScore(id, 15);
							}
						}
					}
					/*
					 * ��Ϸ��̸
					 */
					if (s.equals("gametalking")) {
						String text = is.readUTF();
						for (int i = 0; i < playerlist.size(); i++) {
							ServerThread th = playerlist.get(i);
							if (th != this && th.tablenum == this.tablenum
									&& th.location.equals("seat")) {
								th.os.writeUTF("gametalking");
								th.os.writeUTF(this.id + "");
								th.os.writeUTF(text);
							}
						}
					}

					/*
					 * �˳�seat��Ϣ
					 */
					if (s.equals("exitseat")) {
						String tn = is.readUTF();
						String sn = is.readUTF();
						for (int i = 0; i < playerlist.size(); i++) {
							ServerThread th = playerlist.get(i);
							if (th != this && th.location.equals("hall")) {
								th.os.writeUTF("oneplayerexitseat");
								th.os.writeUTF(tn);
								th.os.writeUTF(sn);
							}
							if ((th.id).equals(id)
									&& th.location.equals("hall")) {
								th.os.writeUTF("youcanstartagain");
							}
							if (th != this && th.tablenum == this.tablenum
									&& th.location.equals("seat")) {
								th.os.writeUTF("oneplayerexit");
								th.os.writeUTF(seatnum + "");
								th.os.writeUTF(id);
								th.readyflag = false;
							}
						}
						// playerlist�����Ƴ�
						playerlist.remove(this);
						break;
					}
					/*
					 * �˳�������Ϣ
					 */
					if (s.equals("exit")) {
						s = is.readUTF();
						for (int i = 0; i < playerlist.size(); i++) {
							ServerThread th = playerlist.get(i);
							if (th != this) {
								th.os.writeUTF("someoneexit");
								th.os.writeUTF(id);
							}
						}
						// ���Լ���playerlistȥ��
						playerlist.remove(this);
						// ��������¼��־
						dao.setState("0", id);
						nowtime = tempDate.format(new java.util.Date());
						text.append(nowtime + "\n�û���" + id + "\nIP��ַ��"
								+ youraddress.toString() + " �뿪�˷���\n");
						this.stop();
					}

					// END
				} catch (IOException e) {
					e.printStackTrace();
					try {
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					// dao.setState("0", id);
					// nowtime = tempDate.format(new java.util.Date());
					// text.append(nowtime + "\n�û���" + id + "\nIP��ַ��"
					// + youraddress.toString() + " �뿪�˷���\n");
					
					break;
				}
			}
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		int result = JOptionPane.showConfirmDialog(this, "ȷ���˳���", "��ʾ",
				JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			dao.setAllState("0");
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		
		new Server();
	}
}
