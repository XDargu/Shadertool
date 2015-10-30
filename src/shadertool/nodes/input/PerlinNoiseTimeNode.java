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
import shadertool.ui.PerlinNoiseTimeSelector;
import shadertool.ui.SizeSelector;

public class PerlinNoiseTimeNode extends InputNode implements SizeInterface {

	private static final long serialVersionUID = 1L;
	
	protected transient ShaderProgram shader;
	protected int width = 300;
	protected int height = 300;
	
	protected float scaleX = 0.01f;
	protected float scaleY = 0.01f;
	protected float scaleT = 1.0f;
	protected float persistence = 0.1f;
	protected int octaves = 4;
	
	protected static final Random rand = new Random();
	
	private Output imgOut = new Output("Ruido", IOType.Image2f);
	
	public PerlinNoiseTimeNode() throws SlickException {
		super("Perlin (tiempo)");
		
		outputs.add(imgOut);
	}

	protected void updateShader() throws SlickException {
		if (shader != null)
			shader.release();
		
		// Código perlin 4D (3D + tiempo)
		// Tomado de https://github.com/ashima/webgl-noise/blob/master/src/classicnoise4D.glsl
		// Liberado bajo licencia MIT (ver LICENSE-MIT.txt)
		String pixel =
			"#version 120" + "\n" +
            "uniform sampler2D tex;" + "\n" +
            "uniform float time;" + "\n" +
            "vec3 mod289(vec3 x) {\n" +
            "    return x - floor(x * (1.0 / 289.0)) * 289.0;\n" +
            "}\n" +

            "vec4 mod289(vec4 x) {\n" +
            "    return x - floor(x * (1.0 / 289.0)) * 289.0;\n" +
            "}\n" +
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
            "float snoise(vec3 v)\n" +
            "{ \n" +
            "const vec2  C = vec2(1.0/6.0, 1.0/3.0) ;\n" +
            "const vec4  D = vec4(0.0, 0.5, 1.0, 2.0);\n" +

            // First corner
            "vec3 i  = floor(v + dot(v, C.yyy) );\n" +
            "vec3 x0 =   v - i + dot(i, C.xxx) ;\n" +

            // Other corners
            "vec3 g = step(x0.yzx, x0.xyz);\n" +
            "vec3 l = 1.0 - g;\n" +
            "vec3 i1 = min( g.xyz, l.zxy );\n" +
            "vec3 i2 = max( g.xyz, l.zxy );\n" +
            
            "vec3 x1 = x0 - i1 + C.xxx;\n" +
            "vec3 x2 = x0 - i2 + C.yyy; // 2.0*C.x = 1/3 = C.y\n" +
            "vec3 x3 = x0 - D.yyy;      // -1.0+3.0*C.x = -0.5 = -D.y\n" +

            // Permutations
            "i = mod289(i); \n" +
            "vec4 p = permute( permute( permute( \n" +
            "           i.z + vec4(0.0, i1.z, i2.z, 1.0 ))\n" +
            "         + i.y + vec4(0.0, i1.y, i2.y, 1.0 )) \n" +
            "         + i.x + vec4(0.0, i1.x, i2.x, 1.0 ));\n" +

            // Gradients: 7x7 points over a square, mapped onto an octahedron.
            // The ring size 17*17 = 289 is close to a multiple of 49 (49*6 = 294)
            "float n_ = 0.142857142857; // 1.0/7.0\n" +
            "vec3  ns = n_ * D.wyz - D.xzx;\n" +

            "vec4 j = p - 49.0 * floor(p * ns.z * ns.z);  //  mod(p,7*7)\n" +

            "vec4 x_ = floor(j * ns.z);\n" +
            "vec4 y_ = floor(j - 7.0 * x_ );    // mod(j,N)\n" +

            "vec4 x = x_ *ns.x + ns.yyyy;\n" +
            "vec4 y = y_ *ns.x + ns.yyyy;\n" +
            "vec4 h = 1.0 - abs(x) - abs(y);\n" +

            "vec4 b0 = vec4( x.xy, y.xy );\n" +
            "vec4 b1 = vec4( x.zw, y.zw );\n" +
            "vec4 s0 = floor(b0)*2.0 + 1.0;\n" +
            "vec4 s1 = floor(b1)*2.0 + 1.0;\n" +
            "vec4 sh = -step(h, vec4(0.0));\n" +

            "vec4 a0 = b0.xzyw + s0.xzyw*sh.xxyy ;\n" +
            "vec4 a1 = b1.xzyw + s1.xzyw*sh.zzww ;\n" +

            "vec3 p0 = vec3(a0.xy,h.x);\n" +
            "vec3 p1 = vec3(a0.zw,h.y);\n" +
            "vec3 p2 = vec3(a1.xy,h.z);\n" +
            "vec3 p3 = vec3(a1.zw,h.w);\n" +

            //Normalise gradients
            "vec4 norm = taylorInvSqrt(vec4(dot(p0,p0), dot(p1,p1), dot(p2, p2), dot(p3,p3)));\n" +
            "p0 *= norm.x;\n" +
            "p1 *= norm.y;\n" +
            "p2 *= norm.z;\n" +
            "p3 *= norm.w;\n" +

            // Mix final noise value
            "vec4 m = max(0.6 - vec4(dot(x0,x0), dot(x1,x1), dot(x2,x2), dot(x3,x3)), 0.0);\n" +
            "m = m * m;\n" +
            "return 42.0 * dot( m*m, vec4( dot(p0,x0), dot(p1,x1), \n" +
            "                              dot(p2,x2), dot(p3,x3) ) );\n" +
            "}\n" +
			
			// Código de llamada a snoise. Aplica varias octavas para obtener ruido
			"float perlin3d(vec3 pos) {\n" +
			"	float total = 0;\n" +
			// Para cada octava...
		    "   for (int i = 0; i < " + octaves + "; i++) {\n" +
			// ...calculamos frecuencia y amplitud...
		    "      float freq = 2 * i;\n" +
		    "      float amp = " + persistence + " * i;\n" +
			// ...y sumamos al total el resultado obtenido.
			"      total += snoise(pos * freq) * amp;\n" +
			"   }\n" +
			"    return (total / " + octaves + ") / 2 + 0.5 ;\n" +
			"}\n" +
            "void main() {" + "\n" +
			"  vec2 pos = gl_FragCoord.xy + vec2(" + rand.nextInt(10000) + ", " + rand.nextInt(10000) + "); \n" +
			"  float noise = perlin3d(vec3(pos, time) * vec3(" + scaleX + ", " + scaleY + ", " + scaleT + ")); \n" +			
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
				new PerlinNoiseTimeSelector(PerlinNoiseTimeNode.this).setVisible(true);
			}
		});
		actions.add(new AbstractAction("Cambiar tamaño") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SizeSelector(PerlinNoiseTimeNode.this).setVisible(true);	
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
		shader.setUniform1f("time", (float) (System.currentTimeMillis() - start) / 1000f);
		
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
	
	public void setProperties(float scaleX, float scaleY, float scaleT, int octaves, float persistence) throws SlickException {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleT = scaleT;
		this.octaves = octaves;
		this.persistence = persistence;
		updateShader();
	}
	
	public float getScaleX() {
		return scaleX;
	}
	
	public float getScaleY() {
		return scaleY;
	}
	
	public float getScaleT() {
		return scaleT;
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
