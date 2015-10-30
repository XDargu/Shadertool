package shadertool.ui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import shadertool.nodes.input.PerlinNoiseTimeNode;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JSpinner;
import javax.swing.JButton;

import org.newdawn.slick.SlickException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PerlinNoiseTimeSelector extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public PerlinNoiseTimeSelector(final PerlinNoiseTimeNode node) {
		setTitle("Propiedades perlin");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 282, 235);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblEscalaHorizontal = new JLabel("Escala horizontal:");
		JLabel lblEscalaVertical = new JLabel("Escala vertical:");
		JLabel lblEscalaTemporal = new JLabel("Escala temporal:");
		JLabel lblOctavas = new JLabel("Octavas:");
		JLabel lblPersistencia = new JLabel("Persistencia:");
				
		final JSpinner spinnerScaleX = new JSpinner();
		spinnerScaleX.setModel(new SpinnerNumberModel(node.getScaleX(), 0.0f, null, 0.001f));	
		
		final JSpinner spinnerScaleY = new JSpinner();
		spinnerScaleY.setModel(new SpinnerNumberModel(node.getScaleY(), 0.0f, null, 0.001f));
		
		final JSpinner spinnerScaleT = new JSpinner();
		spinnerScaleT.setModel(new SpinnerNumberModel(node.getScaleT(), 0.0f, null, 0.001f));
		
		final JSpinner spinnerOctavas = new JSpinner();
		spinnerOctavas.setModel(new SpinnerNumberModel(node.getOctaves(), 1, null, 1));	
		
		final JSpinner spinnerPersistencia = new JSpinner();
		spinnerPersistencia.setModel(new SpinnerNumberModel(node.getPersistence(), 0.0f, null, 0.01f));
		
		JButton btnAceptar = new JButton("Aceptar");
		btnAceptar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					node.setProperties(
							((Float)spinnerScaleX.getValue()).floatValue(),
							((Float)spinnerScaleY.getValue()).floatValue(),
							((Float)spinnerScaleT.getValue()).floatValue(),
							(int) spinnerOctavas.getValue(),
							((Float) spinnerPersistencia.getValue()).floatValue());
					//dispose();
				} catch (SlickException e1) {
					JOptionPane.showMessageDialog(getContentPane(),
						    "Error al asignar las propiedades: " + e1.getMessage(),
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblEscalaHorizontal)
								.addComponent(lblEscalaVertical)
								.addComponent(lblEscalaTemporal)
								.addComponent(lblOctavas)
								.addComponent(lblPersistencia))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(spinnerScaleX, GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
								.addComponent(spinnerScaleY, GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
								.addComponent(spinnerScaleT, GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
								.addComponent(spinnerOctavas, GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
								.addComponent(spinnerPersistencia, GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(87)
							.addComponent(btnAceptar)))
					.addContainerGap(22, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblEscalaHorizontal)
						.addComponent(spinnerScaleX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblEscalaVertical)
						.addComponent(spinnerScaleY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblEscalaTemporal)
						.addComponent(spinnerScaleT, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblOctavas)
						.addComponent(spinnerOctavas, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPersistencia)
						.addComponent(spinnerPersistencia, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(btnAceptar)
					.addContainerGap(172, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}

}
