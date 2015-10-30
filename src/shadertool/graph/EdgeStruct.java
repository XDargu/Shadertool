package shadertool.graph;

class EdgeStruct<V, E> {
	public final E link;
	public final V node1;
	public final V node2;
	
	public EdgeStruct(V vertex1, V vertex2, E edge) {
		this.link = edge;
		this.node1 = vertex1;
		this.node2 = vertex2;
	}
}