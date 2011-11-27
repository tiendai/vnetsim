/**
 * frmMain.java - Chương trình chính
 *
 * @author : Vũ Thành Công
 *
 * @Purpose: Bài tập lớn môn Tổ chức va quy hoạch mạng viễn thông
 *
 * @Usage: Run program
 *
 * @version 2.0 2011/4/12 
 */
package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import myobjects.Edge;
import myobjects.Graph;
import myobjects.Vertex;
import algorithms.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JCheckBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.KeyStroke;
import javax.swing.JCheckBoxMenuItem;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

import java.awt.Desktop;
import java.io.File;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class frmMain {

	private JFrame frmVnetsim;			// Frame chương trình
	private JFrame frmProgress;			// Frame quá trình hoạt động
	private frmProperties frmProp;		// Frame xem thông số

	public JTextArea txtaProgress;		

	private int status = 0;				// Biến trạng thái, sử dụng để chọn kiểu paint cho đồ thị

	/*
	 * Biến chọn thuật toán cho mạng Access
	 * algorithmSelect = 0 ==> KrusKal
	 * algorithmSelect = 1 ==> Prim
	 * algorithmSelect = 2 ==> Esaus-Williams	
	 */
	private int algorithmSelect = 0;	

	private Font myfont = new Font("Tahoma", Font.PLAIN, 12);   
	private JButton btnGenerate;
	private JButton btnStart;

	private JCheckBox chbAccessNetwork;

	private JLabel lblThongSoHienThi;
	private JLabel lblPath;
	private JLabel lblAlpha;
	private JLabel lblPc;
	private JLabel lblThreshold;
	private JLabel lblRpD;
	private JLabel lblWmax;

	private JTextField txtN;

	private Graph myGraph;				// Đồ thị chính của chương trình

	private int numberOfVertices = 50;// Mặc định đồ thị có 50 đỉnh        	
	/*
	 * Các tham số chính của chương trình
	 */
	private double alpha = 0.5; 
	private double pc =0.5;
	private int wThreshold;
	private int radius;                       
	private int lengthOfGraph = 50;
	private int wMax4AN;


	private pnlDisplay pnlNoidung;
	private JComboBox cbbAlgorithms;
	private JPanel pnlThamso;
	private JSlider sldThreshold;
	private JSlider sldWmax4AN ;
	private JTextField txtGamma;
	private JComboBox cbbFrom;
	private JComboBox cbbTo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frmMain window = new frmMain();
					window.frmVnetsim.setVisible(true);

					window.frmProgress.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public frmMain() {
		initialize();
		frmVnetsim.pack();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		/*
		 * Khởi tạo Frame trạng thái
		 */
		frmProgress = new JFrame("Log");
		frmProgress.setIconImage(Toolkit.getDefaultToolkit().getImage(frmMain.class.getResource("/VnetsimIcons/iconLog.png")));
		frmProgress.setSize(new Dimension(300,500));
		frmProgress.getContentPane().setFont(myfont);

		txtaProgress = new JTextArea("*****\n");
		txtaProgress.setEditable(false);
		txtaProgress.setFont(myfont);
		JScrollPane scr = new JScrollPane(txtaProgress);
		frmProgress.getContentPane().add(scr,BorderLayout.CENTER);
		/*
		 * Khởi tạo cửa sổ chính
		 */
		frmVnetsim = new JFrame();
		frmVnetsim.setIconImage(Toolkit.getDefaultToolkit().getImage(frmMain.class.getResource("/VnetsimIcons/iconMain.png")));
		frmVnetsim.setResizable(false);
		frmVnetsim.getContentPane().setFont(myfont);
		frmVnetsim.getContentPane().setLayout(new BorderLayout(0, 0));
		frmVnetsim.setFont(new Font("Tahoma", Font.PLAIN, 12));
		frmVnetsim.setTitle("Vnetsim 11 (build 11.0419) - Vũ Thành Công ĐT2-K52");
		frmVnetsim.setBounds(100, 100, 1200, 800);
		frmVnetsim.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/*
		 * Khởi tạo đồ thị và các giá trị
		 */
		myGraph = new Graph(numberOfVertices);
		wThreshold = (int)(myGraph.getMinOfWeights()+myGraph.getMaxOfWeights())/2;
		lengthOfGraph = Collections.max(myGraph.getEdges()).getCost();
		radius = (int)(0.3*lengthOfGraph);
		wMax4AN = 3*myGraph.getMaxOfWeights();

		txtaProgress.append("* Đang khởi tạo đồ thị mới ...\n");
		pnlNoidung = new pnlDisplay();

		pnlNoidung.setFont(myfont);
		txtaProgress.append("* Đã khởi tạo xong.\n");
		pnlNoidung.setPreferredSize(new Dimension(950,650));
		pnlNoidung.setToolTipText("");
		pnlNoidung.setBorder(new TitledBorder(null, "Nội dung", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		frmVnetsim.getContentPane().add(pnlNoidung, BorderLayout.CENTER);

		lblThongSoHienThi = new JLabel("");
		lblThongSoHienThi.setFont(myfont);

		lblPath = new JLabel("");

		GroupLayout gl_pnlNoidung = new GroupLayout(pnlNoidung);
		gl_pnlNoidung.setHorizontalGroup(
				gl_pnlNoidung.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_pnlNoidung.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_pnlNoidung.createParallelGroup(Alignment.LEADING)
								.addComponent(lblThongSoHienThi)
								.addComponent(lblPath))
								.addContainerGap(858, Short.MAX_VALUE))
				);
		gl_pnlNoidung.setVerticalGroup(
				gl_pnlNoidung.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_pnlNoidung.createSequentialGroup()
						.addContainerGap(519, Short.MAX_VALUE)
						.addComponent(lblThongSoHienThi)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(lblPath)
						.addGap(20))
				);
		pnlNoidung.setLayout(gl_pnlNoidung);

		pnlThamso = new JPanel();
		//pnlThamso.setPreferredSize(new Dimension(250,650));
		pnlThamso.setBorder(new TitledBorder(null, "Tham số", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		frmVnetsim.getContentPane().add(pnlThamso, BorderLayout.WEST);

		int wMin = myGraph.getMinOfWeights();
		int wMax = myGraph.getMaxOfWeights();

		String [] algorithms = new String[]{"Kruskal","Prim","Esaus-Williams"};

		Object[] cbbVertices = myGraph.getVertices().toArray();
		GridBagLayout gbl_pnlThamso = new GridBagLayout();
		gbl_pnlThamso.columnWidths = new int[]{74, 107, 0};
		gbl_pnlThamso.rowHeights = new int[]{32, 0, 14, 31, 14, 31, 14, 31, 14, 31, 28, 31, 20, 23, 23, 0, 136, 0};
		gbl_pnlThamso.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_pnlThamso.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		pnlThamso.setLayout(gbl_pnlThamso);

		JLabel lblN = new JLabel("Số đỉnh (N)  = ");
		GridBagConstraints gbc_lblN = new GridBagConstraints();
		gbc_lblN.anchor = GridBagConstraints.WEST;
		gbc_lblN.insets = new Insets(0, 0, 5, 5);
		gbc_lblN.gridx = 0;
		gbc_lblN.gridy = 0;
		pnlThamso.add(lblN, gbc_lblN);

		txtN = new JTextField();
		txtN.setText("50");
		txtN.setColumns(10);
		GridBagConstraints gbc_txtN = new GridBagConstraints();
		gbc_txtN.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtN.insets = new Insets(0, 0, 5, 0);
		gbc_txtN.gridx = 1;
		gbc_txtN.gridy = 0;
		pnlThamso.add(txtN, gbc_txtN);

		sldThreshold = new JSlider(JSlider.HORIZONTAL,wMin,wMax,(wMin+wMax)/2);
		sldThreshold.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JSlider sld = (JSlider)arg0.getSource();
				if(!sld.getValueIsAdjusting()){
					int value = sld.getValue();
					lblThreshold.setText("W(Threshold) = "+value);
					wThreshold = value;
					if(status == 3)status =1;

					updateMentorGraph();

				}
			}
		});

		btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					int num = Integer.parseInt(txtN.getText());
					if(num<=0)throw new IllegalArgumentException();
					if(num>200)throw new IndexOutOfBoundsException();
					numberOfVertices = num;
					txtaProgress.append("* Khởi tạo đồ thị mới với số đỉnh N = "+numberOfVertices+"\n");
					myGraph = new Graph(numberOfVertices);                                     

					status = 0;
					updateSlider();
					updateCombobox(cbbFrom);
					updateCombobox(cbbTo);

					pnlNoidung.newPaint(myGraph,0);
					frmVnetsim.repaint();
					txtaProgress.append("* Xong.\n");
				}catch(IllegalArgumentException ie){
					JOptionPane.showMessageDialog(null, "Nhập sai số đỉnh!");
					txtaProgress.append("* Khởi tạo đồ thị thất bại \n");
				}catch(IndexOutOfBoundsException oe){
					JOptionPane.showMessageDialog(null, "Nhập số đỉnh quá cao sẽ làm chậm chương trình !\nVui lòng chọn số nhỏ hơn 200");
					txtaProgress.append("* Khởi tạo đồ thị thất bại \n");
				}

			}
		});
		GridBagConstraints gbc_btnGenerate = new GridBagConstraints();
		gbc_btnGenerate.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnGenerate.insets = new Insets(0, 0, 5, 0);
		gbc_btnGenerate.gridx = 1;
		gbc_btnGenerate.gridy = 1;
		pnlThamso.add(btnGenerate, gbc_btnGenerate);

		lblAlpha = new JLabel("Alpha = 0.5");
		GridBagConstraints gbc_lblAlpha = new GridBagConstraints();
		gbc_lblAlpha.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblAlpha.insets = new Insets(0, 0, 5, 0);
		gbc_lblAlpha.gridwidth = 2;
		gbc_lblAlpha.gridx = 0;
		gbc_lblAlpha.gridy = 2;
		pnlThamso.add(lblAlpha, gbc_lblAlpha);

		JSlider sldAlpha = new JSlider(JSlider.HORIZONTAL,0,10,5);
		sldAlpha.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JSlider sld = (JSlider)arg0.getSource();
				if(!sld.getValueIsAdjusting()){
					double value = (double)(sld.getValue())/10;
					lblAlpha.setText("Alpha = "+value);
					alpha = value;
					if(status ==3)status = 1;
					updateMentorGraph();
				}
			}
		});
		sldAlpha.setMajorTickSpacing(5);
		sldAlpha.setMinorTickSpacing(1);
		sldAlpha.setPaintTicks(true);
		GridBagConstraints gbc_sldAlpha = new GridBagConstraints();
		gbc_sldAlpha.fill = GridBagConstraints.HORIZONTAL;
		gbc_sldAlpha.anchor = GridBagConstraints.NORTH;
		gbc_sldAlpha.insets = new Insets(0, 0, 5, 0);
		gbc_sldAlpha.gridwidth = 2;
		gbc_sldAlpha.gridx = 0;
		gbc_sldAlpha.gridy = 3;
		pnlThamso.add(sldAlpha, gbc_sldAlpha);


		lblThreshold = new JLabel("W(Threshold) = "+wThreshold);
		lblThreshold.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblThreshold = new GridBagConstraints();
		gbc_lblThreshold.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblThreshold.insets = new Insets(0, 0, 5, 0);
		gbc_lblThreshold.gridwidth = 2;
		gbc_lblThreshold.gridx = 0;
		gbc_lblThreshold.gridy = 4;
		pnlThamso.add(lblThreshold, gbc_lblThreshold);
		sldThreshold.setMajorTickSpacing(10);
		sldThreshold.setMinorTickSpacing(5);
		sldThreshold.setPaintTicks(true);
		GridBagConstraints gbc_sldThreshold = new GridBagConstraints();
		gbc_sldThreshold.fill = GridBagConstraints.HORIZONTAL;
		gbc_sldThreshold.anchor = GridBagConstraints.NORTH;
		gbc_sldThreshold.insets = new Insets(0, 0, 5, 0);
		gbc_sldThreshold.gridwidth = 2;
		gbc_sldThreshold.gridx = 0;
		gbc_sldThreshold.gridy = 5;
		pnlThamso.add(sldThreshold, gbc_sldThreshold);

		JTabbedPane tbdpnThem = new JTabbedPane(JTabbedPane.TOP);

		JPanel pnlRoute = new JPanel();
		tbdpnThem.addTab("Tìm đường", null, pnlRoute, null);

		JSlider sldRpD = new JSlider(JSlider.HORIZONTAL,0,10,3);
		sldRpD.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JSlider sld = (JSlider)arg0.getSource();
				if(!sld.getValueIsAdjusting()){
					double value = (double)(sld.getValue())/10;
					lblRpD.setText("R/D = "+value);
					radius = (int)(value * lengthOfGraph);
					if(status ==3)status =1;
					updateMentorGraph();
				}
			}
		});

		lblRpD = new JLabel("R/D = 0.3");
		GridBagConstraints gbc_lblRpD = new GridBagConstraints();
		gbc_lblRpD.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblRpD.insets = new Insets(0, 0, 5, 0);
		gbc_lblRpD.gridwidth = 2;
		gbc_lblRpD.gridx = 0;
		gbc_lblRpD.gridy = 6;
		pnlThamso.add(lblRpD, gbc_lblRpD);
		sldRpD.setMajorTickSpacing(5);
		sldRpD.setMinorTickSpacing(1);
		sldRpD.setPaintTicks(true);
		GridBagConstraints gbc_sldRpD = new GridBagConstraints();
		gbc_sldRpD.fill = GridBagConstraints.HORIZONTAL;
		gbc_sldRpD.anchor = GridBagConstraints.NORTH;
		gbc_sldRpD.insets = new Insets(0, 0, 5, 0);
		gbc_sldRpD.gridwidth = 2;
		gbc_sldRpD.gridx = 0;
		gbc_sldRpD.gridy = 7;
		pnlThamso.add(sldRpD, gbc_sldRpD);


		lblPc = new JLabel("Pc = 0.5");
		GridBagConstraints gbc_lblPc = new GridBagConstraints();
		gbc_lblPc.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblPc.insets = new Insets(0, 0, 5, 0);
		gbc_lblPc.gridwidth = 2;
		gbc_lblPc.gridx = 0;
		gbc_lblPc.gridy = 8;
		pnlThamso.add(lblPc, gbc_lblPc);
		JSlider sldPc = new JSlider(JSlider.HORIZONTAL,0,10,5);
		sldPc.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JSlider sld = (JSlider)arg0.getSource();
				if(!sld.getValueIsAdjusting()){
					double value = (double)(sld.getValue())/10;
					lblPc.setText("Pc = "+value);
					pc = value;
					updateMentorGraph();
				}
			}
		});
		sldPc.setMajorTickSpacing(5);
		sldPc.setMinorTickSpacing(1);
		sldPc.setPaintTicks(true);
		GridBagConstraints gbc_sldPc = new GridBagConstraints();
		gbc_sldPc.fill = GridBagConstraints.HORIZONTAL;
		gbc_sldPc.anchor = GridBagConstraints.NORTH;
		gbc_sldPc.insets = new Insets(0, 0, 5, 0);
		gbc_sldPc.gridwidth = 2;
		gbc_sldPc.gridx = 0;
		gbc_sldPc.gridy = 9;
		pnlThamso.add(sldPc, gbc_sldPc);

		lblWmax = new JLabel("Wmax (for Access network)= "+wMax4AN);
				GridBagConstraints gbc_lblWmax = new GridBagConstraints();
				gbc_lblWmax.anchor = GridBagConstraints.NORTHWEST;
				gbc_lblWmax.insets = new Insets(0, 0, 5, 0);
				gbc_lblWmax.gridwidth = 2;
				gbc_lblWmax.gridx = 0;
				gbc_lblWmax.gridy = 10;
				pnlThamso.add(lblWmax, gbc_lblWmax);

				sldWmax4AN = new JSlider(JSlider.HORIZONTAL,(2*wMax),(6*wMax),(3*wMax));
				sldWmax4AN.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent arg0) {
						JSlider sld = (JSlider)arg0.getSource();
						if(!sld.getValueIsAdjusting()){
							int value = sld.getValue();
							lblWmax.setText("Wmax (for Access network) = "+value);
									wMax4AN = value;
									if(status == 3)status =1;

									updateMentorGraph();
						}
					}
				});
				sldWmax4AN.setMajorTickSpacing(100);
				sldWmax4AN.setMinorTickSpacing(25);
				sldWmax4AN.setPaintTicks(true);
				GridBagConstraints gbc_sldWmax4AN = new GridBagConstraints();
				gbc_sldWmax4AN.fill = GridBagConstraints.HORIZONTAL;
				gbc_sldWmax4AN.anchor = GridBagConstraints.NORTH;
				gbc_sldWmax4AN.insets = new Insets(0, 0, 5, 0);
				gbc_sldWmax4AN.gridwidth = 2;
				gbc_sldWmax4AN.gridx = 0;
				gbc_sldWmax4AN.gridy = 11;
				pnlThamso.add(sldWmax4AN, gbc_sldWmax4AN);

				JLabel lblAlgorithmForAN = new JLabel("Algorithm for AN : ");
						lblAlgorithmForAN.setFont(new Font("Tahoma", Font.PLAIN, 12));
						GridBagConstraints gbc_lblAlgorithmForAN = new GridBagConstraints();
						gbc_lblAlgorithmForAN.anchor = GridBagConstraints.NORTHWEST;
						gbc_lblAlgorithmForAN.insets = new Insets(0, 0, 5, 5);
						gbc_lblAlgorithmForAN.gridx = 0;
						gbc_lblAlgorithmForAN.gridy = 12;
						pnlThamso.add(lblAlgorithmForAN, gbc_lblAlgorithmForAN);

						chbAccessNetwork = new JCheckBox("Hiện Access Network");
						chbAccessNetwork.addItemListener(new ItemListener() {
							public void itemStateChanged(ItemEvent evt) {
								int state = evt.getStateChange();
								if(state == ItemEvent.SELECTED){
									if((status == 2)||(status == 3)) {
										status = 1;
										//updateMentorGraph();
										pnlNoidung.setPaintOption(1);
										pnlNoidung.repaint();
										frmVnetsim.repaint();
									}
								}
								if(state == ItemEvent.DESELECTED){
									if((status == 1)||(status == 3)) {
										status = 2;
										//updateMentorGraph();
										pnlNoidung.setPaintOption(2);
										pnlNoidung.repaint();
										frmVnetsim.repaint();
									}
								}
							}
						});
						cbbAlgorithms = new JComboBox(algorithms);
						cbbAlgorithms.addItemListener(new ItemListener() {
							public void itemStateChanged(ItemEvent arg0) {
								algorithmSelect = cbbAlgorithms.getSelectedIndex();
								if(status!=0)updateMentorGraph();
							}
						});
						cbbAlgorithms.setFont(new Font("Tahoma", Font.PLAIN, 12));
						cbbAlgorithms.setSelectedIndex(0);
						GridBagConstraints gbc_cbbAlgorithms = new GridBagConstraints();
						gbc_cbbAlgorithms.fill = GridBagConstraints.HORIZONTAL;
						gbc_cbbAlgorithms.anchor = GridBagConstraints.NORTH;
						gbc_cbbAlgorithms.insets = new Insets(0, 0, 5, 0);
						gbc_cbbAlgorithms.gridx = 1;
						gbc_cbbAlgorithms.gridy = 12;
						pnlThamso.add(cbbAlgorithms, gbc_cbbAlgorithms);
						chbAccessNetwork.setSelected(true);
						GridBagConstraints gbc_chbAccessNetwork = new GridBagConstraints();
						gbc_chbAccessNetwork.anchor = GridBagConstraints.NORTH;
						gbc_chbAccessNetwork.fill = GridBagConstraints.HORIZONTAL;
						gbc_chbAccessNetwork.insets = new Insets(0, 0, 5, 0);
						gbc_chbAccessNetwork.gridwidth = 2;
						gbc_chbAccessNetwork.gridx = 0;
						gbc_chbAccessNetwork.gridy = 13;
						pnlThamso.add(chbAccessNetwork, gbc_chbAccessNetwork);

						btnStart = new JButton("Start");
						btnStart.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								txtaProgress.append("* Đang xây dựng mạng ...");
								if(chbAccessNetwork.isSelected()) status = 1;
								else status = 2;

								updateMentorGraph();
								txtaProgress.append("... Xong.\n");
							}
						});
						GridBagConstraints gbc_btnStart = new GridBagConstraints();
						gbc_btnStart.fill = GridBagConstraints.VERTICAL;
						gbc_btnStart.gridheight = 2;
						gbc_btnStart.insets = new Insets(0, 0, 5, 0);
						gbc_btnStart.gridwidth = 2;
						gbc_btnStart.gridx = 0;
						gbc_btnStart.gridy = 14;
						pnlThamso.add(btnStart, gbc_btnStart);
						GridBagLayout gbl_pnlRoute = new GridBagLayout();
						gbl_pnlRoute.columnWidths = new int[]{47, 1, 0};
						gbl_pnlRoute.rowHeights = new int[]{26, 28, 28, 0};
						gbl_pnlRoute.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
						gbl_pnlRoute.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
						pnlRoute.setLayout(gbl_pnlRoute);

						JLabel lblFrom = new JLabel("Từ nút :");
						GridBagConstraints gbc_lblFrom = new GridBagConstraints();
						gbc_lblFrom.insets = new Insets(0, 0, 5, 5);
						gbc_lblFrom.gridx = 0;
						gbc_lblFrom.gridy = 0;
						pnlRoute.add(lblFrom, gbc_lblFrom);

						cbbFrom = new JComboBox(cbbVertices);
						cbbFrom.setEditable(false);
						GridBagConstraints gbc_cbbFrom = new GridBagConstraints();
						gbc_cbbFrom.fill = GridBagConstraints.HORIZONTAL;
						gbc_cbbFrom.anchor = GridBagConstraints.NORTH;
						gbc_cbbFrom.insets = new Insets(0, 0, 5, 0);
						gbc_cbbFrom.gridx = 1;
						gbc_cbbFrom.gridy = 0;
						pnlRoute.add(cbbFrom, gbc_cbbFrom);

						JLabel lblTo = new JLabel("Tới nút :");
						GridBagConstraints gbc_lblTo = new GridBagConstraints();
						gbc_lblTo.insets = new Insets(0, 0, 5, 5);
						gbc_lblTo.gridx = 0;
						gbc_lblTo.gridy = 1;
						pnlRoute.add(lblTo, gbc_lblTo);

						JButton btnGo = new JButton("Go!");
						btnGo.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								if(status == 0) JOptionPane.showMessageDialog(frmVnetsim, "Mạng chưa tối ưu !\nKhông thể tìm thấy đường đi");
								else {
									status = 3;
									Vertex fromNode = (Vertex)cbbFrom.getSelectedItem();
									Vertex toNode = (Vertex)cbbTo.getSelectedItem();
									txtaProgress.append("* Tìm đường đi từ nút "+fromNode.getId()+" tới nút "+toNode.getId()+"\n");

									if(fromNode.equals(toNode)){
										JOptionPane.showMessageDialog(frmVnetsim, "Hai đỉnh giống nhau !");
										txtaProgress.append("* Tìm đường thất bại !\n");
									}
									else {						
										List<Vertex> path = getPath(fromNode,toNode,myGraph);
										txtaProgress.append("* Đang xây dựng đường đi ...\n");
										StringBuilder sPath = new StringBuilder("Đường đi = ");
										for(Vertex node : path){
											sPath.append(node.getId());
											if(!node.equals(toNode))sPath.append("-->");
										}
										clearPath(myGraph);
										for(int i =0; i< path.size()-1;i++){
											Vertex msource = path.get(i);
											Vertex mtarget = path.get(i+1);
											Edge mE = myGraph.getEdge(msource, mtarget);
											mE.setPath(true);
											//System.out.println(mE+ " is path !");
										}
										//updateMentorGraph();
										pnlNoidung.setPaintOption(3);
										pnlNoidung.repaint();
										frmVnetsim.repaint();

										lblPath.setFont(myfont);
										lblPath.setText(sPath.toString());
										txtaProgress.append("* Đã tìm thấy đường đi từ nút "+fromNode.getId()+" tới nút "+toNode.getId()+"\n");
										txtaProgress.append("* "+sPath.toString()+"\n");
										frmVnetsim.repaint();
									}
								}
							}
						});
						cbbTo = new JComboBox(cbbVertices);
						cbbTo.setSelectedIndex(cbbVertices.length-1);
						cbbTo.setEditable(false);
						GridBagConstraints gbc_cbbTo = new GridBagConstraints();
						gbc_cbbTo.fill = GridBagConstraints.HORIZONTAL;
						gbc_cbbTo.anchor = GridBagConstraints.NORTH;
						gbc_cbbTo.insets = new Insets(0, 0, 5, 0);
						gbc_cbbTo.gridx = 1;
						gbc_cbbTo.gridy = 1;
						pnlRoute.add(cbbTo, gbc_cbbTo);
						GridBagConstraints gbc_btnGo = new GridBagConstraints();
						gbc_btnGo.fill = GridBagConstraints.HORIZONTAL;
						gbc_btnGo.anchor = GridBagConstraints.NORTH;
						gbc_btnGo.gridx = 0;
						gbc_btnGo.gridy = 2;
						pnlRoute.add(btnGo, gbc_btnGo);


						JPanel pnlOverload = new JPanel();
						tbdpnThem.addTab("Quá tải", null, pnlOverload, null);
						GridBagLayout gbl_pnlOverload = new GridBagLayout();
						gbl_pnlOverload.columnWidths = new int[]{58, 25, 18, 0};
						gbl_pnlOverload.rowHeights = new int[]{28, 28, 0};
						gbl_pnlOverload.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
						gbl_pnlOverload.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
						pnlOverload.setLayout(gbl_pnlOverload);

						JLabel lblGamma = new JLabel("Gamma = ");
						GridBagConstraints gbc_lblGamma = new GridBagConstraints();
						gbc_lblGamma.insets = new Insets(0, 0, 5, 5);
						gbc_lblGamma.gridx = 0;
						gbc_lblGamma.gridy = 0;
						pnlOverload.add(lblGamma, gbc_lblGamma);

						txtGamma = new JTextField();
						txtGamma.setEditable(false);
						txtGamma.setText("80");
						txtGamma.setColumns(10);
						GridBagConstraints gbc_txtGamma = new GridBagConstraints();
						gbc_txtGamma.fill = GridBagConstraints.HORIZONTAL;
						gbc_txtGamma.anchor = GridBagConstraints.NORTH;
						gbc_txtGamma.insets = new Insets(0, 0, 5, 5);
						gbc_txtGamma.gridx = 1;
						gbc_txtGamma.gridy = 0;
						pnlOverload.add(txtGamma, gbc_txtGamma);

						JLabel label = new JLabel("%");
						GridBagConstraints gbc_label = new GridBagConstraints();
						gbc_label.fill = GridBagConstraints.HORIZONTAL;
						gbc_label.insets = new Insets(0, 0, 5, 0);
						gbc_label.gridx = 2;
						gbc_label.gridy = 0;
						pnlOverload.add(label, gbc_label);

						JButton btnAdd = new JButton("Add");
						btnAdd.setEnabled(false);
						GridBagConstraints gbc_btnAdd = new GridBagConstraints();
						gbc_btnAdd.fill = GridBagConstraints.HORIZONTAL;
						gbc_btnAdd.anchor = GridBagConstraints.NORTH;
						gbc_btnAdd.insets = new Insets(0, 0, 0, 5);
						gbc_btnAdd.gridx = 0;
						gbc_btnAdd.gridy = 1;
						pnlOverload.add(btnAdd, gbc_btnAdd);
						GridBagConstraints gbc_tbdpnThem = new GridBagConstraints();
						gbc_tbdpnThem.fill = GridBagConstraints.BOTH;
						gbc_tbdpnThem.gridwidth = 2;
						gbc_tbdpnThem.gridx = 0;
						gbc_tbdpnThem.gridy = 16;
						pnlThamso.add(tbdpnThem, gbc_tbdpnThem);


						JMenuBar menuBar = new JMenuBar();
						frmVnetsim.setJMenuBar(menuBar);

						JMenu mnFile = new JMenu("File");
						mnFile.setMnemonic(KeyEvent.VK_F);
						mnFile.setFont(new Font("Tahoma", Font.PLAIN, 12));
						menuBar.add(mnFile);

						JMenuItem mntmNew = new JMenuItem("New",KeyEvent.VK_N);
						mntmNew.setEnabled(false);
						mntmNew.setFont(new Font("Tahoma", Font.PLAIN, 12));
						mnFile.add(mntmNew);

						JMenuItem mntmOpen = new JMenuItem("Open",KeyEvent.VK_O);
						mntmOpen.setEnabled(false);
						mntmOpen.setFont(new Font("Tahoma", Font.PLAIN, 12));
						mnFile.add(mntmOpen);

						JSeparator separator_2 = new JSeparator();
						mnFile.add(separator_2);

						JMenuItem mntmSave = new JMenuItem("Save");
						mntmSave.setEnabled(false);
						mntmSave.setFont(new Font("Tahoma", Font.PLAIN, 12));
						mnFile.add(mntmSave);

						JMenuItem mntmSaveAs = new JMenuItem("Save As");
						mntmSaveAs.setEnabled(false);
						mntmSaveAs.setFont(new Font("Tahoma", Font.PLAIN, 12));
						mnFile.add(mntmSaveAs);

						JSeparator separator_1 = new JSeparator();
						mnFile.add(separator_1);

						JMenuItem mntmExit = new JMenuItem("Exit",KeyEvent.VK_E);
						mntmExit.setIcon(new ImageIcon(frmMain.class.getResource("/VnetsimIcons/iconExit.png")));
						mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
						mntmExit.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								System.exit(0);
							}
						});
						mntmExit.setFont(new Font("Tahoma", Font.PLAIN, 12));
						mnFile.add(mntmExit);

						JMenu mnViews = new JMenu("Views");
						mnViews.setMnemonic(KeyEvent.VK_V);
						mnViews.setFont(new Font("Tahoma", Font.PLAIN, 12));
						menuBar.add(mnViews);

						JMenuItem mntmMatTraffic = new JMenuItem("Traffics",KeyEvent.VK_T);
						mntmMatTraffic.setIcon(new ImageIcon(frmMain.class.getResource("/VnetsimIcons/iconTraffic.png")));
						mntmMatTraffic.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
						mntmMatTraffic.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								frmTraffics matW = new frmTraffics(myGraph);
								matW.setVisible(true);
							}
						});

						JCheckBoxMenuItem chbmLog = new JCheckBoxMenuItem("Log");
						chbmLog.setIcon(new ImageIcon(frmMain.class.getResource("/VnetsimIcons/iconLog.png")));
						chbmLog.addItemListener(new ItemListener() {
							public void itemStateChanged(ItemEvent evt) {
								int state = evt.getStateChange();
								if(state == ItemEvent.SELECTED){
									frmProgress.setVisible(true);
								}
								if(state == ItemEvent.DESELECTED){
									frmProgress.setVisible(false);
								}
							}
						});
						chbmLog.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
						chbmLog.setFont(new Font("Tahoma", Font.PLAIN, 12));
						chbmLog.setSelected(true);
						mnViews.add(chbmLog);
						mntmMatTraffic.setFont(new Font("Tahoma", Font.PLAIN, 12));
						mnViews.add(mntmMatTraffic);

						JMenuItem mntmCosts = new JMenuItem("Costs",KeyEvent.VK_C);
						mntmCosts.setIcon(new ImageIcon(frmMain.class.getResource("/VnetsimIcons/iconCost.png")));
						mntmCosts.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
						mntmCosts.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								frmCosts matC = new frmCosts(myGraph);
								matC.setVisible(true);
							}
						});
						mntmCosts.setFont(new Font("Tahoma", Font.PLAIN, 12));
						mnViews.add(mntmCosts);

						JMenuItem mntmProperties = new JMenuItem("Properties");
						mntmProperties.setIcon(new ImageIcon(frmMain.class.getResource("/VnetsimIcons/iconProperties.png")));
						mntmProperties.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {				
								frmProp = new frmProperties(myGraph);
								frmProp.setVisible(true);			

							}
						});
						mntmProperties.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
						mnViews.add(mntmProperties);

						JSeparator separator_7 = new JSeparator();
						mnViews.add(separator_7);

						JMenuItem mntmChangeLaf = new JMenuItem("Change LAF");
						mntmChangeLaf.setIcon(new ImageIcon(frmMain.class.getResource("/VnetsimIcons/iconChangeLAF.png")));
						mntmChangeLaf.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								Object[] optionLAF = {"Nimbus","Metal","CDE/Motif","Windows","Windows Classic"};
								int item = JOptionPane.showOptionDialog(null,
										"Hãy lựa chọn giao diện chương trình ?",
										"Change Look and Feel",
										0,
										JOptionPane.QUESTION_MESSAGE,
										null,
										optionLAF ,
										"Metal");
								if(item == 0)changeLafTo("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
								if(item == 1)changeLafTo("javax.swing.plaf.metal.MetalLookAndFeel");
								if(item == 2)changeLafTo("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
								if(item == 3)changeLafTo("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
								if(item == 4)changeLafTo("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");

								txtaProgress.append("* Thay đổi Look and Feel ... Xong.\n");

							}
						});
						mntmChangeLaf.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
						mntmChangeLaf.setFont(new Font("Tahoma", Font.PLAIN, 12));
						mnViews.add(mntmChangeLaf);

						JMenu mnHelp = new JMenu("Help");
						mnHelp.setMnemonic(KeyEvent.VK_H);
						mnHelp.setFont(new Font("Tahoma", Font.PLAIN, 12));
						menuBar.add(mnHelp);

						JMenuItem mntmHelpContents = new JMenuItem("Help Contents");
						mntmHelpContents.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								try{
									//Runtime.getRuntime().exec("hh.exe myhelpfile.chm");
									Desktop.getDesktop().open( new File("E:/Practice/Java Project/eclipse workspace/Vnetsim/bin/myhelpfile.chm"));
								}
								catch (Exception e){
									JOptionPane.showMessageDialog(null, "Lỗi");
								}
							}
						});
						mntmHelpContents.setIcon(new ImageIcon(frmMain.class.getResource("/VnetsimIcons/iconHelpContents.png")));
						mntmHelpContents.setFont(new Font("Tahoma", Font.PLAIN, 12));
						mnHelp.add(mntmHelpContents);

						JMenuItem mntmRegister = new JMenuItem("Register");
						mntmRegister.setIcon(new ImageIcon(frmMain.class.getResource("/VnetsimIcons/iconRegister.png")));
						mntmRegister.setEnabled(false);
						mntmRegister.setFont(new Font("Tahoma", Font.BOLD, 12));
						mnHelp.add(mntmRegister);

						JSeparator separator = new JSeparator();
						mnHelp.add(separator);

						JMenuItem mntmAboutUs = new JMenuItem("About us",KeyEvent.VK_A);
						mntmAboutUs.setIcon(new ImageIcon(frmMain.class.getResource("/VnetsimIcons/iconAboutUs.png")));
						mntmAboutUs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
						mntmAboutUs.setFont(myfont);
						mntmAboutUs.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								String mess = "Vnetsim\nversion 11.04"
										+"\nPhần mềm mô phỏng topology mạng phân tán"
										+"\n© Copyright 2011 VnetSim project"
										+"\n http://vcorp.com/VnetSim\n"
										+ "\nTrial 365 days\nVũ Thành Công"
										+"\nvuthanhcong.ict@gmail.com"
										+"\nĐT2 - K52"
										+"\nSchool of Electronics and Telecommunication"
										+"\nHanoi University of Science and Technology";
								JOptionPane.showMessageDialog(null,
										mess,
										"About us",
										JOptionPane.INFORMATION_MESSAGE );
							}
						});
						mnHelp.add(mntmAboutUs);	






	}
	/*
	 * Phương thức xóa các đường đi đã tìm cũ
	 */
	private void clearPath(Graph myGr) {
		for(Edge ed :myGr.getEdges()){
			ed.setPath(false);
		}

	}
	/*
	 * Phương thức update combobox khi đồ thị thay đổi
	 */
	private void updateCombobox(JComboBox cbb) {
		Object[] cbbVertices = myGraph.getVertices().toArray();
		cbb.removeAllItems();
		for(Object node :cbbVertices){
			cbb.addItem(node);
		}
		cbb.setSelectedIndex((int)(Math.random()*myGraph.getVertices().size()));
		cbb.setEditable(false);

	}

	/*
	 * Phương thức lấy đường đi giữa 2 nút
	 * Thuật toán : lấy liên tiếp các nút parent cho tới khi nút parent trùng nhau thì dừng và 
	 * xử lý các danh sách sinh ra
	 */
	private List<Vertex> getPath(Vertex fromNode, Vertex toNode,Graph mGr) {
		List<Vertex> path = new ArrayList<Vertex>();
		List<Vertex> fromPath = new ArrayList<Vertex>();
		List<Vertex> toPath = new ArrayList<Vertex>();
		Vertex tmpNode = fromNode;
		//System.out.println(fromNode.getId()+"<>"+toNode.getId());

		try{
			while(!tmpNode.isCenter()){
				fromPath.add(tmpNode);
				tmpNode = mGr.getVertex(tmpNode.getParentId());
			}
			fromPath.add(tmpNode); //Add center
			tmpNode = toNode;
			while(!tmpNode.isCenter()) {
				toPath.add(tmpNode);
				tmpNode = mGr.getVertex(tmpNode.getParentId());
			}
			toPath.add(tmpNode); //Add center
			//System.out.println("FromPath "+fromPath);
			//System.out.println("ToPath "+toPath);
			// Xử lý những phần trùng
			int lengthTo = toPath.size();
			//System.out.println("Length To = "+lengthTo);
			int indexTo = 0;
			for(Vertex node : fromPath){
				if(toPath.contains(node)){
					indexTo = toPath.indexOf(node);
					path.add(node);
					//System.out.println("Path added node trùng "+node);
					break;
				}else {
					path.add(node);
					//System.out.println("Path added node "+node);
				}
			}
			for(int i =lengthTo-1;i>=indexTo;i--){
				//System.out.println("i = "+i+", length = "+lengthTo);
				toPath.remove(i);
			}
		}catch (Exception e ){
			JOptionPane.showMessageDialog(frmVnetsim, "Đã có lỗi xảy ra !\n"+e.getMessage());
		}
		Collections.reverse(toPath);
		path.addAll(toPath);

		return path;
	}

	public Graph getGraph(){
		return this.myGraph;
	}
	/*
	 * Dùng để update trạng thái của đồ thị khi các thông số thay đổi tức thì
	 */
	private void updateMentorGraph(){
		if(frmProp!=null)frmProp.dispose();

		switch (status) {
		case 1:
			DesignGraph dg1 = new DesignGraph(myGraph, wThreshold, alpha, pc,radius,lengthOfGraph,algorithmSelect,wMax4AN);
			dg1.run();
			updateThongSoHienThi();
			pnlNoidung.newPaint(myGraph, 1);
			frmVnetsim.repaint();
			break;
		case 2:
			DesignGraph dg2 = new DesignGraph(myGraph, wThreshold, alpha, pc,radius,lengthOfGraph,algorithmSelect,wMax4AN);
			dg2.run();
			updateThongSoHienThi();
			pnlNoidung.newPaint(myGraph, 2);
			frmVnetsim.repaint();
			break;
		case 3:
			DesignGraph dg3 = new DesignGraph(myGraph, wThreshold, alpha, pc,radius,lengthOfGraph,algorithmSelect,wMax4AN);
			dg3.run();
			updateThongSoHienThi();
			pnlNoidung.newPaint(myGraph, 3);
			frmVnetsim.repaint();
			break;
		default:
			break;
		}
	}
	/*
	 * Phương thức cập nhật các nhãn
	 */
	private void updateThongSoHienThi(){
		int length = 0;
		for(Edge eg : myGraph.getEdges()){
			if(!eg.getStatus().equals("UNKNOWN")){
				length += eg.getCost();
			}
		}
		lblThongSoHienThi.setText("chiều dài cây = "+length);
		lblPath.setText(null);
	}

	/*
	 * Dùng để cập nhật trạng thái của slider khi khởi tạo đồ thị mới
	 */
	private void updateSlider(){

		int max = myGraph.getMaxOfWeights();
		int min = myGraph.getMinOfWeights();
		sldThreshold.setMaximum(max);
		sldThreshold.setMinimum(min);
		sldThreshold.setValue((int)((max+min)/2));
		sldWmax4AN.setMaximum(6*max);
		sldWmax4AN.setMinimum(2*max);
		sldWmax4AN.setValue(3*max);
		txtaProgress.append("* Mức ngưỡng nằm trong khoảng "+min+" tới "+max+"\n");
	}
	/*
	 * Dùng để thay đổi Look and Feel
	 */
	private void changeLafTo( String cName ) {
		try {
			UIManager.setLookAndFeel( cName );
		}
		catch( Exception e ) {
			JOptionPane.showMessageDialog(null, "Đã có lỗi xảy ra !\nKhông thể thay đổi Look and Feel");
		}
		SwingUtilities.updateComponentTreeUI( this.frmVnetsim );
		SwingUtilities.updateComponentTreeUI( this.frmProgress );
		if(frmProp!=null)SwingUtilities.updateComponentTreeUI( this.frmProp );
	}
}
