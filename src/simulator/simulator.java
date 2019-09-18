package simulator;

public class simulator
{
	public static Memory memory = new Memory();
	public static CPU cpu = new CPU(memory);

	public static void main(String[] args)
	{
		memory.store(0, 0b11100011111);
		cpu.set_PC(0);
		cpu.step();
	}

}
