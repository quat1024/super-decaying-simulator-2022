package agency.highlysuspect.superdecayingsimulator2022.client;

import agency.highlysuspect.superdecayingsimulator2022.GeneratingFlowerType;
import agency.highlysuspect.superdecayingsimulator2022.ManaStatsWsd;
import agency.highlysuspect.superdecayingsimulator2022.SuperDecayingSimulator2022;
import agency.highlysuspect.superdecayingsimulator2022.SuperDecayingSimulator2022NetworkHandler;
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
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.botania.client.core.handler.HUDHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManaStatsGui extends Screen {
	public ManaStatsGui(@Nullable ManaStatsWsd stats) {
		super(new TranslationTextComponent("gui.super-decaying-simulator-2022.mana-stats"));
		if(stats != null) setStats(stats);
	}
	
	private static final ResourceLocation WIDGETS = SuperDecayingSimulator2022.id("textures/gui/mana-stats-widgets.png");
	private static final int WIDGETS_WIDTH = 64;
	private static final int WIDGETS_HEIGHT = 64;
	
	private static final int ENTRY_HEIGHT = 24;
	
	private List<Entry> entries = null;
	private float listScrollTopY = 0;
	private int maxNameWidth;
	private int maxPoolsWidth;
	
	public void setStats(ManaStatsWsd stats) {
		entries = new ArrayList<>();
		
		if(stats.table.size() > 0) {
			stats.table.forEach((type, mana) -> entries.add(new Entry(type, mana)));
			Collections.sort(entries);
			
			entries.add(new Entry(new StringTextComponent("Total"), ItemStack.EMPTY, stats.total));
				
			//This is called from the constructor so, no convenience fields like "font" are available rn
			FontRenderer font = Minecraft.getInstance().fontRenderer;
			maxNameWidth = entries.stream().mapToInt(e -> e.nameWidth(font)).max().orElse(0);
			maxPoolsWidth = entries.stream().mapToInt(e -> e.poolWidth(font)).max().orElse(0);
		}
	}
	
	@Override
	public void tick() {
		//If the player is running an integrated server, well, the data's right here, might as well take it.
		assert minecraft != null;
		IntegratedServer server = minecraft.getIntegratedServer();
		if(server != null) setStats(ManaStatsWsd.getFor(server));
	}
	
	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
		assert minecraft != null;
		
		int panelWidth, panelHeight;
		if(entries == null || entries.size() == 0) {
			panelWidth = 270;
			panelHeight = 50;
		} else {
			//Deeply magical numbers. Do not touch
			panelWidth = maxNameWidth + maxPoolsWidth + 172;
			panelHeight = MathHelper.clamp(entries.size() * ENTRY_HEIGHT + 36, 50, height - 40);
		}
		
		int panelX = width / 2 - panelWidth / 2;
		int panelY = height / 2 - panelHeight / 2;
		int panelXMax = width / 2 + panelWidth / 2;
		int panelYMax = height /2 + panelHeight / 2;
		
		int listX = panelX + 10;
		int listY = panelY + 25; //leave space for the title
		int listXMax = panelXMax - 10;
		int listYMax = panelYMax - 10;
		int listWidth = listXMax - listX;
		int listHeight = listYMax - listY;
		
		//Render the gui background and title.
		renderBackground(ms);
		renderNinepatchBackground(ms, panelX, panelXMax, panelY, panelYMax);
		
		//using this instead of drawCenteredString so there's no drop shadow.
		IReorderingProcessor bababa = getTitle().func_241878_f();
		font.func_243248_b(ms, getTitle(), width / 2f - (font.func_243245_a(bababa) / 2f), panelY + 7, 0x404040);
		
		if(entries == null) {
			drawCenteredString(ms, font, new TranslationTextComponent("gui.super-decaying-simulator-2022.mana-stats.waiting"), width / 2, height / 2, 0xffffff);
		} else if(entries.size() > 0) {
			//While I'm here...
			listScrollTopY = MathHelper.clamp(listScrollTopY, 0, (float) Math.max(0, entries.size() * ENTRY_HEIGHT - listHeight));
			
			//Draw the list widget. first, scissor to its dimensions
			if(listWidth < 1 || listHeight < 1) return; //scissor breaks for negative sizes. +1 for some fudge.
			double s = minecraft.getMainWindow().getGuiScaleFactor();
			//yeah scissor is weird dude
			RenderSystem.enableScissor((int) (listX * s), (int) (minecraft.getMainWindow().getFramebufferHeight() - (listYMax * s)),
				(int) (listWidth * s), (int) (listHeight * s));
			
			for(int index = 0; index < entries.size(); index++) {
				Entry entry = entries.get(index);
				
				//the +1s are to fudge things a bit, since slot backgrounds are a little oversized & it has to fit in the scissor.
				//also; only scroll upwards in scaled-pixel increments, (i.e. `y` is an integer and not a float)
				//mainly because item rendering machinery only accepts integer coordinates for some reason
				int x = listX + 1;
				int y = (int) (listY + (index * ENTRY_HEIGHT) - listScrollTopY + 1);
				
				//flower icon
				if(!entry.icon.isEmpty()) {
					renderSlotBackground(ms, x - 1, y - 1);
					minecraft.getItemRenderer().renderItemAndEffectIntoGUI(entry.icon, x, y);
				}
				x += 22;
				
				//label
				font.func_243248_b(ms, entry.text, x, y + 4, 0x404040);
				x += maxNameWidth + 10;
				
				//how many mana pools total
				x += maxPoolsWidth / 2;
				renderManaPoolIcon(ms, x - 8.5f, y);
				
				font.drawStringWithShadow(ms, entry.pools, x - (font.getStringWidth(entry.pools) / 2f), y + 4, 0xffffff);
				x += maxPoolsWidth / 2 + 10;
				
				//leftover portion
				HUDHandler.renderManaBar(ms, x, y + 5, 0x4444ff, 1f, entry.fraction, 1_000_000);
			}
			
			//remember to pop the scissor!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			RenderSystem.disableScissor();
		} else {
			drawCenteredString(ms, font, new TranslationTextComponent("gui.super-decaying-simulator-2022.mana-stats.nothing"), width / 2, height / 2, 0xffffff);
		}
	}
	
	static class Entry implements Comparable<Entry> {
		Entry(GeneratingFlowerType type, long amount) {
			this(type.toText(), new ItemStack(type.icon), amount);
		}
		
		public Entry(ITextComponent text, ItemStack icon, long amount) {
			this.text = text;
			this.icon = icon;
			
			this.pools = Long.toString(amount / 1_000_000L);
			this.fraction = (int) (amount % 1_000_000);
		}
		
		private final ITextComponent text;
		private final ItemStack icon;
		private final String pools; //Lmao long
		private final int fraction;
		
		int nameWidth(FontRenderer font) {
			return font.func_243245_a(text.func_241878_f());
		}
		
		int poolWidth(FontRenderer font) {
			return font.getStringWidth(pools);
		}
		
		@Override
		public int compareTo(Entry o) {
			return text.getString().compareTo(o.text.getString());
		}
	}
	
	private void renderManaPoolIcon(MatrixStack ms, float x, float y) {
		assert minecraft != null;
		minecraft.getTextureManager().bindTexture(WIDGETS);
		
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc(); //renderButton has these, idk if it's important
		RenderSystem.enableDepthTest();
		
		//The mana pool icon is at u 0px, v 32px in the texture, and is 32px square.
		//If you're wondering why I use a texture instead of rendering a real item,
		//I couldn't figure out how to make the text appear above the item...
		//Also i want it to be a little bit transparent.
		blit0(ms, x, x + 16, y, y + 16, 0, 32, 32, 64);
	}
	
	private void renderSlotBackground(MatrixStack ms, float x, float y) {
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
		innerBlit(ms.getLast().getMatrix(), x1, x2, y1, y2, getBlitOffset(),
			(float) u / WIDGETS_WIDTH, (float) (u + 6) / WIDGETS_WIDTH, (float) v / WIDGETS_HEIGHT, (float) (v + 6) / WIDGETS_HEIGHT);
	}
	
	@SuppressWarnings("SameParameterValue")
	private void blit0(MatrixStack ms, float x1, float x2, float y1, float y2, int u1Px, int u2Px, int v1Px, int v2Px) {
		innerBlit(ms.getLast().getMatrix(), x1, x2, y1, y2, getBlitOffset(),
			(float) u1Px / WIDGETS_WIDTH, (float) u2Px / WIDGETS_WIDTH, (float) v1Px / WIDGETS_HEIGHT, (float) v2Px / WIDGETS_HEIGHT);
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
	public boolean isPauseScreen() {
		return false;
	}
	
	@Override
	public void closeScreen() {
		super.closeScreen();
		
		if(minecraft != null && minecraft.getConnection() != null) {
			//Tell the server that i closed the screen, so it'll stop sending me updates.
			SuperDecayingSimulator2022NetworkHandler.CHANNEL.sendToServer(
				new SuperDecayingSimulator2022NetworkHandler.C2SSetGuiStatus(false));
		}
	}
}
