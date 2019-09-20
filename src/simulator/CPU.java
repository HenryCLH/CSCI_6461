package simulator;

public class CPU
{
	private int[] Reg;	// General Purpose Register (GPR) 16 bits
	private int[] XReg;	// Index Register (XR) 16 bits

	private int IR;	// Instruction Register 16 bits
	private int PC;	// Program Counter 12 bits
	private int CC;	// Condition Code 4 bits
					// 0-OVERFLOW  1-UNFERFLOW  2-DIVZERO  3-EQUALORNOT

	private int MAR;	// Memory Address Register 16 bits
	private int MBR;	// Memory Buffer Register 16 bits
	private int MFR;	// Machine Fault Register 4 bits

	// Instruction structure
	// Now is just load/store structure
	private int Opcode;		// 6 bits
	private int R;			// 2 bits
	private int IX;			// 2 bits
	private int I;			// 1 bits
	private int Address;	// 5 bits

	// Reference of Memory object
	private Memory mem;

	CPU(Memory m)
	{
		System.out.println("--------CPU  Start--------"); // print for debug
		mem = m; // connect to the memory
		Reg = new int[] { 0, 0, 0, 0 }; // initiate GPRs with 0
		XReg = new int[] { 0, 0, 0 }; // initiate XRs with 0
	}

	// set the value of PC
	public void set_PC(int pc)
	{ PC = pc; }

	// set the value of GPRs
	public void set_register(int reg, int num)
	{ Reg[num] = reg; }

	// run test program, PC has the address of the first instruction of the test program
	public void run(int pc)
	{
		PC = pc; // set PC
		IR = mem.load(PC); // load instruction
		while (IR != 0) // let 0 value means the end of the test program
		{
			decoder(); // decode and do the instruction, now is just load/store
			IR = mem.load(PC); // get next instruction
		}
	}

	// single step run
	public void step()
	{
		IR = mem.load(PC); // load instruction
		decoder(); // decode and do the instruction, now is just load/store
	}

	// halt button function, just reset the PC and keep other values
	public void halt()
	{ PC = 0; }

	// main part of the CPU, decode and do instructions
	// decode the instruction
	public void decoder()
	{
		// decode the instruction
		Opcode = (IR & 0b1111110000000000) >> 10; // get Opcode
		// load/store instruction structure
		R = (IR & 0b1100000000) >> 8; // index of GPR
		IX = (IR & 0b11000000) >> 6; // index of XR
		I = (IR & 0b100000) >> 5; // sign of indirect
		Address = IR & 0b11111; // address

		// calculate effective address (EA) and store in MAR
		// separate the LDX/STX and others because they have different way to calculate EA
		switch (Opcode)
		{
			case 1: // LDR
			case 2: // STR
			case 3: // LDA
			{
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
			}
			case 041: // LDX
			case 042: // STX
			{
				if (I == 0)
					MAR = Address;
				else
					MAR = mem.load(Address);
			}
		}

		switch (Opcode)
		{
			case 1: // LDR R, IX, Address
				Reg[R] = mem.load(MAR); // load data from memory into GPR
				break;
			case 2: // STR R, IX, Address
				mem.store(MAR, Reg[R]); // store GPR data into memory
				break;
			case 3: // LDA R, IX, Address
				Reg[R] = MAR; // load just the address into GPR
				break;
			case 041: // LDX IX, Address
				XReg[IX - 1] = mem.load(Address); // load data from memory into XR
				break;
			case 042: // STX IX, Address
				mem.store(Address, XReg[IX - 1]); // store XR data into memory
				break;
		}

		PC++; // set PC point to next instruction

		// print for debug
		System.out.println("Opcode:\t" + toBinaryString(Opcode, 6));
		System.out.println("R:\t" + toBinaryString(R, 2));
		System.out.println("IX:\t" + toBinaryString(IX, 2));
		System.out.println("I:\t" + toBinaryString(I, 1));
		System.out.println("Addr:\t" + toBinaryString(Address, 5));
		System.out.println("PC:\t" + toBinaryString(PC, 12));
		System.out.println("-----------");
		System.out.println("Reg[0]: " + Reg[0] + " | XReg[1]: " + XReg[0]);
	}

	// just for debug, will not be in the final program
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
