package simulator;

public class CacheLine
{
	private int address;	// the address of the cache line
	private int value;		// the value of the cache line

	// constructor, need input the address and the value
	CacheLine(int addr, int v)
	{
		address = addr;
		value = v;
	}

	// basic operations of cache line

	public void setAddress(int addr)
	{ address = addr; }

	public void setValue(int v)
	{ value = v; }

	public int getAddress()
	{ return address; }

	public int getValue()
	{ return value; }
}
