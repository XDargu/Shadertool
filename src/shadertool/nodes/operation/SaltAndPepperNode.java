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
import shadertool.ui.SaltAndPepperSelector;

public class SaltAndPepperNode extends OperationNode implements UpdatedFunction {
	private static final long serialVersionUID = 1L;
	
	private Output imgOut = new Output("Imagen", IOType.Image2f);
	private Input imgIn = new Input("Imagen", IOType.Image2f, this);

	private transient ShaderProgram shader;
	
	private float probSalt = 0.01f;
	private float probPepper = 0.01f;

	public SaltAndPepperNode() throws SlickException {
		super("Ruido salt & pepper");

		outputs.add(imgOut);
		inputs.add(imgIn);
	}	
	
	public float getProbSalt() {
		return probSalt;
	}	

	public float getProbPepper() {
		return probPepper;
	}
	
	public void setProb(float probSalt, float probPepper) throws SlickException {
		this.probSalt = probSalt;
		this.probPepper = probPepper;
		updateShader();
	}

	@Override
	public void initialize() throws SlickException {
		actions.add(new AbstractAction("Modificar propiedades") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SaltAndPepperSelector(SaltAndPepperNode.this).setVisible(true);
			}
		});
		
		updateShader();
	}
	
	private void updateShader() throws SlickException {
		if (shader != null)
			shader.release();
		
		String pixel =
			"#version 120" + "\n" +
            "uniform sampler2D tex;" + "\n" +
            "uniform float time;" + "\n" +
            "float rand(vec2 co){\n" +
            "  return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);\n" +
			"}\n\n" +
            "void main() {" + "\n" +
            "  vec4 col1 = texture2D(tex, gl_TexCoord[0].xy);\n";
		
		// Crea puntos blancos y negros con una cierta probabilidad
		pixel += "  float rnd = rand(gl_FragCoord.xy + vec2(time)); \n" +
				 "  float salt = step(1 -" + probSalt + ", rnd); \n" +
				 "  float pepper = step(" + probPepper + ", rnd); \n" +
				 "  float original = step(.5, salt + pepper); \n" +
				 "  float r = col1.r * original + 1.0 * salt + 0.0 * pepper; \n" +
				 "  float g = col1.g * original + 1.0 * salt + 0.0 * pepper; \n" +
				 "  float b = col1.b * original + 1.0 * salt + 0.0 * pepper; \n" +
				 "  gl_FragColor = vec4(r, g, b, col1.a); \n" +
				 "}\n";

		shader = new ShaderProgram(DEFAULT_VERTEX_SHADER, pixel);
	}

	@Override
	public void updated(Image img) throws SlickException {
		imgOut.create(img.getWidth(), img.getHeight());
		Graphics g = imgOut.img.getGraphics();
		shader.bind();
		shader.setUniform1i("tex", 0);
		shader.setUniform1f("time", (float) (System.currentTimeMillis() - start) / 1000f);
		g.drawImage(img, 0, 0);
		g.flush();
		shader.unbind();
		imgOut.updated();
	}

}