package org.example.client.gui.widgets;

import org.example.client.Client;
import org.example.client.gui.GuiGraphics;
import org.example.client.gui.element.Focusable;

import com.raylib.Colors;
import com.raylib.Raylib;

public class TextInputWidget extends AbstractWidget {
    private String text = "";
    private String visibleText = "";

    private int cursorPosition = 0;
    private int selectionAnchor = 0;
    private int scrollOffset = 0;

    public TextInputWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY) {
        // TODO: RENDER A CURSOR AT THE END OF THE BOX AND HAVE SELECTED TEXT BE HIGHLIGHTED

        if (this.isFocused()) {
            graphics.rectangle(this.x, this.y, this.width, this.height, Colors.BLACK); // TEMPORARY FOR DEBUGGING
        } else {
            graphics.rectangle(this.x, this.y, this.width, this.height, Colors.WHITE);
        }

        graphics.text(
                this.visibleText,
                this.x + 4,
                this.y + (TextWidget.getTextHeight(this.text) / 2),
                Colors.BLUE
        );

        graphics.border(this.x, this.y, this.width, this.height);
    }

    @Override
    public void onClick(int mouseX, int mouseY) {
        super.onClick(mouseX, mouseY);

        this.setFocused(true);
    }

    @Override
    public void onKeyPressed(int code) {
        if (!this.isFocused() || this.isInvalidKey(code)) {
            return;
        }

        boolean isCtrlDown = Raylib.IsKeyDown(Raylib.KEY_LEFT_CONTROL) || Raylib.IsKeyDown(Raylib.KEY_RIGHT_CONTROL);
        boolean isCmdDown = Raylib.IsKeyDown(Raylib.KEY_LEFT_SUPER) || Raylib.IsKeyDown(Raylib.KEY_RIGHT_SUPER);
        boolean isShiftDown = Raylib.IsKeyDown(Raylib.KEY_LEFT_SHIFT) || Raylib.IsKeyDown(Raylib.KEY_RIGHT_SHIFT);
        boolean isModifierActive = isCtrlDown || isCmdDown;

        if (isModifierActive) {
            if (code == Raylib.KEY_A) {
                this.selectionAnchor = 0;
                this.cursorPosition = this.text.length();

                return;
            }

            return;
        }

        switch (code) {
            case Raylib.KEY_LEFT -> {
                if (isShiftDown) {
                    if (this.cursorPosition > 0) {
                        this.cursorPosition--;
                    }
                } else {
                    if (hasSelection()) {
                        this.cursorPosition = Math.min(this.cursorPosition, this.selectionAnchor);
                    } else if (this.cursorPosition > 0) {
                        this.cursorPosition--;
                    }

                    this.selectionAnchor = this.cursorPosition;
                }
            }
            case Raylib.KEY_RIGHT -> {
                if (isShiftDown) {
                    if (this.cursorPosition < this.text.length()) {
                        this.cursorPosition++;
                    }
                } else {
                    if (hasSelection()) {
                        this.cursorPosition = Math.max(this.cursorPosition, this.selectionAnchor);
                    } else if (this.cursorPosition < this.text.length()) {
                        this.cursorPosition++;
                    }

                    this.selectionAnchor = this.cursorPosition;
                }
            }
            case Raylib.KEY_BACKSPACE -> {
                if (hasSelection()) {
                    deleteSelection();
                } else if (this.cursorPosition > 0) {
                    this.text = this.text.substring(0, this.cursorPosition - 1) + this.text.substring(this.cursorPosition);
                    this.cursorPosition--;
                    this.selectionAnchor = this.cursorPosition;
                }
            }
            case Raylib.KEY_SPACE -> {
                if (hasSelection()) {
                    deleteSelection();
                }

                this.text = this.text.substring(0, this.cursorPosition) + " " + this.text.substring(this.cursorPosition);
                this.cursorPosition++;
                this.selectionAnchor = this.cursorPosition;
            }
            default -> {
                String keyName = Raylib.GetKeyName(code).getString();

                if (keyName == null || keyName.isEmpty() || keyName.equals("?")) {
                    return;
                }

                if (keyName.length() == 1 && Character.isLetter(keyName.charAt(0))) {
                    keyName = isShiftDown ? keyName.toUpperCase() : keyName.toLowerCase();
                } else if (isShiftDown) {
                    keyName = switch (keyName) {
                        case "1" -> "!";
                        case "2" -> "@";
                        case "3" -> "#";
                        case "4" -> "$";
                        case "5" -> "%";
                        case "6" -> "^";
                        case "7" -> "&";
                        case "8" -> "*";
                        case "9" -> "(";
                        case "0" -> ")";
                        case "-" -> "_";
                        case "=" -> "+";
                        case "[" -> "{";
                        case "]" -> "}";
                        case "\\" -> "|";
                        case ";" -> ":";
                        case "'" -> "\"";
                        case "," -> "<";
                        case "." -> ">";
                        case "/" -> "?";
                        case "`" -> "~";
                        default -> keyName;
                    };
                }

                if (hasSelection()) {
                    deleteSelection();
                }

                this.text = this.text.substring(0, this.cursorPosition) + keyName + this.text.substring(this.cursorPosition);
                this.cursorPosition += keyName.length();
                this.selectionAnchor = this.cursorPosition;
            }
        }

        this.updateVisibleText();
    }

    public String getText() {
        return this.text;
    }

    private boolean hasSelection() {
        return this.cursorPosition != this.selectionAnchor;
    }

    private void deleteSelection() {
        if (!hasSelection()) {
            return;
        }

        int start = Math.min(this.cursorPosition, this.selectionAnchor);
        int end = Math.max(this.cursorPosition, this.selectionAnchor);

        this.text = this.text.substring(0, start) + this.text.substring(end);
        this.cursorPosition = start;
        this.selectionAnchor = start;
    }

    private void updateVisibleText() {
        int maxTextWidth = this.width - 8;

        this.cursorPosition = Math.clamp(this.cursorPosition, 0, this.text.length());
        this.selectionAnchor = Math.clamp(this.selectionAnchor, 0, this.text.length());

        if (TextWidget.getTextWidth(this.text) <= maxTextWidth) {
            this.scrollOffset = 0;
            this.visibleText = this.text;

            return;
        }

        this.scrollOffset = Math.clamp(this.scrollOffset, 0, this.text.length());

        if (this.cursorPosition < this.scrollOffset) {
            this.scrollOffset = this.cursorPosition;
        }

        int end = this.scrollOffset;

        while (end < this.text.length() && TextWidget.getTextWidth(this.text.substring(this.scrollOffset, end + 1)) <= maxTextWidth) {
            end++;
        }

        while (this.cursorPosition > end) {
            this.scrollOffset++;
            end = this.scrollOffset;

            while (end < this.text.length() && TextWidget.getTextWidth(this.text.substring(this.scrollOffset, end + 1)) <= maxTextWidth) {
                end++;
            }
        }

        while (this.scrollOffset > 0) {
            int testEnd = this.scrollOffset - 1;

            while (testEnd < this.text.length() && TextWidget.getTextWidth(this.text.substring(this.scrollOffset - 1, testEnd + 1)) <= maxTextWidth) {
                testEnd++;
            }

            if (this.cursorPosition <= testEnd) {
                this.scrollOffset--;
            } else {
                break;
            }
        }

        end = this.scrollOffset;

        while (end < this.text.length() && TextWidget.getTextWidth(this.text.substring(this.scrollOffset, end + 1)) <= maxTextWidth) {
            end++;
        }

        this.visibleText = this.text.substring(this.scrollOffset, end);
    }

    private boolean isInvalidKey(int code) {
        return code == Raylib.KEY_ENTER || Raylib.KEY_TAB == code || Raylib.KEY_LEFT_CONTROL == code ||
                Raylib.KEY_RIGHT_CONTROL == code || Raylib.KEY_LEFT_SHIFT == code ||
                Raylib.KEY_RIGHT_SHIFT == code || Raylib.KEY_LEFT_ALT == code ||
                Raylib.KEY_RIGHT_ALT == code || Raylib.KEY_LEFT_SUPER == code ||
                Raylib.KEY_RIGHT_SUPER == code || Raylib.KEY_INSERT == code ||
                Raylib.KEY_HOME == code || Raylib.KEY_PAGE_UP == code ||
                Raylib.KEY_DELETE == code || Raylib.KEY_END == code ||
                Raylib.KEY_PAGE_DOWN == code || Raylib.KEY_PRINT_SCREEN == code;
    }
}