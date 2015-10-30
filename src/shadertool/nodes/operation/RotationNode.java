package shadertool.nodes.operation;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import shadertool.nodes.IOType;
import shadertool.nodes.Input;
import shadertool.nodes.OperationNode;
import shadertool.nodes.Output;
import shadertool.nodes.UpdatedFunction;
import shadertool.ui.RotationSelector;

public class RotationNode extends OperationNode implements UpdatedFunction {
	private static final long serialVersionUID = 1L;
	
	private Output imgOut = new Output("Imagen", IOType.Image2f);
	private Input imgIn = new Input("Imagen", IOType.Image2f, this);
	
	protected float rotation = 0;

	public RotationNode() throws SlickException {
		super("Rotación");

		outputs.add(imgOut);
		inputs.add(imgIn);
	}
	
	@Override
	public void initialize() throws SlickException {
		actions.add(new AbstractAction("Cambiar rotación") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new RotationSelector(RotationNode.this).setVisible(true);	
			}
		});
	}

	@Override
	public void updated(Image img) throws SlickException {
		int width = img.getWidth();
		int height = img.getHeight();
		imgOut.create(width, height);
		Graphics g = imgOut.img.getGraphics();
		g.rotate(width/2, height/2, rotation);
		g.drawImage(img, 0, 0);
		g.flush();
		imgOut.updated();
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

}