package net.pl3x.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ITabCompleter;
import net.minecraft.util.TabCompleter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nullable;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiChatBar extends GuiScreen implements ITabCompleter {
    private String historyBuffer = "";
    private int sentHistoryCursor = -1;
    private TabCompleter tabCompleter;
    protected GuiTextField inputField;
    private String defaultInputFieldText = "";

    public GuiChatBar() {
    }

    public GuiChatBar(String defaultText) {
        defaultInputFieldText = defaultText;
    }

    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        sentHistoryCursor = mc.ingameGUI.getChatGUI().getSentMessages().size();
        inputField = new GuiTextField(0, fontRenderer, 4, height - 12, width - 4, 12);
        inputField.setMaxStringLength(256);
        inputField.setEnableBackgroundDrawing(false);
        inputField.setFocused(true);
        inputField.setText(defaultInputFieldText);
        inputField.setCanLoseFocus(false);
        tabCompleter = new ChatTabCompleter(inputField);
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        mc.ingameGUI.getChatGUI().resetScroll();
    }

    public void updateScreen() {
        inputField.updateCursorCounter();
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        tabCompleter.resetRequested();

        if (keyCode == 15) {
            tabCompleter.complete();
        } else {
            tabCompleter.resetDidComplete();
        }

        if (keyCode == 1) {
            mc.displayGuiScreen(null);
        } else if (keyCode != 28 && keyCode != 156) {
            if (keyCode == 200) {
                getSentHistory(-1);
            } else if (keyCode == 208) {
                getSentHistory(1);
            } else if (keyCode == 201) {
                mc.ingameGUI.getChatGUI().scroll(mc.ingameGUI.getChatGUI().getLineCount() - 1);
            } else if (keyCode == 209) {
                mc.ingameGUI.getChatGUI().scroll(-mc.ingameGUI.getChatGUI().getLineCount() + 1);
            } else {
                inputField.textboxKeyTyped(typedChar, keyCode);
            }
        } else {
            String s = inputField.getText().trim();

            if (!s.isEmpty()) {
                sendChatMessage(s);
            }

            mc.displayGuiScreen(null);
        }
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0) {
            if (i > 1) {
                i = 1;
            }

            if (i < -1) {
                i = -1;
            }

            if (!isShiftKeyDown()) {
                i *= 7;
            }

            mc.ingameGUI.getChatGUI().scroll(i);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            ITextComponent itextcomponent = mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

            if (itextcomponent != null && handleComponentClick(itextcomponent)) {
                return;
            }
        }

        inputField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public String getText() {
        return inputField.getText();
    }

    public void setText(String newChatText, boolean shouldOverwrite) {
        if (shouldOverwrite) {
            inputField.setText(newChatText);
        } else {
            inputField.writeText(newChatText);
        }
    }

    public void getSentHistory(int msgPos) {
        int i = sentHistoryCursor + msgPos;
        int j = mc.ingameGUI.getChatGUI().getSentMessages().size();
        i = MathHelper.clamp(i, 0, j);

        if (i != sentHistoryCursor) {
            if (i == j) {
                sentHistoryCursor = j;
                inputField.setText(historyBuffer);
            } else {
                if (sentHistoryCursor == j) {
                    historyBuffer = inputField.getText();
                }

                inputField.setText(mc.ingameGUI.getChatGUI().getSentMessages().get(i));
                sentHistoryCursor = i;
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //drawRect(2, height - 14, width - 2, height - 2, Integer.MIN_VALUE);
        drawRect(2, height - 14, width - 2, height - 2, getBGColor());
        inputField.drawTextBox();
        ITextComponent itextcomponent = mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

        if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null) {
            handleComponentHover(itextcomponent, mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void setCompletions(String... newCompletions) {
        tabCompleter.setCompletions(newCompletions);
    }

    @SideOnly(Side.CLIENT)
    public static class ChatTabCompleter extends TabCompleter {
        private final Minecraft client = Minecraft.getMinecraft();

        public ChatTabCompleter(GuiTextField p_i46749_1_) {
            super(p_i46749_1_, false);
        }

        public void complete() {
            super.complete();

            if (completions.size() > 1) {
                StringBuilder stringbuilder = new StringBuilder();

                for (String s : completions) {
                    if (stringbuilder.length() > 0) {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(s);
                }

                client.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(stringbuilder.toString()), 1);
            }
        }

        @Nullable
        public BlockPos getTargetBlockPos() {
            BlockPos blockpos = null;

            if (client.objectMouseOver != null && client.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                blockpos = client.objectMouseOver.getBlockPos();
            }

            return blockpos;
        }
    }

    public int getBGColor() {
        return (int) ((long) mc.gameSettings.chatBGAlpha << 24 |
                (int) mc.gameSettings.chatBGRed << 16 |
                (int) mc.gameSettings.chatBGGreen << 8 |
                (int) mc.gameSettings.chatBGBlue);
    }
}
