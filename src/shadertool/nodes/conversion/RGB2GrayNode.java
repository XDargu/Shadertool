package shadertool.nodes.conversion;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import shadertool.nodes.ConversionNode;
import shadertool.nodes.IOType;
import shadertool.nodes.Input;
import shadertool.nodes.Output;
import shadertool.nodes.UpdatedFunction;

public class RGB2GrayNode extends ConversionNode implements UpdatedFunction {
	private static final long serialVersionUID = 1L;
	
	private Output imgOut = new Output("Imagen", IOType.Image2f);
	private Input imgIn = new Input("Imagen", IOType.Image2f, this);
	
	private transient ShaderProgram shader;
	
	private static final String PIXEL_SHADER =
			"#version 120" + "\n" +
		    "uniform sampler2D tex;" + "\n" +
		    "void main() {" + "\n" +
			"  vec4 col = texture2D(tex, gl_TexCoord[0].xy);" + "\n" +
		    // Se calcula la luminancia de los pixels y se ponen los tres valores de color con dicho valor
			"  float luminance = dot(vec3(0.299,0.587,0.114), col.rgb);" + "\n" +
			"  gl_FragColor = vec4(vec3(luminance), col.a);" + "\n" +
			"}\n";

	public RGB2GrayNode() throws SlickException {
		super("RGB->GRAY");
		outputs.add(imgOut);
		inputs.add(imgIn);
	}
	
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