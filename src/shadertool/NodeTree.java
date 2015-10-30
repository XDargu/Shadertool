package shadertool;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.newdawn.slick.SlickException;

import shadertool.graph.SavableGraph;
import shadertool.nodes.Node;
import shadertool.nodes.conversion.HSV2RGBNode;
import shadertool.nodes.conversion.RGB2GrayNode;
import shadertool.nodes.conversion.RGB2HSVNode;
import shadertool.nodes.input.FormulaInputNode;
import shadertool.nodes.input.PerlinNoiseNode;
import shadertool.nodes.input.PerlinNoiseTimeNode;
import shadertool.nodes.input.RGBANode;
import shadertool.nodes.input.RGBNode;
import shadertool.nodes.input.RandomColorNode;
import shadertool.nodes.input.RandomNode;
import shadertool.nodes.operation.BinaryOperationNode;
import shadertool.nodes.operation.FormulaNode;
import shadertool.nodes.operation.InverseNode;
import shadertool.nodes.operation.MaskNode;
import shadertool.nodes.operation.MathMaskNode;
import shadertool.nodes.operation.PosterizationNode;
import shadertool.nodes.operation.RotationNode;
import shadertool.nodes.operation.SaltAndPepperNode;
import shadertool.nodes.operation.ScaleNode;
import shadertool.nodes.operation.TranslationNode;
import shadertool.nodes.operation.UmbralizationNode;
import shadertool.nodes.operation.ZoomNode;
import shadertool.nodes.output.InspectorNode;

public class NodeTree extends JTree {
	private static final long serialVersionUID = 1L;

	private DefaultMutableTreeNode importNode = null;

	private final TransferHandler treeHandler = new TransferHandler() {
		private static final long serialVersionUID = 1L;

		DataFlavor[] nodeFlavor = new DataFlavor[] { Node.flavor };
		DataFlavor[] graphFlavor = new DataFlavor[] { SavableGraph.flavor };

		@Override
		protected Transferable createTransferable(JComponent c) {
			JTree tree = (JTree) c;
			final DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();

			Transferable transferable = new Transferable() {

				@Override
				public DataFlavor[] getTransferDataFlavors() {
					if (node.getUserObject() instanceof Node)
						return nodeFlavor;
					else if (node.getUserObject() instanceof SavableGraph)
						return graphFlavor;
					else
						return new DataFlavor[] { DataFlavor.stringFlavor };
				}

				@Override
				public boolean isDataFlavorSupported(DataFlavor flavor) {
					return nodeFlavor[0].equals(flavor);
				}

				@Override
				public Object getTransferData(DataFlavor flavor)
						throws UnsupportedFlavorException, IOException {
					try {
						if (flavor.equals(nodeFlavor[0]))
							return (Node) node.getUserObject();
						else if (flavor.equals(graphFlavor[0])) 
							return (SavableGraph) node.getUserObject();
						else
							return null;
					} catch (ClassCastException cce) {
						throw new UnsupportedFlavorException(flavor);
					}
				}

			};
			return transferable;
		}

		public int getSourceActions(JComponent c) {
			return COPY;
		}

		@Override
		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {

			for (int i = 0; i < transferFlavors.length; i++) {
				if (transferFlavors[i].isFlavorJavaFileListType())
					return true;
			}
			return false;
		}

		@Override
		public boolean importData(TransferHandler.TransferSupport support) {
			if (support.getDataFlavors()[0].isFlavorJavaFileListType()) {
				try {
					@SuppressWarnings("unchecked")
					List<File> droppedFiles = (List<File>) support
							.getTransferable().getTransferData(
									DataFlavor.javaFileListFlavor);
					for (File file : droppedFiles) {
						if (file.getName().endsWith(".node")) {
							NodeTree.this.addNode(file.getPath());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return true;
		}
	};

	public NodeTree() throws SlickException {
		setTransferHandler(treeHandler);
		setDragEnabled(true);
		
		DefaultTreeModel model = new DefaultTreeModel(
				new DefaultMutableTreeNode("Nodos") {
					private static final long serialVersionUID = 1L;
					{
						DefaultMutableTreeNode node_1;
						
						node_1 = new DefaultMutableTreeNode("Entradas");
						node_1.add(new DefaultMutableTreeNode(new RGBANode()));
						node_1.add(new DefaultMutableTreeNode(new RGBNode()));
						node_1.add(new DefaultMutableTreeNode(new FormulaInputNode()));
						node_1.add(new DefaultMutableTreeNode(new RandomNode()));
						node_1.add(new DefaultMutableTreeNode(new RandomColorNode()));
						node_1.add(new DefaultMutableTreeNode(new PerlinNoiseNode()));
						node_1.add(new DefaultMutableTreeNode(new PerlinNoiseTimeNode()));
						add(node_1);

						node_1 = new DefaultMutableTreeNode("Operaciones");
						node_1.add(new DefaultMutableTreeNode(new BinaryOperationNode()));
						node_1.add(new DefaultMutableTreeNode(new MaskNode()));
						node_1.add(new DefaultMutableTreeNode(new MathMaskNode()));
						//node_1.add(new DefaultMutableTreeNode(new FFTNode()));
						node_1.add(new DefaultMutableTreeNode(new FormulaNode()));						
						node_1.add(new DefaultMutableTreeNode(new InverseNode()));
						node_1.add(new DefaultMutableTreeNode(new UmbralizationNode()));
						node_1.add(new DefaultMutableTreeNode(new PosterizationNode()));
						node_1.add(new DefaultMutableTreeNode(new ScaleNode()));
						node_1.add(new DefaultMutableTreeNode(new ZoomNode()));
						node_1.add(new DefaultMutableTreeNode(new RotationNode()));
						node_1.add(new DefaultMutableTreeNode(new TranslationNode()));
						node_1.add(new DefaultMutableTreeNode(new SaltAndPepperNode()));
						add(node_1);
						
						node_1 = new DefaultMutableTreeNode("Conversiones");
						node_1.add(new DefaultMutableTreeNode(new RGB2GrayNode()));
						node_1.add(new DefaultMutableTreeNode(new RGB2HSVNode()));
						node_1.add(new DefaultMutableTreeNode(new HSV2RGBNode()));
						add(node_1);
						
						node_1 = new DefaultMutableTreeNode("Salidas");
						node_1.add(new DefaultMutableTreeNode(new InspectorNode()));
						add(node_1);
					}
				});

		setModel(model);
		
		for (int i = 0; i < getRowCount(); i++) {
	         expandRow(i);
		}
	}

	protected void addNode(Node node) {
		DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

		if (importNode == null) {
			importNode = new DefaultMutableTreeNode("Importados");
			importNode.add(new DefaultMutableTreeNode(node));
			root.add(importNode);
		} else {
			importNode.add(new DefaultMutableTreeNode(node));
		}
		model.reload(root);
		this.expandPath(new TreePath(importNode.getPath()));
	}
	
	protected void addNode(SavableGraph node) {
		DefaultTreeModel model = (DefaultTreeModel) this.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

		if (importNode == null) {
			importNode = new DefaultMutableTreeNode("Importados");
			importNode.add(new DefaultMutableTreeNode(node));
			root.add(importNode);
		} else {
			importNode.add(new DefaultMutableTreeNode(node));
		}
		model.reload(root);
		this.expandPath(new TreePath(importNode.getPath()));
	}

	protected void addNode(String path) {
		try {
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			// Leer el nodo
			Object imported = in.readObject();
			if (imported instanceof Node)
				addNode((Node)imported);
			
			if (imported instanceof SavableGraph)
				addNode((SavableGraph)imported);

			in.close();
			fileIn.close();
		} catch (IOException i) {
			JOptionPane.showMessageDialog(null, "Error al importar el nodo: "
					+ i.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			return;
		} catch (ClassCastException e) {
			JOptionPane.showMessageDialog(null,
					"El nodo tiene un formato incorrecto: " + e.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		} catch (ClassNotFoundException c) {
			JOptionPane.showMessageDialog(
					null,
					"El nodo importado no tiene el formato correcto: "
							+ c.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error al importar el nodo: "
					+ e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
