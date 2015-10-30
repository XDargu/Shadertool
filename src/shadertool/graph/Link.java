package shadertool.graph;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import shadertool.nodes.Input;
import shadertool.nodes.Output;
import shadertool.nodes.UpdatedFunction;

public class Link implements java.io.Serializable, UpdatedFunction {

	private static final long serialVersionUID = 1L;

	private String name;
    
    private Input output;
    private Output input;
    private transient UndoManager undoManager;
    
    public Link() {
    }
    
    public String toString() {
    	String out = "";
    	String in = "";
    	if (output != null)
    		out = output.name;
    	if (input != null)
    		in = input.name;
        return in + " - " + out;
    }
        
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Input getOutput() {
		return output;
	}
	public void setOutput(Input input) {
		undoManager.addEdit(new UndoableSetOutput(this, input, output));
		if (input != null)
			input.used = false;
		output = input;
		output.used = true;
	}
	public Output getInput() {
		return input;
	}
	public void setInput(Output output) {
		undoManager.addEdit(new UndoableSetInput(this, output, input));
		if (input != null)
			input.unsubscribe(this);

		input = output;		
		input.subscribe(this);
	}
	
	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

	public void updated(Image img) throws SlickException {		
		if (output != null)
			output.updated(img);
	}
	
	class UndoableSetOutput extends AbstractUndoableEdit {
		private static final long serialVersionUID = 1L;
		
		Link link;
		Input newInput;
		Input oldInput;
		
		public UndoableSetOutput(Link link, Input newInput, Input oldInput) {
			super();
			this.newInput = newInput;
			this.oldInput = oldInput;
			this.link = link;
		}
		
		public String getPresentationName() {
			return "Asignar entrada";
		}
		
		public void undo() throws CannotUndoException {
		    super.undo();
		    link.output = oldInput;
	    }

		public void redo() throws CannotRedoException {
		    super.redo();
		    link.output = newInput;
	    }
	}
	
	class UndoableSetInput extends AbstractUndoableEdit {
		private static final long serialVersionUID = 1L;
		
		Link link;
		Output newOutput;
		Output oldOutput;
		
		public UndoableSetInput(Link link, Output newOutput, Output oldOutput) {
			super();
			this.newOutput = newOutput;
			this.oldOutput = oldOutput;
			this.link = link;
		}
		
		public String getPresentationName() {
			return "Asignar salida";
		}
		
		public void undo() throws CannotUndoException {
		    super.undo();
		    link.input = oldOutput;
	    }

		public void redo() throws CannotRedoException {
		    super.redo();
		    link.input = newOutput;
	    }
	}
}
