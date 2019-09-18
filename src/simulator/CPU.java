package simulator;

public class CPU
{
	private int[] Reg;	// General Purpose Register 16 bits
	private int[] XReg;	// Index Register 16 bits

	private int IR;	// Instruction Register 16 bits
	private int PC;	// Program Counter 12 bits
	private int CC;	// Condition Code 4 bits
					// 0-OVERFLOW  1-UNFERFLOW  2-DIVZERO  3-EQUALORNOT

	private int MAR;	// Memory Address Register 16 bits
	private int MBR;	// Memory Buffer Register 16 bits
	private int MFR;	// Machine Fault Register 4 bits

	// Instruction
	private int Opcode;		// 6 bits
	private int R;			// 2 bits
	private int IX;			// 2 bits
	private int I;			// 1 bits
	private int Address;	// 5 bits

	// Memory
	private Memory mem;

	CPU(Memory m)
	{
		System.out.println("--------CPU  Start--------");	// print for debug
		mem = m;	// connect to the memory
		Reg = new int[] { 0, 0, 0, 0 };
		XReg = new int[] { 0, 0, 0 };
		run();
	}

	// set PC
	public void set_PC(int pc)
	{ PC = pc; }

	// set register
	public void set_register(int reg, int num)
	{ Reg[num] = reg; }

	// run
	public void run()
	{
		// TODO
	}

	// single step run
	public void step()
	{
		IR = mem.load(PC);
		decoder();
	}

	// decode the instruction
	public void decoder()
	{
		System.out.println("-------Decoder");

		int tmpcode = (IR & 0b1100000000000000) >> 14;
		switch (tmpcode)
		{
			case 0: // 6 bits Opcode
			{
				Opcode = (IR & 0b1111110000000000) >> 10;
				R = (IR & 0b1100000000) >> 8;
				IX = (IR & 0b11000000) >> 6;
				I = (IR & 0b100000) >> 5;
				Address = IR & 0b11111;
				// calculate effective address
				if (I == 0)
				{
					if (IX == 0)
						MAR = Address;
					else
						MAR = Address + mem.load(XReg[IX]);
				}
				else
				{
					if (IX == 0)
						MAR = mem.load(Address);
					else
						MAR = mem.load(Address + mem.load(XReg[IX]));
				}
				// decode Opcode
				switch (Opcode)
				{
					case 1: // LDR R, IX, Address
						Reg[R] = mem.load(MAR);
						break;
					case 2: // STR R, IX, Address
						mem.store(MAR, Reg[R]);
						break;
					case 3: // LDA R, IX, Address
						Reg[R] = MAR;
						break;
				}
				break;
			}
			case 1: // 8 bits Opcode
			{
				Opcode = (IR & 0b1111111100000000) >> 8;
				IX = (IR & 0b11000000) >> 6;
				I = (IR & 0b100000) >> 5;
				Address = IR & 0b11111;
				// calculate effective address
				if (I == 0)
					MAR = Address;
				else
					MAR = mem.load(Address);
				// decode Opcode
				switch (Opcode)
				{
					case 0x41: // LDX IX, Address
						XReg[IX] = mem.load(MAR);
						break;
					case 0x42: // STX IX, Address
						mem.store(MAR, XReg[IX]);
						break;
				}
				break;
			}
		}
		PC++; // set PC to next instruction

		// print for debug
		System.out.println("Opcode:\t" + toBinaryString(Opcode, 6));
		System.out.println("R:\t" + toBinaryString(R, 2));
		System.out.println("IX:\t" + toBinaryString(IX, 2));
		System.out.println("I:\t" + toBinaryString(I, 1));
		System.out.println("Addr:\t" + toBinaryString(Address, 5));
		System.out.println("PC:\t" + toBinaryString(PC, 12));
	}

	// load
	public int load(int address)
	{ return mem.load(address); }

	// store
	public void store(int address, int value)
	{ mem.store(address, value); }

	// function to print registers in binary string
	public String toBinaryString(int num, int bits)
	{
		String s = Integer.toBinaryString(num);
		int length = s.length();
		for (int i = 0; i < bits - length; i++)
		{
			s = "0" + s;
		}
		return s;
	}

	// for UI to read data
	public int[] read_data()
	{
		int[] re_data = new int[13];
		for (int i = 0; i < 4; i++)
		{
			re_data[i] = Reg[i];
		}
		for (int i = 0; i < 3; i++)
		{
			re_data[4 + i] = XReg[i];
		}
		re_data[7] = IR;
		re_data[8] = PC;
		re_data[9] = CC;
		re_data[10] = MAR;
		re_data[11] = MBR;
		re_data[12] = MFR;
		return re_data;
	}
}
