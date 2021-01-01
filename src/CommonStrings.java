public enum CommonStrings {
    VERSION("01 January 2021"),
    CONTACT("\n\nApplication author: Andre Alves\n\n" +
            "    https://www.linkedin.com/in/andre-a-alves/\n" +
            "    https://github.com/andre-a-alves\n\n" +
            "Andre is actively searching for a semester-long internship in the fall of 2021\n" +
            "as part of his information engineering (computer engineering) study program.\n\n" +
            "Version: " + VERSION.getString()),
    ABOUT("This is a small compression program implementing the LZW compression algorithm.\n" +
            "Click the first button to load a visual LZW compressor that will encode Strings\n" +
            "in a way that you can see the compressed string. Otherwise, use the other two\n" +
            "buttons to actually compress files." + CONTACT.string),
    STRING_ABOUT("This application is designed to run a text file through LZW compression\n" +
            "in a way that allows the user to see a string of the encoding. This allows\n" +
            "an opportunity to visualize the compression by playing with the input file\n" +
            "and observing the output file. Only very large text files will actually be\n" +
            "compressed to a smaller file.\n" +
            "The LZW algorithm used is a modified version of the one provided by Sedgewick\n" +
            "in Algorithms in Java: Third Edition." + CONTACT.string),
    NO_OPEN("Unable to load file. Please check the path and try again."),
    TOO_LARGE("Sorry, but that file is too large for this program to compress."),
    WRONG_FORMAT("Sorry, but you have chosen a file that is either corrupted or was\n" +
            "not compressed using this program.");

    private final String string;

    CommonStrings(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }


}
