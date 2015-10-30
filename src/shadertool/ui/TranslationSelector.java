package shadertool.ui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import shadertool.nodes.operation.TranslationNode;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

public class TranslationSelector extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public TranslationSelector(final TranslationNode node) {
		setTitle("Editar traslación");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 285, 131);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblAncho = new JLabel("Traslación X:");
		contentPane.add(lblAncho);
		
		final JSpinner spinnerX = new JSpinner();
		spinnerX.setModel(new SpinnerNumberModel(node.getXOffset(), -Integer.MAX_VALUE, Integer.MAX_VALUE, 1));
		contentPane.add(spinnerX);
		
		JLabel lblAlto = new JLabel("Traslación Y:");
		contentPane.add(lblAlto);
		
		final JSpinner spinnerY = new JSpinner();
		spinnerY.setModel(new SpinnerNumberModel(node.getYOffset(), -Integer.MAX_VALUE, Integer.MAX_VALUE, 1));
		contentPane.add(spinnerY);
		
		JButton btnAplicarCambios = new JButton("Aplicar cambios");
		btnAplicarCambios.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				node.setXOffset((int) spinnerX.getValue());
				node.setYOffset((int) spinnerY.getValue());
			}
		});
		contentPane.add(btnAplicarCambios);
	}

}
