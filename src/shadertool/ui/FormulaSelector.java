package shadertool.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SpinnerNumberModel;

import shadertool.nodes.operation.FormulaNode;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JButton;

import org.newdawn.slick.SlickException;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ImageIcon;

public class FormulaSelector extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public FormulaSelector(final FormulaNode node) {
		setTitle("Editor de f\u00F3rmula");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 331);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblEntradas = new JLabel("Entradas:");
		
		JLabel lblCodigo = new JLabel("C\u00F3digo:");
		
		JScrollPane scrollPane = new JScrollPane();
		
		final JTextArea txtFormula = new JTextArea();
		txtFormula.setText(node.getFormula());
		scrollPane.setViewportView(txtFormula);
		
		final JSpinner spinnerInputs = new JSpinner();
		spinnerInputs.setModel(new SpinnerNumberModel(node.getInputSize(), 1, 4, 1));
		
		JButton btnAplicarCambios = new JButton("Aplicar cambios");
		btnAplicarCambios.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					node.setSettings(
							(Integer)spinnerInputs.getValue(),
							1,
							txtFormula.getText());
				} catch (SlickException ex) {
					JOptionPane.showMessageDialog(
							FormulaSelector.this,
							"¡El código del shader es incorrecto!\n\n" + ex.getMessage(),
							"Error compilando el shader",
							JOptionPane.ERROR_MESSAGE
							);
					return;
				}
			}
		});
		
		JButton btnAyuda = new JButton("");
		btnAyuda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new FormulaHelpUI().setVisible(true);
			}
		});
		btnAyuda.setToolTipText("Ayuda");
		btnAyuda.setIcon(new ImageIcon(FormulaSelector.class.getResource("/shadertool/icons/IconoAyuda.png")));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblEntradas)
							.addGap(10)
							.addComponent(spinnerInputs, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 252, Short.MAX_VALUE)
							.addComponent(btnAyuda, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblCodigo, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnAplicarCambios))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(3)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(3)
							.addComponent(lblEntradas))
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(spinnerInputs, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnAyuda)))
					.addGap(8)
					.addComponent(lblCodigo)
					.addGap(11)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnAplicarCambios))
		);
		contentPane.setLayout(gl_contentPane);
	}
}
