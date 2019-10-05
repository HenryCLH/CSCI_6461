package simulator;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class Computer
{
	public static void main(String[] args)
	{
		// main window frame
		JFrame window = new JFrame("Computer");
		window.setLayout(null);
		window.setSize(600, 700);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// registers choose button panel
		JPanel regBtnPanel = new JPanel();
		regBtnPanel.setLayout(new GridLayout(13, 1));
		regBtnPanel.setBounds(20, 0, 80, 600);
		// register choose buttons
		JRadioButton btnR0 = new JRadioButton("R0");
		JRadioButton btnR1 = new JRadioButton("R1");
		JRadioButton btnR2 = new JRadioButton("R2");
		JRadioButton btnR3 = new JRadioButton("R3");
		JRadioButton btnXR1 = new JRadioButton("XR1");
		JRadioButton btnXR2 = new JRadioButton("XR2");
		JRadioButton btnXR3 = new JRadioButton("XR3");
		JRadioButton btnIR = new JRadioButton("IR");
		JRadioButton btnPC = new JRadioButton("PC");
		JRadioButton btnCC = new JRadioButton("CC");
		JRadioButton btnMAR = new JRadioButton("MAR");
		JRadioButton btnMBR = new JRadioButton("MBR");
		JRadioButton btnMFR = new JRadioButton("MFR");
		// button group of the register buttons
		ButtonGroup group = new ButtonGroup();
		group.add(btnR0);
		group.add(btnR1);
		group.add(btnR2);
		group.add(btnR3);
		group.add(btnXR1);
		group.add(btnXR2);
		group.add(btnXR3);
		group.add(btnIR);
		group.add(btnPC);
		group.add(btnCC);
		group.add(btnMAR);
		group.add(btnMBR);
		group.add(btnMFR);
		// add buttons into the panel
		regBtnPanel.add(btnR0);
		regBtnPanel.add(btnR1);
		regBtnPanel.add(btnR2);
		regBtnPanel.add(btnR3);
		regBtnPanel.add(btnXR1);
		regBtnPanel.add(btnXR2);
		regBtnPanel.add(btnXR3);
		regBtnPanel.add(btnIR);
		regBtnPanel.add(btnPC);
		regBtnPanel.add(btnCC);
		regBtnPanel.add(btnMAR);
		regBtnPanel.add(btnMBR);
		regBtnPanel.add(btnMFR);

		// registers value display panel
		JPanel regLabelPanel = new JPanel();
		regLabelPanel.setLayout(new GridLayout(13, 1));
		regLabelPanel.setBounds(100, 0, 180, 600);
		// register display labels
		JLabel labelR0 = new JLabel("0000,0000,0000,0000");
		JLabel labelR1 = new JLabel("0000,0000,0000,0000");
		JLabel labelR2 = new JLabel("0000,0000,0000,0000");
		JLabel labelR3 = new JLabel("0000,0000,0000,0000");
		JLabel labelXR1 = new JLabel("0000,0000,0000,0000");
		JLabel labelXR2 = new JLabel("0000,0000,0000,0000");
		JLabel labelXR3 = new JLabel("0000,0000,0000,0000");
		JLabel labelIR = new JLabel("0000,0000,0000,0000");
		JLabel labelPC = new JLabel("0000,0000,0000,0000");
		JLabel labelCC = new JLabel("0000,0000,0000,0000");
		JLabel labelMAR = new JLabel("0000,0000,0000,0000");
		JLabel labelMBR = new JLabel("0000,0000,0000,0000");
		JLabel labelMFR = new JLabel("0000,0000,0000,0000");
		// add labels into the panel
		regLabelPanel.add(labelR0);
		regLabelPanel.add(labelR1);
		regLabelPanel.add(labelR2);
		regLabelPanel.add(labelR3);
		regLabelPanel.add(labelXR1);
		regLabelPanel.add(labelXR2);
		regLabelPanel.add(labelXR3);
		regLabelPanel.add(labelIR);
		regLabelPanel.add(labelPC);
		regLabelPanel.add(labelCC);
		regLabelPanel.add(labelMAR);
		regLabelPanel.add(labelMBR);
		regLabelPanel.add(labelMFR);

		// memory data display table
		String[][] tmpString = new String[4096][2];
		for (int i = 0; i < 4096; i++)
		{
			tmpString[i][0] = Integer.toString(i);
		}
		String[] tmpIndex = new String[] { "Index", "Value" };
		JTable memoryTable = new JTable(new DefaultTableModel(tmpString, tmpIndex))
		{
			@Override // set the table cannot be edited direct
			public boolean isCellEditable(int row, int column)
			{ return false; }
		};
		memoryTable.setGridColor(Color.BLACK);
		memoryTable.setRowHeight(18);
		memoryTable.getColumnModel().getColumn(0).setMaxWidth(50);
		// put the table in a scroll pane
		JScrollPane scrollPane = new JScrollPane(memoryTable);
		scrollPane.setBounds(300, 0, 300, 600);

		// input box
		JTextField inputText = new JTextField();
		inputText.setBounds(0, 600, 600, 35);
		// limit the input to be binary string
		inputText.setDocument(new PlainDocument()
		{
			@Override
			public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
			{
				if (str == null)
					return;
				String s = "";
				for (int i = 0; i < str.length(); i++)
				{
					char ch = str.charAt(i);
					if (ch == '0' || ch == '1')
						s += ch;
				}
				if ((getLength() + s.length()) <= 16)
					super.insertString(offset, s, attr);
			}
		});

		// control buttons panel
		JPanel ctrBtnPanel = new JPanel();
		ctrBtnPanel.setLayout(new GridLayout(1, 5));
		ctrBtnPanel.setBounds(0, 635, 600, 35);
		// control buttons
		JButton btnInput = new JButton("Input");
		JButton btnRun = new JButton("Run");
		JButton btnStep = new JButton("Step");
		JButton btnHalt = new JButton("Halt");
		JButton btnIPL = new JButton("IPL");
		// add buttons into the panel
		ctrBtnPanel.add(btnInput);
		ctrBtnPanel.add(btnRun);
		ctrBtnPanel.add(btnStep);
		ctrBtnPanel.add(btnHalt);
		ctrBtnPanel.add(btnIPL);

		// add all the components into window
		window.add(regBtnPanel);
		window.add(regLabelPanel);
		window.add(scrollPane);
		window.add(inputText);
		window.add(ctrBtnPanel);

		window.setVisible(true);

		// memory and CPU objects
		Memory memory = new Memory();
		CPU cpu = new CPU(memory, regLabelPanel, memoryTable);

		// listener and actions for choose registers
		ActionListener regBtnListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				memoryTable.clearSelection();
				switch (e.getActionCommand())
				{
					case "R0":
						cpu.setChoose(0);
						break;
					case "R1":
						cpu.setChoose(1);
						break;
					case "R2":
						cpu.setChoose(2);
						break;
					case "R3":
						cpu.setChoose(3);
						break;
					case "XR1":
						cpu.setChoose(11);
						break;
					case "XR2":
						cpu.setChoose(12);
						break;
					case "XR3":
						cpu.setChoose(13);
						break;
					case "PC":
						cpu.setChoose(101);
						break;
					case "IR":
						cpu.setChoose(102);
						break;
					case "CC":
						cpu.setChoose(103);
						break;
					case "MAR":
						cpu.setChoose(111);
						break;
					case "MBR":
						cpu.setChoose(112);
						break;
					case "MFR":
						cpu.setChoose(113);
						break;
				}
			}
		};
		btnR0.addActionListener(regBtnListener);
		btnR1.addActionListener(regBtnListener);
		btnR2.addActionListener(regBtnListener);
		btnR3.addActionListener(regBtnListener);
		btnXR1.addActionListener(regBtnListener);
		btnXR2.addActionListener(regBtnListener);
		btnXR3.addActionListener(regBtnListener);
		btnIR.addActionListener(regBtnListener);
		btnPC.addActionListener(regBtnListener);
		btnCC.addActionListener(regBtnListener);
		btnMAR.addActionListener(regBtnListener);
		btnMBR.addActionListener(regBtnListener);
		btnMFR.addActionListener(regBtnListener);

		// listener for chose memory data
		MouseListener memoryTableListener = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				group.clearSelection();
				int row = memoryTable.getSelectedRow();
				cpu.setChoose(1000);
				cpu.setMemoryChoose(row);
			}
		};
		memoryTable.addMouseListener(memoryTableListener);

		// buttons listener and actions
		ActionListener ctrBtnListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				switch (e.getActionCommand())
				{
					case "Input":
						cpu.input(inputText.getText());
						break;
					case "Run":
						cpu.play();
						break;
					case "Step":
						cpu.step();
						break;
					case "Halt":
						cpu.halt();
						break;
					case "IPL":
						cpu.loadTest();
						cpu.play();
						break;
				}
			}
		};
		btnInput.addActionListener(ctrBtnListener);
		btnRun.addActionListener(ctrBtnListener);
		btnStep.addActionListener(ctrBtnListener);
		btnHalt.addActionListener(ctrBtnListener);
		btnIPL.addActionListener(ctrBtnListener);

		cpu.start();
	}
}
