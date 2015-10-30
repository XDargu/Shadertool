package shadertool.nodes;

import java.awt.Color;

import org.newdawn.slick.SlickException;

public abstract class InputNode extends Node {

	private static final long serialVersionUID = 1L;
	
	public abstract void run() throws SlickException;

	public InputNode(String name) {
		super(name);
		color = Color.green;
	}
}
