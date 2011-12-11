/**
 * @(#)DesignGraph.java - Class xây dựng mạng phân tán
 * 							Bao gồm 3 bước là : - Tìm nút Center
 * 												- Thiết Lập các nút backbone, access
 * 												- Xây dựng các kết nối
 *
 * @author : Vũ Thành Công
 *
 * @Purpose: Bài tập lớn môn Tổ chức va quy hoạch mạng viễn thông
 *
 * @Usage: Sử dụng để tối ưu các đồ thị
 *
 * @version 2.0 2011/4/12
 */

package com.bkhn.ltnc.vnetsim.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bkhn.ltnc.vnetsim.myobjects.Edge;
import com.bkhn.ltnc.vnetsim.myobjects.Graph;
import com.bkhn.ltnc.vnetsim.myobjects.Vertex;

public class DesignGraph implements Runnable{

	public final int KRUSKAL = 0;
	public final int PRIM = 1;
	public final int ESAUSWILLIAMS = 2;

	private Graph myGraph;
	private int wThreshold;
	private double alpha;
	private double pc;
	private int radius;
	private int lengthOfGraph;
	private int algorithmSelect;
	private int wmax;

	public DesignGraph(Graph myGraph, int wThreshold, double alpha,double pc,int radius, int lengthOfGraph,int algorithmSelect, int wmax){
		this.myGraph = myGraph;
		this.wThreshold = wThreshold;
		this.alpha = alpha;
		this.pc = pc;
		this.radius = radius;
		this.lengthOfGraph = lengthOfGraph;
		this.algorithmSelect = algorithmSelect;
		this.wmax = wmax;
	}

	public void makeGraph(){
		// Set chứa các nút backbone
		Set<Vertex> backbones = new HashSet<Vertex>();
		// List chứa các nút chưa kết nối
		List<Vertex> listNotConnected = new ArrayList<Vertex>();
		// List chứa các cạnh
		List<Edge> listEdges = new ArrayList<Edge>();

		// Clear Graph
		for(Edge ed : myGraph.getEdges()){
			ed.setStatus("UNKNOWN");
		}
		/*
		 * Bước 1 : Tìm nút Center
		 * Nút có giá trị M nhỏ nhất trong các nút.
		 */
		Map<Vertex,Integer> McValue = new HashMap<Vertex, Integer>();
		for(Vertex node : myGraph.getVertices()){
			int value =0;
			for(Vertex neighbour : node.getNeighbours(myGraph)){
				int cost = myGraph.getCost(node, neighbour);

				value+= cost*neighbour.getWeight();
			}
			McValue.put(node, value);
		}
		Vertex center =null;
		for(Vertex node :myGraph.getVertices()){
			if(center == null){
				center = node;
			}else if(McValue.get(node)<McValue.get(center)){
				center = node;
			}
		}
		center.setStatus("CENTER");
		//System.out.println("Center = "+center);
		/*
		 * Bước 2 : Tìm tập các nút Backbone trong mạng
		 * Backbone là các nút có trọng số lớn hơn mức ngưỡng (Threshold)
		 */
		backbones.clear();
		for(Vertex node : myGraph.getVertices()){
			if(!node.equals(center))node.setStatus("UNKNOWN");
			if((!node.isConnected())&&(node.getWeight()>=wThreshold)){
				node.setStatus("BACKBONE");
				backbones.add(node);
				//System.out.println("set node "+node+" is backbone!");
			}
		}
		//System.out.println("Có "+backbones.size()+" nút backbone là  \n"+backbones);

		/*
		 * Khởi tạo tập các nút access cho từng nút backbone
		 * Lấy nút backbone làm tâm, quay vòng tròn bán kính R
		 * Các nút chưa kết nối nằm trong vòng tròn đó là nút access
		 */
		for(Vertex bb : backbones){
			bb.getAccess().clear();
			for(Vertex node : myGraph.getVertices()){
				if((!node.isConnected())&&(node.inCircle(bb, radius))){
					node.setStatus("NORMAL");
					bb.getAccess().add(node);
					//System.out.println(bb+" added "+node+" to access!");
				}
			}
		}

		/*
		 * Cập nhật thêm các nút backbone từ các nút chưa kết nối còn lại
		 */
		// Khởi tạo tập các nút chưa kết nối
		for(Vertex v : myGraph.getVertices()){
			if(!v.isConnected())
				listNotConnected.add(v);
		}
		//System.out.println("Not Connected \n"+listNotConnected);
		Map<Vertex,Double> FcValue = new HashMap<Vertex, Double>();
		for(Vertex node : listNotConnected){
			int cost = myGraph.getCost(node, center);
			int weight = node.getWeight();
			double valueFc = pc*(cost/lengthOfGraph)+(1-pc)*(weight/wThreshold);
			FcValue.put(node, valueFc);
		}
		while(!myGraph.isConnected()){
			// Tìm giá trị Max Fc làm nút backbone
			Vertex maxFc = null;
			for(Vertex node :listNotConnected){
				if(maxFc==null) maxFc = node;
				else if(FcValue.get(node)>FcValue.get(maxFc)){
					maxFc = node;
				}
			}
			maxFc.setStatus("BACKBONE");
			backbones.add(maxFc);
			listNotConnected.remove(maxFc);
			FcValue.remove(maxFc);

			// Khởi tạo nút access cho nút backbone vừa tìm được
			ArrayList<Vertex> tmplist = new ArrayList<Vertex>(listNotConnected);
			for(Vertex node :tmplist){
				node.getAccess().clear();
				//System.out.println("Dang xet nut "+node);
				if((!node.isConnected())&&(node.inCircle(maxFc, radius))){
					node.setStatus("NORMAL");
					maxFc.getAccess().add(node);
					listNotConnected.remove(node);
					FcValue.remove(node);
				}
			}
		}
		//System.out.println(" Tập các nút backbone mới \n"+backbones);
		/*
		 * Bước 3 : Xây dựng mạng kết nối
		 */
		/*
		 * Bước 3.1 : Xây dựng mạng trục - Backbone Network
		 */
		ArrayList<Vertex> newList = new ArrayList<Vertex>(backbones);
		newList.add(center);
		for(Edge e : myGraph.getEdges()){
			if((newList.contains(e.getSource()))&&
					(newList.contains(e.getDestination()))){
				e.setStatus("UNKNOWN");
				listEdges.add(e);
			}
		}
		Graph mentorGraph = new Graph(newList, listEdges);

		MentorAlgorithm dk = new MentorAlgorithm(mentorGraph, alpha);
		//System.out.println("Executing!");
		dk.execute(center);
		//System.out.println("Done");

		/*
		 * Bước 3.2 : Xây dựng mạng truy nhập - Access Network
		 */

		for( Vertex backbone : backbones){
			Set<Vertex> accNodes = backbone.getAccess();
			//System.out.println("Xet nut backbone "+backbone);
			if(!accNodes.isEmpty()){
				//System.out.println("Nut access "+accNodes);
				accNodes.add(backbone);
				List<Edge> accEdges = new ArrayList<Edge>();
				for(Edge ed: myGraph.getEdges()){
					if((accNodes.contains(ed.getSource()))&&
							(accNodes.contains(ed.getDestination())))
						accEdges.add(ed);
				}
				accNodes.remove(backbone);
				//System.out.println("canh access \n"+accEdges);
				ArrayList<Vertex> accNodesList = new ArrayList<Vertex>(accNodes);
				accNodesList.add(backbone);
				Graph accGraph = new Graph(accNodesList, accEdges);

				switch (algorithmSelect) {
				case 0:
					Thread krk = new KruskalAlgorithm(accGraph, wmax,backbone);
					krk.start();
					break;
				case 1:
					Thread prm = new PrimAlgorithm(accGraph, wmax,backbone);
					prm.start();
					break;
				case 2:
					Thread ews = new EsausWilliamsAlgorithm(accGraph, wmax, backbone);
					ews.start();
					break;
				default:
					Thread kruskal = new KruskalAlgorithm(accGraph, wmax,backbone);
					kruskal.start();
					break;
				}


			}
		}


	}

	@Override
	public void run() {
		this.makeGraph();
	}

}
