package simulator;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

public class CPU extends Thread
{
	// Reference of outside objects
	private Memory memory;	// Reference of Memory object

	private JPanel jPanel;	// Reference of the panel on UI
	private JTable jTable;	// Reference of the data table on UI

	// Registers in CPU
	private int[] Reg;	// General Purpose Register (GPR) 16 bits
	private int[] XReg;	// Index Register (XR) 16 bits

	private int IR;	// Instruction Register 16 bits
	private int PC;	// Program Counter 12 bits
	private int CC;	// Condition Code 4 bits
	// 0-OVERFLOW  1-UNFERFLOW  2-DIVZERO  3-EQUALORNOT

	private int MAR;	// Memory Address Register 16 bits
	private int MBR;	// Memory Buffer Register 16 bits
	private int MFR;	// Machine Fault Register 4 bits

	// Flags
	private boolean pauseFlag = false;	// flag of pause the CPU
	private boolean stepFlag = false;	// flag of one step run
	private int choose = 0, memoryChoose = -1;	// flag for choose which value to input

	// constructor
	CPU(Memory m, JPanel p, JTable t)
	{
		memory = m; // connect to the memory
		jPanel = p; // connect to the panel
		jTable = t; // connect to the data table

		// initiate registers with 0
		Reg = new int[] { 0, 0, 0, 0 };
		XReg = new int[] { 0, 0, 0 };
		IR = PC = CC = 0;
		MAR = MBR = MFR = 0;
	}

	// single step run
	public void step()
	{
		stepFlag = true;
		pauseFlag = true;
	}

	// continue the CPU
	public void play()
	{
		stepFlag = false;
		pauseFlag = true;
	}

	// halt the CPU, set the PC value with 0 to stop the CPU
	public void halt()
	{
		PC = 0;
		stepFlag = false;
		pauseFlag = false;
	}

	// load the test program for the CPU
	public void loadTest()
	{
		// store data for test
		memory.store(6, 17);
		memory.store(12, 5);
		memory.store(16, 6);

		// store instructions of test program
		memory.store(30, 0b1000010001010000); // LDX X1, 16
		memory.store(31, 0b1000010010110000); // LDX X2, 16[,I]
		memory.store(32, 0b1000100001010010); // STX X1, 18
		memory.store(33, 0b1000100010100110); // STX X2, 6[,I]

		memory.store(34, 0b0000010000000110); // LDR R0, 6
		memory.store(35, 0b0000010100100110); // LDR R1, 6[,I]
		memory.store(36, 0b0000011001001100); // LDR R2, X1, 12
		memory.store(37, 0b0000011101110010); // LDR R3, X1, 18[,I]

		memory.store(38, 0b0000100000000111); // STR R0, 7
		memory.store(39, 0b0000100100100111); // STR R1, 7[,I]
		memory.store(40, 0b0000101001000010); // STR R2, X1, 2
		memory.store(41, 0b0000101101110010); // STR R3, X1, 18[,I]

		memory.store(42, 0b0000110000001100); // LDA R0, 12
		memory.store(43, 0b0000110100101100); // LDA R1, 12[,I]
		memory.store(44, 0b0000111001001100); // LDA R2, X1, 12
		memory.store(45, 0b0000111101101100); // LDA R3, X1, 12[,I]
		// set the PC be the first instruction of the test program
		PC = 30;
	}

	// run the CPU
	public void run()
	{
		while (true)
		{
			// if the CPU can run
			if (pauseFlag)
			{
				// pre load instruction
				int tmp = memory.load(PC);
				if (tmp != 0) // let 0 value means the end of the test program
				{
					IR = tmp; // load instruction
					decoder(); // decode and do the instruction
				}
				else
					pauseFlag = false;
				// if it is one step run, then just execute one instruction
				if (stepFlag)
				{
					pauseFlag = false;
					PC++;
				}
			}
			// refresh the display data
			refresh();
			// sleep 1 second on execute every instruction
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	// decode and do the instruction
	public void decoder()
	{
		// Instruction structure
		// Now is just load/store structure
		int Opcode;		// 6 bits
		int R;			// 2 bits
		int IX;			// 2 bits
		int I;			// 1 bits
		int Address;	// 5 bits
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
						MAR = Address + memory.load(XReg[IX]);
				}
				else
				{
					if (IX == 0)
						MAR = memory.load(Address);
					else
						MAR = memory.load(Address + memory.load(XReg[IX]));
				}
			}
			case 041: // LDX
			case 042: // STX
			{
				if (I == 0)
					MAR = Address;
				else
					MAR = memory.load(Address);
			}
		}

		switch (Opcode)
		{
			case 1: // LDR R, IX, Address
				Reg[R] = memory.load(MAR); // load data from memory into GPR
				break;
			case 2: // STR R, IX, Address
				memory.store(MAR, Reg[R]); // store GPR data into memory
				break;
			case 3: // LDA R, IX, Address
				Reg[R] = MAR; // load just the address into GPR
				break;
			case 041: // LDX IX, Address
				XReg[IX - 1] = memory.load(Address); // load data from memory into XR
				break;
			case 042: // STX IX, Address
				memory.store(Address, XReg[IX - 1]); // store XR data into memory
				break;
		}

		PC++; // set PC point to next instruction
	}

	// refresh the display data
	public void refresh()
	{
		// refresh registers display data
		Component[] labels = jPanel.getComponents();
		JLabel r0 = (JLabel) labels[0];
		JLabel r1 = (JLabel) labels[1];
		JLabel r2 = (JLabel) labels[2];
		JLabel r3 = (JLabel) labels[3];
		JLabel xr1 = (JLabel) labels[4];
		JLabel xr2 = (JLabel) labels[5];
		JLabel xr3 = (JLabel) labels[6];
		JLabel ir = (JLabel) labels[7];
		JLabel pc = (JLabel) labels[8];
		JLabel cc = (JLabel) labels[9];
		JLabel mar = (JLabel) labels[10];
		JLabel mbr = (JLabel) labels[11];
		JLabel mfr = (JLabel) labels[12];

		r0.setText(toBinaryString(Reg[0]));
		r1.setText(toBinaryString(Reg[1]));
		r2.setText(toBinaryString(Reg[2]));
		r3.setText(toBinaryString(Reg[3]));
		xr1.setText(toBinaryString(XReg[0]));
		xr2.setText(toBinaryString(XReg[1]));
		xr3.setText(toBinaryString(XReg[2]));
		ir.setText(toBinaryString(IR));
		pc.setText(toBinaryString(PC));
		cc.setText(toBinaryString(CC));
		mar.setText(toBinaryString(MAR));
		mbr.setText(toBinaryString(MBR));
		mfr.setText(toBinaryString(MFR));

		// refresh memory display data
		for (int i = 0; i < 4096; i++)
		{
			int tmp = memory.load(i);
			String s = toBinaryString(tmp);
			jTable.setValueAt(s, i, 1);
		}
	}

	// function for the outside to set the chose of register
	public void setChoose(int ch)
	{ choose = ch; }

	// function for the outside to set the chose of memory
	public void setMemoryChoose(int ch)
	{ memoryChoose = ch; }

	// function for push the input button, set the input value to the chose area
	public void input(String s)
	{
		int num = Integer.parseInt(s, 2);
		switch (choose)
		{
			case 0:
				Reg[0] = num;
				break;
			case 1:
				Reg[1] = num;
				break;
			case 2:
				Reg[2] = num;
				break;
			case 3:
				Reg[3] = num;
				break;
			case 11:
				XReg[0] = num;
				break;
			case 12:
				XReg[1] = num;
				break;
			case 13:
				XReg[2] = num;
				break;
			case 101:
				IR = num;
				break;
			case 102:
				PC = num;
				break;
			case 103:
				CC = num;
				break;
			case 111:
				MAR = num;
				break;
			case 112:
				MBR = num;
				break;
			case 113:
				MFR = num;
				break;
			case 1000:
				memory.store(memoryChoose, num);
				break;
		}
		refresh();
	}

	// convert int value to binary string to display, in form like 0000,0000,0000,0000
	public String toBinaryString(int num)
	{
		String s = Integer.toBinaryString(num);
		s = "0000000000000000" + s;
		int l = s.length();
		s = s.substring(l - 16, l - 12) + "," + s.substring(l - 12, l - 8) + "," + s.substring(l - 8, l - 4) + "," + s.substring(l - 4, l);
		return s;
	}

}
