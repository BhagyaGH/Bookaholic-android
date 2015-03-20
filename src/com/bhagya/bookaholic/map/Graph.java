package com.bhagya.bookaholic.map;

import java.util.List;

// Graph represents the book fair map
public class Graph {
	
	// Vertex represents a bookshop stall
	private final List<Vertex> vertexes;
	// Edge is the path between two adjacent bookshop stalls
	private final List<Edge> edges;

	// Constructor with lists of vertices and edges
	public Graph(List<Vertex> vertexes, List<Edge> edges) {
		this.vertexes = vertexes;
		this.edges = edges;
	}

	// Get the list of vertices in the graph
	public List<Vertex> getVertexes() {
		return vertexes;
	}

	// Get the list of edges in the graph
	public List<Edge> getEdges() {
		return edges;
	}
	
}

