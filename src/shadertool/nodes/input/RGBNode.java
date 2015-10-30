package shadertool.nodes.input;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;


public class RGBNode extends RGBANode {
	private static final long serialVersionUID = 1L;
	
	private static final String PIXEL_SHADER =
			"#version 120\n" +
		    "uniform sampler2D tex;\n\n" +
		    "void main() {\n" +
		    "  gl_FragColor = vec4(texture2D(tex, gl_TexCoord[0].xy).rgb, 1.0);\n" +
		    "}";
	
	private transient ShaderProgram shader;

	public RGBNode() {
		super("RGB");
	}
	
	@Override
	public void initialize() throws SlickException {
		super.initialize();
		
		if (shader != null)
			shader.release();

		shader = new ShaderProgram(DEFAULT_VERTEX_SHADER, PIXEL_SHADER);
	}
	
	@Override
	public void run() throws SlickException {
		if (imageOut.img != null && img != null) {
			if (!drawn) {
				Graphics g = imageOut.img.getGraphics();
				shader.bind();
				shader.setUniform1i("tex", 0);
				g.drawImage(img, 0, 0);
				g.flush();
				shader.unbind();

				drawn = true;				
			}
			imageOut.updated();
		}
	}

}
