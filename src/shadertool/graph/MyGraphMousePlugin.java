package shadertool.graph;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

import javax.swing.undo.UndoManager;

import shadertool.nodes.Node;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class MyGraphMousePlugin extends AbstractGraphMousePlugin implements MouseListener{

	UndoManager undoManager;
	
	public MyGraphMousePlugin(UndoManager undoManager) {
		this(MouseEvent.BUTTON1_MASK);
		this.undoManager = undoManager;
	}
	
	public MyGraphMousePlugin(int modifiers) {
		super(modifiers);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void mouseClicked(MouseEvent e) {
        final VisualizationViewer<Node,Link> vv = (VisualizationViewer<Node,Link>)e.getSource();                    
        final Point2D p = e.getPoint(); 
        final MyGraph<Node, Link> graph = (MyGraph)vv.getGraphLayout().getGraph();
        
        GraphElementAccessor<Node,Link> pickSupport = vv.getPickSupport();
        
        if ((pickSupport != null) && (e.isControlDown())) {        	
        	// Nodo donde estamos pulsando
        	Object picked = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
            final Node station = picked instanceof MyGraph ? null : (Node)picked;        
            
            if (station != null)
            {
            	final PickedState<Node> pickedState = vv.getPickedVertexState();
            	Object[] nodes = pickedState.getSelectedObjects();           	
            	
            	
            	for (Object node : nodes) {
            		if (node instanceof Node) {
            			Link link = new Link();
            			link.setUndoManager(undoManager);
            			graph.newLink((Node)node, station, link);
            		}
            	}
            }
        }
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent e) { }
	@Override
	public void mousePressed(MouseEvent e) { }
}
