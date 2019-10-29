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
	private JTextPane logTextPane;	// reference of log console on UI

	private int[] mem;		// 2048 words each is 16 bits
	private int[] expMem;	// an expend 2048 words
	private boolean expFlag;	// flag mark whether the memory has expanded

	private LinkedList<CacheLine> cache;	// cache list

	// constructor
	Memory()
	{
		// use integer to simulate the memory to store word
		mem = new int[2048];
		expMem = new int[2048];
		expFlag = false;

		cache = new LinkedList<CacheLine>();
	}

	// load data from memory
	public int load(int address)
	{
		if (address < 2048)
			return mem[address];
		else if (expFlag)
			return expMem[address - 2048];
		else
			return Integer.MAX_VALUE;
	}

	// store data into memory
	public int store(int address, int value)
	{
		if (address < 2048)
		{
			mem[address] = value;
			return 0;
		}
		else if (expFlag)
		{
			expMem[address - 2048] = value;
			return 1;
		}
		else
			return -1;
	}

	// load data from cache
	public int loadCache(int address)
	{
		// load from memory
		int re = load(address);
		if (re == Integer.MAX_VALUE)
		{
			printLog("Load Failed! Invalid Address");
			return re;
		}
		else
		{
			CacheLine cacheLine;
			// check if the cache has the address
			for (int i = 0; i < cache.size(); i++)
			{
				cacheLine = cache.get(i);
				if (cacheLine.getAddress() == address)	// hit
				{
					//printLog("Cache: Load Hit");
					return cacheLine.getValue();
				}
			}
			// not hit, create a new cache line and add it into cache
			cacheLine = new CacheLine(address, re);
			if (cache.size() == 16)
				cache.removeLast();
			cache.addFirst(cacheLine);
			//printLog("Cache: Load Miss");
			return cacheLine.getValue();
		}
	}

	// store data into cache, also into memory synchronously
	public void storeCache(int address, int value)
	{
		// store into memory
		int re = store(address, value);
		if (re == -1)
		{
			printLog("Store Failed! Invalid Address");
		}
		else
		{
			CacheLine cacheLine;
			// check if the cache has the address
			for (int i = 0; i < cache.size(); i++)
			{
				cacheLine = cache.get(i);
				if (cacheLine.getAddress() == address) // hit
				{
					cacheLine.setValue(value);
					//printLog("Cache: Store Hit");
					return;
				}
			}
			// not hit, create a new cache line and add it into cache
			cacheLine = new CacheLine(address, value);
			if (cache.size() == 16)
				cache.removeLast();
			cache.addFirst(cacheLine);
			//printLog("Cache: Store Miss");
		}
	}

	// print log of memory
	public void printLog(String s)
	{
		Document doc = logTextPane.getDocument();
		s = "\n" + s;
		SimpleAttributeSet attrSet = null;
		if (s.contains("Invalid"))
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
	}

	// set the log console reference
	public void setTextPane(JTextPane log)
	{ logTextPane = log; }

	// clear the memory, reset all value to 0
	public void clear()
	{
		mem = new int[2048];
		expMem = new int[2048];
		expFlag = false;

		cache = new LinkedList<CacheLine>();
	}

	// load the memory value of Program 1 when push load1 button
	public void load1()
	{
		clear();
		//data
		store(29, 50);
		store(30, 60);
		store(53, Short.MAX_VALUE);
		store(54, 20);
		// instruction
		store(61, 0b1000010010011101);
		store(62, 0b1000010011011110);
		store(63, 0b0000011110000100);
		store(64, 0b0001101000000001);
		store(65, 0b0000101000011110);
		store(66, 0b1000010001011110);
		store(67, 0b1100010000000000);
		store(68, 0b0000100001011110);
		store(69, 0b0011101110001110);
		store(70, 0b0001111000010101);
		store(71, 0b0001101100000001);
		store(72, 0b0000101100011110);
		store(73, 0b1000010001011110);
		store(74, 0b1100010000000000);
		store(75, 0b0000100010000001);
		store(76, 0b0000010010000001);
		store(77, 0b0001010001011110);
		store(78, 0b0011110011010110);
		store(79, 0b0100000010000000);
		store(80, 0b0000100100011110);
		store(81, 0b0000010000011110);
		store(82, 0b0000100000011110);
		store(83, 0b0001010010000011);
		store(84, 0b0011110011011100);
		store(85, 0b0000010000011110);
		store(86, 0b0000100010000011);
		store(87, 0b0000101110000010);
		store(88, 0b0000010010000100);
		store(89, 0b0001101100000001);
		store(90, 0b0000101100011110);
		store(91, 0b1000010001011110);
		store(92, 0b0000101100011110);
		store(93, 0b0001010000011110);
		store(94, 0b0011110011010000);
		store(95, 0b0000010010000010);
		store(96, 0b0000100000011110);
		store(97, 0b1000010001011110);
		store(98, 0b0000010001011110);
		store(99, 0b1100100000000001);
	}

	// load the memory value of a test program when push the IPL button
	public void loadROM()
	{
		clear();
		store(6, 17);
		store(12, 5);
		store(16, 6);

		store(30, 0b1000010001010000); // LDX X1, 16
		store(31, 0b1000010010110000); // LDX X2, 16[,I]
		store(32, 0b1000100001010010); // STX X1, 18
		store(33, 0b1000100010100110); // STX X2, 6[,I]

		store(34, 0b0000010000000110); // LDR R0, 6
		store(35, 0b0000010100100110); // LDR R1, 6[,I]
		store(36, 0b0000011001001100); // LDR R2, X1, 12
		store(37, 0b0000011101110010); // LDR R3, X1, 18[,I]

		store(38, 0b0000100000000111); // STR R0, 7
		store(39, 0b0000100100100111); // STR R1, 7[,I]
		store(40, 0b0000101001000010); // STR R2, X1, 2
		store(41, 0b0000101101110010); // STR R3, X1, 18[,I]

		store(42, 0b0000110000001100); // LDA R0, 12
		store(43, 0b0000110100101100); // LDA R1, 12[,I]
		store(44, 0b0000111001001100); // LDA R2, X1, 12
		store(45, 0b0000111101101100); // LDA R3, X1, 12[,I]
	}
}
