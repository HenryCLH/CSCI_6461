package simulator;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class Computer
{
	private CPU cpu;
	private Memory memory;

	private JFrame window;

	private JTable registerTable;
	private JScrollPane registerScrollPane;

	private JTable memoryTable;
	private JScrollPane memoryScrollPane;

	private JPanel buttonPanel;
	private JButton IPL;
	private JButton runButton;
	private JButton stepButton;
	private JButton loadButton1;
	private JButton executeButton;

	private JTextField inputTextField;

	private JTextArea logTextArea;
	private JScrollPane logScrollPane;

	public static void main(String[] arge)
	{
		/*
		Runnable run = new Runnable() {
			public void run()
			{
				Computer computer = new Computer();
			}
		};
		new Thread(run).start();
		*/

		Computer computer = new Computer();
	}

	Computer()
	{
		memory = new Memory();
		cpu = new CPU(memory);
		initComponents();
		initListener();
		memory.setTextArea(logTextArea);
		cpu.setTextArea(logTextArea);
	}

	public void initComponents()
	{
		window = new JFrame("Computer");
		window.setLayout(null);
		window.setSize(800, 650);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		{
			// register
			String[] columnName = { "Register", "Binary Value" };
			String[][] data = { { "R0" }, { "R1" }, { "R2" }, { "R3" }, { "XR1" }, { "XR2" }, { "XR3" }, { "PC" }, { "IR" }, { "CC" }, { "MAR" }, { "MBR" }, { "MFR" } };
			registerTable = new JTable(new DefaultTableModel(data, columnName)
			{
				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex)
				{
					if (columnIndex == 0)
						return false;
					else
						return true;
				}
			});
			registerTable.setGridColor(Color.BLACK);
			registerTable.setRowHeight(30);
			registerTable.getColumnModel().getColumn(0).setMaxWidth(50);

			registerScrollPane = new JScrollPane(registerTable);
			registerScrollPane.setBounds(2, 30, 220, 550);
		}
		{
			// memory
			String[] columnName = { "Index", "BinaryValue" };
			String[][] data = new String[2048][2];
			for (int i = 0; i < 2048; i++)
			{
				data[i][0] = Integer.toString(i);
			}
			memoryTable = new JTable(new DefaultTableModel(data, columnName)
			{
				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex)
				{
					if (columnIndex == 0)
						return false;
					else
						return true;
				}
			});
			memoryTable.setGridColor(Color.BLACK);
			memoryTable.setRowHeight(20);
			memoryTable.getColumnModel().getColumn(0).setMaxWidth(40);

			memoryScrollPane = new JScrollPane(memoryTable);
			memoryScrollPane.setBounds(235, 30, 220, 550);
		}
		{
			// button
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridLayout(1, 5));
			buttonPanel.setBounds(2, 590, 400, 30);

			IPL = new JButton("IPL");
			runButton = new JButton("Run");
			stepButton = new JButton("Step");
			loadButton1 = new JButton("Load1");
			executeButton = new JButton("Execute");

			buttonPanel.add(IPL);
			buttonPanel.add(runButton);
			buttonPanel.add(stepButton);
			buttonPanel.add(loadButton1);
			buttonPanel.add(executeButton);

			// input field
			inputTextField = new JTextField();
			inputTextField.setBounds(410, 590, 150, 30);
			inputTextField.setDocument(new PlainDocument()
			{
				public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
				{
					if (str != null && getLength() < 16)
					{
						String s = "";
						for (int i = 0; i < str.length(); i++)
						{
							char ch = str.charAt(i);
							if (ch == '0' || ch == '1')
								s += ch;
						}
						if ((getLength() + s.length()) > 16)
							s = s.substring(0, 16 - getLength());
						super.insertString(offset, s, attr);
					}
				}
			});
		}
		{
			// log
			logTextArea = new JTextArea();
			logTextArea.setEditable(false);

			logScrollPane = new JScrollPane(logTextArea);
			logScrollPane.setBounds(465, 30, 333, 550);
		}

		Label registerLabel = new Label("Register");
		registerLabel.setBounds(3, 0, 100, 30);
		window.add(registerLabel);
		window.add(registerScrollPane);

		Label memoryLabel = new Label("Memory");
		memoryLabel.setBounds(236, 0, 100, 30);
		window.add(memoryLabel);
		window.add(memoryScrollPane);

		Label logLabel = new Label("Log");
		logLabel.setBounds(466, 0, 100, 30);
		window.add(logLabel);
		window.add(logScrollPane);

		window.add(buttonPanel);
		window.add(inputTextField);

		window.setVisible(true);
	}

	public void initListener()
	{
		registerTable.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				if (e.getKeyChar() == '\n')
				{
					int row = registerTable.getSelectedRow();
					int column = registerTable.getSelectedColumn();
					String s = (String) registerTable.getValueAt(row, column);
					String ss = "";
					int count = 0;
					for (int i = 0; i < s.length(); i++)
					{
						char ch = s.charAt(i);
						if (ch != '0' && ch != '1' && ch != ',')
							return;
						else if (ch != ',')
						{
							ss += ch;
							count++;
							if (count > 16)
								return;
						}
					}
					cpu.setRegister(row, Integer.parseInt(ss, 2));
				}
				refresh();
			}
		});
		registerTable.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{ memoryTable.clearSelection(); }
		});

		memoryTable.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				if (e.getKeyChar() == '\n')
				{
					int row = memoryTable.getSelectedRow();
					int column = memoryTable.getSelectedColumn();
					String s = (String) memoryTable.getValueAt(row, column);
					String ss = "";
					int count = 0;
					for (int i = 0; i < s.length(); i++)
					{
						char ch = s.charAt(i);
						if (ch != '0' && ch != '1' && ch != ',')
							return;
						else if (ch != ',')
						{
							ss += ch;
							count++;
							if (count > 16)
								return;
						}
					}
					memory.store(row, Integer.parseInt(ss, 2));
				}
				refresh();
			}
		});
		memoryTable.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{ registerTable.clearSelection(); }
		});

		ActionListener buttonListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				switch (e.getActionCommand())
				{
					case "IPL":
						memory.loadROM();
						cpu.clear();
						cpu.setRegister(7, 30);
						break;
					case "Run":
						cpu.run();
						break;
					case "Step":
						cpu.stepRun();
						break;
					case "Load1":

						break;
					case "Execute":
						String s = inputTextField.getText();
						inputTextField.setText("");
						int tmp = Integer.parseInt(s, 2);
						if (tmp >= 0 && tmp <= 65536)
						{
							cpu.setIR(tmp);
							cpu.runInstruction();
						}
						break;
				}
				refresh();
			}
		};
		IPL.addActionListener(buttonListener);
		runButton.addActionListener(buttonListener);
		stepButton.addActionListener(buttonListener);
		loadButton1.addActionListener(buttonListener);
		executeButton.addActionListener(buttonListener);
	}

	public void refresh()
	{
		// register
		for (int i = 0; i < registerTable.getRowCount(); i++)
		{
			int value = cpu.getRegister(i);
			String s = Integer.toBinaryString(value);
			s = "0000000000000000" + s;
			s = s.substring(s.length() - 16, s.length());
			String ss = "";
			for (int j = 0; j < s.length(); j++)
			{
				ss += s.charAt(j);
				if (j % 4 == 3 && j < 15)
					ss += ",";
			}
			registerTable.setValueAt(ss, i, 1);
		}

		// memory
		for (int i = 0; i < memoryTable.getRowCount(); i++)
		{
			int value = memory.load(i);
			String s = Integer.toBinaryString(value);
			s = "0000000000000000" + s;
			s = s.substring(s.length() - 16, s.length());
			String ss = "";
			for (int j = 0; j < s.length(); j++)
			{
				ss += s.charAt(j);
				if (j % 4 == 3 && j < 15)
					ss += ",";
			}
			memoryTable.setValueAt(ss, i, 1);
		}
	}
}
