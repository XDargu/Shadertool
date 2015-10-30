package shadertool.nodes;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.newdawn.slick.SlickException;

public abstract class Node implements java.io.Serializable, Transferable {

	private static final long serialVersionUID = 1L;

	public Color color;
	
	protected String name;
	// Entradas y salidas
	protected ArrayList<Input> inputs;
	protected ArrayList<Output> outputs;
	
	// Acciones que puede ejecutar el nodo
	protected transient ArrayList<AbstractAction> actions;
	protected transient long start;
	
	// Vertex shader por defecto
	public static final String DEFAULT_VERTEX_SHADER =
			"#version 120\n" +
	        "void main() {\n" +
            "  gl_TexCoord[0] = gl_MultiTexCoord0;\n" +
            "  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n" +
            "}";
	
	public static transient final DataFlavor flavor = new DataFlavor(Node.class, "Node");
	
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
	
	public Node(String name)
	{
		this.name = name;
		inputs = new ArrayList<Input>();
        outputs = new ArrayList<Output>();
        actions = new ArrayList<AbstractAction>();
	}
    
    public String toString() { 
        return name;       
    }
    
    public void createActions() {
    	actions = new ArrayList<AbstractAction>();
    	
    	actions.add(new AbstractAction("Renombrar") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
        		String s = (String)JOptionPane.showInputDialog(
                        null,
                        "Escribe el nuevo nombre del nodo:",
                        "Renombrar nodo",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        Node.this.name);
        		Node.this.name = s;        		
        	}
        });
    }
    
    public ArrayList<Input> getInputs() {
		return inputs;
	}
    
    // Clases de entrada y salida
	public ArrayList<Output> getOutputs() {
		return outputs;
	}	
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
    public ArrayList<AbstractAction> getActions() {
		return actions;
	}
    
    public void init() throws SlickException {
    	start = System.currentTimeMillis();
    	initialize();
    }
    
    public abstract void initialize() throws SlickException;
}
