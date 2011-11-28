/**
 * @(#)PrimAlgorithm.java - Class xây dựng cây CMST dựa vào thuật toán Prim
 *
 * @author : Vũ Thành Công
 *
 * @Purpose: Bài tập lớn môn Tổ chức va quy hoạch mạng viễn thông
 *
 * @Usage: Sử dụng để xây dựng mạng Access Network
 *
 * @version 1.0 2011/4/18 
 */

package com.bkhn.ltnc.vnetsim.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bkhn.ltnc.vnetsim.myobjects.Edge;
import com.bkhn.ltnc.vnetsim.myobjects.Graph;
import com.bkhn.ltnc.vnetsim.myobjects.Vertex;


public class PrimAlgorithm extends Thread{
	private final Graph myGraph;
	private final List<Vertex> nodes;		// Tập các đỉnh
	private final List<Edge> edges;			// Tập các cạnh
	private Vertex source;					// Nút nguồn
	private int wMax;
	private Set<Vertex> listTree;
	private Set<Vertex> listFringe;
	private Map<Vertex, Integer> distance;
	private Map<Vertex,Set<Vertex>> setMST;

	public PrimAlgorithm(Graph graph,int wmax,Vertex source){
		this.myGraph = graph;
		this.wMax = wmax;
		this.nodes = new ArrayList<Vertex>(graph.getVertices());
		//System.out.println(nodes);
		this.edges = new ArrayList<Edge>(graph.getEdges());
		this.source = source;
	}

	public void execute(){
		//Initialize
		
		listTree = new HashSet<Vertex>();
		listTree.add(source);
		listFringe = new HashSet<Vertex>(myGraph.getVertices());
		listFringe.remove(source);

		distance = new HashMap<Vertex, Integer>();

		setMST = new HashMap<Vertex, Set<Vertex>>();
		for(Vertex node : nodes){			
			Set<Vertex> mset = new HashSet<Vertex>();
			mset.add(node);
			setMST.put(node, mset);			
		}

		parentInit();
		refreshFringe(source);
		
		//System.out.println("Source = "+source);
		while(!listFringe.isEmpty()){
			
			Vertex minNode = getMinimum(listFringe);
			//System.out.println("Min = "+minNode+",distance = "+distance.get(minNode)+", Parent = "+minNode.getParentId());
			if(satisfiedWeight(minNode)){
				
				updateCMST(minNode);

				listTree.add(minNode);
				listFringe.remove(minNode);
				

				refreshFringe(minNode);

				unionSets(minNode, myGraph.getParent(minNode));
				//System.out.println(minNode+"-->MST");
			}
			else {
				//System.out.println(minNode+ " Khong thoa man dk trong so");
				minNode.setParent(source);
				distance.remove(minNode);
				distance.put(minNode, Integer.MAX_VALUE);
				//System.out.println(listFringe);
			}

		}

	}

	private void unionSets(Vertex src, Vertex des) {
		Set<Vertex> s1 = setMST.get(src);
		Set<Vertex> s2 = setMST.get(des);
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
	private boolean satisfiedWeight(Vertex node) {
		Vertex parent = myGraph.getParent(node);
		if(parent.getId()==source.getId()){
			return true;
		}else if((getSumOfWeights(setMST.get(node))+getSumOfWeights(setMST.get(parent)))<=wMax)return true;
		else return false;
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

	private void updateCMST(Vertex minNode) {
		myGraph.getEdge(minNode.getId(), minNode.getParentId()).setStatus("AN");		
	}

	private Vertex getMinimum(Set<Vertex> fringe) {
		//System.out.println("Chay getMinimum!");
		Vertex minimum = null;
		for(Vertex node : fringe){
			if(minimum == null){
				minimum = node;
			}
			else if(distance.get(node)<distance.get(minimum)){
				minimum = node;
			}
		}
		return minimum;
	}

	private void refreshFringe(Vertex node) {
		ArrayList<Vertex> neighbours = getNeighbours(node);
		for(Vertex nb: neighbours){
			if(!listTree.contains(nb)){
				int cost = myGraph.getCost(node, nb);
				if(cost < distance.get(nb)){
					nb.setParent(node);
					//System.out.println(nb+" set parent is "+node);
					distance.remove(nb);
					distance.put(nb,cost);
				}
			}
		}

	}

	private ArrayList<Vertex> getNeighbours(Vertex node) {
		ArrayList<Vertex> neighbours = new ArrayList<Vertex>();
		for(Edge edge : edges){
			if(edge.getSource().equals(node))neighbours.add(edge.getDestination());
			else if (edge.getDestination().equals(node))neighbours.add(edge.getSource());
		}
		return neighbours;

	}

	private void parentInit() {
		for(Vertex node:listFringe){
			node.setParent(source);
			int cost = myGraph.getCost(node, source);
			//System.out.println("Node = "+node+ ",Cost = "+cost);
			distance.put(node,cost);
		}

	}
	public void run(){
		this.execute();
	}
}
