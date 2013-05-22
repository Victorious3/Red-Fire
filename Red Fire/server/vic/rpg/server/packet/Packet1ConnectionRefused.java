package vic.rpg.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet1ConnectionRefused extends Packet
{
	public String message;
	
	public Packet1ConnectionRefused(String message) 
	{
		super(1);
	}
	
	public Packet1ConnectionRefused()
	{
		super(1);
	}

	@Override
	public void readData(DataInputStream stream) 
	{
		try {
			message = stream.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeData(DataOutputStream stream) 
	{
		try {
			stream.writeUTF(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
