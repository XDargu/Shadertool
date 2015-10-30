package shadertool.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JEditorPane;

import shadertool.Editor;
import javax.swing.JScrollPane;

public class FormulaHelpUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormulaHelpUI frame = new FormulaHelpUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FormulaHelpUI() {
		setTitle("Ayuda f\u00F3rmulas");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 354);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		try {
			URL url = Editor.class.getResource("html/ayuda_formulas.html");
			
			JScrollPane scrollPane = new JScrollPane();
			contentPane.add(scrollPane, BorderLayout.CENTER);
			
			JEditorPane editorPane = new JEditorPane();
			editorPane.setEditable(false);
			scrollPane.setViewportView(editorPane);
			editorPane.setPage(url);			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
