package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

//import java.util.HashMap;
//import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	
//	private Map<Integer, Album> idMap;
	private Graph<Album, DefaultWeightedEdge> graph;
	private ItunesDAO dao;
	private List<Album> best;
	
	public Model() {
		this.graph = new SimpleDirectedWeightedGraph<Album, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.dao = new ItunesDAO();
//		this.idMap = new HashMap<>(this.dao.getAllAlbums());
	}
	
	public void creaGrafo(int n) {
		Graphs.addAllVertices(this.graph, this.dao.getVertex(n));
		
		for(Album a1: this.graph.vertexSet()) {
			for(Album a2: this.graph.vertexSet()) {
				if(!a1.equals(a2)) {
					DefaultWeightedEdge edge = this.graph.getEdge(a1, a2);
					DefaultWeightedEdge edgeBack = this.graph.getEdge(a2, a1);
				
					double weight = Math.abs((int)a1.getnCanzoni()-(int)a2.getnCanzoni());
				
					if(edge == null && edgeBack == null && a1.getnCanzoni() != a2.getnCanzoni()) {
						if(a1.getnCanzoni() < a2.getnCanzoni())
							edge = this.graph.addEdge(a1, a2);
						else 
							edge = this.graph.addEdge(a2, a1); 
					
						this.graph.setEdgeWeight(edge, weight);
					}
				}
			}
		}
		
		
		for(Album a: this.graph.vertexSet()) {
			double sommaIn = 0;
			for(DefaultWeightedEdge entrante: this.graph.incomingEdgesOf(a)) {
				sommaIn += (int)this.graph.getEdgeWeight(entrante);
			}
			
			double sommaOut = 0;
			for(DefaultWeightedEdge uscente: this.graph.outgoingEdgesOf(a)) {
				sommaOut += (int)this.graph.getEdgeWeight(uscente);
			}
			
			double bilancio = sommaIn - sommaOut;
			a.setBilancio(bilancio);
		}
			
		System.out.println("#VERTICI: " + this.graph.vertexSet().size());
		System.out.println("#ARCHI: " + this.graph.edgeSet().size());
	}
	
	public String getNVertex() {
		return "#VERTICI: " + this.graph.vertexSet().size();
	}
	
	public String getNEdge() {
		return "#ARCHI: " + this.graph.edgeSet().size();
	}
	
	public Graph<Album, DefaultWeightedEdge> getGraph(){
		return this.graph;
	}
	
	public String getAdiacenze(Album a1) {
		String res = "";
		List<Album> result = new LinkedList<>();
		
		for(Album a : Graphs.successorListOf(this.graph, a1)) {
			result.add(a);
		}
		
		Collections.sort(result);
		
		for(Album a : result) {
			res += a + ", bilancio= " + a.getBilancio() + "\n";
		}
		
		return res;
	}
	
	public String calcolaPercorso(Album a1, Album a2, int x) {
		this.best = new ArrayList<Album>();
		List<Album> parziale = new ArrayList<Album>();
		parziale.add(a1);
		
		this.ricorsiva(parziale, a1, a2, x, a1);
		
		String res = "Percorso:\n";
		for(Album a: this.best)
			res += a+"\n";
			
		return res;
	}

	private void ricorsiva(List<Album> parziale, Album a1, Album a2, int x, Album last) {
		if(last.equals(a2)) {
			if(parziale.size() > this.best.size()) 
				this.best = new ArrayList<Album>(parziale);
		} else {
			for(Album a: Graphs.neighborListOf(this.graph, last)) {
				if(!parziale.contains(a)) {
					DefaultWeightedEdge edge = this.graph.getEdge(last, a);
					double weight = 0.0;
					if(edge != null)
						weight = this.graph.getEdgeWeight(edge);
					
					if(weight >= x) {
						if(a.getBilancio() > a1.getBilancio()) {
							parziale.add(a);
							this.ricorsiva(parziale, a1, a2, x, a);
							parziale.remove(a);
						}
					}
				}
			}
		}
		
	}
}
