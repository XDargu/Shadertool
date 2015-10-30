package shadertool.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import shadertool.nodes.input.FormulaInputNode;
import javax.swing.JTextArea;
import javax.swing.JButton;

import org.newdawn.slick.SlickException;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;

public class FormulaInputSelector extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	JTextArea txtFormula;

	/**
	 * Create the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public FormulaInputSelector(final FormulaInputNode node) {
		setTitle("Editor de f\u00F3rmula");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 331);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
				
		JLabel lblCodigo = new JLabel("C\u00F3digo:");
		
		JScrollPane scrollPane = new JScrollPane();
		
		txtFormula = new JTextArea();
		txtFormula.setText(node.getFormula());
		scrollPane.setViewportView(txtFormula);
		
		JButton btnAplicarCambios = new JButton("Aplicar cambios");
		btnAplicarCambios.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					node.setSettings(
							1,
							txtFormula.getText());
				} catch (SlickException ex) {
					JOptionPane.showMessageDialog(
							FormulaInputSelector.this,
							"¡El código del shader es incorrecto!\n\n" + ex.getMessage(),
							"Error compilando el shader",
							JOptionPane.ERROR_MESSAGE
							);
					return;
				}
			}
		});
		
		JLabel lblEjemplos = new JLabel("Ejemplos:");
		
		final JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(examples));
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setExample(comboBox.getSelectedIndex());
			}
		});
		
		JButton button = new JButton("");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new FormulaHelpUI().setVisible(true);
			}
		});
		button.setIcon(new ImageIcon(FormulaInputSelector.class.getResource("/shadertool/icons/IconoAyuda.png")));
		button.setToolTipText("Ayuda");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblCodigo, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 336, Short.MAX_VALUE)
							.addComponent(button, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnAplicarCambios)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblEjemplos)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(22)
							.addComponent(lblCodigo))
						.addComponent(button, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnAplicarCambios)
						.addComponent(lblEjemplos)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
		);
		contentPane.setLayout(gl_contentPane);
	}
	
	private String[] examples = new String[]{ "Ajedrez", "Blobs", "Corazón", "Función", "Figura 3D", "Carretera" };
	
	private void setExample(int index) {
		String example = "";
		switch (index) {
		case 0:
			example = "float pixelsize = 32.0; \n" +
						"\n" +
						"vec3 color1 = vec3(0.0); \n" +
						"float  xpos = floor(pos.x/pixelsize); \n" +
						"float  ypos = floor(pos.y/pixelsize); \n" +
						"float col = mod(xpos,2.); \n" +
						"if (mod(ypos,2.)>0.) \n" +
						"	if (col>0.) \n" +
						"		col=0.; \n" +
						"	else \n" +
						"		col = 1.; \n" +	
						"color1 = vec3(col, col , col); \n" +							
						"color = vec4(color1, 1.0 );";
			break;		
		case 1:
			example = "vec2 move1; \n" +
						"move1.x = cos(time)*0.4; \n" +
						"move1.y = sin(time*1.5)*0.4; \n" +
						"vec2 move2; \n" +
						"move2.x = cos(time*2.0)*0.4; \n" +
						"move2.y = sin(time*3.0)*0.4; \n" +
						"\n" +
						"//screen coordinates \n" +
						"vec2 p = -1.0 + 2.0 * pos / size; \n" +
						"\n" +
						"//radius for each blob \n" +
						"float r1 =(dot(p-move1,p-move1))*8.0; \n" +
						"float r2 =(dot(p+move2,p+move2))*16.0; \n" +
						"\n" +
						"//sum the meatballs \n" +
						"float metaball =(1.0/r1+1.0/r2); \n" +
						"//alter the cut-off power \n" +
						"float col = pow(metaball,8.0); \n" +
						"\n" +
						"//set the output color \n" +
						"color = vec4(col,col,col,1.0);";					
			break;
		case 2:
			example = "vec2 p = (2.0*pos-size)/-size.y; \n" +
						"p.y -= 0.25; \n" +
						"// animate \n" +
						"float tt = mod(time,2.0)/2.0; \n" +
						"float ss = pow(tt,.2)*0.5 + 0.5; \n" +
						"ss -= ss*0.2*sin(tt*6.2831*5.0)*exp(-tt*6.0); \n" +
						"p *= vec2(0.5,1.5) + ss*vec2(0.5,-0.5); \n" +
						"\n" +
						"float a = atan(p.x,p.y)/3.141593; \n" +
						"float r = length(p); \n" +
						"\n" +
						"// shape \n" +
						"float h = abs(a); \n" +
						"float d = (13.0*h - 22.0*h*h + 10.0*h*h*h)/(6.0-5.0*h); \n" +
						"\n" +
						"// color \n" +
						"float f = step(r,d) * pow(1.0-r/d,0.25); \n" +
						"\n" +
						"color = vec4(f,0.0,0.0,1.0);";
			break;
		case 3:
			example = "vec2 p = (2.0*pos-size)/-size.y; \n" +
						"vec2 uPos = ( pos / size ); \n" +
						"\n" +
						"uPos.x -= 1.1; \n" +
						"uPos.y -= 0.5; \n" +
						"\n" +
						"vec3 color1 = vec3(0.0); \n" +
						"float vertColor = 0.0; \n" +
						"for( float i = 2.0; i < 4.0; ++i ) \n" +
						"{ \n" +
						"	float t = time * (1.9); \n" +
						"\n" +
						"	uPos.y += (sin( uPos.x*(exp(i+1.0)) - (t+i/2.0) )) * 0.2; \n" +
						"	float fTemp = abs(1.0 / uPos.y / 100.0); \n" +
						"	vertColor += fTemp; \n" +
						"	color1 += vec3( fTemp*(2.0-i)/10.0, fTemp*i/4.0, pow(fTemp,0.99)*1.2 ); \n" +
						"} \n" +
						"\n" +
						"vec4 color_final = vec4(color1, 1.0); \n" +
						"color = color_final;";
			break;
		case 4:
			example = "float t = 0.5; \n" +
						"lowp vec4 P=vec4(0,0,-2,0),D=gl_FragCoord/vec4(size/2.,1,1)-1.,S=vec4(cos(t),-sin(t),cos(time*.9),0)*.8,C=vec4(.5,.6,.7,9),f,T; \n" +
						"for(int r=0;r<70;r++) { \n" +
						"	T=P;T.w=dot(S,S)/(5.0+sin(time)); \n" +
						"	lowp float s=D.z=1.0,k=dot(T,T); \n" +
						"	for(int m=0;m<7;m++)k<4.?s*=4.*k,f=2.*T.x*T,f.x=2.*T.x*T.x-k,k=dot(T=f+S,T):k;s=sqrt(k/s)*log(k)/4.;P+=D/length(D)*s; \n" +
						"	if(.002>s) {  \n" +
						"		C*=log(k);break; \n" +
						"	} \n" +
						"} \n" +
						"color=C+D.y*.4;";
			break;
		case 5:
			example = "vec2 p = pos / size * 2.0 - 1.0; \n" +
						" \n" +
						"vec2 uv = vec2(p.x/abs(p.y), 1.0/p.y); \n" +
						"uv.y += time; \n" +
						" \n" +
						"vec3 col = vec3(0.45, 0.6, 1.0); \n" +
						"float rx = sin(uv.y)/2.; \n" +
						"float rw = 1.0; \n" +
						"if (p.y > 0.0) \n" +
						"{ \n" +
						"	mod(uv.y, 2.) >= 1. ? col = vec3(0., 1., 0.) : col = vec3(0., 0.9, 0.); \n" +
						"	if (uv.x >= rx-rw-0.1 && uv.x <= rx+rw+0.1) { mod(uv.y, 0.4) >= 0.2 ? col = vec3(1.) : col = vec3(0.9, 0., 0.); } \n" +
						"	if (uv.x >= rx-rw && uv.x <= rx+rw) col = vec3(0.3); \n" +
						"	if (uv.x >= rx-0.025 && uv.x <= rx+0.025 && mod(uv.y, 1.) >= 0.5) col = vec3(1.0, 0.8, 0.); \n" +
						"} \n" +
						"color = vec4(col, 1.0);";
			break;
		}
		
		txtFormula.setText(example);
	}
}
