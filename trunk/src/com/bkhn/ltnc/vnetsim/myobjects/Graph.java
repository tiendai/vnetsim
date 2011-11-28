/**
 * @(#)Graph.java - định nghĩa đối tượng đồ thị (Graph) 
 *
 * @author : Vũ Thành Công
 *
 * @Purpose: Bài tập lớn tổ chức và quy hoạch mạng viễn thông
 *
 * @Usage: Quản lý các đồ thị được tạo ra
 *
 * @version 2.00 2011/4/12
 */

package com.bkhn.ltnc.vnetsim.myobjects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;
public class Graph {
	/*
	 * Đồ thị là một danh sách các đỉnh và các nút
	 */
	private List<Vertex> vertices;
	private List<Edge> edges;

	// 
	//private Vertex center = new Vertex();

	/*
	 * Các hàm tạo (constructor) của đồ thị
	 */
	public Graph(){
		this.vertices = new ArrayList<Vertex>();
		this.edges = new ArrayList<Edge>();
		this.vertices.clear();
		this.edges.clear();
		Random rd1 = new Random();
		Random rd2 = new Random();

		for(int i=0;i<50;i++){
			Vertex v = new Vertex(i);
			int x = (rd1.nextInt(10)+1)*80;
			int y = (rd2.nextInt(10)+1)*50;
			v.setCoordinate(x, y);
			this.vertices.add(v);
		}
		for(int i=0;i<50;i++){
			for(int j=i+1;j<50;j++){
				Edge e = new Edge(this.vertices.get(i),this.vertices.get(j));
				this.edges.add(e);
			}
		}
		update();
	}
	public Graph(int n){
		this.vertices = new ArrayList<Vertex>();
		this.edges = new ArrayList<Edge>();
		this.vertices.clear();
		this.edges.clear();
		Random rd1 = new Random();
		Random rd2 = new Random();

		try{
			if(n <=0){
				throw new IllegalArgumentException();
			}
			else{
				for(int i=0;i<n;i++){
					Vertex v = new Vertex(i);
					int x = rd1.nextInt(900)+40;
					int y = rd2.nextInt(500)+20;
					v.setCoordinate(x, y);
					this.vertices.add(v);
				}
				for(int i=0;i<n;i++){
					for(int j=i+1;j<n;j++){
						Edge e = new Edge(this.vertices.get(i),this.vertices.get(j));
						this.edges.add(e);
					}
				}
			}
		}
		catch(IllegalArgumentException e){JOptionPane.showMessageDialog(null, "Nhap sai n "+n+e.getMessage());}
		update();
	}
	public Graph(List<Vertex> vertices,List<Edge> edges){
		this.vertices = vertices;
		this.edges = edges;
	}
	/*
	 * Hàm tạo từ hai ma trận : Ma trận giá liên kết (matCost)
	 * & Ma trận lưu lượng (matTraffic) - Hàm này chưa cần dùng đến
	 * Cả hai ma trận đêu là ma trận vuông nxn với n là số đỉnh của đồ thị
	 * Chưa xây dựng hàm kiểm tra nếu nhập sai
	 */
	public Graph(int[][] matCost, int[][] matTraffic, Vertex root){
		int n = matCost.length;			// Số đỉnh = chiều dài ma trận

		vertices = new ArrayList<Vertex>();		// Các đỉnh
		edges = new ArrayList<Edge>();			// Các cạnh
		ArrayList<Edge> tmpEdges = new ArrayList<Edge>();   // Mảng phụ
		/*
		 * Khởi tạo các giá trị Đỉnh (vertex) Cạnh (edge) từ 2 ma trận
		 */
		// Khởi tạo tập các đỉnh
		for(int i = 0;i<n;i++) vertices.add(new Vertex(i));
		for(int i=0;i<n;i++){			
			for(int j=i+1;j<n;j++){
				tmpEdges.add(new Edge( vertices.get(i),
						vertices.get(j),
						matCost[i][j],
						matTraffic[i][j]));					
			}

		}
	              
		/*
		 * Thiết lập trọng số
		 * Trọng số được tính thông qua ma trận lưu lượng
		 * Chỉ tính lưu lượng những cạnh có giá khác 0
		 */
		for(int i = 0;i<n;i++){
			int wi =0;						// Giá trị trọng số nút i
			for(int j=0;j<n;j++) {
				if((i!=j)&&(matCost[i][j]!=0))
					wi += matTraffic[i][j];
			}
			vertices.get(i).setWeight(wi);
		}
		/*
		 * Lọc các cạnh có giá bằng 0
		 */
		for(Edge e : tmpEdges){
			if(e.getCost() != 0)this.edges.add(e);
		}
	}
	/*
	 * Hàm tạo đồ thị từ một ma trận giá liên kết nxn
	 * Khi đó không tính đến lưu lượng và mặc định tất cả trọng số của đỉnh
	 * 		bằng nhau và bằng 1.
	 * Hàm này dùng để test bài tập ví dụ thuật toán Kruskal của thầy
	 */
	public Graph(int[][] matCost, Vertex root){
		int n = matCost.length;					// Số đỉnh = chiều dài ma trận

		vertices = new ArrayList<Vertex>();		// Các đỉnh
		edges = new ArrayList<Edge>();			// Các cạnh
		ArrayList<Edge> tmpEdges = new ArrayList<Edge>(); // Tập chứa các cạnh tạm thời
		/*
		 * Khởi tạo các giá trị Đỉnh (vertex) Cạnh (edge) từ ma trận giá liên kết
		 */
		// Khởi tạo tập các đỉnh
		for(int i =0;i<n;i++) vertices.add(new Vertex(i, 1));
		for(int i=0;i<n;i++){
			for(int j=i+1;j<n;j++){
				tmpEdges.add(new Edge(vertices.get(i),
						vertices.get(j),
						matCost[i][j],
						0));
				/*
				 * Trọng số của nút i được gán mặc định bằng 1
				 * Lưu lượng nút mặc định bằng 0 (!!)
				 */
			}
		}
		/*
		 * Chọn những cạnh có giá khác không lưu vào tập edges
		 */
		for(Edge e : tmpEdges){
			System.out.println("Cost "+e.getCost());
			if(e.getCost() != 0)this.edges.add(e);
		}		
	}		

	/*
	 * Các phương thức
	 */
	/*
	 * Lấy tất cả các đỉnh
	 */
	public List<Vertex> getVertices(){
		return this.vertices;		
	}
	/*
	 * Lấy đỉnh thông qua tên (id)
	 */
	public Vertex getVertex(int name){
		Vertex myVertex = new Vertex();
		for(Vertex v : this.vertices){
			if(v.getId()== name)
				myVertex = v;
		}
		return this.vertices.get(this.vertices.indexOf(myVertex));
	}
	
	public Vertex getVertex(Vertex v){
		return this.getVertex(v.getId());
	}
	
	/*
	 * Lấy tâp các cạnh
	 */
	public List<Edge> getEdges(){
		return this.edges;
	}
	/*
	 * Lấy cạnh thông qua 2 đỉnh
	 */
	public Edge getEdge(Vertex i,Vertex j){
		/*
		 * Do danh sách edges chỉ lưu các cạnh dưới dạng Vi < Vj, 
		 * do đó ta cần sắp xếp lại i,j => phiền quá đi mất !!
		 */
		Vertex a = (i.getId()<j.getId())?i:j;
		Vertex b = (i.getId()<j.getId())?j:i;
		Edge newEdge = new Edge();
		try{
			for(Edge e: this.edges){
				if((e.getSource().getId()== a.getId())&&
						(e.getDestination().getId() == b.getId()))
					newEdge = e;
			}
		}catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Có lỗi xảy ra !\n"+e.getMessage());
		}
		return newEdge;
	}
	// Lấy cạnh thông qua tên 2 đỉnh
	public Edge getEdge(int i, int j){
		return this.getEdge(new Vertex(i),
				new Vertex(j));
	}
	// Lấy số lượng các đỉnh của đồ thị
	public int getNumberOfVertices(){
		return this.vertices.size();
	}
	// Lấy số lượng các cạnh trong đồ thị
	/*
	 * Không tính các cạnh có giá bằng 0 (coi như không có)
	 */
	public int getNumberOfEdges(){
		return edges.size();
	}
	public int getNumberOfEdgesMENTOR(){
		int count =0;
		for(Edge ed: this.getEdges()){
			if(ed.inNetwork())count ++;
		}
		if(count == 0)count = this.getNumberOfEdges();
		return count;
	}
	/*
	 * Lấy giá (Cost) của cạnh
	 */
	public int getCost(Vertex a, Vertex b){
		return this.getEdge(a, b).getCost();
	}

	public Vertex getParent(Vertex v){
		if(v.isCenter()) return v;
		else return this.getVertex(v.getParentId());
	}

	/*
	 * Kiểm tra đồ thị đã kết nối chưa
	 * đồ thị được gọi là kết nối khi tất cả các nút đều đã connected
	 */
	public boolean isConnected(){
		boolean conn = true;
		for(Vertex v : this.vertices){
			if(!v.isConnected()){
				conn = false;
				break;
			}
			else conn = true;
		}
		return conn;
	}
	/*
	 * update các giá trị về lưu lượng, trọng số khi khởi tạo ngẫu nhiên đồ thị
	 */
	private void update(){
		Random rd = new Random();
		for(Edge e : this.edges){
			// Lưu lượng ngẫu nhiên cho từng cạnh, lấy từ 1 tới 4
			int traffic = rd.nextInt(4)+1;
			e.setTraffic(traffic);
			// Trọng số qua một nút bằng tổng lưu lượng
			int wi = e.getSource().getWeight();
			int wj = e.getDestination().getWeight();
			e.getSource().setWeight(wi+traffic);
			e.getDestination().setWeight(wj+traffic);

			/*
			 * Giá liên kết xấp xỉ với khoảng cách
			 * Công thức còn phụ thuộc vào kích cỡ panel
			 */
			int x1 = e.getSource().getX();
			int y1 = e.getSource().getY();
			int x2 = e.getDestination().getX();
			int y2 = e.getDestination().getY();
			int cost = (int)Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2))/80;
			if(cost<1)cost = 1; // Giá nhỏ nhất bằng 1
			e.setCost(cost);

		}

	}
	/*
	 * Lấy các giá trị lớn nhất và nhỏ nhất của trọng số
	 * Sử dụng để thiết lập mức Threshold của mạng đường trục
	 */
	public int getMinOfWeights(){
		return Collections.min(vertices, Vertex.WeightComparator).getWeight();
	}
	public int getMaxOfWeights(){
		return Collections.max(vertices,Vertex.WeightComparator).getWeight();
	}








}
