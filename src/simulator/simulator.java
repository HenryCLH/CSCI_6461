package simulator;

public class simulator
{
	public static Memory memory = new Memory();
	public static CPU cpu = new CPU(memory);

	public static void main(String[] args)
	{
		memory.store(0b11111, 123);
		memory.store(0, 0b0000011100011111);
		memory.store(1, 0b0000101100011110);
		cpu.run(0);
		System.out.println(memory.load(0b11111));
		System.out.println(memory.load(0b11110));
	}

}
