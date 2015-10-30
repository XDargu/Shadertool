package shadertool.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JSeparator;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JList;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.AbstractListModel;
import javax.swing.ListSelectionModel;

import org.newdawn.slick.SlickException;

import shadertool.Settings;
import shadertool.nodes.operation.MaskNode;
import javax.swing.JScrollPane;

public class MaskSelector extends JFrame {
	private static final long serialVersionUID = 1L;

	private JSplitPane splitPane;
	private JTable table;
	JList<String> list;
	private MaskNode Mycreator;
	
	ArrayList<Mask> masks;

	/**
	 * Create the frame.
	 */
	public MaskSelector(MaskNode creator) {
		this.Mycreator = creator;
		createMasks();
		
		setTitle("Selector de m\u00E1scara");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 386, 369);
		
		splitPane = new JSplitPane();
		splitPane.setBounds(0, 0, 370, 262);		
		setContentPane(splitPane);
		
		table = new JTable();
		splitPane.setLeftComponent(table);
		splitPane.setDividerLocation(150);
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);
		
		int fil = Mycreator.getMask().length;
		int col = Mycreator.getMask()[0].length;
		
		String[] names = new String[col];
		for (int i = 0; i<col; i++)
			names[i] = "New column";
		
		Object[][] model = new Object[fil][col];
		for (int i = 0; i<fil; i++)
			for (int j = 0; j<col; j++)
				model[i][j] = Mycreator.getMask()[i][j];
		
		table.setModel(new DefaultTableModel(
			model,
			names
		));
		
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		
		JLabel lblAlto = new JLabel("Alto:");
		
		JLabel lblAncho = new JLabel("Ancho:");
		
		NumberFormat f = NumberFormat.getIntegerInstance();		
		
		final JFormattedTextField frmtdtxtfldA = new JFormattedTextField(f);
		frmtdtxtfldA.setText("3");
		
		final JFormattedTextField formattedTextField = new JFormattedTextField(f);
		formattedTextField.setText("3");
		
		JButton btnAceptar = new JButton("Redimensionar");
		btnAceptar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int fil = Integer.parseInt(frmtdtxtfldA.getText());
				int col = Integer.parseInt(formattedTextField.getText());
				
				if ((fil <= 0) || (col <= 0)) {
					JOptionPane.showMessageDialog(splitPane,
						    "No se permiten valores nulos o negativos",
						    "Aviso",
						    JOptionPane.WARNING_MESSAGE);
					frmtdtxtfldA.setText(String.valueOf(table.getRowCount()));
					formattedTextField.setText(String.valueOf(table.getColumnCount()));
					return;
				}
				
				if ((fil % 2 == 0) || (col % 2 == 0)) {
					JOptionPane.showMessageDialog(splitPane,
						    "Las máscaras deben tener un tamaño impar",
						    "Aviso",
						    JOptionPane.WARNING_MESSAGE);
					frmtdtxtfldA.setText(String.valueOf(table.getRowCount()));
					formattedTextField.setText(String.valueOf(table.getColumnCount()));
					return;
				}
				
				Object[][] model = new Object[fil][col];
				for (int i = 0; i<fil; i++)
					for (int j = 0; j<col; j++)
						model[i][j] = 1;
				
				String[] names = new String[col];
				for (int i = 0; i<col; i++)
					names[i] = "New column";
				
				table.setModel(new DefaultTableModel(model, names));
			}
		});
		
		JButton btnAplicarMscara = new JButton("Aplicar m\u00E1scara");
		btnAplicarMscara.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int fil = table.getModel().getRowCount();
				int col = table.getModel().getColumnCount();
				
				float[][] mascara = new float[fil][col];
				try {
					for (int i = 0; i<fil; i++)
						for (int j = 0; j<col; j++)
							mascara[i][j] = Float.parseFloat(table.getModel().getValueAt(i, j).toString());
					
					//Mycreator.setIgnoreBorders(chckbxIgnorarBordes.isSelected());
					Mycreator.setMask(mascara);
					if (Settings.autoNameNodes) {
						for (Mask mask : masks) {
							if (mask.equal(mascara))
								Mycreator.setName("Máscara " + mask.name + " (" + fil + "x" + col + ")");
						}
					}
					dispose();				
					
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(splitPane,
						    "La máscara tiene valores incorrectos",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
				} catch (SlickException e) {
					JOptionPane.showMessageDialog(splitPane,
						    "Error al aplicar el shader" + e.getMessage(),
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		JLabel lblMscarasPrehechas = new JLabel("M\u00E1scaras prehechas");
		
		JSeparator separator = new JSeparator();
		
		JButton btnSeleccionarMscara = new JButton("Seleccionar");
		btnSeleccionarMscara.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Mask mask = masks.get(list.getSelectedIndex());
				
				int fil = mask.mask.length;
				int col = mask.mask[0].length;
				
				Object[][] model = new Object[fil][col];
				for (int i = 0; i<fil; i++)
					for (int j = 0; j<col; j++)
						model[i][j] = mask.mask[i][j];
				
				String[] names = new String[col];
				for (int i = 0; i<col; i++)
					names[i] = "New column";
				
				table.setModel(new DefaultTableModel(model, names));
			}
		});
		
		JSeparator separator_1 = new JSeparator();
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
						.addComponent(btnSeleccionarMscara, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
						.addComponent(separator, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(10)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(lblAlto, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
									.addGap(10)
									.addComponent(frmtdtxtfldA, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(lblAncho, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
									.addGap(10)
									.addComponent(formattedTextField, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE))
								.addComponent(btnAceptar, GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)))
						.addComponent(btnAplicarMscara, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
						.addComponent(separator_1, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
						.addComponent(lblMscarasPrehechas, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
					.addGap(18))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(3)
							.addComponent(lblAlto))
						.addComponent(frmtdtxtfldA, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(5)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(3)
							.addComponent(lblAncho))
						.addComponent(formattedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(8)
					.addComponent(btnAceptar)
					.addGap(8)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(42)
					.addComponent(lblMscarasPrehechas)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSeleccionarMscara)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnAplicarMscara)
					.addContainerGap())
		);
		
		list = new JList<String>();
		scrollPane.setViewportView(list);
		list.setVisibleRowCount(5);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;

			public int getSize() {
				return masks.size();
			}
			public String getElementAt(int index) {
				return masks.get(index).name;
			}
		});
		panel.setLayout(gl_panel);
	}
	
	private void createMasks()
	{
		masks = new ArrayList<Mask>();
		masks.add(new Mask("gaussiana", new float[][] {
			{1/16f, 1/8f, 1/16f},
			{1/8f,  1/4f, 1/8f},
			{1/16f, 1/8f, 1/16f}
		}));
		
		masks.add(new Mask("laplaciana", new float[][] {
				{-1, -1, -1},
				{-1,  8, -1},
				{-1, -1, -1}
			}));
		
		masks.add(new Mask("media", new float[][] {
				{1/9f, 1/9f, 1/9f},
				{1/9f, 1/9f, 1/9f},
				{1/9f, 1/9f, 1/9f}
			}));
		
		masks.add(new Mask("sobelGx", new float[][] {
				{1, 0, -1},
				{2, 0, -2},
				{1, 0, -1}
			}));
		
		masks.add(new Mask("sobelGy", new float[][] {
				{-1, -2, -1},
				{0, 0, 0},
				{1, 2, 1}
			}));
		
		masks.add(new Mask("previttGx", new float[][] {
				{1, 0, -1},
				{1, 0, -1},
				{1, 0, -1}
			}));
		
		masks.add(new Mask("previttGy", new float[][] {
				{-1, -1, -1},
				{0, 0, 0},
				{1, 1, 1}
			}));
		
		masks.add(new Mask("robertsGx", new float[][] {
				{0, 0, -1},
				{0, 1, 0},
				{0, 0, 0}
			}));
		
		masks.add(new Mask("robertsGy", new float[][] {
				{-1, 0, 0},
				{0, 1, 0},
				{0, 0, 0}
			}));
	}
	
	// Clase máscara
	class Mask {
		protected float[][] mask;
		protected String name;
		
		public Mask(String name, float[][] mask) {
			this.mask = mask;
			this.name = name;
		}
		
		public boolean equal(float[][] mask) {			
			if ((mask.length != this.mask.length) || (mask[0].length != this.mask[0].length))
				return false;
			
			for (int i = 0; i<this.mask.length; i++) {
				for (int j = 0; j<this.mask[0].length; j++) {
					if (this.mask[i][j] != mask[i][j])
						return false;
				}
			}					
					
			return true;
		}
	}
}
