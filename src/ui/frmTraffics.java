/**
 * @(#)frmTraffics.java - Frame hiển thị ma trận lưu lượng
 *
 * @author : Vũ Thành Công
 *
 * @Purpose: Bài tập lớn môn Tổ chức va quy hoạch mạng viễn thông
 *
 * @Usage: Sử dụng để xem cho biết
 *
 * @version 2.0 2011/4/12 
 */
package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.table.TableColumn;

import myobjects.*;
import java.awt.Toolkit;

public class frmTraffics extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public frmTraffics(Graph myGraph){
		super("Bảng ma trận lưu lượng ");
		setIconImage(Toolkit.getDefaultToolkit().getImage(frmTraffics.class.getResource("/VnetsimIcons/iconTraffic.png")));
		setSize(new Dimension(1200,700));

		int side = myGraph.getNumberOfVertices();
		String[][] data = new String[side][side+1];
		String [] columnNames = new String[side+1];

		columnNames[0]="X";
		int countColumn =0;		
		for(Vertex node : myGraph.getVertices()){
			columnNames[countColumn+1] = "V"+node.getId();
			int countRow = 0;
			data[countColumn][countRow] = "V"+node.getId();
			for(Vertex target : myGraph.getVertices()){
				String text;
				int traffic =0;
				if(node.equals(target)) text = "-";
				else {
					traffic = myGraph.getEdge(node, target).getTraffic();
					text = ""+traffic;
				}
				data[countColumn][countRow+1]= text;
				countRow ++;
			}
			countColumn++;
		}

		JTable tblTraffic = new JTable(data,columnNames){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int rowIndex, int colIndex) {
		        return false;   //Disallow the editing of any cell
		    }
		};
		TableColumn column = null;
		for (int i = 0; i <= side; i++) {
			column = tblTraffic.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(100);
			} else {
				column.setPreferredWidth(50);
			}
		} 

		JScrollPane scr = new JScrollPane(tblTraffic,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tblTraffic.setFillsViewportHeight(true);
		getContentPane().add(scr,BorderLayout.CENTER);
	}
}
