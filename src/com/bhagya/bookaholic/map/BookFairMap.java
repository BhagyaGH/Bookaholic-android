package com.bhagya.bookaholic.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

public class BookFairMap {

	// For demonstration I use only the "A" building & it has 85 stalls
	private final String PREFIX = "A";
	private final int STALLS = 89;

	// Stalls are the nodes of the graph
	private Graph graph;
	private List<Vertex> nodes;
	private List<Edge> edges;

	private String source, destination;

	// Get the shortest path from Dijkstra
	private DijkstraAlgorithm dijkstra;

	public BookFairMap(String source, String destination) {
		super();
		this.source = source;
		this.destination = destination;
	}

	// This returns the coordinates of a particular stall
	public int[] getCoordinates(String point) {
		int index = Integer.parseInt(point.substring(1)) - 1;

		Vertex vertex = nodes.get(index);

		int[] coordinates = { vertex.getX(), vertex.getY() };

		return coordinates;
	}

	// Create the graph for the book fair premises
	public void createMap() {
		nodes = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();

		for (int i = 0; i < STALLS; i++) {
			Vertex location = new Vertex(PREFIX + (i + 1));
			nodes.add(location);
		}

		// From 1 to 37
		for (int i = 0; i < 36; i++) {
			addLane(i, i + 1, 1);
			addLane(i + 1, i, 1);
		}

		addLane(0, 36, 2);
		addLane(36, 0, 2);

		// From 38 to 49
		for (int i = 37; i < 48; i++) {
			addLane(i, i + 1, 1);
			addLane(i + 1, i, 1);
		}

		// From 50 to 52
		for (int i = 49; i < 51; i++) {
			addLane(i, i + 1, 1);
			addLane(i + 1, i, 1);
		}

		// From 53 to 60
		for (int i = 52; i < 59; i++) {
			addLane(i, i + 1, 1);
			addLane(i + 1, i, 1);
		}

		// From 61 to 63
		for (int i = 60; i < 62; i++) {
			addLane(i, i + 1, 1);
			addLane(i + 1, i, 1);
		}

		// From 64 to 66
		for (int i = 63; i < 65; i++) {
			addLane(i, i + 1, 1);
			addLane(i + 1, i, 1);
		}

		// From 67 to 74
		for (int i = 66; i < 73; i++) {
			addLane(i, i + 1, 1);
			addLane(i + 1, i, 1);
		}

		// From 75 to 77
		for (int i = 74; i < 76; i++) {
			addLane(i, i + 1, 1);
			addLane(i + 1, i, 1);
		}

		// From 78 to 89
		for (int i = 77; i < 88; i++) {
			addLane(i, i + 1, 1);
			addLane(i + 1, i, 1);
		}

		addLane(37, 48, 1);
		addLane(49, 62, 1);
		addLane(51, 60, 1);
		addLane(52, 59, 1);
		addLane(66, 73, 1);
		addLane(65, 74, 1);
		addLane(63, 76, 1);
		addLane(77, 88, 1);

		// Bidir
		addLane(48, 37, 1);
		addLane(62, 49, 1);
		addLane(60, 51, 1);
		addLane(59, 52, 1);
		addLane(73, 66, 1);
		addLane(74, 65, 1);
		addLane(76, 63, 1);
		addLane(88, 77, 1);

		addLane(0, 62, 1);
		addLane(1, 49, 1);
		addLane(4, 48, 1);
		addLane(5, 48, 1);
		addLane(6, 37, 1);

		// Bidir
		addLane(62, 0, 1);
		addLane(49, 1, 1);
		addLane(48, 4, 1);
		addLane(48, 5, 1);
		addLane(37, 6, 1);

		for (int i = 7; i < 12; i++) {
			addLane(i, i + 30, 1);
			addLane(i + 30, i, 1);
		}

		addLane(12, 41, 1);
		addLane(13, 42, 1);
		addLane(14, 42, 1);
		addLane(16, 55, 1);
		addLane(17, 55, 1);
		addLane(18, 56, 1);
		addLane(21, 69, 1);
		addLane(22, 70, 1);
		addLane(23, 70, 1);
		addLane(25, 83, 1);
		addLane(26, 84, 1);

		// Bidir
		addLane(41, 12, 1);
		addLane(42, 13, 1);
		addLane(42, 14, 1);
		addLane(55, 16, 1);
		addLane(55, 17, 1);
		addLane(56, 18, 1);
		addLane(69, 21, 1);
		addLane(70, 22, 1);
		addLane(70, 23, 1);
		addLane(83, 25, 1);
		addLane(84, 26, 1);

		for (int i = 27; i < 32; i++) {
			addLane(i, i + 57, 1);
			addLane(i + 57, i, 1);
		}

		addLane(32, 88, 1);
		addLane(33, 77, 1);
		addLane(34, 77, 1);

		// Bidir
		addLane(88, 32, 1);
		addLane(77, 33, 1);
		addLane(77, 34, 1);

		addLane(35, 76, 1);
		addLane(36, 76, 1);

		// Bidir
		addLane(76, 35, 1);
		addLane(76, 36, 1);

		addLane(48, 51, 1);
		addLane(43, 52, 1);
		addLane(42, 53, 1);

		// Bidir
		addLane(51, 48, 1);
		addLane(52, 43, 1);
		addLane(53, 42, 1);

		addLane(56, 69, 1);
		addLane(57, 68, 1);
		addLane(58, 67, 1);
		addLane(59, 66, 1);

		// Bidir
		addLane(69, 56, 1);
		addLane(68, 57, 1);
		addLane(67, 58, 1);
		addLane(66, 59, 1);

		addLane(72, 83, 1);
		addLane(73, 82, 1);
		addLane(74, 77, 1);

		// Bidir
		addLane(83, 72, 1);
		addLane(82, 73, 1);
		addLane(77, 74, 1);

		addLane(60, 65, 1);
		addLane(61, 64, 1);
		addLane(62, 63, 1);

		// Bidir
		addLane(65, 60, 1);
		addLane(64, 61, 1);
		addLane(63, 62, 1);

		graph = new Graph(nodes, edges);
	}
	
	// Get a list of stalls to be passed to get to the destination
	public String[] getDirections() {
		dijkstra = new DijkstraAlgorithm(graph);

		// Set the source node
		Log.v("src", source);
		Log.v("des", destination);

		int sourceIndex = Integer.parseInt(source.substring(1)) - 1;
		int desIndex = Integer.parseInt(destination.substring(1)) - 1;

		Log.v("srcIndex", Integer.toString(sourceIndex));
		Log.v("desIndex", Integer.toString(desIndex));

		dijkstra.executeDijkstra(nodes.get(sourceIndex));
		LinkedList<Vertex> path = dijkstra.getPath(nodes.get(desIndex));

		String[] pathStalls = new String[path.size()];
		// Get the path
		for (int i = 0; i < path.size(); i++) {
			Vertex vertex = path.get(i);
			pathStalls[i] = vertex.getId();
			Log.v("Path Test", vertex.getId());
		}
		return pathStalls;
	}

	private void addLane(int sourceLocNo, int destLocNo, int duration) {
		Edge lane = new Edge(nodes.get(sourceLocNo), nodes.get(destLocNo),
				duration);
		edges.add(lane);
	}

}