package shadertool.ui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import shadertool.nodes.input.SizeInterface;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

public class SizeSelector extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public SizeSelector(final SizeInterface node) {
		setTitle("Editar tama\u00F1o");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 81);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblAncho = new JLabel("Ancho:");
		contentPane.add(lblAncho);
		
		final JSpinner spinnerWidth = new JSpinner();
		spinnerWidth.setModel(new SpinnerNumberModel(node.getWidth(), 1, Integer.MAX_VALUE, 1));
		contentPane.add(spinnerWidth);
		
		JLabel lblAlto = new JLabel("Alto:");
		contentPane.add(lblAlto);
		
		final JSpinner spinnerHeight = new JSpinner();
		spinnerHeight.setModel(new SpinnerNumberModel(node.getHeight(), 1, Integer.MAX_VALUE, 1));
		contentPane.add(spinnerHeight);
		
		JButton btnAplicarCambios = new JButton("Aplicar cambios");
		btnAplicarCambios.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				node.setWidth((int) spinnerWidth.getValue());
				node.setHeight((int) spinnerHeight.getValue());
			}
		});
		contentPane.add(btnAplicarCambios);
	}

}
