package shadertool.graph;

import java.awt.geom.Point2D;
import java.io.Serializable;

import shadertool.nodes.Node;

class VertexPointStruct implements Serializable{
	private static final long serialVersionUID = 1L;
	
	Node vertex;
	Point2D point;
	
	public VertexPointStruct(Node vertex, Point2D point) {
		this.vertex = vertex;
		this.point = point;
	}
}	
