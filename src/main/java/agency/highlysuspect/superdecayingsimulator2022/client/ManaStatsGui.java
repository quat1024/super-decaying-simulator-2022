package agency.highlysuspect.superdecayingsimulator2022.client;

import agency.highlysuspect.superdecayingsimulator2022.GeneratingFlowerType;
import agency.highlysuspect.superdecayingsimulator2022.ManaStatsWsd;
import agency.highlysuspect.superdecayingsimulator2022.SuperDecayingSimulator2022;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManaStatsGui extends Screen {
	public ManaStatsGui(@Nullable Screen parent, ManaStatsWsd stats) {
		super(new StringTextComponent("Mana Stats"));
		this.parentScreen = parent;
		setStats(stats);
	}
	
	private static final ResourceLocation WIDGETS = SuperDecayingSimulator2022.id("textures/gui/mana-stats-widgets.png");
	private final @Nullable Screen parentScreen;
	
	private final List<Entry> entries = new ArrayList<>();
	private static final int ENTRY_HEIGHT = 24;
	private float listScrollTopY = 0;
	private float listScrollTopMax;
	private int maxNameWidth;
	
	private void setStats(ManaStatsWsd stats) {
		entries.clear();
		stats.table.forEach((type, mana) -> entries.add(new Entry(type, mana)));
		Collections.sort(entries);
		
		if(stats.total != 0) entries.add(new Entry(new StringTextComponent("Total"), new ItemStack(ModItems.lifeEssence), stats.total));
		
		//This is called from the constructor so, no convenience fields like "font" are available rn
		FontRenderer font = Minecraft.getInstance().fontRenderer;
		maxNameWidth = entries.stream().mapToInt(e -> e.nameWidth(font)).max().orElse(0);
	}
	
	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		assert minecraft != null;
		
		int panelX = 20;
		int panelY = 20;
		int panelXMax = width - 20;
		int panelYMax = height - 20;
		int panelWidth = panelXMax - panelX;
		int panelHeight = panelYMax - panelY;
		
		int listX = panelX + 10;
		int listY = panelY + 25; //leave space for the title
		int listXMax = panelXMax - 10;
		int listYMax = panelYMax - 10;
		int listWidth = listXMax - listX;
		int listHeight = listYMax - listY;
		
		//While I'm here...
		listScrollTopMax = Math.max(0, entries.size() * ENTRY_HEIGHT - listHeight);
		listScrollTopY = MathHelper.clamp(listScrollTopY, 0, listScrollTopMax);
		
		//Render the gui background and title.
		renderBackground(ms);
		renderNinepatchBackground(ms, panelX, panelXMax, panelY, panelYMax);
		
		//using this instead of drawCenteredString so there's no drop shadow.
		IReorderingProcessor bababa = getTitle().func_241878_f();
		font.func_243248_b(ms, getTitle(), width / 2f - (font.func_243245_a(bababa) / 2f), 27, 0x404040);
		
		//Draw the list widget. first, scissor to its dimensions
		if(listWidth < 1 || listHeight < 1) return; //scissor breaks for negative sizes. +1 for some fudge.
		double s = minecraft.getMainWindow().getGuiScaleFactor();
		//yeah scissor is weird dude
		RenderSystem.enableScissor((int) (listX * s),     (int) (minecraft.getMainWindow().getFramebufferHeight() - (listYMax * s)),
		                           (int) (listWidth * s), (int) (listHeight * s));
		
		for(int index = 0; index < entries.size(); index++) {
			Entry entry = entries.get(index);
			
			//the +1s are to fudge things a bit since slot backgrounds are a little oversized.
			//also; only scroll upwards in scaled-pixel increments, mainly because items can only be drawn at integer coordinates for some reason
			int entryY = (int) (listY + (index * ENTRY_HEIGHT) - listScrollTopY + 1);
			int x = listX + 1;
			
			//flower icon
			renderSlotBackground(ms, x - 1, ((int) entryY) - 1);
			minecraft.getItemRenderer().renderItemAndEffectIntoGUI(entry.icon, x, (int) entryY); //lol @ having to round the position
			x += 22;
			
			//label
			font.func_243248_b(ms, entry.text, x, entryY + 4, 0x404040);
			x += maxNameWidth + 5;
			
			//how much mana
			font.drawString(ms, Long.toString(entry.amount), x, entryY + 4, 0x303030);
		}
		
		//remember to pop the scissor!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		RenderSystem.disableScissor();
	}
	
	static class Entry implements Comparable<Entry> {
		Entry(GeneratingFlowerType type, long amount) {
			this(type.representative.getTranslatedName(), new ItemStack(type.representative), amount);
		}
		
		public Entry(ITextComponent text, ItemStack icon, long amount) {
			this.text = text;
			this.icon = icon;
			this.amount = amount;
		}
		
		private final ITextComponent text;
		private final ItemStack icon;
		private final long amount;
		
		int nameWidth(FontRenderer font) {
			return font.func_243245_a(text.func_241878_f());
		}
		
		@Override
		public int compareTo(Entry o) {
			return text.getString().compareTo(o.text.getString());
		}
	}
	
	private void renderSlotBackground(MatrixStack ms, float x, float y) {
		//I pasted one into this texture
		assert minecraft != null;
		minecraft.getTextureManager().bindTexture(WIDGETS);
		
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc(); //renderButton has these, idk if it's important
		RenderSystem.enableDepthTest();
		
		//The slot background is at u 32px, v 0px in the texture, and is 18px square
		blit0(ms, x, x + 18, y, y + 18, 32, 32 + 18, 0, 18);
	}
	
	@SuppressWarnings("SameParameterValue")
	private void renderNinepatchBackground(MatrixStack ms, int xMin, int xMax, int yMin, int yMax) {
		//If the window is really really small. Just bail
		if(yMax <= yMin + 12 || xMax <= xMin + 12) return;
		
		assert minecraft != null;
		minecraft.getTextureManager().bindTexture(WIDGETS);
		
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc(); //renderButton has these, idk if it's important
		RenderSystem.enableDepthTest();
		
		//NB:
		// the 32/64f in blit0 come from the size of the texture;
		// the 6s are because the ninepatch portion of the texture is 18x18 with nine 6x6 cells;
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
	
	private void blit0(MatrixStack ms, float x1, float x2, float y1, float y2, int cellSelect) {
		//cellSelect:
		//012
		//345
		//678
		int u = (cellSelect % 3) * 6;
		int v = (cellSelect / 3) * 6;
		innerBlit(ms.getLast().getMatrix(), x1, x2, y1, y2, getBlitOffset(), u / 64f, (u + 6) / 64f, v / 32f, (v + 6) / 32f);
	}
	
	@SuppressWarnings("SameParameterValue")
	private void blit0(MatrixStack ms, float x1, float x2, float y1, float y2, int u1Px, int u2Px, int v1Px, int v2Px) {
		innerBlit(ms.getLast().getMatrix(), x1, x2, y1, y2, getBlitOffset(), u1Px / 64f, u2Px / 64f, v1Px / 32f, v2Px / 32f);
	}
	
	//Copypasted out of AbstractGui because lol private.
	//Thanks to MCP for giving these param names lol
	//Also i made them take floats.
	private static void innerBlit(Matrix4f matrix, float x1, float x2, float y1, float y2, int blitOffset, float minU, float maxU, float minV, float maxV) {
		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(matrix, x1, y2, (float)blitOffset).tex(minU, maxV).endVertex();
		bufferbuilder.pos(matrix, x2, y2, (float)blitOffset).tex(maxU, maxV).endVertex();
		bufferbuilder.pos(matrix, x2, y1, (float)blitOffset).tex(maxU, minV).endVertex();
		bufferbuilder.pos(matrix, x1, y1, (float)blitOffset).tex(minU, minV).endVertex();
		bufferbuilder.finishDrawing();
		RenderSystem.enableAlphaTest();
		WorldVertexBufferUploader.draw(bufferbuilder);
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		listScrollTopY -= delta * 10;
		return true;
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		listScrollTopY -= dragY;
		return true;
	}
	
	@Override
	public void closeScreen() {
		if(minecraft != null)	minecraft.displayGuiScreen(parentScreen);
	}
}
