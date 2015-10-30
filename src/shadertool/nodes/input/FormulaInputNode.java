package shadertool.nodes.input;

import java.awt.event.ActionEvent;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import javax.swing.AbstractAction;

import shadertool.nodes.IOType;
import shadertool.nodes.InputNode;
import shadertool.nodes.Output;
import shadertool.ui.FormulaInputSelector;
import shadertool.ui.SizeSelector;

public class FormulaInputNode extends InputNode implements SizeInterface {

	private static final long serialVersionUID = 1L;

	private String formula = "color = vec4(pos/size, 0.0, 1.0);";
	private transient ShaderProgram shader;
	protected int width = 300;
	protected int height = 300;
	
	public FormulaInputNode() throws SlickException {
		super("Fórmula (entrada)");
		
		outputs.add(new Output("Salida 1", IOType.Image2f));
	}
	
	@Override
	public void initialize() throws SlickException {
		actions.add(new AbstractAction("Editar fórmula") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new FormulaInputSelector(FormulaInputNode.this).setVisible(true);	
			}
		});
		
		actions.add(new AbstractAction("Cambiar tamaño") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SizeSelector(FormulaInputNode.this).setVisible(true);	
			}
		});
		
		updateShader(getOutputSize(), formula);
	}
	
	private void updateShader(int outputSize, String formula) throws SlickException {
	    // PIXEL SHADER
		String pixel =
				"#version 120\n" +
				"uniform vec2 size;\n" +
				"uniform float time;\n" +
				// Devuelve un valor aleatorio basando en una semilla
	            "float rand(vec2 co){\n" +
	            "  return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);\n" +
				"}\n\n" +
	    		"void main() {\n" +
	    		"  vec2 pos = gl_FragCoord.xy;\n" +
	    		"  vec4 color = vec4(0.0);\n" +
	    		formula +
	    		"  gl_FragColor = color;" +
	    		"}\n";
	    
		shader = new ShaderProgram(DEFAULT_VERTEX_SHADER, pixel);
	}

	public String getFormula() {
		return formula;
	}
	public int getInputSize() {
		return inputs.size();
	}
	
	public void setSettings(int outputSize, String formula) throws SlickException {
		updateShader(outputSize, formula);
		
		int size = outputs.size();
		if (size < outputSize) {
			for (int i = size; i < outputSize; i++) {
				outputs.add(new Output("Salida " + (i+1), IOType.Image2f));
			}
		} else if (size < outputSize) {
			for (int i = outputSize; i == size; i--) {
				outputs.remove(i);
			}
		}
		
		this.formula = formula;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getOutputSize() {
		return outputs.size();
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
		Texture tex = imgOut.img.getTexture();
		shader.setUniform2f("step", tex.getWidth()/width, tex.getHeight()/height);
		
		GL11.glTranslatef(0, 0, 0);
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
}
