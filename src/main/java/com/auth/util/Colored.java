package com.auth.util;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.text.MutableText;
import com.auth.exception.MalformedParsedString;



/*
Message example:

¤1&biu&Hello!

This will produce a dark blue, bold, italic, and underlined "Hello!".

"¤" is the color code prefix, followed by a character representing the color.
The "&" character indicates the start of formatting options, which can include:
- b: bold
- i: italic
- u: underlined
- s: strikethrough
- o: obfuscated (random characters)
The text after the second "&" character is the actual message.

the "&" arguments are optional, so you can have just a color code without any formatting. But if you want
to use any "&" arguments, you must have a color code before it. MUST.
*/

/*Color List: 
0: BLACK
1: DARK_BLUE
2: DARK_GREEN
3: DARK_AQUA
4: DARK_RED
5: DARK_PURPLE
6: GOLD
7: GRAY
8: DARK_GRAY
9: BLUE
a: GREEN
b: AQUA
c: RED
d: LIGHT_PURPLE
e: YELLOW
f: WHITE
* : RAINBOW (will cycle through the colors in the color list)
*/

/**
 * @param String message
 * @return A MutableText object representing the parsed message.
 * @throws MalformedParsedString If the message is malformed.
 * @example
 * <pre>
 * MutableText text = Colored.parse("¤1&biu&Hello!");
 * </pre>
 * This will produce a dark blue, bold, italic, and underlined "Hello!".
 * @note For color codes and more info, see the class file comments.
 */
public class Colored {
    public static MutableText parse(String message) throws MalformedParsedString {
    
        MutableText coloredMessage = Text.literal("");
        String[] parts = message.split("¤"); //means we are starting a new colored section. the next char will be the color code, then we have "&" to idicate if 
        //it is bold, italic, obfuscated, strikethrough, or underline. Example: "¤1&biu&Hello!" will be dark blue bold italic underlined "Hello!". the text betwenn the "&" will say if it is bold, italic, obfuscated, strikethrough, or underline.
        try {
            for (var part : parts) {
                boolean isBold = false;
                boolean isItalic = false;
                boolean isObfuscated = false;
                boolean isStrikethrough = false;
                boolean isUnderlined = false;

                if (part.isEmpty()) continue; // Skip empty parts

                String textPart = "";
                String fmt = ""; // Formatting string to hold styles like "biu" for bold, italic, underlined, etc.
                char colorCode = part.charAt(0);
                if(part.length() > 1){
                    if(part.charAt(1) == '&') {
                        String[] subParts = part.split("&");
                        if (subParts.length < 3) throw new MalformedParsedString("Malformed format section: " + part);

                        fmt = subParts[1];
                        textPart = subParts[2];

                        isBold          = fmt.contains("b");
                        isItalic        = fmt.contains("i");
                        isObfuscated    = fmt.contains("o");
                        isStrikethrough = fmt.contains("s");
                        isUnderlined    = fmt.contains("u");
                    } else {
                        // If the second character is not '&' or '*', treat it as a normal text part
                        textPart = part.substring(1); // Skip the color code
                    }

                    if(colorCode == '*') {
                        // If the color code is '*', treat it as a rainbow text
                        coloredMessage.append(rainbow(textPart, fmt));
                        continue; // Skip the rest of the loop for this part
                    }

                } else {
                    textPart = part.substring(1);
                }

                Formatting color = switch (colorCode) {
                    case '0' -> Formatting.BLACK;
                    case '1' -> Formatting.DARK_BLUE;
                    case '2' -> Formatting.DARK_GREEN;
                    case '3' -> Formatting.DARK_AQUA;
                    case '4' -> Formatting.DARK_RED;
                    case '5' -> Formatting.DARK_PURPLE;
                    case '6' -> Formatting.GOLD;
                    case '7' -> Formatting.GRAY;
                    case '8' -> Formatting.DARK_GRAY;
                    case '9' -> Formatting.BLUE;
                    case 'a' -> Formatting.GREEN;
                    case 'b' -> Formatting.AQUA;
                    case 'c' -> Formatting.RED;
                    case 'd' -> Formatting.LIGHT_PURPLE;
                    case 'e' -> Formatting.YELLOW;
                    case 'f' -> Formatting.WHITE;
                    default -> null; // No color
                };

                if (color != null) {
                    var baseStyle = net.minecraft.text.Style.EMPTY
                            .withColor(color)
                            .withBold(isBold)
                            .withItalic(isItalic)
                            .withObfuscated(isObfuscated)
                            .withStrikethrough(isStrikethrough)
                            .withUnderline(isUnderlined);
                    coloredMessage.append(Text.literal(textPart).setStyle(baseStyle));
                } else {
                    coloredMessage.append(Text.literal(part)); // Append without color
                }
            }
        } catch (Exception e) {
            throw new MalformedParsedString(e.getMessage(), e);
        }

        return coloredMessage;
    }

    private static MutableText rainbow(String str, String styles) {
        MutableText rainbowText = Text.literal("");
        Formatting[] colorFormats = {
            Formatting.RED, Formatting.GOLD, Formatting.GREEN, Formatting.AQUA, Formatting.BLUE, Formatting.LIGHT_PURPLE
        };

        boolean isBold = false;
        boolean isItalic = false;
        boolean isUnderlined = false;
        boolean isStrikethrough = false;
        boolean isObfuscated = false;

        if (styles != null && !styles.isEmpty()) {
            if (styles.contains("b")) isBold = true;
            if (styles.contains("i")) isItalic = true;
            if (styles.contains("u")) isUnderlined = true;
            if (styles.contains("s")) isStrikethrough = true;
            if (styles.contains("o")) isObfuscated = true;
        }

        int colorIndex = 0;
        for (char c : str.toCharArray()) {
            net.minecraft.text.Style style = net.minecraft.text.Style.EMPTY
                .withColor(colorFormats[colorIndex % colorFormats.length])
                .withBold(isBold)
                .withItalic(isItalic)
                .withUnderline(isUnderlined)
                .withStrikethrough(isStrikethrough)
                .withObfuscated(isObfuscated);

            rainbowText.append(Text.literal(String.valueOf(c)).setStyle(style));
            colorIndex++;
        }

        return rainbowText;
    }
}
