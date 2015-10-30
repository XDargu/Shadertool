package shadertool.nodes.input;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import shadertool.nodes.IOType;
import shadertool.nodes.InputNode;
import shadertool.nodes.Output;
import shadertool.ui.SizeSelector;

public class RandomNode extends InputNode implements SizeInterface {

	private static final long serialVersionUID = 1L;
	private static final String PIXEL_SHADER =
			"#version 120" + "\n" +
            "uniform sampler2D tex;" + "\n" +
            "uniform float time;" + "\n" +
            "float rand(vec2 co){\n" +
            "  return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);\n" +
			"}\n\n" +
            "void main() {" + "\n" +
			// Asigna un único valor aleatorio a todas las coordenadas
            "  gl_FragColor = vec4(" +
            "    rand(gl_FragCoord.xy + vec2(time)),\n" +
            "    rand(gl_FragCoord.xy + vec2(time)),\n" +
            "    rand(gl_FragCoord.xy + vec2(time)),\n" +
            "    rand(gl_FragCoord.xy + vec2(time)));" +
		    "}";
	
	private Output imgOut = new Output("Random", IOType.Image2f);
	protected transient ShaderProgram shader;
	private int width = 100;
	private int height = 100;
	
	public RandomNode() {
		this("Random");
	}
	
	public RandomNode(String name) {
		super(name);
		outputs.add(imgOut);
	}
	
	@Override
	public void run() throws SlickException {
		Output imgOut = outputs.get(0);
		imgOut.create(width, height);
		Graphics g = imgOut.img.getGraphics();
		Graphics.setCurrent(g);
		
		shader.bind();
		shader.setUniform2f("size", width, height);
		shader.setUniform1f("time", (float) (System.currentTimeMillis() - start) / 1000f);

        GL11.glBegin(GL11.GL_QUADS);
	        GL11.glVertex3f(0, 0, 0);
	        GL11.glVertex3f(0, height, 0);
	        GL11.glVertex3f(width, height, 0);
	        GL11.glVertex3f(width, 0, 0); 
        GL11.glEnd(); 

		g.flush();
		shader.unbind();
			
		imgOut.updated();
	}

	@Override
	public void initialize() throws SlickException {
		actions.add(new AbstractAction("Cambiar tamaño") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SizeSelector(RandomNode.this).setVisible(true);	
			}
		});
		
		shader = new ShaderProgram(DEFAULT_VERTEX_SHADER, PIXEL_SHADER);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
	}

}
