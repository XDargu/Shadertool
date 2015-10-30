package shadertool.graph;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.subLayout.GraphCollapser;

import shadertool.nodes.Node;

public class SavableGraph implements Serializable, Transferable{
	private static final long serialVersionUID = 1L;
	
	transient MyGraph<Node, Link> graph;
	MyGraph<Node, Link> expandedGraph;
	ArrayList<VertexPointStruct> positions;
	public Object[] settings;
	ArrayList<CollapsedNodes> collapsedNodes;
	public String name;
	
	public static transient final DataFlavor flavor = new DataFlavor(SavableGraph.class, "CollapsedNode");
	
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { flavor };
	}
	
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor == Node.flavor;
	}
	
	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (flavor == Node.flavor)
			return this;
		else
			return null;
	}
	
	public SavableGraph() {
		name = "";
	}
	
	public MyGraph<Node, Link> getGraph() {
		return expandedGraph;
	}

	@SuppressWarnings("unchecked")
	public void setGraph(MyGraph<Node, Link> graph) {
		this.graph = graph;
		this.expandedGraph = getExpandedGraph(graph);
	}
	
	@SuppressWarnings({ "rawtypes" })
	private MyGraph getExpandedGraph(MyGraph graph) {
		MyGraph rtr = graph;
		int count = 0;
		for (Object v : graph.getVertices())
			if (v instanceof MyGraph)
				count++;
		while (count > 0) {
			for (Object v : graph.getVertices()) {
				if (v instanceof MyGraph) {
					GraphCollapser collapser = ((MyGraph)v).collapser;
		            MyGraph g = (MyGraph) collapser.expand(rtr, (Graph)v);
		            //vv.getRenderContext().getParallelEdgeIndexFunction().reset();
		            rtr = g;
				}
			}
			count = 0;
			for (Object v : rtr.getVertices())
				if (v instanceof MyGraph)
					count++;
		}
		return rtr;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void savePositions(VisualizationViewer vv) {
		positions = new ArrayList<VertexPointStruct>();
		
		//final MyGraph<Node, Link> graph = (MyGraph)vv.getGraphLayout().getGraph();
        final Layout layout = vv.getGraphLayout();
        
        VertexPointStruct vps;
        for (Object node : graph.getAllVertices()) {
        	if (node instanceof Node) {
				Point2D p = (Point2D)layout.transform(node);
		    	vps = new VertexPointStruct((Node)node, p);
		    	positions.add(vps);
        	}
        }
	}
	
	public StaticLayout<Node, Link> getInitialLayout() {
		StaticLayout<Node, Link> layout = new StaticLayout<Node, Link>(graph,
				 new Transformer<Node, Point2D>() {
					@Override
					public Point2D transform(Node n) {
						for (VertexPointStruct vps : positions) {
							if (vps.vertex == n)
								return vps.point;
						}
						return new Point2D.Double(0, 0);
					}
				}
				);
		return layout;
	}
	
	
	@SuppressWarnings("rawtypes")
	public void loadCollapsedNodes(VisualizationViewer vv, Grafo grafo) {
		loadCollapsedNodes(vv, grafo, new Point2D.Double(0, 0));
	}
	
	@SuppressWarnings({ "rawtypes" })
	public void loadCollapsedNodes(VisualizationViewer vv, Grafo grafo, Point2D offset) {
		ArrayList<CollapsedNodes> closed = new ArrayList<CollapsedNodes>();
		MyGraph graph = (MyGraph)vv.getGraphLayout().getGraph();
		
		ArrayList<Object> nodes;
		// Interrupción de seguridad para evitar whiles infinitos
		// El número máximo de operaciones posibles (teóricas) es que el nodo a
		// expandir esté siempre el último de la lista, y solo se expanda uno
		// en cada iteración. Es decir, el número máximo de iteraciones es la
		// cantidad de nodos colapsados. Se añaden 10 más por si acaso.
		int breakIteration = collapsedNodes.size() + 10;
		int counter = 0;
		while (closed.size() < collapsedNodes.size()) {
			counter++;
			if (counter >= breakIteration) { break; }
			// Por cada nodo colapsado buscamos si todos sus hijos están ya en el grafo
			// En caso de que estén en el grafo, se colapsan y se añade a la lista de cerrados
			for (CollapsedNodes cn : collapsedNodes) {
				if (!closed.contains(cn)) {
					nodes = new ArrayList<Object>();
					for (Object node : graph.getVertices()) {
						for (Object node2 : cn.nodes) {
							if (nodeEquals(node, node2)) {
								nodes.add(node);
							}
						}
					}
					
					if (nodes.size() == cn.nodes.size()) {
						Point2D point = new Point2D.Double(cn.point.getX() + offset.getX(), cn.point.getY() + offset.getY());
						collapse(vv, grafo, nodes, graph, point, cn.name);
						graph = (MyGraph)vv.getGraphLayout().getGraph();
						closed.add(cn);
					}
				}
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	private boolean nodeEquals(Object node1, Object node2) {
		if (node1 instanceof MyGraph) {
			if (node2 instanceof MyGraph)
				return collapsedEquals((MyGraph)node1, (MyGraph)node2);
			else
				return false;
		} else {
			if (node2 instanceof MyGraph)
				return false;
			else
				return node1.equals(node2);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean collapsedEquals(MyGraph collapsed1, MyGraph collapsed2) {
		return 
			collapsed1.getAllVertices().containsAll(collapsed2.getAllVertices()) &&
			collapsed2.getAllVertices().containsAll(collapsed1.getAllVertices());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void collapse(VisualizationViewer vv, Grafo grafo, Collection picked, MyGraph graph, Point2D point, String name) {
		Layout layout = vv.getGraphLayout();
		
		GraphCollapser collapser = new GraphCollapser(graph);
    	MyGraph inGraph = (MyGraph) layout.getGraph();
        MyGraph clusterGraph = (MyGraph) collapser.getClusterGraph(inGraph, picked);

        MyGraph g = (MyGraph) collapser.collapse(layout.getGraph(), clusterGraph);
        
        vv.getRenderContext().getParallelEdgeIndexFunction().reset();
        layout.setGraph(g);
        layout.setLocation(clusterGraph, point);
        grafo.g = g;
        grafo.initializeGraph(g);
        clusterGraph.collapser = collapser;
        clusterGraph.setName(name);
        vv.getPickedVertexState().clear();
        vv.updateUI();
	}
	
	@SuppressWarnings({ "rawtypes" })
	public void saveCollapsedNodes(VisualizationViewer vv) {		
		collapsedNodes = new ArrayList<CollapsedNodes>();
		for (Object node : graph.getVertices()) {
			if (node instanceof MyGraph)
				collapsedNodes.addAll(recursiveCollapsedNodeFinder(vv, (MyGraph)node));
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void saveCollapsedWithRoot(VisualizationViewer vv) {
		collapsedNodes = new ArrayList<CollapsedNodes>();
		collapsedNodes.addAll(recursiveCollapsedNodeFinder(vv, graph));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ArrayList<CollapsedNodes> recursiveCollapsedNodeFinder(VisualizationViewer vv, MyGraph graph) {
		ArrayList<CollapsedNodes> collapsed = new ArrayList<CollapsedNodes>();
		ArrayList<Object> objects = new ArrayList<Object>();
		
		final Layout layout = vv.getGraphLayout();		
		
		for (Object node : graph.getVertices()) {
			objects.add(node);
			// Grafo colapsado en grafo colapsado
			if (node instanceof MyGraph) {
				ArrayList<CollapsedNodes> collapsedIn = recursiveCollapsedNodeFinder(vv, (MyGraph)node);
				collapsed.addAll(collapsedIn);
			}
		}
		
		// Añadir todos los nodos al nuevo collapsedNode
		Point2D point = (Point2D)layout.transform(graph);
		CollapsedNodes cn = new CollapsedNodes(objects, graph.getName(), point);
		collapsed.add(cn);
		
        return collapsed;
	}
	
	public ArrayList<VertexPointStruct> getPositions() {
		return positions;
	}
	
	@SuppressWarnings("rawtypes")
	public int getGraphCount(MyGraph graph) {
		int count = 0;
		for (Object node : graph.getVertices()) {
			if (node instanceof MyGraph) {
				count += getGraphCount((MyGraph)node) + 1;
			}
		}
		return count;
	}
	
	@SuppressWarnings("rawtypes")
	public int getGraphCount(Collection nodes) {
		int count = 0;
		for (Object node : nodes) {
			if (node instanceof MyGraph) {
				count += getGraphCount((MyGraph)node) + 1;
			}
		}
		return count;
	}
	
	private CollapsedNodes getRoot() {
		int max = -1;
		CollapsedNodes root = null;
		for (CollapsedNodes cn : collapsedNodes) {
			if (getGraphCount(cn.nodes) > max) {
				root = cn;
				max = getGraphCount(cn.nodes);
			}
		}
		return root;
	}
	
	public Point2D getRootPoint() {
		return getRoot().point;
	}
	
	public boolean hasCollapsed() {
		return collapsedNodes.size() > 0;
	}

	@Override
	public String toString() {
		return name;
	}
	
	class CollapsedNodes implements Serializable {
		private static final long serialVersionUID = 1L;
		
		ArrayList<Object> nodes;
		String name;
		Point2D point;
		
		public CollapsedNodes(ArrayList<Object> nodes, String name, Point2D point) {
			this.nodes = nodes;
			this.name = name;
			this.point = point;
		}
	}
}
