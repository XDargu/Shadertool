package shadertool.graph;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.newdawn.slick.SlickException;

import shadertool.Editor;
import shadertool.Settings;
import shadertool.nodes.Node;
import shadertool.nodes.Output;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.subLayout.GraphCollapser;

public class MyGraph<V, E> extends DirectedSparseMultigraph<V, E>{
	private static final long serialVersionUID = 1L;
	
	private String name;
	private transient Editor vista;
	private transient Grafo grafo;
	
	public transient GraphCollapser collapser;
	
	public MyGraph() {
		super();
	}
	
	public void initialize(Editor vista, Grafo grafo) {
		this.vista = vista;
		this.grafo = grafo;
	}
	
	public boolean isModified() {
		return grafo.modified;
	}
	
	public void setModified(boolean modified) {
		grafo.modified = modified;
		redrawView();
	}
	
	private void redrawView() {
		vista.setRedoEnabled(grafo.undoManager.canRedo());
		vista.setUndoEnabled(grafo.undoManager.canUndo());
	}
	
	public void newNode(V node) {
		grafo.undoManager.addEdit(new UndoableAddNode(node));
		addVertex(node);
		setModified(true);
	}
	
	public void deleteNode(V node) {
		grafo.undoManager.addEdit(new UndoableRemoveNode(node));
		removeVertex(node);
		setModified(true);
	}
	
	public void newLink(V node1, V node2, E link) {		
		grafo.undoManager.addEdit(new UndoableAddLink(node1, node2, link));
		addEdge(link, node1, node2);
		setModified(true);
		
		if (Settings.autoAsignInputs) {
			Node n1 = (Node)node1;
			Node n2 = (Node)node2;
			Link l = (Link)link;
			if (n1.getOutputs().size() == 1)
				l.setInput(n1.getOutputs().get(0));
			if (n2.getInputs().size() == 1) {
				if (!n2.getInputs().get(0).used)
					l.setOutput(n2.getInputs().get(0));
			}
			else {
				for (int i=0; i<n2.getInputs().size(); i++) {
					if (!n2.getInputs().get(i).used)
						l.setOutput(n2.getInputs().get(i));
				}
			}
		}			
	}
	
	@Override
	public boolean addEdge(E edge, V vertex1, V vertex2) {
		Link l = (Link)edge;
		if (l.getOutput() != null)
			l.getOutput().used = true;
		
		boolean rtr = super.addEdge(edge, vertex1, vertex2);
		grafo.checkInputs((Node)vertex2);
		return rtr;
	}
	
	@Override
	public boolean removeVertex(V vertex) {
		Node node = ((Node)vertex);
		for (Output o: node.getOutputs()) {
	    	try {
				o.flush();
			} catch (SlickException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

		ArrayList<V> vertices = new ArrayList<V>();
		for (E e : getEdges()) {
			if (getSource(e) == vertex)
				vertices.add(getDest(e));
		}
		boolean rtr = super.removeVertex(vertex);
		for (V v : vertices) {
			grafo.checkInputs((Node)v);
		}
		return rtr;		
	}
	
	@Override
	public boolean removeEdge(E edge) {
		Link link = (Link) edge;
		link.getInput().unsubscribe(link);
		try {
			link.getInput().flush();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//link.getOutput().flush();
		
		V vertex = getDest(edge);
		boolean rtr = super.removeEdge(edge);
		if (vertex != null)
			grafo.checkInputs((Node)vertex);
		return rtr;
	}
	
	public void deleteLink(E link) {
		if ((!(super.getSource(link) instanceof MyGraph)) && (!(super.getDest(link) instanceof MyGraph))) {
			grafo.undoManager.addEdit(new UndoableRemoveLink(getSource(link), getDest(link), link));
			removeEdge(link);
			setModified(true);
			
			Link l = (Link)link;
			if (l.getOutput() != null)
				l.getOutput().used = false;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public V getSource(E edge) {
		if (super.getSource(edge) instanceof MyGraph) {
			MyGraph vertex = (MyGraph)super.getSource(edge);
			MyGraph g = (MyGraph) grafo.collapser.expand(grafo.g, (Graph)vertex);
			grafo.collapser.collapse(g, (Graph)vertex);			
            return (V) g.getSource(edge);
		} else {
			return super.getSource(edge);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public V getDest(E edge) {
		if (super.getDest(edge) instanceof MyGraph) {
			MyGraph vertex = (MyGraph)super.getDest(edge);
			MyGraph g = (MyGraph) grafo.collapser.expand(grafo.g, vertex);
			grafo.collapser.collapse(g, (Graph)vertex);
            return (V) g.getDest(edge);
		} else {
			return super.getDest(edge);
		}
	}
	
	public void undo() {
		if (canUndo())
			grafo.undoManager.undo();
		redrawView();
	}
	
	public void redo() {
		if (canRedo())
			grafo.undoManager.redo();
		redrawView();
	}
	
	public boolean canUndo() {
		return grafo.undoManager.canUndo();
	}
	
	public boolean canRedo() {
		return grafo.undoManager.canRedo();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public ArrayList<V> getAllVertices() {
		return getAllVertices(this);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<V> getAllVertices(MyGraph graph) {
		ArrayList<V> vertices = new ArrayList<V>();
		
		for (Object node : graph.getVertices()) {
			if (node instanceof MyGraph) {
				vertices.addAll(getAllVertices((MyGraph)node));
			} else {
				vertices.add((V)node);
			}
		}
		
		return vertices;
	}
	
	public void mergeGraph(MyGraph<V, E> graph) {
		for (V v : graph.getAllVertices())
			addVertex(v);
		for (E e : graph.getEdges())
			addEdge(e, graph.getSource(e), graph.getDest(e));
	}

	@Override
	public String toString() {
		return this.name;
	}
	
	class UndoableAddNode extends AbstractUndoableEdit {
		private static final long serialVersionUID = 1L;
		V node;
		
		public UndoableAddNode(V vertex) {
			super();
			this.node = vertex;
		}
		
		public String getPresentationName() {
			return "Añadir nodo";
		}
		
		public void undo() throws CannotUndoException {
		    super.undo();
		    removeVertex(node);
	    }

		public void redo() throws CannotRedoException {
		    super.redo(); 
		    addVertex(node);
	    }
	}
	
	class UndoableRemoveNode extends AbstractUndoableEdit {
		private static final long serialVersionUID = 1L;
		
		private final V node;
		private final ArrayList<EdgeStruct<V, E>> links = new ArrayList<EdgeStruct<V, E>>();
		
		public UndoableRemoveNode(V vertex) {
			super();
			this.node = vertex;
			for (E edge : getEdges()) {
				
				if (getSource(edge).equals(vertex))
					links.add(new EdgeStruct<V, E>(vertex, getDest(edge), edge));
				
				if (getDest(edge).equals(vertex))
					links.add(new EdgeStruct<V, E>(getSource(edge), vertex, edge));
			}
		}
		
		public String getPresentationName() {
			return "Eliminar nodo";
		}
		
		public void undo() throws CannotUndoException {
		    super.undo();
		    addVertex(node);
		    
		    for (EdgeStruct<V, E> edge : links) {
		    	addEdge(edge.link, edge.node1, edge.node2);
		    }
	    }

		public void redo() throws CannotRedoException {
		    super.redo(); 
		    removeVertex(node);
	    }
	}
	
	class UndoableRemoveLink extends AbstractUndoableEdit {
		private static final long serialVersionUID = 1L;
		
		private final E link;
		private final V node1;
		private final V node2;
		
		public UndoableRemoveLink(V vertex1, V vertex2, E edge) {
			super();
			this.link = edge;
			this.node1 = vertex1;
			this.node2 = vertex2;
		}
		
		public String getPresentationName() {
			return "Eliminar enlace";
		}
		
		public void undo() throws CannotUndoException {
		    super.undo();
		    addEdge(link, node1, node2);
	    }

		public void redo() throws CannotRedoException {
		    super.redo(); 
		    removeEdge(link);
	    }
	}
	
	class UndoableAddLink extends AbstractUndoableEdit {
		private static final long serialVersionUID = 1L;

		private final E link;
		private final V node1;
		private final V node2;
		
		public UndoableAddLink(V vertex1, V vertex2, E edge) {
			super();
			this.link = edge;
			this.node1 = vertex1;
			this.node2 = vertex2;
		}
		
		public String getPresentationName() {
			return "Añadir enlace";
		}
		
		public void undo() throws CannotUndoException {
		    super.undo();
		    removeEdge(link);
	    }

		public void redo() throws CannotRedoException {
		    super.redo();
		    addEdge(link, node1, node2);
	    }
	}	
}