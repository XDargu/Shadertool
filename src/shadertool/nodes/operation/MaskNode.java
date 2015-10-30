package shadertool.nodes.operation;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import shadertool.nodes.IOType;
import shadertool.nodes.Input;
import shadertool.nodes.OperationNode;
import shadertool.nodes.Output;
import shadertool.nodes.UpdatedFunction;
import shadertool.ui.MaskSelector;

public class MaskNode extends OperationNode implements UpdatedFunction {
	private static final long serialVersionUID = 1L;

	private float[][] mask;
	private boolean ignoreBorders;
	
	private Output imgOut = new Output("Imagen", IOType.Image2f);
	private Input imgIn = new Input("Imagen", IOType.Image2f, this);
	
	private transient ShaderProgram shader;

	public MaskNode() throws SlickException {
		super("Máscara");

		mask = new float[][] { { 0, 0, 0 }, { 0, 1, 0 }, { 0, 0, 0 } };
		ignoreBorders = true;

		outputs.add(imgOut);
		inputs.add(imgIn);
	}

	public float[][] getMask() {
		return mask;
	}

	public void setMask(float[][] mask) throws SlickException {
		this.mask = mask;
		updateShader();
	}

	public void setIgnoreBorders(boolean ignoreBorders) throws SlickException {
		this.ignoreBorders = ignoreBorders;
		updateShader();
	}

	public boolean getIgnoreBorders() {
		return this.ignoreBorders;
	}
	
	private void updateShader() throws SlickException {
		if (shader != null)
			shader.release();
		
		String pixel =
			"#version 120" + "\n" +
            "uniform sampler2D tex;" + "\n" +
            "uniform vec2 step;" + "\n" +
            "void main() {" + "\n" +
            "  vec4 color = vec4(0.0, 0.0, 0.0, 0.0);\n";
		
		// Se realiza un bucle for que, por cada pixel de la máscara, suma su color en la textura original 
		// multiplicado por el peso en la máscara
		int backStepX = mask.length/2;
		int backStepY = mask[0].length/2;
		for (int i=0; i<mask.length; i++) {
			for (int j=0; j<mask[i].length; j++) {
				pixel += "  color += texture2D(tex, gl_TexCoord[0].xy + step * vec2(" + (i-backStepX) + ", " + (j-backStepY) + ")) * " + mask[i][j] + ";\n"; 
			}
		}
		
		pixel += "  color.a = 1.0;\n" +
		         "  gl_FragColor = color;\n" +
		         "}\n";

		shader = new ShaderProgram(DEFAULT_VERTEX_SHADER, pixel);
	}

	@Override
	public void initialize() throws SlickException {
		final MaskNode m = this;

		actions.add(new AbstractAction("Modificar máscara") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				new MaskSelector(m).setVisible(true);
			}
		});
		
		updateShader();
	}

	@Override
	public void updated(Image img) throws SlickException {
		imgOut.create(img.getWidth(), img.getHeight());
		Graphics g = imgOut.img.getGraphics();
		shader.bind();
		shader.setUniform1i("tex", 0);
		Texture tex = img.getTexture();
		shader.setUniform2f("step", tex.getWidth()/img.getWidth(), tex.getHeight()/img.getHeight());		
		g.drawImage(img, 0, 0);
		g.flush();
		shader.unbind();
		imgOut.updated();
	}

}