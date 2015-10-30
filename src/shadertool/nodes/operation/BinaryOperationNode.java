package shadertool.nodes.operation;

import java.awt.event.ActionEvent;
import java.io.Serializable;

import javax.swing.AbstractAction;

import org.newdawn.slick.SlickException;

import shadertool.Settings;
import shadertool.nodes.IOType;
import shadertool.nodes.Input;
import shadertool.ui.BinaryOperationSelector;

public class BinaryOperationNode extends FormulaNode {
	private static final long serialVersionUID = 1L;
	
	private class BinaryOperation implements Serializable {
		private static final long serialVersionUID = 1L;
		String name;
		String code;		
		public BinaryOperation(String name, String code) {
			this.name = name;
			this.code = code;
		}
	}
	
	// Aquí se gurdan todas las operaciones y su código
	private BinaryOperation[] operations = new BinaryOperation[] {
		new BinaryOperation("Suma", "color = col1 + col2;"),
		new BinaryOperation("Resta", "color = col1 - col2;"),
		new BinaryOperation("Producto", "color = col1 * col2;"),
		new BinaryOperation("División", "color = col1 / col2;"),
		new BinaryOperation("Mínimo", "color = min(col1, col2);"),
		new BinaryOperation("Máximo", "color = max(col1, col2);"),
		new BinaryOperation("Suma absoluta", "color = abs(col1) + abs(col2);"),
	};
	
	private int currentOperation;	
	
	public int getCurrentOperation() {
		return currentOperation;
	}

	public void setCurrentOperation(int currentOperation) {
		try {			
			this.currentOperation = currentOperation;
			setSettings(2, 1, operations[currentOperation].code);
			updateShader(getInputSize(), getOutputSize(), getFormula());
			if (Settings.autoNameNodes)
				name = operations[currentOperation].name;
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public BinaryOperationNode() throws SlickException {
		super();		
		name = "Operación binaria";
		currentOperation = 0;
		// **** Entrada 1 y Salida 1 ya se crean en el FormulaNode
		//inputs.add(new Input("Entrada 1", IOType.Image2f, this));
		//outputs.add(new Output("Salida 1", IOType.Image2f));
		inputs.add(new Input("Entrada 2", IOType.Image2f, this));
	}

	@Override
	public void initialize() throws SlickException {
		setSettings(2, 1, operations[currentOperation].code);
		updateShader(getInputSize(), getOutputSize(), getFormula());
		
		actions.add(new AbstractAction("Cambiar operación") {
			private static final long serialVersionUID = 1L;
			
			public void actionPerformed(ActionEvent e) {
				String[] names = new String[operations.length];			
				for (int i=0; i<operations.length; i++) {
					names[i] = operations[i].name;
				}
				new BinaryOperationSelector(BinaryOperationNode.this, names).setVisible(true);
			}
		});
	}
}