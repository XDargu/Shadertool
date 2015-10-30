package shadertool.ui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import shadertool.nodes.operation.RotationNode;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

public class RotationSelector extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public RotationSelector(final RotationNode node) {
		setTitle("Editar rotación");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 415, 80);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblAncho = new JLabel("Rotación (grados):");
		contentPane.add(lblAncho);
		
		final JSpinner spinnerRotation = new JSpinner();
		spinnerRotation.setModel(new SpinnerNumberModel(node.getRotation(), -180f, 180f, 0.1f));
		contentPane.add(spinnerRotation);
		
		JButton btnAplicarCambios = new JButton("Aplicar cambios");
		btnAplicarCambios.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				node.setRotation(((Double) spinnerRotation.getValue()).floatValue());
			}
		});
		contentPane.add(btnAplicarCambios);
	}

}
