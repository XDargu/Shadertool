package shadertool.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import shadertool.nodes.operation.MathMaskNode;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.newdawn.slick.SlickException;

public class MathMaskSelector extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MathMaskSelector(final MathMaskNode node, final String[] names) {
		setTitle("Selector de m\u00E1scara matem\u00E1tica");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 205, 353);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblTamaoDeLa = new JLabel("Tama\u00F1o de la m\u00E1scara");
		
		JLabel lblAncho = new JLabel("Ancho:");
		
		JLabel lblAlto = new JLabel("Alto:");
		
		final JSpinner spinner = new JSpinner();
		
		final JSpinner spinner_1 = new JSpinner();
		
		JLabel lblOperacin = new JLabel("Operaci\u00F3n:");
		
		JScrollPane scrollPane = new JScrollPane();
		
		final JList list = new JList();
		
		JButton btnAceptar = new JButton("Aceptar");
		btnAceptar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Comprobación
				if (((int)spinner.getValue() % 2 == 0) || ((int)spinner_1.getValue() % 2 == 0)) {
					JOptionPane.showMessageDialog(getContentPane(),
						    "El tamaño de la máscara debe ser impar",
						    "Aviso",
						    JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				// Asignación
				try {
					node.setWidth((int)spinner.getValue());
					node.setHeight((int)spinner_1.getValue());
					//node.setIgnoreBorders(rdbtnIgnorarBordes.isSelected());
					if (!list.isSelectionEmpty())
						node.setMask(list.getSelectedIndex());
					dispose();
				} catch (SlickException e1) {
					JOptionPane.showMessageDialog(getContentPane(),
						    "Error al asignar la máscara",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
		});	
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
						.addComponent(lblOperacin)
						.addComponent(lblTamaoDeLa, GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblAncho)
								.addComponent(lblAlto))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(spinner_1, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
								.addComponent(spinner, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnAceptar, GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblTamaoDeLa)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAncho)
						.addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblAlto)
						.addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(lblOperacin)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
					.addGap(37)
					.addComponent(btnAceptar)
					.addContainerGap())
		);
		
		list.setModel(new AbstractListModel() {
			private static final long serialVersionUID = 1L;
			public int getSize() {
				return names.length;
			}
			public Object getElementAt(int index) {
				return names[index];
			}
		});
		scrollPane.setViewportView(list);
		contentPane.setLayout(gl_contentPane);		
		
		spinner.setValue(node.getWidth());
		spinner_1.setValue(node.getHeight());
	}
}
