package shadertool.nodes.operation;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import shadertool.nodes.IOType;
import shadertool.nodes.Input;
import shadertool.nodes.OperationNode;
import shadertool.nodes.Output;
import shadertool.nodes.UpdatedFunction;
import shadertool.ui.PosterizationSelector;

public class PosterizationNode extends OperationNode implements UpdatedFunction {
	private static final long serialVersionUID = 1L;

	private int umbrales = 3;
	
	private Output imgOut = new Output("Imagen", IOType.Image2f);
	private Input imgIn = new Input("Imagen", IOType.Image2f, this);
	
	private transient ShaderProgram shader;

	public PosterizationNode() throws SlickException {
		super("Posterización");

		outputs.add(imgOut);
		inputs.add(imgIn);
	}

	public void setUmbral(int umbrales) throws SlickException {
		this.umbrales = umbrales;
		updateShader();
	}	
	
	public int getUmbrales() {
		return umbrales;
	}

	private void updateShader() throws SlickException {
		if (shader != null)
			shader.release();
		
		// Posteriza basándose en el umbral más cercano al valor del color
		String pixel =
			"#version 120" + "\n" +
            "uniform sampler2D tex;" + "\n" +
			"void main() { \n" +
			"  vec4 col = texture2D(tex, gl_TexCoord[0].xy);\n" +
			"  vec3 c = col.rgb;\n" +
			"  c = c * " + (umbrales + 1) + ";\n" +
			"  c = floor(c);\n" +
			"  c = c / " + (umbrales + 1) + ";\n" +
			"  gl_FragColor = vec4(c, col.a);\n" +
			"}\n";

		System.out.println(pixel);
		
		shader = new ShaderProgram(DEFAULT_VERTEX_SHADER, pixel);
	}

	@Override
	public void initialize() throws SlickException {
		actions.add(new AbstractAction("Número de umbrales") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				new PosterizationSelector(PosterizationNode.this).setVisible(true);
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
		g.drawImage(img, 0, 0);
		g.flush();
		shader.unbind();
		imgOut.updated();
	}

}