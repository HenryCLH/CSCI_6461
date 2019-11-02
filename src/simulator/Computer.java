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
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Computer
{
	private CPU cpu;
	private Memory memory;

	private JFrame window;

	private JTable registerTable;
	private JScrollPane registerScrollPane;
	private Label registerLabel;

	private JTable memoryTable;
	private JScrollPane memoryScrollPane;
	private JButton expandButton;
	private Label memoryLabel;
	private DefaultTableModel memoryTableModel;

	private JPanel buttonPanel;
	private JButton IPL;
	private JButton runButton;
	private JButton stepButton;
	private JButton loadButton1;
	private JButton executeButton;

	private JTextField inputTextField;

	private JTextPane logTextPane;
	private JScrollPane logScrollPane;
	private Label logLabel;

	private JTextArea cardReaderTextArea;
	private JScrollPane cardReaderScrollPane;
	private JButton cardReaderButton;
	private Label cardReaderLabel;

	private JButton keyboardButton;
	private JTextField keyboardTextField;

	// main entrance
	public static void main(String[] arge)
	{ Computer computer = new Computer(); }

	// constructor
	Computer()
	{
		memory = new Memory(); // create memory
		cpu = new CPU(memory); // create CPU
		initComponents(); // initiate all components on console
		initListener(); // initiate all listeners for components
		memory.setTextPane(logTextPane); // link memory and log console
		cpu.setTextPane(logTextPane); // link CPU and log console
	}

	// initiate components
	public void initComponents()
	{
		// main frame
		window = new JFrame("Computer");
		window.setLayout(null);
		window.setSize(800, 650);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// registers
		String[] registerColumnName = { "Register", "Binary Value" };
		String[][] registerData = { { "R0" }, { "R1" }, { "R2" }, { "R3" }, { "XR1" }, { "XR2" }, { "XR3" }, { "PC" }, { "IR" }, { "CC" }, { "MAR" }, { "MBR" }, { "MFR" } };
		registerTable = new JTable(new DefaultTableModel(registerData, registerColumnName)
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
		registerScrollPane.setBounds(2, 30, 220, 410);

		registerLabel = new Label("Register");
		registerLabel.setBounds(3, 0, 100, 30);

		// memory
		String[] memoryColumnName = { "Index", "BinaryValue" };
		String[][] memoryData = new String[2048][2];
		for (int i = 0; i < 2048; i++)
		{
			memoryData[i][0] = Integer.toString(i);
		}
		memoryTableModel = new DefaultTableModel(memoryData, memoryColumnName)
		{
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				if (columnIndex == 0)
					return false;
				else
					return true;
			}
		};
		memoryTable = new JTable(memoryTableModel);
		memoryTable.setGridColor(Color.BLACK);
		memoryTable.setRowHeight(20);
		memoryTable.getColumnModel().getColumn(0).setMaxWidth(40);

		memoryScrollPane = new JScrollPane(memoryTable);
		memoryScrollPane.setBounds(235, 30, 220, 550);
		// memory expand button
		expandButton = new JButton("Expand");
		expandButton.setBounds(380, 0, 80, 30);

		memoryLabel = new Label("Memory");
		memoryLabel.setBounds(236, 0, 140, 30);

		// buttons
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

		// input field for instructions
		inputTextField = new JTextField();
		inputTextField.setBounds(400, 590, 150, 30);
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
		// log console
		logTextPane = new JTextPane();
		logTextPane.setEditable(false);

		logScrollPane = new JScrollPane(logTextPane);
		logScrollPane.setBounds(465, 30, 333, 550);

		logLabel = new Label("Log");
		logLabel.setBounds(466, 0, 100, 30);

		// card reader
		cardReaderTextArea = new JTextArea();
		cardReaderTextArea.setDocument(new PlainDocument()
		{
			public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
			{
				if (str != null)
				{
					String s = "";
					for (int i = 0; i < str.length(); i++)
					{
						char ch = str.charAt(i);
						if (ch == '0' || ch == '1' || ch == '\n')
							s += ch;
					}
					super.insertString(offset, s, attr);
				}
			}
		});

		cardReaderScrollPane = new JScrollPane(cardReaderTextArea);
		cardReaderScrollPane.setBounds(2, 470, 220, 110);
		// card reader input button
		cardReaderButton = new JButton("Read");
		cardReaderButton.setBounds(112, 440, 90, 30);

		cardReaderLabel = new Label("Card Reader");
		cardReaderLabel.setBounds(2, 440, 110, 30);

		// keyboard button
		keyboardButton = new JButton("Keyboard");
		keyboardButton.setBounds(550, 590, 90, 30);
		// keyboard input field
		keyboardTextField = new JTextField();
		keyboardTextField.setBounds(640, 590, 150, 30);
		keyboardTextField.setDocument(new PlainDocument()
		{
			public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
			{
				if (str != null)
				{
					String s = "";
					for (int i = 0; i < str.length(); i++)
					{
						char ch = str.charAt(i);
						if (ch >= '0' && ch <= '9')
							s += ch;
					}
					super.insertString(offset, s, attr);
				}
			}
		});

		// add all components into main frame
		window.add(registerLabel);
		window.add(registerScrollPane);

		window.add(memoryLabel);
		window.add(memoryScrollPane);
		window.add(expandButton);

		window.add(logLabel);
		window.add(logScrollPane);

		window.add(buttonPanel);
		window.add(inputTextField);

		window.add(cardReaderLabel);
		window.add(cardReaderButton);
		window.add(cardReaderScrollPane);

		window.add(keyboardButton);
		window.add(keyboardTextField);

		window.setVisible(true);
	}

	// initiate listeners
	public void initListener()
	{
		// register table input listener
		registerTable.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				// use key Enter to input value
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
						{
							printLog("Set Register Value Failed! Value Has Invalid Character");
							refresh();
							return;
						}
						else if (ch != ',')
						{
							ss += ch;
							count++;
							if (count > 16)
							{
								printLog("Set Register Value Failed! Value Is Over Range");
								refresh();
								return;
							}
						}
					}
					cpu.setRegister(row, (char) Integer.parseInt(ss, 2));
				}
				refresh();
			}
		});
		registerTable.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (memoryTable.isEditing())
					memoryTable.getCellEditor().stopCellEditing();
				memoryTable.clearSelection();
				refresh();
			}
		});

		// memory table listener
		memoryTable.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				// use key Enter to input value
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
						{
							printLog("Set Memory Value Failed! Value Has Invalid Character");
							refresh();
							return;
						}
						else if (ch != ',')
						{
							ss += ch;
							count++;
							if (count > 16)
							{
								printLog("Set Memory Value Failed! Value Is Over Range");
								refresh();
								return;
							}
						}
					}
					memory.store(row, (char) Integer.parseInt(ss, 2));
					printLog("Set Memory[" + row + "] = " + ss);
				}
				refresh();
			}
		});
		memoryTable.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (registerTable.isEditing())
					registerTable.getCellEditor().stopCellEditing();
				registerTable.clearSelection();
				refresh();
			}
		});

		// keyboard text field listener
		keyboardTextField.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				// use key Enter to input value
				if (e.getKeyChar() == '\n')
					cpu.setKeyboardInput(Integer.parseInt(keyboardTextField.getText()));
			}
		});

		// button action listener
		ActionListener buttonListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				switch (e.getActionCommand())
				{
					case "IPL":
					{
						memoryTableModel.setRowCount(2048);
						expandButton.setVisible(true);
						memoryLabel.setText("Memory (2048 Words)");
						memory.loadROM();
						cpu.clear();
						logTextPane.setText("-------Start-------");
						break;
					}
					case "Run":
						cpu.run();
						break;
					case "Step":
						cpu.stepRun();
						break;
					case "Load1":
					{
						memory.load1();
						cpu.clear();
						cpu.setRegister(7, (char) 61);
						cpu.run();
						break;
					}
					case "Execute":
					{
						String s = inputTextField.getText();
						inputTextField.setText("");
						if (s != null && s.length() > 0)
						{
							int tmp = Integer.parseInt(s, 2);
							cpu.setIR((char) tmp);
							cpu.runInstruction();
						}
						break;
					}
					case "Keyboard":
						cpu.setKeyboardInput(Integer.parseInt(keyboardTextField.getText()));
						break;
					case "Read":
					{
						String s = cardReaderTextArea.getText();
						String[] ss = s.split("\n");
						int tmpFlag = 0;
						for (int i = 0; i < ss.length; i++)
						{
							if (ss[i].length() != 16 && ss[i].length() != 0)
							{
								cardReaderTextArea.setText("Read failed! Every line must be 16 bits");
								tmpFlag = 1;
								break;
							}
						}
						if (tmpFlag == 0)
							cpu.setCardReaderInput(ss);
						break;
					}
					case "Expand":
					{
						memory.expand();
						for (int i = 2048; i < 4096; i++)
						{
							Vector<String> v = new Vector<String>();
							v.add(Integer.toString(i));
							memoryTableModel.addRow(v);
						}
						expandButton.setVisible(false);
						memoryLabel.setText("Memory (4096 Words)");
						break;
					}
				}
				refresh();
			}
		};
		IPL.addActionListener(buttonListener);
		runButton.addActionListener(buttonListener);
		stepButton.addActionListener(buttonListener);
		loadButton1.addActionListener(buttonListener);
		executeButton.addActionListener(buttonListener);
		keyboardButton.addActionListener(buttonListener);
		cardReaderButton.addActionListener(buttonListener);
		expandButton.addActionListener(buttonListener);
	}

	// refresh the display value
	public void refresh()
	{
		// registers
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

	// print log
	public void printLog(String s)
	{
		Document doc = logTextPane.getDocument();
		s = "\n" + s;
		SimpleAttributeSet attrSet = null;
		if (s.contains("Failed"))
		{
			attrSet = new SimpleAttributeSet();
			StyleConstants.setForeground(attrSet, Color.RED);
		}
		try
		{
			doc.insertString(doc.getLength(), s, attrSet);
		} catch (BadLocationException e)
		{
			System.out.println("BadLocationException: " + e);
		}
		logTextPane.setCaretPosition(doc.getLength());
	}
}
