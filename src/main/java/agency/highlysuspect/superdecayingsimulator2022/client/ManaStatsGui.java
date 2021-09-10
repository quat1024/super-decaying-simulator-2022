package agency.highlysuspect.superdecayingsimulator2022.client;

import agency.highlysuspect.superdecayingsimulator2022.SuperDecayingSimulator2022;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.StringTextComponent;

public class ManaStatsGui extends Screen {
	public ManaStatsGui() {
		super(new StringTextComponent("Mana Stats"));
	}
	
	private static final ResourceLocation NINEPATCH = SuperDecayingSimulator2022.id("textures/gui/6px-ninepatch.png");
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		renderNinepatchBackground(matrixStack, 20, width - 20, 20, height - 20);
	}
	
	@SuppressWarnings("SameParameterValue")
	private void renderNinepatchBackground(MatrixStack ms, int xMin, int xMax, int yMin, int yMax) {
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bindTexture(NINEPATCH);
		
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc(); //renderButton has these, idk if it's important
		RenderSystem.enableDepthTest();
		
		//NB:
		// the 32f in blit0 comes from the texture being 32 pixels big;
		// the 6s are because the ninepatch portion of the texture is 18x18 with nine 6x6 cells
		// The 6 in blit0 correspond to the 6s here, only because i want to draw the texture at 1x size.
		
		blit0(ms, xMin    , xMin + 6, yMin    , yMin + 6, 0); //top left
		blit0(ms, xMax - 6, xMax    , yMin    , yMin + 6, 2); //top right
		blit0(ms, xMin    , xMin + 6, yMax - 6, yMax    , 6); //bottom left
		blit0(ms, xMax - 6, xMax    , yMax - 6, yMax    , 8); //bottom right
		
		blit0(ms, xMin + 6, xMax - 6, yMin    , yMin + 6, 1); //top
		blit0(ms, xMin + 6, xMax - 6, yMax - 6, yMax    , 7); //bottom
		blit0(ms, xMin    , xMin + 6, yMin + 6, yMax - 6, 3); //left
		blit0(ms, xMax - 6, xMax    , yMin + 6, yMax - 6, 5); //right
		
		blit0(ms, xMin + 6, xMax - 6, yMin + 6, yMax - 6, 4); //center
	}
	
	private void blit0(MatrixStack ms, int x1, int x2, int y1, int y2, int cell) {
		//cell:
		//012
		//345
		//678
		int u = (cell % 3) * 6;
		int v = (cell / 3) * 6;
		innerBlit(ms.getLast().getMatrix(), x1, x2, y1, y2, getBlitOffset(), u / 32f, (u + 6) / 32f, v / 32f, (v + 6) / 32f);
	}
	
	//Copypasted out of AbstractGui because lol private.
	//Thanks to MCP for giving these param names lol
	private static void innerBlit(Matrix4f matrix, int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV) {
		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(matrix, (float)x1, (float)y2, (float)blitOffset).tex(minU, maxV).endVertex();
		bufferbuilder.pos(matrix, (float)x2, (float)y2, (float)blitOffset).tex(maxU, maxV).endVertex();
		bufferbuilder.pos(matrix, (float)x2, (float)y1, (float)blitOffset).tex(maxU, minV).endVertex();
		bufferbuilder.pos(matrix, (float)x1, (float)y1, (float)blitOffset).tex(minU, minV).endVertex();
		bufferbuilder.finishDrawing();
		RenderSystem.enableAlphaTest();
		WorldVertexBufferUploader.draw(bufferbuilder);
	}
}
