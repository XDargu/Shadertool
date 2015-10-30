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
import shadertool.ui.TranslationSelector;

public class TranslationNode extends OperationNode implements UpdatedFunction {
	private static final long serialVersionUID = 1L;
	
	private Output imgOut = new Output("Imagen", IOType.Image2f);
	private Input imgIn = new Input("Imagen", IOType.Image2f, this);
	
	protected int offsetX = 0;
	protected int offsetY = 0;

	public TranslationNode() throws SlickException {
		super("Traslación");

		outputs.add(imgOut);
		inputs.add(imgIn);
	}
	
	@Override
	public void initialize() throws SlickException {
		actions.add(new AbstractAction("Cambiar traslación") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new TranslationSelector(TranslationNode.this).setVisible(true);	
			}
		});
	}

	@Override
	public void updated(Image img) throws SlickException {
		int width = img.getWidth();
		int height = img.getHeight();
		imgOut.create(width, height);
		Graphics g = imgOut.img.getGraphics();
		g.drawImage(img, offsetX, offsetY);
		g.flush();
		imgOut.updated();
	}

	public int getXOffset() {
		return offsetX;
	}

	public void setXOffset(int offsetX) {
		this.offsetX = offsetX;
	}

	public int getYOffset() {
		return offsetY;
	}

	public void setYOffset(int offsetY) {
		this.offsetY = offsetY;
	}

}