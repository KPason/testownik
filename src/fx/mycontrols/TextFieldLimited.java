package fx.mycontrols;


import javafx.scene.control.TextField;

public class TextFieldLimited extends TextField {
    private int maxLength;

    public TextFieldLimited() {
        this.maxLength = 100;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public void replaceText(int start, int end, String text) {
        // Delete or backspace user input.
        if (text.equals("")) {
            super.replaceText(start, end, text);
        } else if (getText().length() < maxLength) {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String text) {
        // Delete or backspace user input.
        if (text.equals("")) {
            super.replaceSelection(text);
        } else if (getText().length() < maxLength) {
            // Add characters, but don't exceed maxLength.
            if (text.length() > maxLength - getText().length()) {
                text = text.substring(0, maxLength - getText().length());
            }
            super.replaceSelection(text);
        }
    }
}

