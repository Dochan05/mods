package telepads;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class GuiNameTelepad extends GuiScreen{

	private GuiTextField textfield;
	public EntityPlayer thePlayer;
	public TETelepad te;

	public GuiNameTelepad(EntityPlayer player, TETelepad te){
		thePlayer = player;
		this.te = te;
	}

	@Override
	public void initGui() {

		int posX = (this.width ) / 2;
		int posY = (this.height ) / 2;
		this.buttonList.clear();

		textfield = new GuiTextField(fontRendererObj, posX-(150/2) , posY-50, 150, 20);

		String text = te.telepadname.equals("TelePad") ? te.getWorldObj().getBiomeGenForCoords(te.xCoord, te.zCoord).biomeName : te.telepadname;

		if(textfield != null){
			textfield.setText(text);
			textfield.setMaxStringLength(50);
		}
	}


	@Override
	public void drawScreen(int par1, int par2, float par3) {

		int posX = (this.width ) / 2;
		int posY = (this.height ) / 2;
		try{
			fontRendererObj.drawSplitString("Press Enter to confirm", posX+1 -75, posY-1, 180 ,0x000000);
			fontRendererObj.drawSplitString("Press Enter to confirm", posX -75, posY, 180 ,0xffffff);

			fontRendererObj.drawSplitString("Name Your TelePad : "+textfield.getText(), posX+1 -75, posY-1-20, 180 ,0x000000);
			fontRendererObj.drawSplitString("Name Your TelePad : "+textfield.getText(), posX   -75, posY  -20, 180 ,0xff0000);
		}finally{
			if(textfield != null) textfield.drawTextBox();
		}

	}

	protected void keyTyped(char c, int i)
	{
		super.keyTyped(c, i);
		if(i == Keyboard.KEY_RETURN){
			sendPacket(textfield.getText());
		}

		if(textfield != null) 
			textfield.textboxKeyTyped(c, i);
	}

	protected void mouseClicked(int i, int j, int k)
	{
		super.mouseClicked(i, j, k);
		if(textfield != null) textfield.mouseClicked(i, j, k);
	}

	public boolean doesGuiPauseGame(){
		return false;
	}


	@Override
	public void actionPerformed(GuiButton button) {
		sendPacket(textfield.getText());
		this.mc.thePlayer.closeScreen();
	}

	public void sendPacket(String padName){
		ByteBuf buf = Unpooled.buffer();
		ByteBufOutputStream out = new ByteBufOutputStream(buf);
		try {

			out.writeInt(ServerPacketHandler.IDENTIFIER_NAMEPAD);
			out.writeInt(te.xCoord);
			out.writeInt(te.yCoord);
			out.writeInt(te.zCoord);
			out.writeUTF(padName);
			out.close();
			
			Telepads.Channel.sendToServer(new FMLProxyPacket(buf, Telepads.channelName ));

		} catch (IOException e) {
			e.printStackTrace();
		}

		this.mc.thePlayer.closeScreen();
	}
}
