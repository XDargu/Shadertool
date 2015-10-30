package shadertool.nodes.operation;

import java.awt.event.ActionEvent;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import javax.swing.AbstractAction;

import shadertool.nodes.IOType;
import shadertool.nodes.Input;
import shadertool.nodes.OperationNode;
import shadertool.nodes.Output;
import shadertool.nodes.UpdatedFunction;
import shadertool.ui.FormulaSelector;

public class FormulaNode extends OperationNode implements UpdatedFunction {

	private static final long serialVersionUID = 1L;

	private String formula = "color = col1;";
	private transient ShaderProgram shader;

	public FormulaNode() throws SlickException {
		super("Fórmula");
		
		inputs.add(new Input("Entrada 1", IOType.Image2f, this));
		outputs.add(new Output("Salida 1", IOType.Image2f));
	}
	
	@Override
	public void initialize() throws SlickException {
		actions.add(new AbstractAction("Editar fórmula") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new FormulaSelector(FormulaNode.this).setVisible(true);	
			}
		});
		
		updateShader(getInputSize(), getOutputSize(), formula);
	}
	
	protected void updateShader(int inputSize, int outputSize, String formula) throws SlickException {
		// VERTEX SHADER
		String vertex =
	    		"#version 120\n" +
	    		"void main() {\n";
	    
	    for (int i = 0; i < inputSize; i++) {
	    	vertex += "  gl_TexCoord[" + i + "] = gl_MultiTexCoord" + i + ";\n";	    	
	    }
	    		
	    vertex += "  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n}";
	    
	    // PIXEL SHADER
		String pixel =
				"#version 120\n" +
				"uniform float time;\n" +
				"uniform vec2 size;\n" +
				"uniform vec2 step;\n";
		String newFormula = formula;

		// Se modifican todos los parámetros simplificados (col1, col2...) a su código real en glsl
		for (int i = 0; i < inputSize; i++) {
            pixel += "uniform sampler2D tex" + (i+1) + ";\n";

			if (formula.contains("col" + (i+1))) {
				newFormula = "vec4 col" + (i+1) + " = texture2D(tex" + (i+1) + ", gl_TexCoord[" + i + "].xy);\n" + newFormula;
			}
			if (formula.contains("orig" + (i+1))) {
				newFormula = "vec4 orig" + (i+1) + " = texture2D(tex" + (i+1) + ", gl_TexCoord[0].xy);\n" + newFormula;
			}
		}
				
		// Se aplica la fórmula
	    pixel +=
	            "float rand(vec2 co){\n" +
	            "  return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);\n" +
				"}\n\n" +
	    		"void main() {\n" +
	    		"  vec2 pos = gl_FragCoord.xy;" +
	    		"  vec4 color = vec4(0.0);" +
	    		newFormula +
	    		"  gl_FragColor = color;" +
	    		"}\n";
	    
		shader = new ShaderProgram(vertex, pixel);
	}

	public String getFormula() {
		return formula;
	}
	public int getInputSize() {
		return inputs.size();
	}
	
	public void setSettings(int inputSize, int outputSize, String formula) throws SlickException {
		updateShader(inputSize, outputSize, formula);
		
		int size = inputs.size();
		if (size < inputSize) {
			for (int i = size; i < inputSize; i++) {
				inputs.add(new Input("Entrada " + (i+1), IOType.Image2f, this));
			}
		} else if (size < inputSize) {
			for (int i = inputSize; i == size; i--) {
				inputs.remove(i);
			}
		}
		
		size = outputs.size();
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

	public int getOutputSize() {
		return outputs.size();
	}
	
	@Override
	public void updated(Image img) throws SlickException {
		int inputSize = getInputSize();
		Image[] image = new Image[inputSize];
		float[] texWidth = new float[inputSize];
		float[] texHeight = new float[inputSize];
		
		for (int i = 0; i < inputSize; i++) {
			image[i] = inputs.get(i).img;
			if (image[i] == null)
				return;
			
			image[i].clampTexture();

			texWidth[i] = image[i].getTextureWidth();
			texHeight[i] = image[i].getTextureHeight();
		}
		
		int width = image[0].getWidth();
		int height = image[0].getHeight();
		
		Output imgOut = outputs.get(0);
		imgOut.create(width, height);
		Graphics g = imgOut.img.getGraphics();
		Graphics.setCurrent(g);
		
		shader.bind();
		for (int i = 0; i < inputSize; i++) {
			shader.setUniform1i("tex" + (i+1), i);
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
			inputs.get(i).img.bind();
		}
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		Texture tex = image[0].getTexture();
		shader.setUniform2f("step", tex.getWidth()/width, tex.getHeight()/height);
		shader.setUniform2f("size", width, height);
		shader.setUniform1f("time", (float) (System.currentTimeMillis() - start) / 1000f);
		
        GL11.glBegin(GL11.GL_QUADS);
        	for (int i = 0; i < inputSize; i++) {
        		GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0 + i, 0, 0);
        	}
	        GL11.glVertex3f(0, 0, 0);
	        for (int i = 0; i < inputSize; i++) {
        		GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0 + i, 0, texHeight[i]);
        	}
	        GL11.glVertex3f(0, height, 0);
	        for (int i = 0; i < inputSize; i++) {
        		GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0 + i, texWidth[i], texHeight[i]);
        	}
	        GL11.glVertex3f(width, height, 0);
	        for (int i = 0; i < inputSize; i++) {
        		GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0 + i, texWidth[i], 0);
        	}
	        GL11.glVertex3f(width, 0, 0); 
        GL11.glEnd(); 

		g.flush();
		shader.unbind();
			
		imgOut.updated();
	}
}
