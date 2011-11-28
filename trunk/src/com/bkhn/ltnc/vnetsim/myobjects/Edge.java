/**
 * @(#)Edge.java - Class định nghĩa các cạnh của đồ thị
 *
 * @author:	Vũ Thành Công
 *
 * @Purpose: Bài tập lớn môn Tổ chức và quy hoạch mạng viễn thông
 *
 * @Usage: Khởi tạo các cạnh trong đồ thị
 * 
 * @version 2.0 2011/4/12
 */

package com.bkhn.ltnc.vnetsim.myobjects;

import java.util.Comparator;

public class Edge implements Comparable<Object>{
	/*
	 * Các thuộc tính của đối tượng cạnh
	 */
	private Vertex source;					// Đỉnh bắt đầu của cạnh
	private Vertex destination;     		// Đỉnh kết thúc của cạnh

	private int cost = 0; 					// Giá của cạnh
	private int traffic = 0;				// Lưu lượng của cạnh (2 chiều)

	private String status = "UNKNOWN";		// Trạng thái của cạnh : UNKNOWN, AN,BN
	private boolean isPath = false; 		// Cạnh nằm trên đường đi (Dùng để vẽ khi tìm đường
											// đi giữa 2 nút

	/* 
	 * Trong phạm vi bài tập lớn này chúng ta chỉ xét tới cạnh luôn có lưu lượng 2 chiều bằng nhau
	 * Cạnh ij và cạnh ji là gống nhau
	 * Ma trận Giá liên kết và ma trận Lưu lượng là Ma trận 2 chiều đối xứng
	 */

	// Hàm khởi tạo (Constructor)
	public Edge(){
		this.source = new Vertex();
		this.destination = new Vertex();
	}
	public Edge(Edge e){
		this.source = new Vertex(e.getSource());
		this.destination = new Vertex(e.getDestination());
		this.cost = e.getCost();
		this.traffic = e.getTraffic();
	}
	public Edge(Vertex vI,Vertex vJ){
		this.source = (vI.getId()<vJ.getId())?vI:vJ;
		this.destination = (vI.getId()<vJ.getId())?vJ:vI;
	}
	public Edge(int vI, int vJ){
		this.source = (vI<vJ)?(new Vertex(vI)):(new Vertex(vJ));
		this.destination = (vI<vJ)?(new Vertex(vJ)):(new Vertex(vI));
	}
	public Edge(Vertex vI,Vertex vJ,int cost, int traffic){
		this.source = (vI.getId()<vJ.getId())?vI:vJ;
		this.destination = (vI.getId()<vJ.getId())?vJ:vI;
		this.cost = cost;
		this.traffic = traffic;
	}
	/*
	 * Phương thức so sánh thông qua giá (Cost)
	 */
	public int compareTo(Object obj){
		if(this.cost == ((Edge)obj).getCost())return 0;
		else if(this.cost > ((Edge)obj).getCost())
			return 1;
		else return -1;
	}

	public static Comparator<Object> CostComparator = new Comparator<Object>(){
		public int compare(Object o1,Object o2){
			int e1 = ((Edge)o1).getCost();
			int e2 = ((Edge)o2).getCost();
			if(e1>e2)return 1;
			else if(e1<e2)return -1;
			else return 0;
		}
	};
	/*
	 * Các phương thức nhập xuất
	 * Các phương thức set dùng khi nhập các giá trị bằng tay cho đồ thị,
	 * tuy nhiên chưa cần dùng đến do tất cả các giá trị đều lấy ngẫu nhiên
	 */
	public int getCost(){					// Lấy giá của liên kết i-j
		return cost;
	}
	public void setCost(int cost){			// Thiết lập giá của liên kết i-j
		this.cost = cost;					
	}
	public int getTraffic(){				// Lấy giá trị lưu lượng của cạnh i->j
		return traffic;						// Bằng lưu lượng j->i	
	}
	public void setTraffic(int traffic){    // Thiết lập giá trị lưu lượng của liên kết
		this.traffic = traffic;
	}

	public Vertex getSource(){				// Lấy nút khởi đầu
		return this.source;
	}
	public Vertex getDestination(){			// Lấy nút kết thúc 
		return this.destination;
	}

	public String getStatus() {				// Lấy trạng thái của liên kết
		return status;
	}

	public void setStatus(String stt) {		// Cập nhật trạng thái cho liên kết
		if(stt.equals("AN"))this.status="AN";
		else if(stt.equals("BN"))this.status="BN";
		else this.status="UNKNOWN";

	}

	public boolean inBackboneNetwork(){		// Kiểm tra cạnh thuộc Backbone Network (BN)
		return this.status.equals("BN");
	}
	public boolean inAccessNetwork(){		// Kiểm tra cạnh thuốc Access Network (AN)
		return this.status.equals("AN");
	}
	public boolean inNetwork(){				// Kiểm tra cạnh thuộc đồ thị đã xây dựng
		return (!this.status.equals("UNKNOWN"));
	}
	
	public boolean isPath(){				// Cạnh nằm trên đường đi
		return this.isPath;
	}
	public void setPath(boolean ispath){
		this.isPath = ispath;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * Dùng để kiểm tra chương trình khi chạy lỗi
	 */
	@Override
	public String toString(){
		String  txt = new
		StringBuilder("[ ").append(this.source.getId()).append("<=>").
		append(this.destination.getId()).append(" , Cost = ").
		append(this.cost).append(" ]").toString();
		return txt;
	}

}
