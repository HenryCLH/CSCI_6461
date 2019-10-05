package simulator;

public class Memory
{
	private int[] mem;		// 2048 words each is 16 bits
	private int[] expMem;	// an expend 2048 words

	Memory()
	{
		// use int to simulate the memory to store word
		mem = new int[2048];
		expMem = new int[2048];
	}

	// load data from memory
	public int load(int address)
	{
		if (address < 2048)
			return mem[address];
		else
			return expMem[address - 2048];
	}

	// store data into memory
	public void store(int address, int value)
	{
		if (address < 2048)
			mem[address] = value;
		else
			expMem[address - 2048] = value;
	}
}
