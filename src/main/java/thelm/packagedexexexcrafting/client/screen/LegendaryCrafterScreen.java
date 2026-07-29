package thelm.packagedexexexcrafting.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.BlockPos;
import thelm.packagedexexexcrafting.network.Networking;
import thelm.packagedexexexcrafting.network.PacketGridClick;
import thelm.packagedexexexcrafting.network.PacketGridDrag;
import net.minecraft.client.gui.screens.Screen;
import thelm.packagedauto.client.screen.BaseScreen;
import thelm.packagedexexexcrafting.menu.LegendaryCrafterMenu;

public class LegendaryCrafterScreen extends BaseScreen<LegendaryCrafterMenu> {

    @SuppressWarnings("deprecation")
    public static final ResourceLocation BACKGROUND = new ResourceLocation("packagedexexexcrafting:textures/gui/legendary_crafter.png");

	public LegendaryCrafterScreen(LegendaryCrafterMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		imageWidth = 342;
		imageHeight = 346;
	}

    // drag state for distributing items across multiple grid slots
    private final java.util.Set<Integer> dragIndices = new java.util.HashSet<>();
    private boolean dragging = false;

	@Override
	protected ResourceLocation getBackgroundTexture() {
		return BACKGROUND;
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(graphics, partialTicks, mouseX, mouseY);
		graphics.blit(BACKGROUND, leftPos+282, topPos+125, 342, 0, menu.blockEntity.getScaledProgress(22), 16, 512, 512);
		int scaledEnergy = menu.blockEntity.getScaledEnergy(40);
		graphics.blit(BACKGROUND, leftPos+10, topPos+82+40-scaledEnergy, 342, 16+40-scaledEnergy, 12, scaledEnergy, 512, 512);
		// draw 13x13 grid items from block entity (client-side copy synced via BE NBT / custom packets)
		for(int i = 0; i < 13; ++i) {
			for(int j = 0; j < 13; ++j) {
				int idx = i*13 + j;
				int x = leftPos + 44 + j*18;
				int y = topPos + 17 + i*18;
				var stack = menu.blockEntity.getItemHandler().getStackInSlot(idx);
				if(!stack.isEmpty()) {
					graphics.renderItem(stack, x, y);
					graphics.renderItemDecorations(font, stack, x, y, null);
				}
			}
		}
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		String s = menu.blockEntity.getDisplayName().getString();
		graphics.drawString(font, s, imageWidth/2 - font.width(s)/2 - 12, 6, 0x404040, false);
		graphics.drawString(font, menu.inventory.getDisplayName().getString(), menu.getPlayerInvX(), menu.getPlayerInvY()-11, 0x404040, false);
		if(mouseX-leftPos >= 10 && mouseY-topPos >= 82 && mouseX-leftPos <= 21 && mouseY-topPos <= 121) {
			graphics.renderTooltip(font, Component.literal(menu.blockEntity.getEnergyStorage().getEnergyStored()+" / "+menu.blockEntity.getEnergyStorage().getMaxEnergyStored()+" FE"), mouseX-leftPos, mouseY-topPos);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		double x = mouseX - leftPos;
		double y = mouseY - topPos;
		if(x >= 44 && x < 44 + 13*18 && y >= 17 && y < 17 + 13*18) {
			int col = (int)((x - 44) / 18);
			int row = (int)((y - 17) / 18);
			int idx = row*13 + col;
			// send packet to server to handle grid click (include shift state)
			boolean shift = Screen.hasShiftDown();
			Networking.CHANNEL.sendToServer(new PacketGridClick(new BlockPos(menu.blockEntity.getBlockPos()), idx, button, shift));
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		double x = mouseX - leftPos;
		double y = mouseY - topPos;
		if(x >= 44 && x < 44 + 13*18 && y >= 17 && y < 17 + 13*18) {
			int col = (int)((x - 44) / 18);
			int row = (int)((y - 17) / 18);
			int idx = row*13 + col;
			dragging = true;
			dragIndices.add(idx);
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if(dragging && !dragIndices.isEmpty()) {
			int[] arr = dragIndices.stream().mapToInt(Integer::intValue).toArray();
			Networking.CHANNEL.sendToServer(new PacketGridDrag(new BlockPos(menu.blockEntity.getBlockPos()), arr, button));
			dragIndices.clear();
			dragging = false;
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
}
