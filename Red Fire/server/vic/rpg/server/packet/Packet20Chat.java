package vic.rpg.server.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet20Chat extends Packet 
{
	public String message;
	public String player;
	public String prefix = "";
	public String suffix = "";

	public Packet20Chat(String message, String player) 
	{
		super(20);
		
		this.message = message;
		this.player = player;		
	}
	
	public Packet20Chat()
	{
		super(20);
	}

	@Override
	public void readData(DataInputStream stream) 
	{
		try {
			player = stream.readUTF();
			message = stream.readUTF();
			prefix = stream.readUTF();
			suffix = stream.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeData(DataOutputStream stream) 
	{
		try {
			stream.writeUTF(player);
			stream.writeUTF(message);
			stream.writeUTF(prefix);
			stream.writeUTF(suffix);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
