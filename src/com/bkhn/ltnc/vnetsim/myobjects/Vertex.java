/**
 * @(#)Vertex.java - Class định nghĩa đối tượng đỉnh (vertex)
 *
 * @author : Vũ Thành Công
 *
 * @Purpose: Bài tập lớn môn Tổ chức va quy hoạch mạng viễn thông
 *
 * @Usage: Sử dụng để khởi tạo các đỉnh trong đồ thị
 *
 * @version 2.0 2011/4/12
 */

package com.bkhn.ltnc.vnetsim.myobjects;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Vertex implements Comparable<Object> {

	private int id;							// Tên của đỉnh
	private int weight;						// Trọng số của đỉnh

	private String status = "UNKNOWN";     	// Trạng thái nút
											// Có 4 trạng thái là UNKNOWN, NORMAL, BACKBONE, CENTER
											//

	private int parentId = -1;              // Tên Nút cha
	/*
	 * Nếu là nút backbone thì sẽ có một tập các nút access
	 */
	private final Set<Vertex> access;
	/*
	 * Các giá trị thể hiện vị trí của nút
	 * Dùng để vẽ
	 */
	private int x;      //Hoành độ
	private int y;      //Tung độ

	/**
	 * khoảng cách từ nút tới nút center
	 * Chỉ sử dụng trong phương thức mentor.makeCMST();
	 */
	private double distance =
			Double.MAX_VALUE;
	//Khoảng cách tới nút Center, defaul = vô cùng

	/*
	 * Các hàm khởi tạo
	 */
	public Vertex() {
		this.id = 0;
		this.weight = 0;
		access = new HashSet<Vertex>();
	}
	public Vertex(int name){
		this.id = name;
		access = new HashSet<Vertex>();
	}
	public Vertex(int name, int weight) {
		this.id = name;
		this.weight = weight;
		access = new HashSet<Vertex>();
	}
	public Vertex(Vertex v){
		this.id = v.getId();
		this.weight = v.getWeight();
		access = new HashSet<Vertex>();
	}
	/*
	 * Phương thức để gọi hàm so sánh theo tên
	 */
	@Override
	public int compareTo(Object obj){
		if(this.id ==((Vertex)obj).getId())
			return 0;
		else if(this.id>((Vertex)obj).getId())
			return 1;
		else return -1;
	}
	/*
	 * Phương thức để gọi hàm so sánh theo trọng số
	 */
	public static Comparator<Object> WeightComparator = new Comparator<Object>(){
		@Override
		public int compare(Object o1,Object o2){
			int v1 = ((Vertex)o1).getWeight();
			int v2 = ((Vertex)o2).getWeight();
			if(v1>v2)return 1;
			else if(v1<v2)return -1;
			else return 0;
		}
	};
	/*
	 * Phương thức so sánh theo chiều dài đến nút center
	 */
	public static Comparator<Object> DistanceComparator = new Comparator<Object>(){
		@Override
		public int compare(Object o1,Object o2){
			double v1 = ((Vertex)o1).getDistance();
			double v2 = ((Vertex)o2).getDistance();
			if(v1>v2)return 1;
			else if(v1<v2)return -1;
			else return 0;
		}
	};
	/*
	 * Các phương thức thông thường
	 */
	// Name
	public int getId(){
		return this.id;
	}
	public void setId(int name){
		this.id = name;
	}
	// Weight
	public int getWeight(){
		return this.weight;
	}
	public void setWeight(int weight){
		this.weight = weight;
	}
	// Length to Center
	public double getDistance(){
		return this.distance;
	}
	public void setDistance(double value){
		this.distance = value;
	}
	// Parent
	public int getParentId(){
		return parentId;
	}
	public void setParent(Vertex parent){
		this.parentId = parent.getId();
	}
	// status
	public String getStatus(){
		return this.status;
	}
	public void setStatus(String type){
		String[] mStatus = new String[]{"UNKNOWN",
				"NORMAL",
				"CENTER",
		"BACKBONE"};
		if(type.equals(mStatus[1]))this.status=mStatus[1];
		else if(type.equals(mStatus[2]))this.status=mStatus[2];
		else if(type.equals(mStatus[3]))this.status=mStatus[3];
		else this.status=mStatus[0];

	}
	public boolean isCenter(){
		return this.status.equals("CENTER");
	}
	public boolean isBackbone(){
		return this.status.equals("BACKBONE");
	}
	public boolean isConnected(){
		return (!this.status.equals("UNKNOWN"));
	}

	/*
	 * Các hàm thiết lập vị trí của nút trong đồ thị, sử dụng để vẽ và
	 * để tạo các giá trị ngẫu nhiên cho trọng số, giá, lưu lượng ..v.v
	 */
	public int getX(){
		return this.x;
	}
	public void setX(int value){
		this.x = value;
	}
	public int getY(){
		return this.y;
	}
	public void setY(int value){
		this.y = value;
	}
	public void setCoordinate(int x,int y){
		this.x = x;
		this.y = y;
	}
	/*
	 * Lấy tập các nút access trực thuộc (chỉ các nút backbone mới có )
	 */
	public Set<Vertex> getAccess(){
		return this.access;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if(id != other.id)return false;
		else if(weight!=other.weight)return false;
		else return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 59 * hash + this.id;
		return hash;
	}

	@Override
	public String toString(){
		return "[ "+this.id+" , w = "+this.weight+" ]";
	}

	/*
	 * Lấy tập các nút hàng xóm của một nút với đồ thị tương ứng
	 * Là các nút nằm trong đồ thị và có tồn tại cạnh kết nối đến nó
	 */
	public ArrayList<Vertex> getNeighbours(Graph myGraph){
		ArrayList<Vertex> neighbours = new ArrayList<Vertex>();
		for(Vertex v: myGraph.getVertices()){
			if(myGraph.getEdge(this, v).getCost()!=0)
				neighbours.add(v);
		}
		return neighbours;
	}

	/*
	 * Lấy tập các nút của đồ thị mà đã được xây dựng
	 *
	 */
	public ArrayList<Vertex> getNeighboursMENTOR(Graph myGraph){
		ArrayList<Vertex> neighbours = new ArrayList<Vertex>();
		for(Vertex v: myGraph.getVertices()){
			Edge ed = myGraph.getEdge(this, v);
			if((ed.getCost()!=0)&&(ed.inNetwork()))
				neighbours.add(v);
		}
		return neighbours;
	}

	/*
	 * Kiểm tra xem nút có thuộc vòng tròn của nút center hay không
	 * Từ nút center quay vòng tròn bán kính R,
	 * nếu khoảng cách từ nút đó tới center nhỏ hơn thì trả về true
	 */
	public boolean inCircle(Vertex center, int radius){
		int xl = Math.abs(center.getX()-this.x);
		int yl = Math.abs(center.getY()-this.y);
		int mdistance = (int)(Math.sqrt(xl*xl+yl*yl)/80);
		if(mdistance<1) mdistance=1;
		if(mdistance<radius)return true;
		else return false;
	}


}