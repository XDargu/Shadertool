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

public class RGB2HSVNode extends ConversionNode implements UpdatedFunction {
	private static final long serialVersionUID = 1L;
	
	private Output imgOut = new Output("Imagen", IOType.Image2f);
	private Input imgIn = new Input("Imagen", IOType.Image2f, this);
	
	private transient ShaderProgram shader;
	
	private static final String PIXEL_SHADER =
			"#version 120" + "\n" +
		    "uniform sampler2D tex;" + "\n" +
		    // Convierte de HSV a RGB utilizando las fórmulas correspondientes
		    "vec3 RGBtoHSV(vec3 rgb) {\n" +
		    "    vec3 hsv = vec3(0.0, 0.0, 0.0);\n" +
		    "    float min_val = min(min(rgb.x, rgb.y), rgb.z);\n" +
		    "    float max_val = max(max(rgb.x, rgb.y), rgb.z);\n" +
		    "    float delta = max_val - min_val;\n" +
		    "    hsv.z = max_val;\n" +
		    "    if (delta != 0.0) {\n" +
		    "        hsv.y = delta / max_val;\n" +
		    "        vec3 del_rgb = ( ( ( vec3(max_val,max_val,max_val) - rgb ) / 6.0 ) + ( delta / 2.0 ) ) / delta;\n" +
		    "        if (rgb.x >= max_val)\n" +
		    "            hsv.x = del_rgb.z - del_rgb.y;\n" +
		    "        else if (rgb.y == max_val)\n" +
		    "            hsv.x = (1.0/3.0) + del_rgb.x - del_rgb.z;\n" +
		    "        else if (rgb.z == max_val)\n" +
		    "            hsv.x = (2.0/3.0) + del_rgb.y - del_rgb.x;\n" +
		    "        if (hsv.x < 0.0)\n" +
		    "            hsv.x += 1.0;\n" +
		    "        if (hsv.x > 1.0)\n" +
		    "            hsv.x -= 1.0;\n" +
		    "    }\n" +
		    "    return hsv;\n" +
		    "}\n" +
		    "void main() {" + "\n" +
			"  vec4 col = texture2D(tex, gl_TexCoord[0].xy);" + "\n" +
			"  gl_FragColor = vec4(RGBtoHSV(col.rgb), col.a);" + "\n" +
			"}\n";

	public RGB2HSVNode() throws SlickException {
		super("RGB->HSV");
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