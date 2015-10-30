package shadertool.nodes;

import java.awt.Color;

public abstract class OperationNode extends Node {

	private static final long serialVersionUID = 1L;

	protected OperationNode(String name) {
		super(name);
		color = Color.cyan;
	}
}
