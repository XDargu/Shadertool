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

public class HSV2RGBNode extends ConversionNode implements UpdatedFunction {
	private static final long serialVersionUID = 1L;
	
	private Output imgOut = new Output("Imagen", IOType.Image2f);
	private Input imgIn = new Input("Imagen", IOType.Image2f, this);
	
	private transient ShaderProgram shader;
	
	private static final String PIXEL_SHADER =
			"#version 120" + "\n" +
		    "uniform sampler2D tex;" + "\n" +
			// Convierte de RGB a HSV utilizando las fórmulas correspondientes
			"vec3 hsv (vec3 hsv) {\n" +
		    "  float h = hsv.r;\n" +
		    "  float s = hsv.g;\n" +
		    "  float v = hsv.b;\n" +
		    "  return mix(vec3(1.), clamp((abs(fract(h+vec3(3.,2.,1.)/3.)*6.-3.)-1.),0.,1.),s)*v;" +
		    "}\n\n" +
		    "void main() {" + "\n" +
			"  vec4 col = texture2D(tex, gl_TexCoord[0].xy);" + "\n" +
			"  gl_FragColor = vec4(hsv(col.rgb), col.a);" + "\n" +
			"}\n";

	public HSV2RGBNode() throws SlickException {
		super("HSV->RGB");
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