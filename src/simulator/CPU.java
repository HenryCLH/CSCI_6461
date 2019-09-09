package simulator;

public class CPU
{
	private int IR;	// 16 bits

	// Instruction
	private int Opcode;
	private int R;
	private int IX;
	private int I;
	private int Address;

	CPU()
	{
		System.out.println("CPU start");
		IR = 0b0000011100011111;
		decoder();
	}

	public void decoder()
	{
		Opcode = (IR & 0b1111110000000000) >> 10;
		R = (IR & 0b1100000000) >> 8;
		IX = (IR & 0b11000000) >> 6;
		I = (IR & 0b100000) >> 5;
		Address = IR & 0b11111;

		System.out.println("Opcode:\t" + Integer.toBinaryString(Opcode));
		System.out.println("R:\t" + Integer.toBinaryString(R));
		System.out.println("IX:\t" + Integer.toBinaryString(IX));
		System.out.println("I:\t" + Integer.toBinaryString(I));
		System.out.println("Address:" + Integer.toBinaryString(Address));
	}
}
