package shadertool;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.CanvasGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import shadertool.graph.Grafo;
import shadertool.graph.Link;
import shadertool.graph.SavableGraph;
import shadertool.nodes.InputNode;
import shadertool.nodes.Node;
import shadertool.nodes.Output;
import shadertool.nodes.OutputNode;
import shadertool.ui.HelpUI;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.util.Animator;
import javax.swing.JCheckBoxMenuItem;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;

public class Editor {
	
	private static final int FPS_DRAW_STEPS = 100;
	
	/*private static final int FRAMES_PER_MEASURE = 21;
	
	private float[] measures = new float[FRAMES_PER_MEASURE];
	private int currentMeasure = 0;*/

	private JFrame frmEditor;
	private Grafo grafo;
	private String path;

	private NodeTree tree;
	private JPanel panel;
	
	private JFrame canvasFrame;
	
	private CanvasGameContainer canvas;

	//private JButton btnDeshacer;
	//private JButton btnRehacer;

	public void setUndoEnabled(boolean enabled) {
		// TODO ************* DESHACER Y REHACER -Aun contienen varios bugs - *************
		//btnDeshacer.setEnabled(enabled);
	}

	public void setRedoEnabled(boolean enabled) {
		// TODO ************* DESHACER Y REHACER -Aun contienen varios bugs - *************
		//btnRehacer.setEnabled(enabled);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager
							.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} catch (Exception e) {
				}

				try {
					Editor window = new Editor();
					window.frmEditor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws SlickException
	 */
	public Editor() throws SlickException {
		ShaderProgram.setStrictMode(false);
		
		initialize();

		JMenuBar menuBar = new JMenuBar();
		frmEditor.setJMenuBar(menuBar);

		JMenu mnArchivo = new JMenu("Archivo");
		menuBar.add(mnArchivo);

		JMenuItem mntmNuevo = new JMenuItem("Nuevo");
		mntmNuevo.setIcon(new ImageIcon(Editor.class
				.getResource("icons/IconoNuevo.png")));
		mntmNuevo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					nuevo();
				} catch (SlickException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		mntmNuevo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK));
		mnArchivo.add(mntmNuevo);

		JMenuItem mntmAbrir = new JMenuItem("Abrir");
		mntmAbrir.setIcon(new ImageIcon(Editor.class
				.getResource("icons/IconoAbrir.png")));
		mntmAbrir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrir();
			}
		});
		mntmAbrir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_MASK));
		mnArchivo.add(mntmAbrir);

		JSeparator separator = new JSeparator();
		mnArchivo.add(separator);

		JMenuItem mntmGuardar = new JMenuItem("Guardar");
		mntmGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guardar();
			}
		});
		mntmGuardar.setIcon(new ImageIcon(Editor.class
				.getResource("icons/IconoGuardar1.png")));
		mntmGuardar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		mnArchivo.add(mntmGuardar);

		JMenuItem mntmGuardarComo = new JMenuItem("Guardar como...");
		mntmGuardarComo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guardarComo();
			}
		});
		mntmGuardarComo.setIcon(new ImageIcon(Editor.class
				.getResource("icons/IconoGuardar.png")));
		mntmGuardarComo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mnArchivo.add(mntmGuardarComo);

		JSeparator separator_1 = new JSeparator();
		mnArchivo.add(separator_1);

		JMenuItem mntmSalir = new JMenuItem("Salir");
		mntmSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmEditor.dispose();
			}
		});
		mnArchivo.add(mntmSalir);

		JMenu mnEditar = new JMenu("Editar");
		menuBar.add(mnEditar);

		// TODO ************* DESHACER Y REHACER -Aun contienen varios bugs - *************
		/*JMenuItem mntmDeshacer = new JMenuItem("Deshacer");
		mntmDeshacer.setIcon(new ImageIcon(Editor.class
				.getResource("icons/IconoDeshacer.png")));
		mntmDeshacer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				grafo.undo();
			}
		});
		mntmDeshacer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				InputEvent.CTRL_MASK));
		mnEditar.add(mntmDeshacer);

		JMenuItem mntmRehacer = new JMenuItem("Rehacer");
		mntmRehacer.setIcon(new ImageIcon(Editor.class
				.getResource("icons/IconoRehacer.png")));
		mntmRehacer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				grafo.redo();
			}
		});
		mntmRehacer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
				InputEvent.CTRL_MASK));
		mnEditar.add(mntmRehacer);

		JSeparator separator_2 = new JSeparator();
		mnEditar.add(separator_2);*/

		JMenuItem mntmAutoorganizar = new JMenuItem("Auto-organizar");
		mntmAutoorganizar.setIcon(new ImageIcon(Editor.class
				.getResource("icons/IconoOrganizar.png")));
		mntmAutoorganizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FRLayout<Node, Link> layout = new FRLayout<Node, Link>(grafo.g);
				layout.setSize(grafo.vv.getGraphLayout().getSize());
				LayoutTransition<Node, Link> lt = new LayoutTransition<Node, Link>(
						grafo.vv, grafo.vv.getGraphLayout(), layout);
				Animator animator = new Animator(lt);
				animator.start();
				grafo.vv.getRenderContext().getMultiLayerTransformer()
						.setToIdentity();
				grafo.vv.repaint();
			}
		});
		mnEditar.add(mntmAutoorganizar);

		JMenuItem mntmAjustarAPantalla = new JMenuItem("Ajustar a pantalla");
		mntmAjustarAPantalla.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FRLayout<Node, Link> layout = new FRLayout<Node, Link>(grafo.g);
				layout.setSize(panel.getSize());
				LayoutTransition<Node, Link> lt = new LayoutTransition<Node, Link>(
						grafo.vv, grafo.vv.getGraphLayout(), layout);
				Animator animator = new Animator(lt);
				animator.start();
				grafo.vv.getRenderContext().getMultiLayerTransformer()
						.setToIdentity();
				grafo.vv.repaint();
			}
		});
		mnEditar.add(mntmAjustarAPantalla);
		
		JMenu mnOpciones = new JMenu("Opciones");
		mnEditar.add(mnOpciones);
		
		final JCheckBoxMenuItem chckbxmntmAutoasignarEs = new JCheckBoxMenuItem("Auto-asignar E/S");
		chckbxmntmAutoasignarEs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Settings.autoAsignInputs = chckbxmntmAutoasignarEs.isSelected();
			}
		});
		chckbxmntmAutoasignarEs.setSelected(true);
		mnOpciones.add(chckbxmntmAutoasignarEs);
		
		final JCheckBoxMenuItem chckbxmntmAutonombrarNodos = new JCheckBoxMenuItem("Auto-nombrar nodos");
		chckbxmntmAutonombrarNodos.setSelected(true);
		chckbxmntmAutonombrarNodos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Settings.autoNameNodes = chckbxmntmAutonombrarNodos.isSelected();
			}
		});
		mnOpciones.add(chckbxmntmAutonombrarNodos);

		JMenu mnAyuda = new JMenu("Ayuda");
		menuBar.add(mnAyuda);

		JMenuItem mntmManual = new JMenuItem("Manual");
		mntmManual.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		mntmManual.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new HelpUI().setVisible(true);
			}
		});
		mntmManual.setIcon(new ImageIcon(Editor.class
				.getResource("icons/IconoAyuda.png")));
		mnAyuda.add(mntmManual);
		frmEditor.getContentPane().setLayout(new BorderLayout(0, 0));

		JToolBar toolBar = new JToolBar();
		toolBar.setToolTipText("");
		frmEditor.getContentPane().add(toolBar, BorderLayout.NORTH);

		JButton btnNuevo = new JButton("");
		btnNuevo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					nuevo();
				} catch (SlickException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnNuevo.setToolTipText("Nuevo");
		btnNuevo.setBorder(UIManager.getBorder("RadioButtonMenuItem.border"));
		btnNuevo.setIcon(new ImageIcon(Editor.class.getResource("/shadertool/icons/IconoNuevoGrande.png")));
		toolBar.add(btnNuevo);

		JButton btnAbrir = new JButton("");
		btnAbrir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrir();
			}
		});
		btnAbrir.setToolTipText("Abrir");
		btnAbrir.setBorder(UIManager.getBorder("RadioButtonMenuItem.border"));
		btnAbrir.setIcon(new ImageIcon(Editor.class.getResource("/shadertool/icons/IconoAbrirGrande.png")));
		toolBar.add(btnAbrir);

		JButton btnGuardar = new JButton("");
		btnGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guardar();
			}
		});
		btnGuardar.setToolTipText("Guardar (Ctrl+S)");
		btnGuardar.setBorder(UIManager.getBorder("CheckBoxMenuItem.border"));
		btnGuardar.setIcon(new ImageIcon(Editor.class.getResource("/shadertool/icons/IconoGuardarGrande.png")));
		toolBar.add(btnGuardar);

		JButton btnGuardarcomo = new JButton("");
		btnGuardarcomo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guardarComo();
			}
		});
		btnGuardarcomo.setToolTipText("Guardar como (Ctrl+Shift+S)");
		btnGuardarcomo
				.setBorder(UIManager.getBorder("CheckBoxMenuItem.border"));
		btnGuardarcomo.setIcon(new ImageIcon(Editor.class.getResource("/shadertool/icons/IconoGuardarComoGrande.png")));
		toolBar.add(btnGuardarcomo);

		// TODO ************* DESHACER Y REHACER -Aun contienen varios bugs - *************
		/*toolBar.addSeparator();

		btnDeshacer = new JButton("");
		btnDeshacer.setEnabled(false);
		btnDeshacer.setToolTipText("Deshacer (Ctrl+Z)");
		btnDeshacer.setBorder(UIManager.getBorder("CheckBoxMenuItem.border"));
		btnDeshacer.setIcon(new ImageIcon(Editor.class.getResource("/shadertool/icons/IconoDeshacerGrande.png")));
		btnDeshacer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				grafo.undo();
			}
		});
		toolBar.add(btnDeshacer);

		btnRehacer = new JButton("");
		btnRehacer.setEnabled(false);
		btnRehacer.setToolTipText("Rehacer (Ctrl+Y)");
		btnRehacer.setBorder(UIManager.getBorder("CheckBoxMenuItem.border"));
		btnRehacer.setIcon(new ImageIcon(Editor.class.getResource("/shadertool/icons/IconoRehacerGrande.png")));
		btnRehacer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				grafo.redo();
			}
		});
		toolBar.add(btnRehacer);

		toolBar.addSeparator();*/

		JSplitPane splitPane = new JSplitPane();
		frmEditor.getContentPane().add(splitPane, BorderLayout.CENTER);

		panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				grafo.vv.setSize(panel.getSize());
			}
		});
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		splitPane.setLeftComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		
		panel_1.add(scrollPane, BorderLayout.CENTER);
		
		JPanel panel_2 = new JPanel();
		scrollPane.setViewportView(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		tree = new NodeTree();
		panel_2.add(tree, BorderLayout.CENTER);
		
				JButton btnImportar = new JButton("Importar...");
				panel_2.add(btnImportar, BorderLayout.SOUTH);
				btnImportar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						importarNodo();
					}
				});
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int selRow = tree.getRowForLocation(e.getX(), e.getY());
					TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
					if(selRow != -1) {
						if(e.getClickCount() == 2) {
							// Doble click
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
							if (node.getUserObject() instanceof Node)
								try {
									grafo.newNode((Node)node.getUserObject());
								} catch (SlickException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
						}
					}
				}
			}
		});
		splitPane.setDividerLocation(150);

		// Cosas mias
		// ***** Chequeo de estradas y salidas *****
		Settings.autoAsignInputs = chckbxmntmAutoasignarEs.isSelected();
		Settings.autoNameNodes = chckbxmntmAutonombrarNodos.isSelected();
		Settings.version = 100;

		// Path donde se ha guardado el grafo
		path = "";
		// Crear un nuevo grafo
		nuevo();

		canvas = new CanvasGameContainer(new Inspector(), true);
		canvasFrame = new JFrame();
		canvasFrame.getContentPane().add(canvas);
		canvas.setSize(300, 300);
		canvasFrame.pack();
		canvasFrame.setTitle("Inspector");
		canvasFrame.setVisible(true);
		canvasFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		canvas.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmEditor = new JFrame();
		frmEditor.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (grafo != null) {
					if (grafo.g.isModified()) {
						int n = JOptionPane.showConfirmDialog(frmEditor,
								"¿Deseas guardar los cambios realizados?",
								"Salir", JOptionPane.YES_NO_CANCEL_OPTION);

						if (n == JOptionPane.CANCEL_OPTION)
							return;

						if (n == JOptionPane.OK_OPTION)
							if (!guardar())
								return;
					}
				}
				
				Display.destroy();
				System.exit(1);
			}
		});
		frmEditor.getContentPane().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});
		frmEditor.setTitle("Shadertool");
		frmEditor.setBounds(100, 100, 800, 600);
		frmEditor.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	private void importarNodo() {
		// Abrir un nodo serializado
		try {
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileNameExtensionFilter("Nodos", "node"));
			int returnVal = fc.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				tree.addNode(fc.getSelectedFile().getPath());
			} else {
				return;
			}
		} catch (ClassCastException e) {
			JOptionPane.showMessageDialog(null,
					"El nodo tiene un formato incorrecto: " + e.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error al importar el nodo: "
					+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void nuevo() throws SlickException {
		if (grafo != null) {
			if (grafo.g.isModified()) {
				int n = JOptionPane.showConfirmDialog(frmEditor,
						"¿Deseas guardar los cambios realizados?", "Nuevo",
						JOptionPane.YES_NO_CANCEL_OPTION);

				if (n == JOptionPane.CANCEL_OPTION)
					return;

				if (n == JOptionPane.OK_OPTION)
					if (!guardar())
						return;
			}
			grafo.unattachToJPanel(panel);
		}
		grafo = new Grafo(this);
		grafo.attachToJPanel(panel);
		grafo.vv.setSize(panel.getSize());
	}

	private boolean guardarComo() {
		String pathAux = "";

		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("Grafos", "graph"));
		int returnVal = fc.showSaveDialog(null);
		 
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			if (fc.getSelectedFile().getPath().endsWith(".graph"))
				pathAux = fc.getSelectedFile().getPath();
			else
				pathAux = fc.getSelectedFile().getPath() + ".graph";
			grafo.serializar(pathAux);
		}	      

		if (!pathAux.isEmpty())
			path = pathAux;

		return returnVal == JFileChooser.APPROVE_OPTION;
	}

	private boolean guardar() {
		boolean rtr = true;
		if (path.isEmpty())
			rtr = guardarComo();
		else
			grafo.serializar(path);
		return rtr;
	}

	private void abrir() {
		if (grafo != null) {
			if (grafo.g.isModified()) {
				int n = JOptionPane.showConfirmDialog(frmEditor,
						"¿Deseas guardar los cambios realizados?", "Abrir",
						JOptionPane.YES_NO_CANCEL_OPTION);

				if (n == JOptionPane.CANCEL_OPTION)
					return;

				if (n == JOptionPane.OK_OPTION)
					if (!guardar())
						return;
			}
		}
		
		//MyGraph<Node, Link> graph = null;
		SavableGraph sg = null;
		String pathAux = "";
		try {
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileNameExtensionFilter("Grafos", "graph"));
			fc.addChoosableFileFilter(new FileFilter() {

				@Override
				public String getDescription() {
					return ".graph";
				}

				@Override
				public boolean accept(File f) {
					if (f.isDirectory())
						return true;
					if (f.getName().toLowerCase().endsWith(".graph"))
						return true;
					return false;
				}
			});
			int returnVal = fc.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				pathAux = fc.getSelectedFile().getPath();
				FileInputStream fileIn = new FileInputStream(pathAux);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				// Leer el nodo
				sg = (SavableGraph) in.readObject();

				in.close();
				fileIn.close();

				if (!pathAux.isEmpty())
					path = pathAux;

				grafo.unattachToJPanel(panel);
				grafo = new Grafo(this, sg.getGraph());
				grafo.setStaticLayout(sg.getInitialLayout(), sg.hasCollapsed());
				Settings.setSettings(sg.settings);
				grafo.attachToJPanel(panel);
				grafo.vv.setSize(panel.getSize());
				sg.loadCollapsedNodes(grafo.vv, grafo);
			} else {
				return;
			}
		} catch (IOException i) {
			JOptionPane.showMessageDialog(null,
					"Error al abrir: " + i.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (ClassNotFoundException c) {
			JOptionPane.showMessageDialog(
					null,
					"El archivo importado no tiene el formato correcto: "
							+ c.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (ClassCastException e) {
			JOptionPane
					.showMessageDialog(
							null,
							"El archivo tiene un formato incorrecto: "
									+ e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Error al abrir: " + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	private class Inspector extends BasicGame {
		private Image toDraw = null;
		private float offX = 0;
		private float offY = 0;
		private float scale = 1.0f;
		private int step = 0;
		
		public Inspector() {
			super("Inspector");
		}

		@Override
		public void init(GameContainer gc) throws SlickException {
			gc.getInput().enableKeyRepeat();
			
			gc.setAlwaysRender(true);
			gc.setUpdateOnlyWhenVisible(false);
			gc.setVerbose(true);
			gc.setShowFPS(false);
			gc.setClearEachFrame(true);
		}

		@Override
		public void update(GameContainer gc, int delta) throws SlickException {			
			toDraw = null;
			
			PickedState<Node> pickedNodes = grafo.vv.getPickedVertexState();
			Set<Node> nodes = pickedNodes.getPicked();
			
			for (Object n : nodes) {
				if (n instanceof OutputNode) {
					OutputNode node = (OutputNode)n;
					if (node.getInputs().get(0).img != null) {
						toDraw = node.getInputs().get(0).img;
					}
				}
			}
			
			String inputName = null;
			
			if (toDraw == null) {
				PickedState<Link> picked = grafo.vv.getPickedEdgeState();
				Set<Link> links = picked.getPicked();
				
				for (Link l: links) {
					Output input = l.getInput();
					if (input != null) {
						inputName = input.name;
						toDraw = input.img;
					}
				}
			}
			
			step++;
			if (step == FPS_DRAW_STEPS) {
				step = 0;
				
				if (inputName != null) {
					canvasFrame.setTitle("Inspector - " + inputName + " - " + gc.getFPS() + "FPS");
				} else {
					canvasFrame.setTitle("Inspector - " + gc.getFPS() + "FPS");
				}
			}
		}

		@Override
		public void render(GameContainer gc, Graphics g) throws SlickException {
			
			//long start = System.nanoTime();
			for (Object n: grafo.g.getVertices()) {
				if (n instanceof InputNode) {
					InputNode node = (InputNode)n;
					node.run();
				}
			}
			/*GL11.glFinish();
			float dt = (float) (System.nanoTime() - start);
			measures[currentMeasure] = dt / 1000000000.0f;
			currentMeasure++;
			if (currentMeasure == FRAMES_PER_MEASURE) {
				java.util.Arrays.sort(measures);
				float media = 0.0f;
				for (int i=0; i < FRAMES_PER_MEASURE; i++) {
					media += measures[i];
				}
				
				media /= FRAMES_PER_MEASURE;
				
				float mediana = measures[FRAMES_PER_MEASURE/2];
				
				System.out.println("-----");
				System.out.println("Media: " + media);
				System.out.println("Mediana: " + mediana);

				currentMeasure = 0;
			}*/

			Input in = gc.getInput();
			
			if (in.isKeyDown(Input.KEY_R)) {
				offX = 0;
				offY = 0;
				scale = 1.0f;
			}
			if (in.isKeyDown(Input.KEY_UP)) {
				offY += 0.3f;
			}
			if (in.isKeyDown(Input.KEY_DOWN)) {
				offY -= 0.3f;
			}
			if (in.isKeyDown(Input.KEY_LEFT)) {
				offX += 0.3f;
			}
			if (in.isKeyDown(Input.KEY_RIGHT)) {
				offX -= 0.3f;
			}
			if (in.isKeyDown(Input.KEY_ADD)) {
				scale += 0.001f;
			}
			if (in.isKeyDown(Input.KEY_SUBTRACT)) {
				scale -= 0.001f;
			}
			
			
			if (toDraw != null) {
				//GL11.glEnable(GL11.GL_TEXTURE_2D);
				TextureImpl.bindNone();
                
				g.scale(scale, scale);
				g.drawImage(toDraw, offX, offY);
			}
			
			g.flush();
			
			/*try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
		}
	}
}