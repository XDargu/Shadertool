package shadertool.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import shadertool.Editor;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.JTabbedPane;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

@SuppressWarnings("serial")
public class HelpUI extends JFrame {
	
	private JPanel contentPane;
	private JTextField textField;
	private JEditorPane editorPane;
	
	private JTree tree;
	@SuppressWarnings("rawtypes")
	private JList list;
	private JTabbedPane tabbedPane;

	/**
	 * Create the frame.
	 */
	@SuppressWarnings("rawtypes")
	public HelpUI() {
		setTitle("Manual");
		setIconImage(Toolkit.getDefaultToolkit().getImage(HelpUI.class.getResource("/shadertool/icons/IconoAyuda.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 640, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{36, 150, 50, 27, 0};
		gbl_panel.rowHeights = new int[]{22, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblBuscar = new JLabel("Buscar:");
		GridBagConstraints gbc_lblBuscar = new GridBagConstraints();
		gbc_lblBuscar.anchor = GridBagConstraints.WEST;
		gbc_lblBuscar.insets = new Insets(0, 0, 0, 5);
		gbc_lblBuscar.gridx = 0;
		gbc_lblBuscar.gridy = 0;
		panel.add(lblBuscar, gbc_lblBuscar);
		
		textField = new JTextField();
		textField.setColumns(18);	
		textField.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					buscar();
			}
			public void keyPressed(KeyEvent e) {}
		});
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.anchor = GridBagConstraints.WEST;
		gbc_textField.insets = new Insets(0, 0, 0, 5);
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		panel.add(textField, gbc_textField);
		
		Button button = new Button("Ok");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buscar();
			}
		});
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 0, 5);
		gbc_button.anchor = GridBagConstraints.NORTHWEST;
		gbc_button.gridx = 2;
		gbc_button.gridy = 0;
		panel.add(button, gbc_button);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(3);
		splitPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
		scrollPane.setBorder(null);
		
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setBorder(null);
		scrollPane.setViewportView(editorPane);
		editorPane.setContentType("text/html");		
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setLeftComponent(tabbedPane);
		
		tree = new JTree();
		tabbedPane.addTab("Contenido", null, tree, null);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				String selection = basicStringOf(e.getNewLeadSelectionPath().getLastPathComponent().toString());				
				setHelpURL(selection);
			}
		});
		tree.setModel(new DefaultTreeModel(
				new DefaultMutableTreeNode("Manual") {
					{
						DefaultMutableTreeNode node_1;
						DefaultMutableTreeNode node_2;
						node_1 = new DefaultMutableTreeNode("Interfaz gr\u00E1fica");
							node_1.add(new DefaultMutableTreeNode("Editor"));
							node_1.add(new DefaultMutableTreeNode("\u00C1rbol de nodos"));
							node_1.add(new DefaultMutableTreeNode("Barra de herramientas"));
							node_1.add(new DefaultMutableTreeNode("Inspector"));
						add(node_1);
						node_1 = new DefaultMutableTreeNode("Nodos");
							node_1.add(new DefaultMutableTreeNode("Funcionamiento b\u00E1sico"));
							node_2 = new DefaultMutableTreeNode("Tipos de nodos");
							node_1.add(node_2);
							node_1.add(new DefaultMutableTreeNode("A\u00F1adir y eliminar nodos"));
							node_1.add(new DefaultMutableTreeNode("Modificar propiedades de los nodos"));
						add(node_1);
						node_1 = new DefaultMutableTreeNode("Enlaces");
							node_1.add(new DefaultMutableTreeNode("Añadir un enlace"));
							node_1.add(new DefaultMutableTreeNode("Eliminar un enlace"));
							node_1.add(new DefaultMutableTreeNode("Asignar entradas y salidas"));
						add(node_1);
					}
				}
			));
		tree.setBorder(null);		
		
		list = new JList();
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (list.getSelectedValue() != null) {
					String category = list.getSelectedValue().toString();
					String name = basicStringOf(category);
					setHelpURL(name);
				}
			}
		});
		tabbedPane.addTab("Búsqueda", null, list, null);
		
		splitPane.setDividerLocation(170);
		
		// Mis cosas
		setHelpURL("manual");
	}
	
	private String basicStringOf(String string) {
		String selection = string;
		selection = selection.toLowerCase();
		selection = selection.replace('á', 'a');
		selection = selection.replace('é', 'e');
		selection = selection.replace('í', 'i');
		selection = selection.replace('ó', 'o');
		selection = selection.replace('ú', 'u');
		selection = selection.replace(' ', '_');
		selection = selection.replace('ñ', 'n');
		return selection;
	}
	
	private void setHelpURL(String name) {
		try {
			URL url = Editor.class.getResource("html/" + name + ".html");
			editorPane.setPage(url);			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private String getHTML(String name) {
		String text = "";
		try {
			URL url = Editor.class.getResource("html/" + name + ".html");
			FileReader fr = new FileReader(url.getFile());
			BufferedReader br = new BufferedReader(fr);
			String line;			
			while ((line = br.readLine()) != null) {
				text += line + "\n";
			}
			br.close();
			fr.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return text;
	}
	
	private String html2plainText(String html) {
		String rtr = "";
		boolean label = false;
		for (int i = 0; i < html.length(); i++) {
			char c = html.charAt(i);
			if (c == '<')
				label = true;
			if (!label)
				rtr += c;
			else
				rtr += ' ';
			if (c == '>')
				label = false;
		}
		
		return rtr;
	}
	
	private ArrayList<String> childList(JTree tree) {
		ArrayList<String> rtr = new ArrayList<String>();
		
		Object root = tree.getModel().getRoot();
		rtr = childList(root);
		
		return rtr;
	}
	
	private ArrayList<String> childList(Object node) {
		ArrayList<String> rtr = new ArrayList<String>();
		
		if (tree.getModel().isLeaf(node)) {
			rtr.add(node.toString());
			return rtr;
		}			
		
		rtr.add(node.toString());
		int count = tree.getModel().getChildCount(node);
		for (int i=0; i<count; i++) {			
			rtr.addAll(childList(tree.getModel().getChild(node, i)));
		}
		
		return rtr;
	}
	
	@SuppressWarnings("unchecked")
	private void buscar() {
		String texto = textField.getText();
		texto = texto.toLowerCase();
		if (!texto.isEmpty()) {
			// Borrar la lista
			list.setListData(new String[]{});
			ArrayList<String> findList = new ArrayList<String>();
			
			for (String category : childList(tree)) {
				String selection = basicStringOf(category);
				String html = getHTML(selection);	
				String plain = html2plainText(html);
				plain = plain.toLowerCase();
				if (plain.indexOf(texto) != -1) {
					findList.add(category);
				}
			}
			// Añadir las categorías en las que se ha encontrado el texto a buscar
			list.setListData(findList.toArray());
			if (list.getModel().getSize() > 0)
				tabbedPane.setSelectedIndex(1);
		}
	}
}

