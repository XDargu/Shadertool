package shadertool.nodes.operation;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import shadertool.nodes.IOType;
import shadertool.nodes.Input;
import shadertool.nodes.OperationNode;
import shadertool.nodes.Output;
import shadertool.nodes.UpdatedFunction;

public class InverseNode extends OperationNode implements UpdatedFunction {
	private static final long serialVersionUID = 1L;
	private static final String PIXEL_SHADER =
			"#version 120" + "\n" +
            "uniform sampler2D tex;" + "\n" +
            "void main() {" + "\n" +
            "  vec4 color = texture2D(tex, gl_TexCoord[0].xy);\n" +
            // Se le resta el valor de cada componente a 1 para hacer la inversa
		    "  gl_FragColor = vec4(vec3(1.0) - color.rgb, color.a);\n" +
		    "}";
	
	private Output imgOut = new Output("Imagen", IOType.Image2f);
	private Input imgIn = new Input("Imagen", IOType.Image2f, this);

	private transient ShaderProgram shader;

	public InverseNode() throws SlickException {
		super("Inversa");

		outputs.add(imgOut);
		inputs.add(imgIn);
	}
	
	@Override
	public void initialize() throws SlickException {
		shader = new ShaderProgram(DEFAULT_VERTEX_SHADER, PIXEL_SHADER);
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