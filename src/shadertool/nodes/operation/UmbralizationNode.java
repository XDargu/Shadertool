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
import shadertool.ui.UmbralSelector;

public class UmbralizationNode extends OperationNode implements UpdatedFunction {
	private static final long serialVersionUID = 1L;

	private float umbralR;
	private float umbralG;
	private float umbralB;
	
	private Output imgOut = new Output("Imagen", IOType.Image2f);
	private Input imgIn = new Input("Imagen", IOType.Image2f, this);
	
	private transient ShaderProgram shader;

	public UmbralizationNode() throws SlickException {
		super("Umbralización");

		umbralR = 0.5f;
		umbralG = 0.5f;
		umbralB = 0.5f;

		outputs.add(imgOut);
		inputs.add(imgIn);
	}

	public void setUmbral(float umbralR, float umbralG, float umbralB) throws SlickException {
		this.umbralR = umbralR;
		this.umbralG = umbralG;
		this.umbralB = umbralB;
		updateShader();
	}	
	
	public float getUmbralR() {
		return umbralR;
	}

	public float getUmbralG() {
		return umbralG;
	}

	public float getUmbralB() {
		return umbralB;
	}

	private void updateShader() throws SlickException {
		if (shader != null)
			shader.release();
		
		String pixel =
			"#version 120" + "\n" +
            "uniform sampler2D tex;" + "\n" +
            "void main() {" + "\n";
		
		
		pixel += "  vec4 col1 = texture2D(tex, gl_TexCoord[0].xy);\n";
		pixel += "  vec3 umbral = vec3(" + umbralR + "," + umbralG + "," + umbralB + "); \n" +
					"vec3 ou = step(umbral, col1.xyz); \n" +
					"gl_FragColor = vec4(ou, col1.a); \n" +
					"}\n";

		shader = new ShaderProgram(DEFAULT_VERTEX_SHADER, pixel);
	}

	@Override
	public void initialize() throws SlickException {
		actions.add(new AbstractAction("Modificar umbral") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				new UmbralSelector(UmbralizationNode.this).setVisible(true);
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