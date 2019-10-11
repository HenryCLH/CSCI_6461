package simulator;

import java.util.LinkedList;

import javax.swing.JTextArea;

public class Memory
{
	private JTextArea logTextArea;

	private int[] mem;		// 2048 words each is 16 bits
	private int[] expMem;	// an expend 2048 words
	private boolean expFlag;	// flag mark whether the memory has expanded

	private LinkedList<CacheLine> cache;	// cache list

	Memory()
	{
		// use int to simulate the memory to store word
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
		CacheLine cacheLine;
		// check if the cache has the address
		for (int i = 0; i < cache.size(); i++)
		{
			cacheLine = cache.get(i);
			if (cacheLine.getAddress() == address)	// hit
			{
				printLog("Load Hit Cache");
				return cacheLine.getValue();
			}
		}
		// not hit, create a new cache line and add it into cache
		int re = load(address);
		if (re == Integer.MAX_VALUE)
		{
			printLog("Load Failed! Invalid Address");
			return re;
		}
		else
		{
			cacheLine = new CacheLine(address, re);
			if (cache.size() == 16)
				cache.removeLast();
			cache.addFirst(cacheLine);
			printLog("Load Not Hit Cache");
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
					printLog("Store Hit Cache");
					return;
				}
			}
			// not hit, create a new cache line and add it into cache
			cacheLine = new CacheLine(address, value);
			cache.addFirst(cacheLine);
			printLog("Store Not Hit Cache");
		}
	}

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

	public void printLog(String s)
	{ logTextArea.append(s + "\n"); }

	public void setTextArea(JTextArea log)
	{ logTextArea = log; }

	public void clear()
	{
		mem = new int[2048];
		expMem = new int[2048];
		expFlag = false;

		cache = new LinkedList<CacheLine>();
	}
}
