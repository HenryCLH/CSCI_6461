package simulator;

public class CPU
{
	private int R0, R1, R2, R3;	// General Purpose Register 16 bits
	private int X1, X2, X3;		// Index Register 16 bits

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

	CPU()
	{
		System.out.println("-------CPU start");
		run();
	}

	// set value of PC
	public void set_PC(int pc)
	{
		PC = pc;
		System.out.println("PC:\t" + toBinaryString(PC, 12)); // print for debug
	}

	// run
	public void run()
	{
		// TODO
	}

	// single step run
	public void step()
	{
		// TODO
	}

	// input instructions
	public void instruction_in(int instruction)
	{
		IR = instruction;
		System.out.println("IR:\t" + toBinaryString(IR, 16)); // print for debug
	}

	// decode the instruction
	public void decoder()
	{
		System.out.println("-------Decoder");
		Opcode = (IR & 0b1111110000000000) >> 10;
		R = (IR & 0b1100000000) >> 8;
		IX = (IR & 0b11000000) >> 6;
		I = (IR & 0b100000) >> 5;
		Address = IR & 0b11111;

		// print for debug
		System.out.println("Opcode:\t" + toBinaryString(Opcode, 6));
		System.out.println("R:\t" + toBinaryString(R, 2));
		System.out.println("IX:\t" + toBinaryString(IX, 2));
		System.out.println("I:\t" + toBinaryString(I, 1));
		System.out.println("Addr:\t" + toBinaryString(Address, 5));
	}

	// load
	public void load()
	{
		// TODO
	}

	// store
	public void store()
	{
		// TODO
	}

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
}
