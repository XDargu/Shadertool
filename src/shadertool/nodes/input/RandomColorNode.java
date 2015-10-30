package shadertool.nodes.input;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;

public class RandomColorNode extends RandomNode {

	private static final long serialVersionUID = 1L;
	private static final String PIXEL_SHADER =
			"#version 120" + "\n" +
            "uniform sampler2D tex;" + "\n" +
            "uniform float time;" + "\n" +
            "float rand(vec2 co){\n" +
            "  return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);\n" +
			"}\n\n" +
            "void main() {" + "\n" +
			// Asigna valores de color aleatorios a todas las coordenadas
            "  gl_FragColor = vec4(" +
            "    rand(gl_FragCoord.xy + vec2(time)),\n" +
            "    rand(gl_FragCoord.xy + vec2(time*223.225)),\n" +
            "    rand(gl_FragCoord.xy + vec2(time*3.12355854)),\n" +
            "    rand(gl_FragCoord.xy + vec2(time*4.837694)));" +
		    "}";
	
	public RandomColorNode() {
		super("Random (color)");
	}

	@Override
	public void initialize() throws SlickException {
		super.initialize();
		shader.release();
		shader = new ShaderProgram(DEFAULT_VERTEX_SHADER, PIXEL_SHADER);
	}

}
