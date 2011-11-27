/**
 * @(#)EsausWilliamsAlgorithm.java - Class xây dựng cây CMST dựa vào thuật toán Prim
 *
 * @author : Vũ Thành Công
 *
 * @Purpose: Bài tập lớn môn Tổ chức va quy hoạch mạng viễn thông
 *
 * @Usage: Sử dụng để xây dựng mạng Access Network
 *
 * @version 1.0 2011/4/18 
 */

package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import myobjects.Edge;
import myobjects.Graph;
import myobjects.Vertex;

public class EsausWilliamsAlgorithm extends Thread{
	private final Graph myGraph;
	private final List<Edge> edges;			// Tập các cạnh
	private final List<Vertex> nodes;		// Tập các đỉnh
	private Vertex source;					// Nút nguồn
	private int wmax;						// Wmax

	private Set<Edge> MstEdges;
	private Set<Vertex> MstNodes;

	private Map<Vertex,Integer> distance;		// Giá của 1 nút tới nguồn
	private Map<Vertex,Set<Vertex>> setMST;
	private Map<Vertex,Set<Vertex>> settledNodes; 
	private Map<Vertex,Integer> tradeOffValue;	// Hàm trade off của một nút (chứa giá trị)
	private Map<Vertex,Edge> tradeOffEdge;		// Hàm trade off của một nút (chứa cạnh)

	public EsausWilliamsAlgorithm(Graph graph,int wmax,Vertex source){
		this.myGraph = graph;
		this.nodes = new ArrayList<Vertex>(graph.getVertices());
		this.edges = new ArrayList<Edge>(graph.getEdges());
		this.wmax = wmax;
		this.source = source;
	}
	public void execute(){

		initialize();
		System.out.println("distance\n"+distance);
		System.out.println("Tradeoff Value\n"+tradeOffValue);

		while(MstEdges.size() < nodes.size()-1){
			Vertex minimum = getMinTradeOff();
			System.out.println("Min = "+minimum);
			System.out.println("TradeOff = "+tradeOffValue.get(minimum));
			if(tradeOffValue.get(minimum)<0){	// Nếu Trade off < 0
				System.out.println("TO < 0");
				if(satisfiedWeight(minimum)){	// Nếu Thỏa mãn điều kiện trọng số
					// Thêm liên kết MN
					MstEdges.add(tradeOffEdge.get(minimum));
					MstNodes.add(minimum);
					// Bỏ Liên kết M0
					updateTradeOff(minimum);
					// Tính toán giá trị tổng
				}else {
					// Nếu ko thỏa mãn đk trọng số ==> rejected
					System.out.println(minimum+" Khong thoa man dieu kien trong so!");

					rejectTradeOff(minimum);
					// Quay lại
				}
			} else {
				// Nếu Trade off >=0 
				// Nối nút nguồn tới mọi nút chưa kết nối và dừng
				finishCMST();
			}
		}
	}

	private void finishCMST() {
		System.out.println("Finish CMST!");
		for(Edge edge : MstEdges){
			edge.setStatus("AN");			
		}
		for(Vertex node : this.nodes){
			if(!MstNodes.contains(node)){
				Edge me = myGraph.getEdge(source, node);
				me.setStatus("AN");
				MstEdges.add(me);
			}
		}

	}
	private void rejectTradeOff(Vertex minimum) {
		System.out.println("Chay rejectTradeOff");

		Set<Vertex> rejected = settledNodes.get(minimum);
		Edge minEdge = tradeOffEdge.get(minimum);
		Vertex target = (minEdge.getSource().equals(minimum))? minEdge.getDestination():minEdge.getSource();

		rejected.add(target);

		settledNodes.remove(minimum);
		settledNodes.put(minimum, rejected);

		// cập nhật distance
		
		distance.remove(minimum);
		// Get mincost
		Vertex minCost = null;
		for(Vertex node : this.nodes){
			if(!settledNodes.get(minimum).contains(node)){
				if(minCost == null)minCost = node;
				else {
					if(myGraph.getCost(minimum, node) < myGraph.getCost(minimum, target)){
						target = node;
					}
				}
			}
		}
		int newDistance =  myGraph.getCost(minimum, minCost);
		distance.put(minimum, newDistance);
		// Tìm nút hàng xóm có giá nhỏ nhất
		Vertex newMin = null;
		for(Vertex node : minimum.getNeighbours(myGraph)){
			if(!(settledNodes.get(minimum)).contains(node)){
				if(newMin == null)newMin = node;
				else {
					if(myGraph.getCost(minimum, node) < myGraph.getCost(minimum, target)){
						newMin = node;
					}
				}
			}
		}
		// Tính lại Giá trị Trade Off
		Edge newEdge = myGraph.getEdge(minimum, newMin);
		int newTradeOffValue = newEdge.getCost() - distance.get(minimum);
		// Cập nhật TradeOff
		tradeOffValue.remove(minimum);
		tradeOffValue.put(minimum, newTradeOffValue);

		tradeOffEdge.remove(newEdge);
		tradeOffEdge.put(minimum, newEdge);

	}
	private void updateTradeOff(Vertex minimum) {
		System.out.println("Chay updateTradeOff");

		Set<Vertex> rejected = settledNodes.get(minimum);
		Edge minEdge = tradeOffEdge.get(minimum);
		Vertex target = (minEdge.getSource().equals(minimum))? minEdge.getDestination():minEdge.getSource();

		rejected.add(target);

		settledNodes.remove(minimum);
		settledNodes.put(minimum, rejected);

		//Edge minEdge = tradeOffEdge.get(minimum);		
		//Vertex target = (minEdge.getSource().equals(minimum))? minEdge.getDestination():minEdge.getSource();
		// cập nhật distance
		distance.remove(minimum);
		int newDistance = distance.get(target);
		distance.put(minimum, newDistance);
		// Tìm nút hàng xóm có giá nhỏ nhất
		Vertex newMin = null;
		for(Vertex node : this.nodes){
			if(!(settledNodes.get(minimum)).contains(node)){
				if(newMin == null)newMin = node;
				else {
					if(myGraph.getCost(minimum, node) < myGraph.getCost(minimum, target)){
						newMin = node;
					}
				}
			}
		}
		// Tính lại Giá trị Trade Off
		Edge newEdge = myGraph.getEdge(minimum, newMin);
		int newTradeOffValue = newEdge.getCost() - distance.get(minimum);
		// Cập nhật TradeOff
		tradeOffValue.remove(minimum);
		tradeOffValue.put(minimum, newTradeOffValue);

		tradeOffEdge.remove(newEdge);
		tradeOffEdge.put(minimum, newEdge);

	}
	private boolean satisfiedWeight(Vertex minimum) {
		// TODO Auto-generated method stub
		return true;
	}

	private Vertex getMinTradeOff() {
		System.out.println("Chay getMinTradeOff");
		Vertex minimum = null;
		for(Vertex node : this.nodes){			
			if(!node.equals(source)){
				System.out.println("Xet node "+node);
				if(minimum == null)minimum = node;
				else {
					System.out.println("Trade Off :"+node+"="+tradeOffValue.get(node)+",minimum = "+minimum+"="+tradeOffValue.get(minimum));
					if(tradeOffValue.get(node) < tradeOffValue.get(minimum)){
						minimum = node;
						System.out.println("< OK, minimum = "+minimum);
					}
				}
			}
		}
		return minimum;
	}
	private Vertex getMinCost(Vertex src){
		Vertex target = null;
		for(Vertex node : this.nodes){
			if((!node.equals(source))&&(!node.equals(src))){
				if(target == null)target = node;
				else {
					if(myGraph.getCost(src, node) < myGraph.getCost(src, target)){
						target = node;
					}
				}
			}
		}
		return target;
	}
	private void initialize() {
		// khởi tạo 
		MstEdges = new HashSet<Edge>();
		MstNodes = new HashSet<Vertex>();
		tradeOffValue = new HashMap<Vertex, Integer>();
		tradeOffEdge = new HashMap<Vertex, Edge>();
		distance = new HashMap<Vertex, Integer>();
		setMST = new HashMap<Vertex, Set<Vertex>>();
		settledNodes = new HashMap<Vertex, Set<Vertex>>();
		for(Vertex node : this.nodes){
			if(!node.equals(source)){

				int dis = myGraph.getCost(node, source);
				distance.put(node, dis);

				Vertex minCost = getMinCost(node);
				Edge minEdge = myGraph.getEdge(minCost, node);
				tradeOffEdge.put(node, minEdge);

				int tradeOff =  minEdge.getCost() - dis;
				tradeOffValue.put(node, tradeOff);


				Set<Vertex> mst =  new HashSet<Vertex>();
				mst.add(node);
				setMST.put(node, mst);

				Set<Vertex> rejected = new HashSet<Vertex>();
				rejected.add(node);
				rejected.add(source);
				settledNodes.put(node, rejected);
			}
		}

	}
	public void run(){
		this.execute();
	}

}
