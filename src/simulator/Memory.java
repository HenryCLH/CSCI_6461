package simulator;

import java.awt.Color;
import java.util.LinkedList;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Memory
{
	private JTextPane textPane;	// reference of log console on UI

	private char[] memory;		// 2048 words each is 16 bits
	private boolean expandFlag;	// flag mark if the memory has been expanded
	private LinkedList<CacheLine> cache;	// cache list

	// constructor
	Memory()
	{
		// use short value to simulate the memory to store word
		memory = new char[2048];
		expandFlag = false;
		cache = new LinkedList<CacheLine>();
	}

	// load data from memory
	public char load(int address)
	{
		if (address >= 4096 || (!expandFlag && address >= 2048))
		{
			printError("Error: Load Memory Address Out of Range: " + address);
			return 0;
		}
		else
			return memory[address];
	}

	// store data into memory
	public void store(int address, char value)
	{
		if (address >= 4096 || (!expandFlag && address >= 2048))
			printError("Error: Store Memory Address Out of Range: " + address);
		else
			memory[address] = value;
	}

	// load data from cache
	public int loadCache(int address)
	{
		// check if the address is valid
		if (address >= 4096 || (!expandFlag && address >= 2048))
		{
			printError("Error: Load Memory Address Out of Range: " + address);
			return Integer.MIN_VALUE;
		}
		else
		{
			CacheLine cacheLine;
			// check if the cache has the address
			for (int i = 0; i < cache.size(); i++)
			{
				cacheLine = cache.get(i);
				if (cacheLine.getAddress() == address)	// hit
					return cacheLine.getValue();
			}
			// not hit, load from memory, create a new cache line and add it into cache
			char value = load(address);
			cacheLine = new CacheLine(address, value);
			if (cache.size() == 16)
				cache.removeLast();
			cache.addFirst(cacheLine);
			return cacheLine.getValue();
		}
	}

	// store data into cache, also into memory synchronously
	public int storeCache(int address, char value)
	{
		// check is the address is valid
		if (address >= 4096 || (!expandFlag && address >= 2048))
		{
			printError("Error: Store Memory Address Out of Range: " + address);
			return Integer.MIN_VALUE;
		}
		else
		{
			// store into memory
			store(address, value);
			CacheLine cacheLine;
			// check if the cache has the address
			for (int i = 0; i < cache.size(); i++)
			{
				cacheLine = cache.get(i);
				if (cacheLine.getAddress() == address) // hit
				{
					cacheLine.setValue(value);
					return 0;
				}
			}
			// not hit, create a new cache line and add it into cache
			cacheLine = new CacheLine(address, value);
			if (cache.size() == 16)
				cache.removeLast();
			cache.addFirst(cacheLine);
			return 0;
		}
	}

	//expand memory size from 2048 to 4096
	public void expand()
	{
		if (expandFlag)
			printError("Error: Memory has been expanded");
		else
		{
			char[] tmp = memory;
			memory = new char[4096];
			for (int i = 0; i < 2048; i++)
			{
				memory[i] = tmp[i];
			}
			expandFlag = true;
		}
	}

	// clear the memory, reset all values to initial state
	public void clear()
	{
		memory = new char[2048];
		expandFlag = false;
		cache = new LinkedList<CacheLine>();
	}

	// set the log console reference
	public void setTextPane(JTextPane log)
	{ textPane = log; }

	// print error message of memory
	public void printError(String s)
	{
		Document doc = textPane.getDocument();
		s = "\n" + s;
		SimpleAttributeSet attrSet = null;
		attrSet = new SimpleAttributeSet();
		StyleConstants.setForeground(attrSet, Color.RED);
		try
		{
			doc.insertString(doc.getLength(), s, attrSet);
		} catch (BadLocationException e)
		{
			System.out.println("BadLocationException: " + e);
		}
		textPane.setCaretPosition(doc.getLength());
	}

	// load Test Program 1 into memory
	public void load1()
	{
		clear();
		//data
		store(29, (char) 50);
		store(30, (char) 60);
		store(53, (char) 32767);
		store(54, (char) 20);
		// instruction
		store(61, (char) 0b1000010010011101);
		store(62, (char) 0b1000010011011110);
		store(63, (char) 0b0000011110000100);
		store(64, (char) 0b0001101000000001);
		store(65, (char) 0b0000101000011110);
		store(66, (char) 0b1000010001011110);
		store(67, (char) 0b1100010000000000);
		store(68, (char) 0b0000100001011110);
		store(69, (char) 0b0011101110001110);
		store(70, (char) 0b0001111000010101);
		store(71, (char) 0b0001101100000001);
		store(72, (char) 0b0000101100011110);
		store(73, (char) 0b1000010001011110);
		store(74, (char) 0b1100010000000000);
		store(75, (char) 0b0000100010000001);
		store(76, (char) 0b0000010010000001);
		store(77, (char) 0b0001010001011110);
		store(78, (char) 0b0011110011010110);
		store(79, (char) 0b0100000010000000);
		store(80, (char) 0b0000100100011110);
		store(81, (char) 0b0000010000011110);
		store(82, (char) 0b0000100000011110);
		store(83, (char) 0b0001010010000011);
		store(84, (char) 0b0011110011011100);
		store(85, (char) 0b0000010000011110);
		store(86, (char) 0b0000100010000011);
		store(87, (char) 0b0000101110000010);
		store(88, (char) 0b0000010010000100);
		store(89, (char) 0b0001101100000001);
		store(90, (char) 0b0000101100011110);
		store(91, (char) 0b1000010001011110);
		store(92, (char) 0b0000101100011110);
		store(93, (char) 0b0001010000011110);
		store(94, (char) 0b0011110011010000);
		store(95, (char) 0b0000010010000010);
		store(96, (char) 0b0000100000011110);
		store(97, (char) 0b1000010001011110);
		store(98, (char) 0b0000010001011110);
		store(99, (char) 0b1100100000000001);
	}

	// load IPL program into memory
	public void loadROM()
	{
		clear();
		store(0, (char) 7); // PC for a Trap
		store(1, (char) 6); // PC for a machine fault
		store(6, (char) 4); // HLT for machine fault
		// Trap instruction entries
		// We use just 8 entries and all jump to same instructions
		store(7, (char) 0b0010110000001111); // JMA jump to 15
		store(8, (char) 0b0010110000001111); // JMA jump to 15
		store(9, (char) 0b0010110000001111); // JMA jump to 15
		store(10, (char) 0b0010110000001111); // JMA jump to 15
		store(11, (char) 0b0010110000001111); // JMA jump to 15
		store(12, (char) 0b0010110000001111); // JMA jump to 15
		store(13, (char) 0b0010110000001111); // JMA jump to 15
		store(14, (char) 0b0010110000001111); // JMA jump to 15
		// Trap instructions
		store(15, (char) 0b0000110000010101); // LDA 0, 0, 21
		store(16, (char) 0b0110010011000010); // SRC 0, 2, 1, 1
		store(17, (char) 0b1100100000000001); // OUT 0, 1 -- 'T'
		store(18, (char) 0b0001110000000010); // SIR 0, 2
		store(19, (char) 0b1100100000000001); // OUT 0, 1 -- 'R'
		store(20, (char) 0b0001110000010001); // SIR 0, 17
		store(21, (char) 0b1100100000000001); // OUT 0, 1 -- 'A'
		store(22, (char) 0b0001100000001111); // AIR 0, 1111
		store(23, (char) 0b1100100000000001); // OUT 0, 1 -- 'P'
		store(24, (char) 0b0000011100000010); // LDR 3, 0, 2
		store(25, (char) 0b0011010000000000); // RFS
	}
}