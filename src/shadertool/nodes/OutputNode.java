package shadertool.nodes;

import java.awt.Color;

public abstract class OutputNode extends Node {

	private static final long serialVersionUID = 1L;
	
	protected OutputNode(String name) {
		super(name);
		color = Color.white;
	}
}
