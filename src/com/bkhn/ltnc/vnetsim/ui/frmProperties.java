/**
 * @(#)frmProperties.java - Frame hiển thị thông tin chung của đồ thị và từng đỉnh
 *
 * @author : Vũ Thành Công
 *
 * @Purpose: Bài tập lớn môn Tổ chức va quy hoạch mạng viễn thông
 *
 * @Usage: Sử dụng để tối ưu các đồ thị
 *
 * @version 2.0 2011/4/12 
 */
package com.bkhn.ltnc.vnetsim.ui;

import java.awt.Dimension;

import javax.swing.JFrame;


import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;

import com.bkhn.ltnc.vnetsim.myobjects.Graph;
import com.bkhn.ltnc.vnetsim.myobjects.Vertex;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.awt.Toolkit;

public class frmProperties extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Graph myGraph;
	private JTextArea txtaGraph;
	private JTextArea txtaVertex;

	public frmProperties(Graph mGraph){
		super("Xem Thông tin đồ thị");
		setIconImage(Toolkit.getDefaultToolkit().getImage(frmProperties.class.getResource("/VnetsimIcons/iconProperties.png")));
		
		setTitle("Properties");
		setSize(new Dimension(300,500));

		this.myGraph = mGraph;

		txtaGraph = new JTextArea();
		JScrollPane scrGraph = new JScrollPane(txtaGraph);
		txtaGraph.append("* Đồ thị có "+myGraph.getNumberOfVertices()+" đỉnh\n");
		txtaGraph.append("* Đồ thị có "+myGraph.getNumberOfEdges()+" cạnh lúc mới khởi tạo\n");		
		txtaGraph.append("* Sau khi tối ưu, đồ thị còn lại "+myGraph.getNumberOfEdgesMENTOR()+" Cạnh");

		txtaGraph.setEditable(false);

		JLabel lblXemThnTin = new JLabel("Thông tin đỉnh :");
		lblXemThnTin.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JLabel lblThngTin = new JLabel("Thông tin đồ thị : ");
		lblThngTin.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JScrollPane scrollPane = new JScrollPane();
		txtaVertex = new JTextArea();
		txtaVertex.setEditable(false);
		scrollPane.setViewportView(txtaVertex);

		int count =0;
		String[] sVertices = new String[myGraph.getNumberOfVertices()];
		for(Vertex node : myGraph.getVertices()){
			sVertices[count] = "Vertex "+node.getId();
			count++;
		}
		final JComboBox cbbVertices = new JComboBox(sVertices);
		cbbVertices.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				int index = cbbVertices.getSelectedIndex();
				Vertex node = myGraph.getVertex(index);
				txtaVertex.setText(""+index);
				updateProperties(node);				
			}

		});
		cbbVertices.setSelectedIndex(0);
		updateProperties(myGraph.getVertex(0));





		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
										.addComponent(lblThngTin)
										.addContainerGap(472, Short.MAX_VALUE))
										.addGroup(groupLayout.createSequentialGroup()
												.addComponent(lblXemThnTin)
												.addPreferredGap(ComponentPlacement.UNRELATED)
												.addComponent(cbbVertices, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
												.addContainerGap(367, Short.MAX_VALUE))
												.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
														.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
																.addComponent(scrGraph, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
																.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE))
																.addContainerGap())))
		);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(6)
						.addComponent(lblThngTin)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(scrGraph, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblXemThnTin)
								.addComponent(cbbVertices, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
								.addContainerGap())
		);


		getContentPane().setLayout(groupLayout);
	}
	private void updateProperties(Vertex node) {
		txtaVertex.setText("* Id = "+node.getId());
		txtaVertex.append("\n* Weight = "+node.getWeight());
		txtaVertex.append("\n* Status = "+ node.getStatus());
		txtaVertex.append("\n* Predecessor = Vertex "+node.getParentId());

		ArrayList<Vertex> neighbours = node.getNeighboursMENTOR(myGraph);
		if(neighbours.isEmpty()){
			neighbours= node.getNeighbours(myGraph); 
		}
		txtaVertex.append("\n* Có "+neighbours.size()+" nút hàng xóm là :");
		for(Vertex ve:neighbours){
			txtaVertex.append("\n** Vertex "+ve.getId()+" : "+myGraph.getEdge(ve, node));
		}


	}
}
