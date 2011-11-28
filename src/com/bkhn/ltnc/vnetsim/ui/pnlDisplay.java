/**
 * @(#)pnlDisplay.java - Định nghĩa panel chứa các hàm paint ra đồ thị
 *
 * @author : Vũ Thành Công
 *
 * @Purpose: Bài tập lớn môn Tổ chức va quy hoạch mạng viễn thông
 *
 * @Usage: Sử dụng để hiển thị đồ thị dưới dạng đồ họa
 *
 * @version 2.1 2011/11/27 
 */
package com.bkhn.ltnc.vnetsim.ui;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import com.bkhn.ltnc.vnetsim.myobjects.*;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
public class pnlDisplay extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Graph myGraph;

	private int paintOption = 0;

	final static BasicStroke strokeAN = new BasicStroke(1.0f);
	final static BasicStroke strokeBN = new BasicStroke(1.25f);
	final static BasicStroke strokePath = new BasicStroke(2.0f);

	public pnlDisplay() {
		super();
		this.myGraph = new Graph(50);
		final JTextArea nodeContent = new JTextArea();
		nodeContent.setFont(new Font("Tahoma", Font.ITALIC, 9));
		nodeContent.setEditable(false);
		final JPopupMenu popup = new JPopupMenu();
		popup.add(nodeContent);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				setToolTipText(null);
				int x = evt.getX();
				int y = evt.getY();
				Vertex target = null;
				for(Vertex node : myGraph.getVertices()){
					if((Math.abs(node.getX()-x)<=6)&&(Math.abs(node.getY()-y)<=6)){
						target = node;
						break;
					}						
				}

				if(target!=null){
					String endline = " \n";
					nodeContent.setText("Vertex "+target.getId()+endline+
										"Weight = "+target.getWeight()+endline+
										"Status = "+target.getStatus()+endline);					
					if(paintOption!=0){
						nodeContent.append("Parent = "+target.getParentId()+endline);
						nodeContent.append("Neighbours :"+endline);
						ArrayList<Vertex> neighbours = target.getNeighboursMENTOR(myGraph);
						for(Vertex nb : neighbours){
							nodeContent.append(nb+endline);
						}
					}
					
					popup.show(evt.getComponent(), x, y);
					
				}
			}
		});
		
	}
	public pnlDisplay(Graph myGraph){
		super();
		this.myGraph = new Graph(50);
		this.paintOption = 0;
	}
	@Override
	public void paintComponent(Graphics g){
		clear(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		//g2d.setStroke(strokeAN);
		g2d.setFont(new Font("Tahoma", Font.BOLD, 9));

		switch (paintOption) {
		case 1:	// Hiện thị cả Backbone network và Access network
			for(Edge e : myGraph.getEdges()){
				if(e.inNetwork()){
					int x1 = e.getSource().getX();
					int y1 = e.getSource().getY();
					int x2 = e.getDestination().getX();
					int y2 = e.getDestination().getY();
					if(e.inBackboneNetwork())
					{
						g2d.setStroke(strokeBN);
						g2d.setColor(Color.BLACK);
						g2d.drawLine(x1, y1, x2, y2);
					}
					else{
						g2d.setStroke(strokeAN);
						g2d.setColor(Color.GREEN);
						g2d.drawLine(x1, y1, x2, y2);
					}
				}
			}
			// Vẽ các nút sau
			g2d.setStroke(strokeAN);
			for(Vertex myVertex: myGraph.getVertices()){
				int x = myVertex.getX()-6;
				int y = myVertex.getY()-6;
				if(myVertex.isCenter()){
					g2d.setColor(Color.RED);
					g2d.fill(new Rectangle2D.Float(x,y,12,12));
				}else if(myVertex.isBackbone()){
					g2d.setColor(Color.MAGENTA);
					g2d.fill(new Ellipse2D.Float(x,y,12,12));
				}else{
					g2d.setColor(Color.BLUE);
					g2d.fill(new Ellipse2D.Float(x,y,12,12));
				}                            
				g2d.drawString(""+myVertex.getId(),x+15, y+15);				
			}
			break;

		case 2: 	// Chỉ vẽ Backbone Network
			for(Edge e : myGraph.getEdges()){
				if(e.inBackboneNetwork()){
					int x1 = e.getSource().getX();
					int y1 = e.getSource().getY();
					int x2 = e.getDestination().getX();
					int y2 = e.getDestination().getY();

					g2d.setStroke(strokeBN);
					g2d.setColor(Color.BLACK);
					g2d.drawLine(x1, y1, x2, y2);

				}
			}
			// Vẽ các nút sau
			g2d.setStroke(strokeAN);
			for(Vertex myVertex: myGraph.getVertices()){
				int x = myVertex.getX()-6;
				int y = myVertex.getY()-6;
				if(myVertex.isCenter()){
					g2d.setColor(Color.RED);
					g2d.fill(new Rectangle2D.Float(x,y,12,12));
				}else if(myVertex.isBackbone()){
					g2d.setColor(Color.MAGENTA);
					g2d.fill(new Ellipse2D.Float(x,y,12,12));
				}else{
					g2d.setColor(Color.BLUE);
					g2d.fill(new Ellipse2D.Float(x,y,12,12));
				}                            
				g2d.drawString(""+myVertex.getId(),x+15, y+15);
			}
			break;
		case 3:	// Vẽ lại đồ thị và cả đường đi
			for(Edge e : myGraph.getEdges()){
				if(e.inNetwork()){
					int x1 = e.getSource().getX();
					int y1 = e.getSource().getY();
					int x2 = e.getDestination().getX();
					int y2 = e.getDestination().getY();
					if(e.inBackboneNetwork())
					{
						if(e.isPath()){
							g2d.setStroke(strokePath);
							g2d.setColor(Color.RED);
						}else {
							g2d.setStroke(strokeBN);
							g2d.setColor(Color.BLACK);
						}
						
						g2d.drawLine(x1, y1, x2, y2);
					}
					else{
						if(e.isPath()){
							g2d.setStroke(strokePath);
							g2d.setColor(Color.RED);
						}else {
							g2d.setStroke(strokeBN);
							g2d.setColor(Color.GREEN);
						}
						g2d.drawLine(x1, y1, x2, y2);
					}
				}
			}
			// Vẽ các nút
			g2d.setStroke(strokeAN);
			for(Vertex myVertex: myGraph.getVertices()){
				int x = myVertex.getX()-6;
				int y = myVertex.getY()-6;
				if(myVertex.isCenter()){
					g2d.setColor(Color.RED);
					g2d.fill(new Rectangle2D.Float(x,y,12,12));
				}else if(myVertex.isBackbone()){
					g2d.setColor(Color.MAGENTA);
					g2d.fill(new Ellipse2D.Float(x,y,12,12));
				}else{
					g2d.setColor(Color.BLUE);
					g2d.fill(new Ellipse2D.Float(x,y,12,12));
				}
				//g2d.setFont(new Font("Tahoma", Font.BOLD, 9));
				g2d.drawString(""+myVertex.getId(),x+15, y+15);
				//g2d.setFont(new Font("Tahoma", Font.PLAIN, 8));
				//g2d.setColor(Color.BLACK);
				//g2d.drawString("w ="+myVertex.getWeight(),x+30, y+15);
			}
			break;

		default:	// Vẽ đồ thị thông thường
			g2d.setStroke(strokeAN);
			for(Edge e : myGraph.getEdges()){
				int x1 = e.getSource().getX();
				int y1 = e.getSource().getY();
				int x2 = e.getDestination().getX();
				int y2 = e.getDestination().getY();
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.drawLine(x1, y1, x2, y2);
			}
			// Vẽ các nút sau
			for(Vertex myVertex: myGraph.getVertices()){
				int x = myVertex.getX()-6;
				int y = myVertex.getY()-6;
				g2d.setColor(Color.BLUE);
				g2d.fill(new Ellipse2D.Float(x,y,12,12));
				g2d.drawString(""+myVertex.getId(),x+15, y+15);
			}
			break;
		}

	}
	protected void clear(Graphics g){
		super.paintComponents(g);
	}
	public void setPaintOption(int value){
		this.paintOption = value;
	}

	public void setGraph(Graph gr){
		this.myGraph = gr;
		System.out.println("Cập nhật đồ thị mới in pnl");
	}
	public void newPaint(Graph myGraph, int paintOption){
		// Truyền tham số Graph mới
		this.myGraph = myGraph;
		this.paintOption = paintOption;
		repaint();
	}

}
