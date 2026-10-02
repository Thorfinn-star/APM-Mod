package fr.chatgpt.overleveltooltip.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ClientTextTooltip.class)
public abstract class ClientTextTooltipMixin {
    @Unique private static final int OLT_RED = 0xFFE53935;
    @Unique private static final int OLT_HORIZONTAL_PADDING = 2;
    @Unique private static final int OLT_VERTICAL_PADDING = 1;

    @Unique
    private static final Pattern OLT_LEVEL_PATTERN =
            Pattern.compile("^(.+?)\\s+(I|II|III|IV|V|VI|VII|VIII|IX|X)$");

    @Unique
    private static final Map<String, Integer> OLT_MAX_LEVELS = Map.ofEntries(
            Map.entry("aqua affinity", 1),
            Map.entry("bane of arthropods", 5),
            Map.entry("blast protection", 4),
            Map.entry("breach", 4),
            Map.entry("channeling", 1),
            Map.entry("curse of binding", 1),
            Map.entry("curse of vanishing", 1),
            Map.entry("density", 5),
            Map.entry("depth strider", 3),
            Map.entry("efficiency", 5),
            Map.entry("feather falling", 4),
            Map.entry("fire aspect", 2),
            Map.entry("fire protection", 4),
            Map.entry("flame", 1),
            Map.entry("fortune", 3),
            Map.entry("frost walker", 2),
            Map.entry("impaling", 5),
            Map.entry("infinity", 1),
            Map.entry("knockback", 2),
            Map.entry("looting", 3),
            Map.entry("loyalty", 3),
            Map.entry("luck of the sea", 3),
            Map.entry("lunge", 3),
            Map.entry("lure", 3),
            Map.entry("mending", 1),
            Map.entry("multishot", 1),
            Map.entry("piercing", 4),
            Map.entry("power", 5),
            Map.entry("projectile protection", 4),
            Map.entry("protection", 4),
            Map.entry("punch", 2),
            Map.entry("quick charge", 3),
            Map.entry("respiration", 3),
            Map.entry("riptide", 3),
            Map.entry("sharpness", 5),
            Map.entry("silk touch", 1),
            Map.entry("smite", 5),
            Map.entry("soul speed", 3),
            Map.entry("sweeping edge", 3),
            Map.entry("swift sneak", 3),
            Map.entry("thorns", 3),
            Map.entry("unbreaking", 3),
            Map.entry("wind burst", 3)
    );

    @Shadow @Final
    private FormattedCharSequence text;

    @Inject(method = "extractText", at = @At("HEAD"))
    private void overlevelTooltip$drawBadge(
            GuiGraphicsExtractor graphics,
            Font font,
            int x,
            int y,
            CallbackInfo ci
    ) {
        String line = overlevelTooltip$plainText(this.text);
        Matcher matcher = OLT_LEVEL_PATTERN.matcher(line);

        if (!matcher.matches()) {
            return;
        }

        String enchantment = matcher.group(1).trim().toLowerCase();
        Integer maxLevel = OLT_MAX_LEVELS.get(enchantment);
        if (maxLevel == null) {
            return;
        }

        int actualLevel = overlevelTooltip$romanToInt(matcher.group(2));
        if (actualLevel <= maxLevel) {
            return;
        }

        String prefix = line.substring(0, line.lastIndexOf(' ') + 1);
        String roman = matcher.group(2);

        int left = x + font.width(prefix) - OLT_HORIZONTAL_PADDING;
        int right = x + font.width(prefix + roman) + OLT_HORIZONTAL_PADDING;
        int top = y - OLT_VERTICAL_PADDING;
        int bottom = y + font.lineHeight - 1 + OLT_VERTICAL_PADDING;

        overlevelTooltip$roundedFill(graphics, left, top, right, bottom, OLT_RED);
    }

    @Unique
    private static String overlevelTooltip$plainText(FormattedCharSequence sequence) {
        StringBuilder out = new StringBuilder();

        sequence.accept((position, style, codepoint) -> {
            out.appendCodePoint(codepoint);
            return true;
        });

        return out.toString();
    }

    @Unique
    private static int overlevelTooltip$romanToInt(String roman) {
        return switch (roman) {
            case "I" -> 1;
            case "II" -> 2;
            case "III" -> 3;
            case "IV" -> 4;
            case "V" -> 5;
            case "VI" -> 6;
            case "VII" -> 7;
            case "VIII" -> 8;
            case "IX" -> 9;
            case "X" -> 10;
            default -> 0;
        };
    }

    @Unique
    private static void overlevelTooltip$roundedFill(
            GuiGraphicsExtractor graphics,
            int left,
            int top,
            int right,
            int bottom,
            int color
    ) {
        int radius = Math.min(2, Math.max(1, (bottom - top) / 2));

        graphics.fill(left + radius, top, right - radius, bottom, color);
        graphics.fill(left, top + radius, right, bottom - radius, color);
    }
}
