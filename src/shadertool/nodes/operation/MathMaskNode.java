package shadertool.nodes.operation;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import shadertool.Settings;
import shadertool.nodes.IOType;
import shadertool.nodes.Input;
import shadertool.nodes.OperationNode;
import shadertool.nodes.Output;
import shadertool.nodes.UpdatedFunction;
import shadertool.ui.MathMaskSelector;

public class MathMaskNode extends OperationNode implements UpdatedFunction {
	private static final long serialVersionUID = 1L;

	private int width;
	private int height;
	private boolean ignoreBorders;
	private String[] masks = new String[]{"Mínimo", "Máximo", "Media", "Mediana"};
	private int currentMask;
	
	private Output imgOut = new Output("Imagen", IOType.Image2f);
	private Input imgIn = new Input("Imagen", IOType.Image2f, this);
	
	private transient ShaderProgram shader;

	public MathMaskNode() throws SlickException {
		super("Máscara matemática");

		width = 3;
		height = 3;
		ignoreBorders = true;
		currentMask = 3;

		outputs.add(imgOut);
		inputs.add(imgIn);
	}

	public void setIgnoreBorders(boolean ignoreBorders) throws SlickException {
		this.ignoreBorders = ignoreBorders;
		updateShader();
	}

	public boolean getIgnoreBorders() {
		return this.ignoreBorders;
	}	
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		
		if (Settings.autoNameNodes)
			name = masks[currentMask] + " (" + width + "x" + height + ")";
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		
		if (Settings.autoNameNodes)
			name = masks[currentMask] + " (" + width + "x" + height + ")";
	}
	
	public void setMask(int mask) throws SlickException {
		this.currentMask = mask;
		updateShader();
		
		if (Settings.autoNameNodes)
			name = masks[currentMask] + " (" + width + "x" + height + ")";
	}

	private void updateShader() throws SlickException {
		if (shader != null)
			shader.release();
		
		String pixel =
			"#version 120" + "\n" +
			"#define TwoSort(x,y)  tmp = min (x, y); y = x + y - tmp; x = tmp; " + "\n" +
			"#define Swap(x,y)  tmp = y; y = x; x = tmp; " + "\n" +
            "uniform sampler2D tex;" + "\n" +
            "uniform vec2 step;" + "\n" +
            "void main() {" + "\n" +
            "";
		
		// Mínimo
		if (currentMask == 0) {
			pixel += "  vec4 minv = vec4(1.0);" + "\n";
			
			// Tomar el valor mínimo de alrededor
			int backStepX = width/2;
			int backStepY = height/2;
			pixel += "  for (int i = 0; i <" + width + " ; i++) { " + "\n" +
					 "    for (int j = 0; j <" + height + " ; j++) { " + "\n" +
					 "      minv = min(minv, texture2D(tex, gl_TexCoord[0].xy + step * vec2(i - " + backStepX + ", j - " + backStepY + ")));" + "\n" +
					 "    }" + "\n" +
					 "  }" + "\n" +
					 "";
			
			pixel += "  gl_FragColor = minv; \n" +
			         "}\n";
			
			System.out.println(pixel);
		}
		// Máximo
		if (currentMask == 1) {
			pixel += "  vec4 maxv = vec4(0.0);" + "\n";
			
			// Tomar el valor mínimo de alrededor
			int backStepX = width/2;
			int backStepY = height/2;
			pixel += "  for (int i = 0; i <" + width + " ; i++) { " + "\n" +
					 "    for (int j = 0; j <" + height + " ; j++) { " + "\n" +
					 "      maxv = max(maxv, texture2D(tex, gl_TexCoord[0].xy + step * vec2(i - " + backStepX + ", j - " + backStepY + ")));" + "\n" +
					 "    }" + "\n" +
					 "  }" + "\n" +
					 "";
			
			pixel += "  gl_FragColor = maxv; \n" +
			         "}\n";
		}
		// Media
		if (currentMask == 2) {
			pixel += "  vec4 color = vec4(0.0);" + "\n";
			
			int backStepX = width/2;
			int backStepY = height/2;
			float media = 1f / (width * height);
			for (int i=0; i<width; i++) {
				for (int j=0; j<height; j++) {
					pixel += "  color += texture2D(tex, gl_TexCoord[0].xy + step * vec2(" + (i-backStepX) + ", " + (j-backStepY) + ")) * " + media + ";\n"; 
				}
			}
			
			pixel += "  color.a = 1.0;\n" +
			         "  gl_FragColor = color;\n" +
			         "}\n";
		}
		// Mediana
		if (currentMask == 3) {
	        pixel += "  vec4 r[" + width * height + "];" + "\n" +
	        		 "  vec4 tmp, x, y;";
			
			// Array con los valores, ahora hay que ordenar
			int backStepX = width/2;
			int backStepY = height/2;
			for (int i=0; i<width; i++) {
				for (int j=0; j<height; j++) {
					pixel += "  r[(" + i + ") * " + width + " + " + j + "] = texture2D(tex, gl_TexCoord[0].xy + step * vec2(" + (i-backStepX) + ", " + (j-backStepY) + ")); \n";
				}
			}
			
			// Ordenar
			/*pixel += "  int i, l, j, m, k;" + "\n" +
					 "  vec4 z;" + "\n" +
					 "  l = 0; m = " + (width * height) + " - 1; k = " + ((width * height) / 2) + "; " + "\n" +
					 "  while (l < m) { " + "\n" +
					 "    x = r[k];" + "\n" +
					 "    i = l;" + "\n" +
					 "    j = m;" + "\n" +
					 "    while (true) {" + "\n" +
					 "      while ( lessThan(r[i], z) == true  ) {i++;}" + "\n" +
					 "      while ( lessThan(z, r[j]) == true ) {j--;}" + "\n" +
					 "      if (i <= j) {" + "\n" +
					 "        Swap(r[i], r[j]);" + "\n" +
					 "        i++;" + "\n" +
					 "        j--;" + "\n" +
					 "      }" + "\n" +
					 "    if ( i > j) { break; }" + "\n" +
					 "    }" + "\n" +
					 "    if (j < k) { l=i; }" + "\n" +
					 "    if (k < i) { m=j; }" + "\n" +
					 "  }" + "\n" +
					 "";*/
			pixel += "  for (int n = " + (width * height - 1) + "; n > 0; n--) { " + "\n" +
					 "    for (int i = 0; i < n; i++) {" + "\n" +
					 "      TwoSort (r[i], r[i+1]);" + "\n" +
					 "    }" + "\n" +
					 "  }" + "\n" +
					 "";
			
			// Tomar el valor central (mediana)
			/*pixel += "  gl_FragColor = r[k]; \n" +
			         "}\n";*/
			pixel += "  gl_FragColor = r[" + ((width * height) / 2) + "]; \n" +
			         "}\n";
		}

		shader = new ShaderProgram(DEFAULT_VERTEX_SHADER, pixel);
	}

	@Override
	public void initialize() throws SlickException {
		actions.add(new AbstractAction("Modificar máscara") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				new MathMaskSelector(MathMaskNode.this, masks).setVisible(true);
			}
		});
		
		updateShader();
	}

	@Override
	public void updated(Image img) throws SlickException {
		imgOut.create(img.getWidth(), img.getHeight());
		Graphics g = imgOut.img.getGraphics();
		shader.bind();
		shader.setUniform1i("tex", 0);
		Texture tex = img.getTexture();
		shader.setUniform2f("step", tex.getWidth()/img.getWidth(), tex.getHeight()/img.getHeight());
		g.drawImage(img, 0, 0);
		g.flush();
		shader.unbind();
		imgOut.updated();
	}

}