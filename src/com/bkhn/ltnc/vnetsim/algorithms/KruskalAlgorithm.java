/**
 * @(#)KruskalAlgorithm.java - Class xây dựng cây CMST dựa vào thuật toán Kruskal
 *
 * @author : Vũ Thành Công
 *
 * @Purpose: Bài tập lớn môn Tổ chức va quy hoạch mạng viễn thông
 *
 * @Usage: Sử dụng để xây dựng mạng Access Network
 *
 * @version 2.0 2011/4/12 
 */

package com.bkhn.ltnc.vnetsim.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

import com.bkhn.ltnc.vnetsim.myobjects.*;

public class KruskalAlgorithm extends Thread{
	private final List<Vertex> nodes;	// Tập các đỉnh
	private final List<Edge> edges;		// Tập các cạnh

	private Map<Vertex,Set<Vertex>> setMST;	// Mỗi đỉnh tương ứng với một tập các đỉnh khác
	// Đọc thêm trong phần thuật toán của báo cáo
	private Set<Edge> settledEdges;			// Tập các cạnh đã được xét
	private Set<Edge> unSettledEdges;		// Tập các cạnh chưa xét
	private Set<Vertex> settledNodes;		// Tập các đỉnh đã xét
	private int wmax;						// Mức ngưỡng trọng số
	private Vertex source;					// Nút nguồn

	public KruskalAlgorithm(Graph graph,int wmax,Vertex source){
		this.source = source; 
		this.nodes = new ArrayList<Vertex>(graph.getVertices());
		//System.out.println(nodes);
		this.edges = new ArrayList<Edge>(graph.getEdges());
		this.wmax = wmax;
	}

	public void execute(){
		
		setMST = new HashMap<Vertex, Set<Vertex>>();
		for(Vertex node : nodes){			
			Set<Vertex> mset = new HashSet<Vertex>();
			mset.add(node);
			setMST.put(node, mset);			
		}

		unSettledEdges = new HashSet<Edge>(this.edges) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;};

			//System.out.println("Unsettled edges:\n"+unSettledEdges);
			settledEdges = new HashSet<Edge>();
			settledNodes = new HashSet<Vertex>();

			while(settledEdges.size()<nodes.size()-1){

				Edge edge = getMinimum(unSettledEdges);
				Vertex node = edge.getSource();
				Vertex target = edge.getDestination();

				if(!isEqual(setMST.get(node),setMST.get(target))){
					//System.out.println("minimum = "+edge);
					if(satisfiedWeight(node,target)){												
						addToMST(edge);
						unionSets(edge);
						settledEdges.add(edge);
						settledNodes.add(node);
						settledNodes.add(target);
						unSettledEdges.remove(edge);
					}else {
						//System.out.println("Không thỏa mãn đk trọng số");
						unSettledEdges.remove(edge);
					}
				}
				else {
					//System.out.println("Removed "+edge);
					unSettledEdges.remove(edge);
				}


			}
			//System.out.println(settledNodes);
			//System.out.println("Settled Edges :\n"+settledEdges);
			Set<Vertex> setNodesOK = new HashSet<Vertex>();
			setNodesOK.add(source);
			updatePredecessor(settledEdges,setNodesOK);
			//System.out.println("setNodesOK:\n"+setNodesOK);


	}

	/*
	 * Phương thức kiểm tra điều kiện trọng số
	 */
	private boolean satisfiedWeight(Vertex node, Vertex target) {
		Set<Vertex>set1 = setMST.get(node);
		Set<Vertex>set2 = setMST.get(target);

		int sum1 = getSumOfWeights(set1);
		int sum2 = getSumOfWeights(set2);
		int sumWeight = sum1+sum2;
		if(node.equals(source)){
			if(sum2>this.wmax)return false;
			else return true;
		}else if(target.equals(source)){
			if(sum1>this.wmax)return false;
			else return true;
		}else if(sumWeight>this.wmax)return false;
		else return true;
	}

	/*
	 * Trả về tổng trọng số các nút trong danh sách
	 */
	private int getSumOfWeights(Set<Vertex> set) {
		int value = 0;
		for(Vertex node : set){
			if(!node.equals(source))
				value+=node.getWeight();
		}
		return value;

	}

	/*
	 * Trả về cạnh có giá nhỏ nhất trong danh sách
	 */
	private Edge getMinimum(Set<Edge> uSE) {
		Edge minimum = null;
		for (Edge ed : uSE) {
			if (minimum == null) {
				minimum = ed;
			} else {
				if (ed.getCost()<minimum.getCost()) {
					minimum = ed;
				}
			}
		}
		//System.out.println("find "+minimum);
		return minimum;
	}

	/*
	 * Thiết lập trạng thái cho cạnh được chọn
	 */
	private void addToMST(Edge edge) {
		edge.setStatus("AN");
		//System.out.println(edge+" set in mst");
	}
	/*
	 * Kiểm tra và lấy hợp của 2 tập hợp
	 * update cho các nút
	 */
	private void unionSets(Edge edge) {
		Vertex mSource = edge.getSource();
		Vertex mDestination = edge.getDestination();
		Set<Vertex> s1 = setMST.get(mSource);
		Set<Vertex> s2 = setMST.get(mDestination);
		// Hợp nhất 2 tập hợp
		Set<Vertex> union = new HashSet<Vertex>(s1);
		union.addAll(s2);
		List<Vertex> tmpList = new ArrayList<Vertex>(union);
		Collections.sort(tmpList, Vertex.WeightComparator);
		union.clear();
		union.addAll(tmpList);
		for(Object obj : union){
			Vertex node = (Vertex)obj;
			setMST.remove(node);
			setMST.put(node, union);
		}
	}
	/*
	 * Cập nhật giá trị nút parent 
	 */

	private void updatePredecessor(Set<Edge> setEdge, Set<Vertex> setNode){
		Set<Edge> tmpEdge = new HashSet<Edge>(setEdge);
		while (!tmpEdge.isEmpty()){
			for(Edge ed : setEdge){
				if(tmpEdge.contains(ed)){
					if(updateEdge(ed, setNode)){
						tmpEdge.remove(ed);
						//System.out.println("removed "+ed+"\n SetNode \n"+setNode);
					}
				}
			}
		}

	}
	private boolean updateEdge(Edge mEd, Set<Vertex> mNode){
		Vertex src = mEd.getSource();
		Vertex des = mEd.getDestination();

		if(mNode.contains(src)){
			des.setParent(src);
			mNode.add(des);
			return true;
		}else if(mNode.contains(des)){
			src.setParent(des);
			mNode.add(src);
			return true;
		}
		return false;
	}
	/*
	 * Phương thức kiểm tra hai tập hợp bằng nhau (điều kiện tạo vòng)
	 */
	private boolean isEqual(Set<Vertex> set1, Set<Vertex> set2) {
		if((set1==null)&&(set2==null))return true;
		if((set1!=null)&&(set2!=null)){
			return (set1.containsAll(set2)&&set2.containsAll(set1));
		}
		return false;
	}

	@Override
	public void run() {
		this.execute();		
	}

}
