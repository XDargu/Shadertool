package shadertool.graph;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.apache.commons.collections15.Transformer;
import org.newdawn.slick.SlickException;

import shadertool.Editor;
import shadertool.Settings;
import shadertool.nodes.Input;
import shadertool.nodes.Node;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ViewTranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.subLayout.GraphCollapser;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import edu.uci.ics.jung.visualization.util.Animator;


public class Grafo {
	
	private final Editor vista;
	public MyGraph<Node, Link> g;
	public VisualizationViewer<Node,Link> vv;
	
	public GraphCollapser collapser;
	public UndoManager undoManager;
	public boolean modified;

	public Grafo(Editor vista) throws SlickException {
		this(vista, new MyGraph<Node, Link>());
	}
	
	public Grafo(Editor vista, MyGraph<Node, Link> g) throws SlickException {
		this.vista = vista;
		initialize(g);		
	}
	
	public void initialize(MyGraph<Node, Link> g) throws SlickException {
		undoManager = new UndoManager();
		modified = false;
		collapser = new GraphCollapser(g);
		
		this.g = g;	
		g.initialize(vista, this);
		
		for (Node node : g.getAllVertices()) {
			node.createActions();
			node.init();
		}
			    
	    Layout<Node, Link> layout = new KKLayout<Node, Link>(g);
	    layout.setSize(new Dimension(640, 480));
	    
	    vv = new VisualizationViewer<Node,Link>(layout);	    
	    vv.setPreferredSize(new Dimension(640,480));	
	        
	    // Aristas
        float dash[] = {10.0f};
        
        final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        final Stroke boldStroke = new BasicStroke(2.0f);
        
        Transformer<Link, Stroke> edgeStrokeTransformer = 
              new Transformer<Link, Stroke>() {
            	public Stroke transform(Link s) {
            		if ((s.getInput() == null) || (s.getOutput() == null))
            			return edgeStroke;
            		
            		if (s.getInput().type.isCompatible(s.getOutput().type))
            			return boldStroke;
            		else
            			return edgeStroke;
            	}
        };    
        
        Transformer<Link, String> edgeToolTipTransformer = new Transformer<Link, String>() {			
			@Override
			public String transform(Link l) {
				String rtr = "";
				if (l.getOutput() != null)
					rtr += " Entrada: " + l.getOutput().name;
				if (l.getInput() != null)
					rtr += " Salida: " + l.getInput().name;
				return rtr;
			}
		};
        
        vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Node>());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<Link>());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.N);
        
        vv.addKeyListener(new MyGraphKeyListener());
        vv.getRenderContext().setVertexShapeTransformer(new ClusterVertexShapeFunction<Node>());
        vv.getRenderContext().setVertexFillPaintTransformer(new ClusterVertexFillPaintTransformer<Node, Paint>(Color.green, vv));
        vv.setEdgeToolTipTransformer(edgeToolTipTransformer);
        
        vv.setBackground(Color.white);
        
        PluggableGraphMouse gm = new PluggableGraphMouse();
        gm.add(new PickingGraphMousePlugin<Node, Link>());
        gm.add(new ViewTranslatingGraphMousePlugin(MouseEvent.BUTTON3_MASK));
        gm.add(new MyPopupGraphMousePlugin(this));
        gm.add(new MyGraphMousePlugin(this.undoManager));        
        
        vv.setGraphMouse(gm);        
        vv.updateUI();       
        
        // Drag & Drop
        TransferHandler th = new TransferHandler(){
			private static final long serialVersionUID = 1L;

			@Override
            public boolean canImport(JComponent comp,
                    DataFlavor[] transferFlavors) {
        		
        		for (int i = 0; i < transferFlavors.length; i++) {
					if ((transferFlavors[i] == Node.flavor) || (transferFlavors[i] == SavableGraph.flavor) || (transferFlavors[i].isFlavorJavaFileListType()))
						return true;
				}
                return false;
            }
        	
        	@SuppressWarnings({ "unchecked" })
			@Override
        	public boolean importData(TransferHandler.TransferSupport support) {
        		if (support.getDataFlavors()[0] == Node.flavor) {
        			try {
        				Node node = (Node)support.getTransferable().getTransferData(Node.flavor);
	                    newNode(node, support.getDropLocation().getDropPoint());
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
        		}
        		else if (support.getDataFlavors()[0] == SavableGraph.flavor) {
        			try {
        				SavableGraph sg = (SavableGraph)support.getTransferable().getTransferData(SavableGraph.flavor);
        				// Se crean las acciones iniciales de todos los nodos
        				for (Node n : sg.getGraph().getAllVertices())
        					n.createActions();
        				// Se unen ambos grafos (el exportado y el nuevo)
        				Grafo.this.g.mergeGraph(sg.getGraph());
        				// Se calcula el offset inicial y se asigna al nodo colapsado padre
        				Point2D originalPoint = sg.getRootPoint();
    					Point2D setupPoint = support.getDropLocation().getDropPoint();
    					Point2D finalPoint = new Point2D.Double(
    							 setupPoint.getX() - originalPoint.getX(),
    							 setupPoint.getY() - originalPoint.getY()
    							 );
        				sg.loadCollapsedNodes(vv, Grafo.this, finalPoint);
        				// Se calcula la nueva posición de todos los hijos y se asigna dicha posición
        				for (VertexPointStruct vps : sg.getPositions()) {
        					 originalPoint = (Point2D) vps.point;
        					 Point2D realPoint = new Point2D.Double(
        							 finalPoint.getX() + originalPoint.getX(),
        							 finalPoint.getY() + originalPoint.getY()
        							 );
        					 vv.getGraphLayout().setLocation(vps.vertex, (Point2D) realPoint);
        				}
        				// Actualización de la Interfaz de Usuario
        				vv.updateUI();
	                } catch (Exception e) {
	                	e.printStackTrace();
	                }
        		}
        		else if (support.getDataFlavors()[0].isFlavorJavaFileListType()) {
        			 try {
						List<File> droppedFiles = (List<File>)support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);                    
	                     for (File file : droppedFiles) {
	                         if (file.getName().endsWith(".node")) {
	                         	cargarNodoExterno(file.getPath(), support.getDropLocation().getDropPoint());
	                         }
	                     }
        			 } catch (Exception e) {
	                     e.printStackTrace();
	                 }
        		 }
                 
                 return true;                 
             }
        };
        vv.setTransferHandler(th);        
	}
	
	@SuppressWarnings("rawtypes")
	public void initializeGraph(MyGraph graph) {
		graph.initialize(vista, this);
	}
	
	public void unattachToJPanel(JPanel panel) {
		panel.remove(vv);
	}
	
	public void attachToJPanel(JPanel panel) {	
		panel.add(vv);
		panel.setVisible(true);		
	}
	
	public void undo() {		
		g.undo();
		vv.updateUI();
	}
	
	public void redo() {
		g.redo();
		vv.updateUI();
	}
	
	public void serializar(String path)
	{
		SavableGraph sg = new SavableGraph();
		sg.setGraph(g);
		sg.savePositions(vv);
		sg.settings = Settings.getSettings();
		sg.saveCollapsedNodes(vv);
		
		try
	      {
			 FileOutputStream fileOut;			 
  	         fileOut = new FileOutputStream(path);
  	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
  	         out.writeObject(sg);
  	         g.setModified(false);
  	         out.close();
      	     fileOut.close();				  	         
			 
	      } catch(IOException i)
	      {
	    	  JOptionPane.showMessageDialog(null,
				    "Error al guardar el grafo: " + i.getMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
	      }
	}
	
	public void newNode(Node node, final Point2D point) throws SlickException {
		node.createActions();
		node.init();
		g.newNode(node);
		
		final MutableTransformer view = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
		
		vv.getGraphLayout().setLocation((Node)node, new Point2D.Double(
				point.getX() - view.getTranslateX(), 
				point.getY() - view.getTranslateY())
		);
	}
	
	public void newNode(Node original_node) throws SlickException {
		Node node;
		try {
			node = original_node.getClass().newInstance();
			
			node.createActions();
			node.init();
			g.newNode(node);
			
			final MutableTransformer view = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);
			
			vv.getGraphLayout().setLocation((Node)node, new Point2D.Double(
					vv.getX() - view.getTranslateX() + vv.getWidth() / 2, 
					vv.getY() - view.getTranslateY() + vv.getHeight() / 2)
			);
		} catch (InstantiationException | IllegalAccessException e) {
			JOptionPane.showMessageDialog(null,
				    "Error al crear el nodo: " + e.getMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}	
	}
	
	private void cargarNodoExterno(String path, Point2D point) throws SlickException {
		Node node = null;
		
		// Abrir un nodo serializado
		try
		{
	         FileInputStream fileIn = new FileInputStream(path);
			 ObjectInputStream in = new ObjectInputStream(fileIn);
			 // Leer el nodo
			 node = (Node)in.readObject();			 
			 in.close();
			 fileIn.close();			 
		}catch(IOException i)
		{
			  JOptionPane.showMessageDialog(null,
				    "Error al importar el nodo: " + i.getMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			  return;
		} catch(ClassNotFoundException c) {
			JOptionPane.showMessageDialog(null,
				    "El nodo importado no tiene el formato correcto: " + c.getMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		      return;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
				    "Error al importar el nodo: " + e.getMessage(),
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}
		
		newNode(node, point);
	}
	
	public void setStaticLayout(StaticLayout<Node, Link> layout, boolean withCollapsed) {
		
		if (withCollapsed) {
			layout.setSize(vv.getGraphLayout().getSize());
			LayoutTransition<Node, Link> lt = new LayoutTransition<Node, Link>(
					vv, vv.getGraphLayout(), layout);
			Animator animator = new Animator(lt);
			animator.start();
			vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
			vv.repaint();
		}
		
		for (Node n : g.getAllVertices()) {
			vv.getGraphLayout().setLocation(n, layout.transform(n));
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void collapse(Collection nodes) {
		MyGraph collapsedNode = privateCollapse(nodes);
		undoManager.addEdit(new UndoableCollapse(collapsedNode));
	}
	
	@SuppressWarnings("rawtypes")
	public void expand(MyGraph graph) {
		undoManager.addEdit(new UndoableExpand(graph));
		privateExpand(graph);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void privateExpand(MyGraph graph) {		
		final Layout layout = vv.getGraphLayout();

    	GraphCollapser collapser = graph.collapser;
        MyGraph g = (MyGraph) collapser.expand(layout.getGraph(), graph);
        graph.collapser = null;
        vv.getRenderContext().getParallelEdgeIndexFunction().reset();
        this.g = g;
        initializeGraph(g);
        layout.setGraph(g);
            
        vv.getPickedVertexState().clear();
        vv.updateUI();
        
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private MyGraph privateCollapse(Collection nodes) {
		final Layout layout = vv.getGraphLayout();
		
		if(nodes.size() > 1) {
        	GraphCollapser collapser = new GraphCollapser(this.g);
        	MyGraph inGraph = (MyGraph) layout.getGraph();
            MyGraph clusterGraph = (MyGraph) collapser.getClusterGraph(inGraph, nodes);

            MyGraph g = (MyGraph) collapser.collapse(layout.getGraph(), clusterGraph);
            double sumx = 0;
            double sumy = 0;
            for(Object v : nodes) {
            	Point2D p = (Point2D)layout.transform(v);
            	sumx += p.getX();
            	sumy += p.getY();
            }
            Point2D cp = new Point2D.Double(sumx/nodes.size(), sumy/nodes.size());
            vv.getRenderContext().getParallelEdgeIndexFunction().reset();
            layout.setGraph(g);
            this.g = g;
            initializeGraph(g);
            layout.setLocation(clusterGraph, cp);
            clusterGraph.collapser = collapser;
            vv.getPickedVertexState().clear();	                            
            vv.updateUI();            
            return clusterGraph;
        }		
		return null;
	}
	
	public void checkInputs(Node node) {
		boolean exists;
		for (Input input : node.getInputs()) {
			exists = false;
			for (Link link : g.getInEdges(node)) {
				if (link.getOutput() != null) {
					if (link.getOutput().equals(input))
						exists = true;
				}
			}
			input.used = exists;
		}
	}
	
	class UndoableCollapse extends AbstractUndoableEdit {
		private static final long serialVersionUID = 1L;
		
		@SuppressWarnings("rawtypes")
		MyGraph collapsedNode;
		
		@SuppressWarnings("rawtypes")
		public UndoableCollapse(MyGraph collapsedNode) {
			super();
			this.collapsedNode = collapsedNode;
		}
		
		public String getPresentationName() {
			return "Colapsar";
		}
		
		public void undo() throws CannotUndoException {
		    super.undo();
		    privateExpand(collapsedNode);
	    }

		public void redo() throws CannotRedoException {
		    super.redo();
		    collapsedNode = privateCollapse(collapsedNode.getVertices());
	    }
	}
	
	class UndoableExpand extends AbstractUndoableEdit {
		private static final long serialVersionUID = 1L;
		
		@SuppressWarnings("rawtypes")
		MyGraph graph;
		
		@SuppressWarnings("rawtypes")
		public UndoableExpand(MyGraph graph) {
			super();
			this.graph = graph;
		}
		
		public String getPresentationName() {
			return "Expandir";
		}
		
		public void undo() throws CannotUndoException {
		    super.undo();
		    graph = privateCollapse(graph.getVertices());
	    }

		public void redo() throws CannotRedoException {
		    super.redo();
		    privateExpand(graph);
	    }
	}
}
