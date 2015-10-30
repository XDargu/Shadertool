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
import shadertool.nodes.input.SizeInterface;
import shadertool.ui.SizeSelector;

public class ZoomNode extends OperationNode implements UpdatedFunction, SizeInterface {
	private static final long serialVersionUID = 1L;
	
	private Output imgOut = new Output("Imagen", IOType.Image2f);
	private Input imgIn = new Input("Imagen", IOType.Image2f, this);
	
	protected int width = 100;
	protected int height = 100;

	public ZoomNode() throws SlickException {
		super("Zoom");

		outputs.add(imgOut);
		inputs.add(imgIn);
	}
	
	@Override
	public void initialize() throws SlickException {
		actions.add(new AbstractAction("Cambiar tamaño") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SizeSelector(ZoomNode.this).setVisible(true);	
			}
		});
	}

	@Override
	public void updated(Image img) throws SlickException {
		imgOut.create(img.getWidth(), img.getHeight());
		Graphics g = imgOut.img.getGraphics();
		
		float scaleX = width / img.getWidth();
		float scaleY = height / img.getHeight(); 
		
		g.scale(scaleX, scaleY);
		g.drawImage(img, 0, 0);
		g.flush();
		imgOut.updated();
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}