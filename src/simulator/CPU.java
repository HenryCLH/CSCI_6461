package simulator;

import javax.swing.JTextArea;

public class CPU extends Thread
{
	private Memory memory;

	private JTextArea logTextArea;
	// Registers in CPU
	private int[] Reg;	// General Purpose Register (GPR) 16 bits
	private int[] XReg;	// Index Register (XR) 16 bits

	private int PC;	// Program Counter 12 bits
	private int IR;	// Instruction Register 16 bits
	private int CC;	// Condition Code 4 bits
	// 0-OVERFLOW  1-UNFERFLOW  2-DIVZERO  3-EQUALORNOT

	private int MAR;	// Memory Address Register 16 bits
	private int MBR;	// Memory Buffer Register 16 bits
	private int MFR;	// Machine Fault Register 4 bits

	// constructor
	CPU(Memory mem)
	{
		memory = mem;
		// initiate registers with 0
		Reg = new int[] { 0, 0, 0, 0 };
		XReg = new int[] { 0, 0, 0 };
		PC = IR = CC = 0;
		MAR = MBR = MFR = 0;
	}

	public void run()
	{
		while (PC != 4)
		{
			stepRun();
		}
	}

	public void stepRun()
	{
		IR = load(PC);
		runInstruction();
	}

	public void runInstruction()
	{
		int opcode = IR >> 10;
		int reg, xreg, I, Addr, EA, A_L, L_R, devID;
		reg = (IR & 0x0300) >> 8;
		xreg = (IR & 0x00C0) >> 6;
		A_L = xreg & 0b10;
		L_R = xreg & 0b01;
		I = (IR & 0x0020) >> 5;
		Addr = IR & 0x001F;
		devID = Addr;

		if (opcode != 041 && opcode != 042 && xreg != 0)
			EA = XReg[xreg - 1];
		else
			EA = 0;
		EA += Addr;
		if (EA > 65536)
		{
			CC = 0b1000;
			printLog("EA OVERFLOW");
			EA = EA & 0x0000FFFF;
		}
		if (I == 1)
			EA += load(EA);

		switch (opcode)
		{
			case 0: // HLT
				PC = 4;
				printLog("HLT: PC -> 4");
				break;

			case 01: // LDR
				Reg[reg] = load(EA);
				PC++;
				printLog("LDR: Reg[" + reg + "] -> " + Reg[reg]);
				break;
			case 02: // STR
				store(EA, Reg[reg]);
				PC++;
				printLog("STR: Memory[" + EA + "] -> " + Reg[reg]);
				break;
			case 03: // LDA
				Reg[reg] = EA;
				PC++;
				break;

			case 04: // AMR
				Reg[reg] += load(EA);
				if (Reg[reg] > Short.MAX_VALUE)
				{
					CC = 0b1000;
					printLog("Result OVERFLOW");
					Reg[reg] = Reg[reg] & 0x0000FFFF;
				}
				else if (Reg[reg] < Short.MIN_VALUE)
				{
					CC = 0b0100;
					printLog("Result UNDERFLOW");
					Reg[reg] = Reg[reg] & 0x0000FFFF;
				}
				else
					CC = 0b0000;
				PC++;
				break;
			case 05: // SMR
				Reg[reg] -= load(EA);
				if (Reg[reg] > Short.MAX_VALUE)
				{
					CC = 0b1000;
					printLog("Result OVERFLOW");
					Reg[reg] = Reg[reg] & 0x0000FFFF;
				}
				else if (Reg[reg] < Short.MIN_VALUE)
				{
					CC = 0b0100;
					printLog("Result UNDERFLOW");
					Reg[reg] = Reg[reg] & 0x0000FFFF;
				}
				else
					CC = 0b0000;
				PC++;
				break;
			case 06: // AIR
				Reg[reg] += IR & 0x001F;
				if (Reg[reg] > Short.MAX_VALUE)
				{
					CC = 0b1000;
					printLog("Result OVERFLOW");
					Reg[reg] = Reg[reg] & 0x0000FFFF;
				}
				else if (Reg[reg] < Short.MIN_VALUE)
				{
					CC = 0b0100;
					printLog("Result UNDERFLOW");
					Reg[reg] = Reg[reg] & 0x0000FFFF;
				}
				else
					CC = 0b0000;
				PC++;
				break;
			case 07: // SIR
				Reg[reg] -= IR & 0x001F;
				if (Reg[reg] > Short.MAX_VALUE)
				{
					CC = 0b1000;
					printLog("Result OVERFLOW");
					Reg[reg] = Reg[reg] & 0x0000FFFF;
				}
				else if (Reg[reg] < Short.MIN_VALUE)
				{
					CC = 0b0100;
					printLog("Result UNDERFLOW");
					Reg[reg] = Reg[reg] & 0x0000FFFF;
				}
				else
					CC = 0b0000;
				PC++;
				break;

			case 010: // JZ
				PC = (Reg[reg] == 0) ? EA : PC + 1;
				break;
			case 011: // JNE
				PC = (Reg[reg] != 0) ? EA : PC + 1;
				break;
			case 012: // JCC
				int tmp = 1 << (3 - reg);
				PC = (CC == tmp) ? EA : PC + 1;
				break;
			case 013: // JMA
				PC = EA;
				break;
			case 014: // JSR
				Reg[3] = PC + 1;
				PC = EA;
				break;
			case 015: // RFS
				Reg[0] = IR & 0x001F;
				PC = Reg[3];
				break;
			case 016: // SOB
				Reg[reg]--;
				if (Reg[reg] > Short.MAX_VALUE)
				{
					CC = 0b1000;
					printLog("Result OVERFLOW");
					Reg[reg] = Reg[reg] & 0x0000FFFF;
				}
				else if (Reg[reg] < Short.MIN_VALUE)
				{
					CC = 0b0100;
					printLog("Result UNDERFLOW");
					Reg[reg] = Reg[reg] & 0x0000FFFF;
				}
				else
					CC = 0b0000;
				PC = (Reg[reg] > 0) ? EA : PC + 1;
				break;
			case 017: // JGE
				PC = (Reg[reg] >= 0) ? EA : PC + 1;
				break;

			case 020: // MLT
				long result = Reg[reg] * Reg[xreg];
				if (result > Integer.MAX_VALUE)
				{
					CC = 0b1000;
					printLog("Result OVERFLOW");
				}
				else if (result < Integer.MIN_VALUE)
				{
					CC = 0b0100;
					printLog("Result UNDERFLOW");
				}
				else
					CC = 0b0000;
				int re = (int) result;
				Reg[reg] = re >>> 16;
				Reg[reg + 1] = re & 0x0000FFFF;
				PC++;
				break;
			case 021: // DVD
				if (Reg[xreg] == 0)
					CC = 0b0010;
				else
				{
					CC = 0b00;
					int quotient = Reg[reg] / Reg[xreg];
					int remainder = Reg[reg] % Reg[xreg];
					Reg[reg] = quotient;
					Reg[reg + 1] = remainder;
				}
				PC++;
				break;
			case 022: // TRR
				CC = (Reg[reg] == Reg[xreg]) ? 0b0001 : 0b0000;
				PC++;
				break;
			case 023: // AND
				Reg[reg] &= Reg[xreg];
				PC++;
				break;
			case 024: // ORR
				Reg[reg] |= Reg[xreg];
				PC++;
				break;
			case 025: // NOT
				Reg[reg] = ~Reg[reg];
				PC++;
				break;

			case 031: // SRC
				if (A_L == 1)
				{
					if (L_R == 1)
						Reg[reg] = Reg[reg] << Addr;
					else
					{
						Reg[reg] = Reg[reg] & 0x0000FFFF;
						Reg[reg] = Reg[reg] >>> Addr;
					}
				}
				else
				{
					int flag = Reg[reg] & 0x00008000;
					if (L_R == 1)
					{
						Reg[reg] = Reg[reg] << Addr;
						Reg[reg] = Reg[reg] & 0x0000FFFF;
						Reg[reg] = Reg[reg] | flag;
					}
					else
						Reg[reg] = Reg[reg] >> Addr;
				}
				PC++;
				break;
			case 032: // RRC
				if (L_R == 1)
				{
					int pre = (Reg[reg] > 0) ? 0x00000000 : 0xFFFF0000;
					int flag = 0;
					for (int i = 0; i < Addr; i++)
					{
						flag = ((Reg[reg] & 0x00008000) == 0) ? 0 : 1;
						Reg[reg] = Reg[reg] << 1;
						Reg[reg] = Reg[reg] | flag;
					}
					Reg[reg] = Reg[reg] | pre;
				}
				else
				{
					Reg[reg] = Reg[reg] & 0x0000FFFF;
					int flag = 0;
					for (int i = 0; i < Addr; i++)
					{
						flag = ((Reg[reg] & 1) == 0) ? 0x00000000 : 0x80000000;
						Reg[reg] = Reg[reg] >> 1;
						Reg[reg] = Reg[reg] | flag;
					}
					Reg[reg] = (flag == 0) ? Reg[reg] : Reg[reg] | 0xFFFF0000;
				}
				PC++;
				break;

			case 036: // TRAP
				// TODO
				break;

			case 041: // LDX
				XReg[xreg - 1] = load(EA);
				PC++;
				printLog("LDX: XReg[" + xreg + "] -> " + XReg[xreg - 1]);
				break;
			case 042: // STX
				store(EA, XReg[xreg - 1]);
				PC++;
				printLog("STX: Memory[" + EA + "] -> " + XReg[xreg - 1]);
				break;

			case 061: // IN
				Reg[reg] = inputFromDevice(devID);
				PC++;
				break;
			case 062: // OUT
				outputToDevice(devID, Reg[reg]);
				PC++;
				break;
			default:
				// TODO
		}
	}

	public void setIR(int ir)
	{ IR = ir; }

	public void store(int address, int value)
	{
		if (address > 65536)
			printLog("Invalid Address " + address + ". Address must no more than 16 bits!");
		else
		{
			MAR = address;
			if (value > 65536)
				printLog("Invalid Value to Store: " + value + ". Value must no more than 16 bits!");
			else
			{
				MBR = value;
				memory.storeCache(address, value);
			}
		}
	}

	public int load(int address)
	{
		if (address > 65536)
		{
			printLog("Invalid Address: " + address + ". Address must no more than 16 bits!");
			return Integer.MAX_VALUE;
		}
		else
		{
			MAR = address;
			int tmp = memory.loadCache(address);
			if (tmp == Integer.MAX_VALUE)
				return Integer.MAX_VALUE;
			else
			{
				MBR = tmp;
				return MBR;
			}
		}
	}

	public int inputFromDevice(int deviceID)
	{
		// TODO
		return 0;
	}

	public void outputToDevice(int deviceID, int value)
	{
		// TODO
	}

	public void setRegister(int index, int value)
	{
		switch (index)
		{
			case 0:
				Reg[0] = value;
				break;
			case 1:
				Reg[1] = value;
				break;
			case 2:
				Reg[2] = value;
				break;
			case 3:
				Reg[3] = value;
				break;
			case 4:
				XReg[0] = value;
				break;
			case 5:
				XReg[1] = value;
				break;
			case 6:
				XReg[2] = value;
				break;
			case 7:
				PC = value;
				break;
			case 8:
				IR = value;
				break;
			case 9:
				CC = value;
				break;
			case 10:
				MAR = value;
				break;
			case 11:
				MBR = value;
				break;
			case 12:
				MFR = value;
				break;
		}
	}

	public int getRegister(int index)
	{
		switch (index)
		{
			case 0:
				return Reg[0];
			case 1:
				return Reg[1];
			case 2:
				return Reg[2];
			case 3:
				return Reg[3];
			case 4:
				return XReg[0];
			case 5:
				return XReg[1];
			case 6:
				return XReg[2];
			case 7:
				return PC;
			case 8:
				return IR;
			case 9:
				return CC;
			case 10:
				return MAR;
			case 11:
				return MBR;
			case 12:
				return MFR;
		}
		return Integer.MAX_VALUE;
	}

	public void setTextArea(JTextArea log)
	{ logTextArea = log; }

	public void printLog(String s)
	{ logTextArea.append(s + "\n"); }

	public void clear()
	{
		Reg = new int[] { 0, 0, 0, 0 };
		XReg = new int[] { 0, 0, 0 };
		PC = IR = CC = 0;
		MAR = MBR = MFR = 0;
	}
}
