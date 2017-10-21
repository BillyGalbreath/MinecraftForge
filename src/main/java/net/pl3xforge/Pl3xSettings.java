package net.pl3xforge;

import net.minecraft.client.Minecraft;

public class Pl3xSettings {
    public static final Pl3xSettings INSTANCE = new Pl3xSettings();
    private static Minecraft mc;

    public int chatBGRed = 0x00;
    public int chatBGGreen = 0x33;
    public int chatBGBlue = 0x66;
    public int chatBGAlpha = 0x80;

    public Pl3xSettings() {
        mc = Minecraft.getMinecraft();
    }

    public int getBGColor() {
        return (int) ((long) chatBGAlpha << 24 | chatBGRed << 16 | chatBGGreen << 8 | chatBGBlue);
    }

    /*
    public void setOptionValue(GameSettings.Options settingsOption, int value) {
        if (settingsOption == GameSettings.Options.CHAT_BG_RED) {
            chatBGRed = value;
            mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.CHAT_BG_GREEN) {
            chatBGGreen = value;
            mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.CHAT_BG_BLUE) {
            chatBGBlue = value;
            mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (settingsOption == GameSettings.Options.CHAT_BG_ALPHA) {
            chatBGAlpha = value;
            mc.ingameGUI.getChatGUI().refreshChat();
        }
    }

    public float getOptionFloatValue(GameSettings.Options settingOption) {
        if (settingOption == GameSettings.Options.CHAT_BG_RED) {
            return chatBGRed;
        } else if (settingOption == GameSettings.Options.CHAT_BG_GREEN) {
            return chatBGGreen;
        } else if (settingOption == GameSettings.Options.CHAT_BG_BLUE) {
            return chatBGBlue;
        } else if (settingOption == GameSettings.Options.CHAT_BG_ALPHA) {
            return chatBGAlpha;
        }
        return 0;
    }

    public String getKeyBinding(GameSettings.Options settingOption) {
        if (settingOption == GameSettings.Options.CHAT_BG_RED) {
            return s + "#" + floatToHex4(chatBGRed) + " (" + (int) chatBGRed + ")";
        } else if (settingOption == GameSettings.Options.CHAT_BG_GREEN) {
            return s + "#" + floatToHex4(chatBGGreen) + " (" + (int) chatBGGreen + ")";
        } else if (settingOption == GameSettings.Options.CHAT_BG_BLUE) {
            return s + "#" + floatToHex4(chatBGBlue) + " (" + (int) chatBGBlue + ")";
        } else if (settingOption == GameSettings.Options.CHAT_BG_ALPHA) {
            return s + "#" + floatToHex4(chatBGAlpha) + " (" + (int) chatBGAlpha + ")";
        }
    }

    public void loadOptions() {
        if ("chatBGRed".equals(s1)) {
            this.chatBGRed = parseFloat(s2);
        }

        if ("chatBGGreen".equals(s1)) {
            this.chatBGGreen = parseFloat(s2);
        }

        if ("chatBGBlue".equals(s1)) {
            this.chatBGBlue = parseFloat(s2);
        }

        if ("chatBGAlpha".equals(s1)) {
            this.chatBGAlpha = parseFloat(s2);
        }
    }

    public void saveOptions() {
        printwriter.println("chatBGRed:" + this.chatBGRed);
        printwriter.println("chatBGGreen:" + this.chatBGGreen);
        printwriter.println("chatBGBlue:" + this.chatBGBlue);
        printwriter.println("chatBGAlpha:" + this.chatBGAlpha);
    }
    */

    public static String floatToHex4(float f) {
        return (f <= 16 ? "0" : "") + Integer.toString((int) f, 16);
    }
}
