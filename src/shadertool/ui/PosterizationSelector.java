package shadertool.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JSpinner;

import shadertool.nodes.operation.PosterizationNode;
import javax.swing.JButton;

import org.newdawn.slick.SlickException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PosterizationSelector extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public PosterizationSelector(final PosterizationNode node) {
		setTitle("Seleccionar n\u00FAmero de umbrales");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 334, 77);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblUmbrales = new JLabel("Umbrales:");
		lblUmbrales.setBounds(10, 11, 59, 14);
		contentPane.add(lblUmbrales);
		
		final JSpinner spinner = new JSpinner();
		spinner.setBounds(67, 8, 143, 20);
		spinner.setModel(new SpinnerNumberModel(node.getUmbrales(), 1, null, 1));
		contentPane.add(spinner);
		
		JButton btnAceptar = new JButton("Aceptar");
		btnAceptar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					node.setUmbral((int) spinner.getValue());
				} catch (SlickException e) {
					e.printStackTrace();
				}
			}
		});
		btnAceptar.setBounds(219, 7, 89, 23);
		contentPane.add(btnAceptar);
	}
}
