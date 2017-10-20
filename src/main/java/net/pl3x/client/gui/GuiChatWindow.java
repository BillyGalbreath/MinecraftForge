package net.pl3x.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiChatWindow extends Gui {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Minecraft mc;
    private final List<String> sentMessages = Lists.newArrayList();
    private final List<ChatLine> chatLines = Lists.newArrayList();
    private final List<ChatLine> drawnChatLines = Lists.newArrayList();
    private int scrollPos;
    private boolean isScrolled;

    public GuiChatWindow(Minecraft mcIn) {
        mc = mcIn;
    }

    public void drawChat(int updateCounter) {
        if (mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int totalLines = getLineCount();
            int drawnLines = drawnChatLines.size();

            float chatOpacity = mc.gameSettings.chatOpacity;
            int a = ((int) mc.gameSettings.chatBGAlpha);
            int r = ((int) mc.gameSettings.chatBGRed);
            int g = ((int) mc.gameSettings.chatBGGreen);
            int b = ((int) mc.gameSettings.chatBGBlue);

            if (drawnLines > 0) {
                boolean chatOpen = getChatOpen();

                float chatScale = getChatScale();
                int chatWidth = MathHelper.ceil((float) getChatWidth() / chatScale);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 8.0F, 0.0F);
                GlStateManager.scale(chatScale, chatScale, 1.0F);
                int lineCounter = 0;

                for (int lineNum = 0; lineNum + scrollPos < drawnChatLines.size() && lineNum < totalLines; ++lineNum) {
                    ChatLine chatline = drawnChatLines.get(lineNum + scrollPos);

                    if (chatline != null) {
                        int j1 = updateCounter - chatline.getUpdatedCounter();

                        if (j1 < 200 || chatOpen) {
                            double d0 = MathHelper.clamp((1.0D - ((double) j1 / 200.0D)) * 10.0D, 0.0D, 1.0D);
                            int fadeFactor = chatOpen ? 255 : (int) (255D * d0 * d0);
                            ++lineCounter;
                            if (fadeFactor > 3) {
                                int j2 = -lineNum * 9;
                                drawRect(-2, j2 - 9, chatWidth + 4, j2, (int) (((long) a * fadeFactor / 255) << 24 | r << 16 | g << 8 | b));
                                GlStateManager.enableBlend();
                                mc.fontRenderer.drawStringWithShadow(chatline.getChatComponent().getFormattedText(),
                                        0.0F, (float) (j2 - 8), 16777215 + ((int) (fadeFactor * chatOpacity) << 24));
                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
                            }
                        }
                    }
                }

                if (chatOpen) {
                    int fontHeight = mc.fontRenderer.FONT_HEIGHT;
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int l2 = drawnLines * fontHeight + drawnLines;
                    int visibleLines = lineCounter * fontHeight + lineCounter;
                    int j3 = scrollPos * visibleLines / drawnLines;
                    int k1 = visibleLines * visibleLines / l2;

                    if (l2 != visibleLines) {
                        int scrollbarAlpha = j3 > 0 ? 170 : 96;
                        int scrollbarColor = isScrolled ? 13382451 : 3355562;
                        drawRect(0, -j3, 2, -j3 - k1, scrollbarColor + (scrollbarAlpha << 24));
                        drawRect(2, -j3, 1, -j3 - k1, 13421772 + (scrollbarAlpha << 24));
                    }
                }

                GlStateManager.popMatrix();
            }
        }
    }

    public void clearChatMessages(boolean p_146231_1_) {
        drawnChatLines.clear();
        chatLines.clear();

        if (p_146231_1_) {
            sentMessages.clear();
        }
    }

    public void printChatMessage(ITextComponent chatComponent) {
        printChatMessageWithOptionalDeletion(chatComponent, 0);
    }

    public void printChatMessageWithOptionalDeletion(ITextComponent chatComponent, int chatLineId) {
        setChatLine(chatComponent, chatLineId, mc.ingameGUI.getUpdateCounter(), false);
        LOGGER.info("[CHAT] {}", chatComponent.getUnformattedText().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    private void setChatLine(ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        if (chatLineId != 0) {
            deleteChatLine(chatLineId);
        }

        int i = MathHelper.floor((float) getChatWidth() / getChatScale());
        List<ITextComponent> list = GuiUtilRenderComponents.splitText(chatComponent, i, mc.fontRenderer, false, false);
        boolean flag = getChatOpen();

        for (ITextComponent itextcomponent : list) {
            if (flag && scrollPos > 0) {
                isScrolled = true;
                scroll(1);
            }

            drawnChatLines.add(0, new ChatLine(updateCounter, itextcomponent, chatLineId));
        }

        while (drawnChatLines.size() > 100) {
            drawnChatLines.remove(drawnChatLines.size() - 1);
        }

        if (!displayOnly) {
            chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));

            while (chatLines.size() > 100) {
                chatLines.remove(chatLines.size() - 1);
            }
        }
    }

    public void refreshChat() {
        drawnChatLines.clear();
        resetScroll();

        for (int i = chatLines.size() - 1; i >= 0; --i) {
            ChatLine chatline = chatLines.get(i);
            setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
        }
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }

    public void addToSentMessages(String message) {
        if (sentMessages.isEmpty() || !sentMessages.get(sentMessages.size() - 1).equals(message)) {
            sentMessages.add(message);
        }
    }

    public void resetScroll() {
        scrollPos = 0;
        isScrolled = false;
    }

    public void scroll(int amount) {
        scrollPos += amount;
        int i = drawnChatLines.size();

        if (scrollPos > i - getLineCount()) {
            scrollPos = i - getLineCount();
        }

        if (scrollPos <= 0) {
            scrollPos = 0;
            isScrolled = false;
        }
    }

    @Nullable
    public ITextComponent getChatComponent(int mouseX, int mouseY) {
        if (!getChatOpen()) {
            return null;
        } else {
            ScaledResolution scaledresolution = new ScaledResolution(mc);
            int i = scaledresolution.getScaleFactor();
            float f = getChatScale();
            int j = mouseX / i - 2;
            int k = mouseY / i - 40;
            j = MathHelper.floor((float) j / f);
            k = MathHelper.floor((float) k / f);

            if (j >= 0 && k >= 0) {
                int l = Math.min(getLineCount(), drawnChatLines.size());

                if (j <= MathHelper.floor((float) getChatWidth() / getChatScale()) && k < mc.fontRenderer.FONT_HEIGHT * l + l) {
                    int i1 = k / mc.fontRenderer.FONT_HEIGHT + scrollPos;

                    if (i1 >= 0 && i1 < drawnChatLines.size()) {
                        ChatLine chatline = drawnChatLines.get(i1);
                        int j1 = 0;

                        for (ITextComponent itextcomponent : chatline.getChatComponent()) {
                            if (itextcomponent instanceof TextComponentString) {
                                j1 += mc.fontRenderer.getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured(((TextComponentString) itextcomponent).getText(), false));

                                if (j1 > j) {
                                    return itextcomponent;
                                }
                            }
                        }
                    }

                    return null;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public boolean getChatOpen() {
        return mc.currentScreen instanceof GuiChat;
    }

    public void deleteChatLine(int id) {
        Iterator<ChatLine> iterator = drawnChatLines.iterator();

        while (iterator.hasNext()) {
            ChatLine chatline = iterator.next();

            if (chatline.getChatLineID() == id) {
                iterator.remove();
            }
        }

        iterator = chatLines.iterator();

        while (iterator.hasNext()) {
            ChatLine chatline1 = iterator.next();

            if (chatline1.getChatLineID() == id) {
                iterator.remove();
                break;
            }
        }
    }

    public int getChatWidth() {
        return calculateChatboxWidth(mc.gameSettings.chatWidth);
    }

    public int getChatHeight() {
        return calculateChatboxHeight(getChatOpen() ? mc.gameSettings.chatHeightFocused : mc.gameSettings.chatHeightUnfocused);
    }

    public float getChatScale() {
        return mc.gameSettings.chatScale;
    }

    public static int calculateChatboxWidth(float scale) {
        return Math.max(MathHelper.floor(scale * 280.0F + 40.0F), new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() / 2);
    }

    public static int calculateChatboxHeight(float scale) {
        return MathHelper.floor(scale * 160.0F + 20.0F);
    }

    public int getLineCount() {
        return getChatHeight() / 9;
    }

    // obfuscation helpers (minimizes diff to patches)
    public void func_146227_a(ITextComponent chatComponent) {
        printChatMessage(chatComponent);
    }

    public void func_146231_a(boolean p_146231_1_) {
        clearChatMessages(p_146231_1_);
    }

    public void func_146239_a(String message) {
        addToSentMessages(message);
    }

    public List<String> func_146238_c() {
        return getSentMessages();
    }

    public void func_146240_d() {
        resetScroll();
    }

    public void func_146229_b(int amount) {
        scroll(amount);
    }

    public ITextComponent func_146236_a(int mouseX, int mouseY) {
        return getChatComponent(mouseX, mouseY);
    }

    public void func_146234_a(ITextComponent chatComponent, int chatLineId) {
        printChatMessageWithOptionalDeletion(chatComponent, chatLineId);
    }

    public int func_146232_i() {
        return getLineCount();
    }

    public void func_146245_b() {
        refreshChat();
    }
}
