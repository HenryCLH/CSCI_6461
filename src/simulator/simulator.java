package simulator;

public class simulator
{
	public static Memory memory = new Memory();
	public static CPU cpu = new CPU(memory);

	public static void main(String[] args)
	{
		memory.store(0b0, 5);
		memory.store(0b1, 7);

		memory.store(0b1000, 0b1000010001000000); // LDX
		memory.store(0b1001, 0b1000100001001000); // STX
		memory.store(0b1010, 0b0000010000000001); // LDR
		memory.store(0b1011, 0b0000100000000111); // STR
		memory.store(0b1100, 0b0000110000000110); // LDA
		memory.store(0b1101, 0b0000100000000101); // STR
		memory.store(0b1110, 0b0000010001000010); // LDR
		cpu.run(0b1000);
		System.out.println("After CPU");
		System.out.println(memory.load(0b0));
		System.out.println(memory.load(0b1));
		System.out.println(memory.load(0b101));
		System.out.println(memory.load(0b111));
		System.out.println(memory.load(0b1000));
	}

}
