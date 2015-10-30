package shadertool.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import shadertool.nodes.operation.BinaryOperationNode;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JButton;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class BinaryOperationSelector extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	
	private JRadioButton[] options;

	/**
	 * Create the frame.
	 */
	public BinaryOperationSelector(final BinaryOperationNode parent, String[] operations) {
		setTitle("Selecci\u00F3n de operaci\u00F3n");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 271, 380);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1));
		
		JLabel lblSeleccionaUnaOperacin = new JLabel("Selecciona una operaci\u00F3n:");
		contentPane.add(lblSeleccionaUnaOperacin);
		
		options = new JRadioButton[operations.length];
		
		ButtonGroup group = new ButtonGroup();
		int counter = 0;
		for (String name : operations) {
			JRadioButton rdbtn = new JRadioButton(name);
			contentPane.add(rdbtn);
			group.add(rdbtn);
			options[counter] = rdbtn;
			counter++;
		}
		
		group.setSelected(options[parent.getCurrentOperation()].getModel(), true);				
		
		JButton btnAceptar = new JButton("Aceptar");
		btnAceptar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int counter = 0;
				for (JRadioButton button : options) {
					if (button.isSelected())
						parent.setCurrentOperation(counter);
					counter++;
				}
				dispose();
			}
		});
		contentPane.add(btnAceptar);
		pack();
	}

}
