package shadertool.nodes.input;

import java.awt.event.ActionEvent;
import java.util.Random;

import javax.swing.AbstractAction;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import shadertool.nodes.IOType;
import shadertool.nodes.InputNode;
import shadertool.nodes.Output;
import shadertool.ui.PerlinNoiseSelector;
import shadertool.ui.SizeSelector;

public class PerlinNoiseNode extends InputNode implements SizeInterface {
	private static final long serialVersionUID = 1L;
	
	protected transient ShaderProgram shader;
	protected int width = 300;
	protected int height = 300;
	
	protected float repX = 0.0f;
	protected float repY = 0.0f;
	protected float scaleX = 0.01f;
	protected float scaleY = 0.01f;
	protected float persistence = 0.1f;
	protected int octaves = 4;
	
	private Output imgOut = new Output("Ruido", IOType.Image2f);
	
	protected static final Random rand = new Random();

	public PerlinNoiseNode() throws SlickException {
		this("Perlin");
	}
	
	protected PerlinNoiseNode(String name) throws SlickException {
		super(name);

		outputs.add(imgOut);
	}

	public void setProperties(float scaleX, float scaleY, float repX, float repY, int octaves, float persistence) throws SlickException {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.repX = repX;
		this.repY = repY;
		this.octaves = octaves;
		this.persistence = persistence;
		updateShader();
	}
	
	public float getRepX() {
		return repX;
	}
	
	public float getRepY() {
		return repY;
	}

	protected void updateShader() throws SlickException {
		if (shader != null)
			shader.release();
		
		// Código perlin 3D
		// Tomado de https://github.com/ashima/webgl-noise/blob/master/src/classicnoise3D.glsl
		// Liberado bajo licencia MIT (ver LICENSE-MIT.txt)
		String pixel =
			"#version 120" + "\n" +
            "uniform sampler2D tex;" + "\n" +
            "vec4 mod289(vec4 x)" + "\n" +
            "{" + "\n" +
            "  return x - floor(x * (1.0 / 289.0)) * 289.0;" + "\n" +
            "}" + "\n" +
            "" + "\n" +
            "vec4 permute(vec4 x)" + "\n" +
            "{" + "\n" +
            "  return mod289(((x*34.0)+1.0)*x);" + "\n" +
            "}" + "\n" +
            "" + "\n" +
            "vec4 taylorInvSqrt(vec4 r)" + "\n" +
            "{" + "\n" +
            "  return 1.79284291400159 - 0.85373472095314 * r;" + "\n" +
            "}" + "\n" +
            "" + "\n" +
            "vec2 fade(vec2 t) {" + "\n" +
            "  return t*t*t*(t*(t*6.0-15.0)+10.0);" + "\n" +
            "}" + "\n" +
            "" + "\n";
		
		if (repX == 0.0f && repY == 0.0f) {
			pixel +=
					"float cnoise(vec2 P)" + "\n" +
				"{" + "\n" +
				"  vec4 Pi = floor(P.xyxy) + vec4(0.0, 0.0, 1.0, 1.0);" + "\n" +
				"  vec4 Pf = fract(P.xyxy) - vec4(0.0, 0.0, 1.0, 1.0);" + "\n" +
				"  Pi = mod289(Pi); // To avoid truncation effects in permutation" + "\n" +
				"  vec4 ix = Pi.xzxz;" + "\n" +
				"  vec4 iy = Pi.yyww;" + "\n" +
				"  vec4 fx = Pf.xzxz;" + "\n" +
				"  vec4 fy = Pf.yyww;" + "\n" +
				"" + "\n" +
				"  vec4 i = permute(permute(ix) + iy);" + "\n" +
				"" + "\n" +
				"  vec4 gx = fract(i * (1.0 / 41.0)) * 2.0 - 1.0 ;" + "\n" +
				"  vec4 gy = abs(gx) - 0.5 ;" + "\n" +
				"  vec4 tx = floor(gx + 0.5);" + "\n" +
				"  gx = gx - tx;" + "\n" +
				"" + "\n" +
				"  vec2 g00 = vec2(gx.x,gy.x);" + "\n" +
				"  vec2 g10 = vec2(gx.y,gy.y);" + "\n" +
				"  vec2 g01 = vec2(gx.z,gy.z);" + "\n" +
				"  vec2 g11 = vec2(gx.w,gy.w);" + "\n" +
				"" + "\n" +
				"  vec4 norm = taylorInvSqrt(vec4(dot(g00, g00), dot(g01, g01), dot(g10, g10), dot(g11, g11)));" + "\n" +
				"  g00 *= norm.x;" + "\n" +  
				"  g01 *= norm.y;" + "\n" +  
				"  g10 *= norm.z;" + "\n" +  
				"  g11 *= norm.w;" + "\n" +  
				"" + "\n" +
				"  float n00 = dot(g00, vec2(fx.x, fy.x));" + "\n" +
				"  float n10 = dot(g10, vec2(fx.y, fy.y));" + "\n" +
				"  float n01 = dot(g01, vec2(fx.z, fy.z));" + "\n" +
				"  float n11 = dot(g11, vec2(fx.w, fy.w));" + "\n" +
				"" + "\n" +
				"  vec2 fade_xy = fade(Pf.xy);" + "\n" +
				"  vec2 n_x = mix(vec2(n00, n01), vec2(n10, n11), fade_xy.x);" + "\n" +
				"  float n_xy = mix(n_x.x, n_x.y, fade_xy.y);" + "\n" +
				"  return 2.3 * n_xy;" + "\n" +
				"}" + "\n" +
				"" + "\n";
		} else {
			pixel +=
				"float pnoise(vec2 rep, vec2 P)" + "\n" +
				"{" + "\n" +
				"  vec4 Pi = floor(P.xyxy) + vec4(0.0, 0.0, 1.0, 1.0);" + "\n" +
				"  vec4 Pf = fract(P.xyxy) - vec4(0.0, 0.0, 1.0, 1.0);" + "\n" +
				"  Pi = mod(Pi, rep.xyxy); // To create noise with explicit period" + "\n" +
				"  Pi = mod289(Pi);        // To avoid truncation effects in permutation" + "\n" +
				"  vec4 ix = Pi.xzxz;" + "\n" +
				"  vec4 iy = Pi.yyww;" + "\n" +
				"  vec4 fx = Pf.xzxz;" + "\n" +
				"  vec4 fy = Pf.yyww;" + "\n" +
				"" + "\n" +
				"  vec4 i = permute(permute(ix) + iy);" + "\n" +
				"" + "\n" +
				"  vec4 gx = fract(i * (1.0 / 41.0)) * 2.0 - 1.0 ;" + "\n" +
				"  vec4 gy = abs(gx) - 0.5 ;" + "\n" +
				"  vec4 tx = floor(gx + 0.5);" + "\n" +
				"  gx = gx - tx;" + "\n" +
				"" + "\n" +
				"  vec2 g00 = vec2(gx.x,gy.x);" + "\n" +
				"  vec2 g10 = vec2(gx.y,gy.y);" + "\n" +
				"  vec2 g01 = vec2(gx.z,gy.z);" + "\n" +
				"  vec2 g11 = vec2(gx.w,gy.w);" + "\n" +
				"" + "\n" +
				"  vec4 norm = taylorInvSqrt(vec4(dot(g00, g00), dot(g01, g01), dot(g10, g10), dot(g11, g11)));" + "\n" +
				"  g00 *= norm.x;" + "\n" +
				"  g01 *= norm.y;" + "\n" +
				"  g10 *= norm.z;" + "\n" +
				"  g11 *= norm.w;" + "\n" +  
				"" + "\n" +
				"  float n00 = dot(g00, vec2(fx.x, fy.x));" + "\n" +
				"  float n10 = dot(g10, vec2(fx.y, fy.y));" + "\n" +
				"  float n01 = dot(g01, vec2(fx.z, fy.z));" + "\n" +
				"  float n11 = dot(g11, vec2(fx.w, fy.w));" + "\n" +
				"" + "\n" +
				"  vec2 fade_xy = fade(Pf.xy);" + "\n" +
				"  vec2 n_x = mix(vec2(n00, n01), vec2(n10, n11), fade_xy.x);" + "\n" +
				"  float n_xy = mix(n_x.x, n_x.y, fade_xy.y);" + "\n" +
				"  return 2.3 * n_xy;" + "\n" +
				"}\n";
		}
		
		// Código propio de llamada a cnoise/pnoise. Aplica varias octavas para obtener ruido
		pixel +=
			"float perlin2d(vec2 pos) {\n" +
			"	float total = 0;\n" +
			// Para cada octava...
		    "   for (int i = 0; i < " + octaves + "; i++) {\n" +
			// ...calculamos frecuencia y amplitud...
		    "      float freq = 2 * i;\n" +
		    "      float amp = " + persistence + " * i;\n" +
			
			"      total += ";
		// ...y sumamos al total el resultado obtenido.
		if (repX == 0.0f && repY == 0.0f) {
			pixel += "cnoise(";
		} else {
			pixel += "pnoise(vec2(" + repX + "," + repY +"), ";
		}
			
		pixel +=
			"pos * freq) * amp;\n" +
			"   }\n" +
			"    return (total / " + octaves + ") / 2 + 0.5 ;\n" +
			"}\n" +
            "void main() {" + "\n" +
			"  vec2 pos = gl_FragCoord.xy + vec2(" + rand.nextInt(10000) + ", " + rand.nextInt(10000) + "); \n" +
			"  float noise = perlin2d(pos * vec2(" + scaleX + ", " + scaleY + ")); \n" +			
			"  gl_FragColor = vec4(noise, noise, noise, 1.0); \n" +
			"}\n";

		try {
			shader = new ShaderProgram(DEFAULT_VERTEX_SHADER, pixel);
		} catch (SlickException ex) {
			ex.printStackTrace();
			System.out.println(pixel);
		}
	}

	@Override
	public void initialize() throws SlickException {
		actions.add(new AbstractAction("Modificar propiedades") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new PerlinNoiseSelector(PerlinNoiseNode.this).setVisible(true);
			}
		});
		actions.add(new AbstractAction("Cambiar tamaño") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SizeSelector(PerlinNoiseNode.this).setVisible(true);	
			}
		});
		
		updateShader();
	}

	@Override
	public void run() throws SlickException {
		
		Output imgOut = outputs.get(0);
		imgOut.create(width, height);
		Graphics g = imgOut.img.getGraphics();
		Graphics.setCurrent(g);
		
		shader.bind();
		
		// Dibujar cuadrado blanco (tamaño de textura)
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

	public float getScaleX() {
		return scaleX;
	}
	
	public float getScaleY() {
		return scaleY;
	}

	public float getPersistence() {
		return persistence;
	}

	public void setPersistence(float persistence) {
		this.persistence = persistence;
	}

	public int getOctaves() {
		return octaves;
	}

	public void setOctaves(int octaves) {
		this.octaves = octaves;
	}

}