import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.IntStream;

public abstract class StringLZW {

    public static StringBuilder encode(String string) {
        StringBuilder output = new StringBuilder();
        LookAheadIn inputStream = new LookAheadIn(string);

        LZWst symbolTable = new LZWst();

        while (!inputStream.isEmpty()) {
            int codeword = symbolTable.getput(inputStream);
            output.append(String.format("%06d", codeword));
        }

        return output;
    }

    public static void encode(File inputFile, File outputFile) throws IOException {
        Files.writeString(outputFile.toPath(), encode(Files.readString(inputFile.toPath())).toString());
    }

    public static void decode(File inputFile, File outputFile) throws IOException {
        Files.writeString(outputFile.toPath(), decode(Files.readString(inputFile.toPath())).toString());
    }

    public static StringBuilder decode(String encodedString) {
        StringBuilder output = new StringBuilder();
        LinkedList<String> encodedIntegers = decodeString(encodedString);
        ArrayList<String> st = new ArrayList<>();
        int i;
        for (i = 0; i < 65536; i++)
            st.add(Character.toString((char) i));

        String prev = "";
        while (!encodedIntegers.isEmpty()) {
            int codeword = Integer.parseInt(encodedIntegers.removeFirst());
            String s;
            if (codeword == i)
                s = prev + prev.charAt(0);
            else s = st.get(codeword);
            output.append(s);
            if (prev.length() > 0){
                st.add(prev + s.charAt(0));
                i++;
            }
            prev = s;
        }
        return output;
    }

    private static LinkedList<String> decodeString(String encodedString) {
        LinkedList<String> partedString = linkedListString(encodedString);
        partedString.removeLast();
        LinkedList<String> decodeString = new LinkedList<>();
        while (!partedString.isEmpty()) {
            StringBuilder nextKey = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                nextKey.append(partedString.removeFirst());
            }
            decodeString.add(nextKey.toString());
        }
        return decodeString;
    }

    private static LinkedList<String> linkedListString(String string) {
        LinkedList<String> stringQueue = new LinkedList<>();
        IntStream stringStream = string.chars();
        stringStream.forEach((e) -> stringQueue.add(String.valueOf((char)e)));
        stringQueue.add("lastChar");
        return stringQueue;
    }

    private static class LookAheadIn {
        LinkedList<String> in;
        char last;
        boolean backup = false;

        LookAheadIn(String string) {
            in = linkedListString(string);
        }

        public void backup() {
            backup = true;
        }

        public char readChar() {
            if (!backup)
                last = in.removeFirst().charAt(0);
            backup = false;
            return last;
        }

        public boolean isEmpty() {
            return in.isEmpty();
        }
    }

    private static class LZWst {
        private int i = 65536;
        private int codeword;
        private final ArrayList<Node> roots;

        public LZWst() {
            roots = new ArrayList<>();
            for (int i = 0; i < 65536; i++)
                roots.add(new Node((char) i, i));
        }

        private static class Node {
            private final char character;
            private final int codeword;
            Node left, mid, right;

            Node(char character, int codeword) {
                this.character = character;
                this.codeword = codeword;
            }
        }

        public int getput(LookAheadIn inputStream) {
            char c = inputStream.readChar();
            roots.set(c, getput(c, roots.get(c), inputStream));
            inputStream.backup();
            return codeword;
        }

        public Node getput(char c, Node x, LookAheadIn in) {
            if (x == null) {
                x = new Node(c, i++);
                return x;
            }
            if (c < x.character) x.left = getput(c, x.left, in);
            else if (c > x.character) x.right = getput(c, x.right, in);
            else {
                if (!in.isEmpty()) {
                    char next = in.readChar();
                    codeword = x.codeword;
                    x.mid = getput(next, x.mid, in);
                }
            }
            return x;
        }
    }
}
