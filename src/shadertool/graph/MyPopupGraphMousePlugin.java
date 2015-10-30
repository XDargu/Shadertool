package shadertool.graph;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.filechooser.FileNameExtensionFilter;

import shadertool.nodes.Node;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

/**
 * a GraphMousePlugin that offers popup
 * menu support
 */
public class MyPopupGraphMousePlugin extends AbstractPopupGraphMousePlugin implements MouseListener {
	
	private final Grafo grafo;
	
    public MyPopupGraphMousePlugin(Grafo grafo) {
        this(grafo, MouseEvent.BUTTON3_MASK);        
    }
    
    public MyPopupGraphMousePlugin(Grafo grafo, int modifiers) {
        super(modifiers);
        this.grafo = grafo;
    }
    
    private Point2D punto;
    
    @Override
    public void mousePressed(MouseEvent e)
    {
    	super.mousePressed(e);
    	punto = e.getPoint();    
    }
    
    /**
     * If this event is over a node, pop up a menu to
     * allow the user to center view to the node
     *
     * @param e
     */
    @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	protected void handlePopup(MouseEvent e) {
    	
    	Point2D punto2 = e.getPoint();
    	
    	if (punto.distance(punto2) > 1)
    		return;
    	
        final VisualizationViewer<Node,Link> vv =
            (VisualizationViewer<Node,Link>)e.getSource();
        final Point2D p = e.getPoint(); 
        final MyGraph<Node, Link> graph = (MyGraph)vv.getGraphLayout().getGraph();
        //final Layout layout = vv.getGraphLayout();

        GraphElementAccessor<Node,Link> pickSupport = vv.getPickSupport();
               
        if(pickSupport != null) {
        	
        	// Nodo donde estamos pulsando
        	Object picked = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
            final Node station = picked instanceof MyGraph ? null : (Node)picked;
            final MyGraph graphStation = picked instanceof MyGraph ? (MyGraph)picked : null;
            // Arista donde estamos pulsando
            final Link link = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
        	
        	// ****************************** GRAFO COLAPSADO ******************************
            if(graphStation != null) {
            	JPopupMenu popup = new JPopupMenu();
            	
            	// ************* Expandir ***************
            	popup.add(new AbstractAction("Expandir") {
                    public void actionPerformed(ActionEvent e) {
		            	grafo.expand(graphStation);
                    }
            	});
            	// ************* Renombrar ***************
            	popup.add(new AbstractAction("Renombrar") {
		        	public void actionPerformed(ActionEvent e) {
		        		String s = (String)JOptionPane.showInputDialog(
		                        null,
		                        "Escribe el nuevo nombre del nodo:",
		                        "Renombrar nodo",
		                        JOptionPane.PLAIN_MESSAGE,
		                        null,
		                        null,
		                        graphStation.getName());
		        		graphStation.setName(s);
		        		vv.updateUI();
		        	}
		        });
            	
            	popup.addSeparator();
            	
            	// ************* Propiedades de cada nodo ***************
            	
            	for (Object object : graphStation.getAllVertices()) {
            		if (object instanceof Node) {
            			Node node = (Node) object;
            			JMenu menu = new JMenu(node.getName());
            			for (int i=0; i< node.getActions().size(); i++) {
            				menu.add(node.getActions().get(i));
                        }
            			popup.add(menu);
            		}
            	}
            	
            	popup.addSeparator();
            	
            	// ************* Exportar ***************
            	popup.add(new AbstractAction("Exportar nodo") {
                    public void actionPerformed(ActionEvent e) {
                    	SavableGraph sg = new SavableGraph();
                    	sg.setGraph(graphStation);
                    	sg.name = graphStation.getName();
                    	sg.saveCollapsedWithRoot(vv);
                    	sg.savePositions(vv);
                    	try
				  	      {
				  			 JFileChooser fc = new JFileChooser();
				  			 fc.setFileFilter(new FileNameExtensionFilter("Nodos", "node"));
				  			 int returnVal = fc.showSaveDialog(null);
				  			 FileOutputStream fileOut;
				  			 
				  			 if (returnVal == JFileChooser.APPROVE_OPTION)
				  			 {
				  				 String path = "";
				  				 if (fc.getSelectedFile().getPath().endsWith(".node"))
				  					 path = fc.getSelectedFile().getPath();
				  				 else
				  					path = fc.getSelectedFile().getPath() + ".node";
					  	         fileOut = new FileOutputStream(path);
					  	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
					  	         out.writeObject(sg);
					  	         
					  	         out.close();
					      	     fileOut.close();				  	         
				  			 }
				  	      } catch(IOException i)
				  	      {
				  	    	  JOptionPane.showMessageDialog(null,
				  				    "Error al exportar el nodo: " + i.getMessage(),
				  				    "Error",
				  				    JOptionPane.ERROR_MESSAGE);
				  	      }
                    }
            	});
            	
            	popup.show(vv, e.getX(), e.getY());
            }
            
            // ****************************** SIN NODOS NI ARISTAS ******************************
            
            
            // *********** NODOS ***********
            if(station != null) {
                JPopupMenu popup = new JPopupMenu();
                
                // ************* Colapsar ***************
                /*if (vv.getPickedVertexState().getPicked().size() > 1)
                {
	                popup.add(new AbstractAction("Colapsar") {
						private static final long serialVersionUID = 1L;

						public void actionPerformed(ActionEvent e) {
	                    	Collection picked = new HashSet(vv.getPickedVertexState().getPicked());
	                    	grafo.collapse(picked);
	                    }
	                });
                }*/
                
                // *********** Fin colapsar *************

                popup.add(new AbstractAction("Centrar en nodo") {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {

                        MutableTransformer layout = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);

                        Point2D ctr = vv.getCenter();                   

                        double scale = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();

                        double deltaX = (ctr.getX() - p.getX())*1/scale;
                        double deltaY = (ctr.getY() - p.getY())*1/scale;

                        layout.translate(deltaX, deltaY);
                    }
                });  
                
                popup.add(new AbstractAction("Eliminar") {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
                    	graph.deleteNode(station);
                    	vv.updateUI();
                    }
            	});
                
                popup.add(new Separator());
                
                for (int i=0; i< station.getActions().size(); i++) {
                	popup.add(station.getActions().get(i));
                }
                
                if (station.getActions().size() > 0)
                	popup.add(new Separator());
                
				popup.add(new AbstractAction("Exportar nodo") {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
						try
				  	      {
				  			 JFileChooser fc = new JFileChooser();
				  			 fc.setFileFilter(new FileNameExtensionFilter("Nodos", "node"));
				  			 int returnVal = fc.showSaveDialog(null);
				  			 FileOutputStream fileOut;
				  			 
				  			 if (returnVal == JFileChooser.APPROVE_OPTION)
				  			 {
				  				 String path = "";
				  				 if (fc.getSelectedFile().getPath().endsWith(".node"))
				  					 path = fc.getSelectedFile().getPath();
				  				 else
				  					path = fc.getSelectedFile().getPath() + ".node";
					  	         fileOut = new FileOutputStream(path);
					  	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
					  	         out.writeObject(station);
					  	         
					  	         out.close();
					      	     fileOut.close();				  	         
				  			 }
				  	      } catch(IOException i)
				  	      {
				  	    	  JOptionPane.showMessageDialog(null,
				  				    "Error al exportar el nodo: " + i.getMessage(),
				  				    "Error",
				  				    JOptionPane.ERROR_MESSAGE);
				  	      }
					}
				});

                popup.show(vv, e.getX(), e.getY());
            }
            
            // *********** ARISTAS ***********
            if (link != null)
            {
            	JPopupMenu popup = new JPopupMenu();
            	
            	popup.add(new AbstractAction("Eliminar") {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
                    	graph.deleteLink(link);
                    	vv.updateUI();
                    }
            	});
            	
            	popup.add(new Separator());
            	
            	JMenu popupEntradas = new JMenu("Elegir entrada");
                popup.add(popupEntradas);
                
                for (int i=0; i< graph.getDest(link).getInputs().size(); i++)
                {
                	final int index = i;
                	
                	String name = graph.getDest(link).getInputs().get(index).name;
                	// Mensaje de entrada seleccionada
                	if (link.getOutput() != null)
                		if (name.equals(link.getOutput().name))
                			name += " (Seleccionada)";
                	// Mensaje de entrada en uso
                	if (graph.getDest(link).getInputs().get(index).used)
                		name += " (En uso)";
                	
                	popupEntradas.add(new AbstractAction(name) {
						private static final long serialVersionUID = 1L;

						public void actionPerformed(ActionEvent e) {							
							link.setOutput(graph.getDest(link).getInputs().get(index));
							vv.updateUI();							
						}
					});
                }
            	
            	JMenu popupSalidas = new JMenu("Elegir salida");
                popup.add(popupSalidas);
                
                for (int i=0; i< graph.getSource(link).getOutputs().size(); i++)
                {
                	final int index = i;
                	
                	String name = graph.getSource(link).getOutputs().get(index).name;
                	// Mensaje de salida seleccionada
                	if (link.getInput() != null)
                		if (name.equals(link.getInput().name))
                			name += " (Seleccionada)";
                	
	                popupSalidas.add(new AbstractAction(name) {
						private static final long serialVersionUID = 1L;

						public void actionPerformed(ActionEvent e) {							
							link.setInput(graph.getSource(link).getOutputs().get(index));
							vv.updateUI();
						}
					});
                }
                
                popup.show(vv, e.getX(), e.getY());
            }
        }        
    }
}
