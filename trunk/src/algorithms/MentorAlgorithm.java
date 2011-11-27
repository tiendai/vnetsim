/**
 * @(#)MentorAlgorithm.java - Class xây dựng mạng bằng thuật toán Mentor
 * 		+ Khi alpha = 0. Trở thành thuật toán Prim tìm cây MST
 * 		+ Khi alpha = 1. Trở thành thuật toán Dijkstra tìm cây PST
 *
 * @author : Vũ Thành Công
 *
 * @Purpose: Bài tập lớn môn Tổ chức va quy hoạch mạng viễn thông
 *
 * @Usage: Sử dụng để xây dựng mạng đường trục Backbone Network
 *
 * @version 2.0 2011/4/12
 */
package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import myobjects.*;

public class MentorAlgorithm {

	private final List<Vertex> nodes;			// Tập các đỉnh
	private final List<Edge> edges;				// Tập các cạnh
	private Set<Vertex> settledNodes;			// Tập các đỉnh được xét
	private Set<Vertex> unSettledNodes;			// Tập các đỉnh chưa xét
	private Map<Vertex, Vertex> predecessors;	// Mỗi đỉnh tương ứng với một nút tiền bối
	private Map<Vertex, Double> distance;		// Mỗi đỉnh tương ứng với một giá trị distance
	
	private double alpha;						// Hệ số của thuật toán Mentor

	public MentorAlgorithm(Graph graph,double alpha) {		
		this.nodes = new ArrayList<Vertex>(graph.getVertices());
		this.edges = new ArrayList<Edge>(graph.getEdges());
		this.alpha = alpha;
	}

	public void execute(Vertex source) {
		settledNodes = new HashSet<Vertex>();
		unSettledNodes = new HashSet<Vertex>(this.nodes);
		distance = new HashMap<Vertex, Double>();
		predecessors = new HashMap<Vertex, Vertex>();
		distance.put(source, 0.0);
		for(Vertex node : this.nodes){
			if(!node.equals(source)){
				predecessors.put(node, source);
				node.setParent(source);
			}
		}
		while (unSettledNodes.size() > 0) {
			Vertex node = getMinimum(unSettledNodes);
			//System.out.println("Minimum "+node);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			updateCMST(node);
			findMinimalDistances(node);
		}
	}
	/*
	 * Cập nhật khoảng cách ngắn nhất cho các đỉnh
	 */
	private void findMinimalDistances(Vertex node) {
		List<Vertex> adjacentNodes = getNeighbours(node);
		for (Vertex target : adjacentNodes) {
			if (getShortestDistance(target) > alpha * getShortestDistance(node)
					+ getDistance(node, target)) {
				distance.put(target, getShortestDistance(node)
						+ getDistance(node, target));
				predecessors.remove(target);
				predecessors.put(target, node);
				target.setParent(node);                                
				unSettledNodes.add(target);
			}
		}

	}

	/*
	 * Lấy distance
	 */
	private int getDistance(Vertex node, Vertex target) {
		for (Edge edge : edges) {
			if ((edge.getSource().equals(node)&&(edge.getDestination().equals(target)))||
					((edge.getSource().equals(target))&&(edge.getDestination().equals(node)))) {
				return edge.getCost();
			}
		}
		//System.out.println("Get Distance failed "+node.getId()+"-->"+target.getId());
		throw new RuntimeException("Should not happen !");
	}

	/*
	 * Phương thức lấy tập các nút hàng xóm
	 */
	private List<Vertex> getNeighbours(Vertex node) {
		List<Vertex> neighbors = new ArrayList<Vertex>();
		for (Edge edge : edges) {
			if (edge.getSource().equals(node)
					&& !isSettled(edge.getDestination())) {
				neighbors.add(edge.getDestination());
			}
			else if (edge.getDestination().equals(node)
					&& !isSettled(edge.getSource()))
				neighbors.add(edge.getSource());
		}
		return neighbors;
	}
	/*
	 * Phương thức lấy đỉnh có giá trị distance nhỏ nhất trong danh sách
	 */
	private Vertex getMinimum(Set<Vertex> vertexes) {
		Vertex minimum = null;
		for (Vertex vertex : vertexes) {
			if (minimum == null) {
				minimum = vertex;
			} else {
				if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
					minimum = vertex;
				}
			}
		}
		return minimum;
	}
	/*
	 * Kiểm tra xem nút được xét hay chưa
	 */
	private boolean isSettled(Vertex vertex) {
		return settledNodes.contains(vertex);
	}

	/*
	 * Phương thức lấy distance ngắn nhất
	 */
	private double getShortestDistance(Vertex destination) {
		Double d = distance.get(destination);
		if (d == null) {
			return Double.MAX_VALUE;
		} else {
			return d;
		}
	}

	/*
	 * Phương thức update các biến trạng thái khi một nút được chọn vào CMST
	 */
	private void updateCMST(Vertex node){
		if(predecessors.get(node)==null){
			//System.out.println(node+" khong co predecessor!");
		}else{
			Vertex step = predecessors.get(node);
			//System.out.println("predecessor cua "+node+" la "+step);
			for(Edge edge :edges){
				if ((edge.getSource().equals(node)&&(edge.getDestination().equals(step)))||
						((edge.getSource().equals(step))&&(edge.getDestination().equals(node)))){
					edge.setStatus("BN");
					//System.out.println(edge+" set in CMST");
				}

			}
		}
	}

}