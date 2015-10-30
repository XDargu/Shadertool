package shadertool.nodes;

import java.awt.Color;

public abstract class ConversionNode extends Node {

	private static final long serialVersionUID = 1L;
	
	protected ConversionNode(String name) {
		super(name);
		color = Color.gray;
	}
}
