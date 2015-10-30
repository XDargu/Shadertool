package shadertool.graph;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Set;

import shadertool.nodes.Node;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class MyGraphKeyListener implements KeyListener{
	@Override
	public void keyTyped(KeyEvent e) {}
	public MyGraphKeyListener() {}
	@SuppressWarnings("unchecked")
	@Override
	public void keyPressed(KeyEvent e) {
		final VisualizationViewer<Node,Link> vv = (VisualizationViewer<Node,Link>)e.getSource();
        @SuppressWarnings("rawtypes")
		final MyGraph<Node, Link> graph = (MyGraph)vv.getGraphLayout().getGraph();
        
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
	        GraphElementAccessor<Node,Link> pickSupport = vv.getPickSupport();        
	        
	        if (pickSupport != null) {        	
	            Set<Link> links = vv.getPickedEdgeState().getPicked();  
	            
	            for (Link link : links) {
	            	graph.deleteLink(link);
	            }
	            
	            Set<Node> nodes = vv.getPickedVertexState().getPicked();
	            
	            for (Object node : nodes) {
	            	if (!(node instanceof MyGraph)) {
	            		graph.deleteNode((Node)node);
	            	}
	            }
	            
	        	vv.updateUI();        	
	        }
        }
	}
	@Override
	public void keyReleased(KeyEvent e) {}
}
