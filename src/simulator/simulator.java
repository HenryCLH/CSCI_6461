package simulator;

public class simulator
{

	public static void main(String[] args)
	{
		System.out.println("-------------TEST-------------");
		CPU cpu = new CPU();

		int inst = 0b0000011100011111;
		cpu.instruction_in(inst);
		cpu.decoder();
	}

}
