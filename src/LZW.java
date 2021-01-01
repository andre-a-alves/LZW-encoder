import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public abstract class LZW {
    private static final int maxSymbolTableSize = Double.valueOf(Math.pow(2,16)).intValue();

    private static class SymbolTable {
        private static int nextCodeword;
        private final Node[] roots;

        public SymbolTable() {
            roots = new Node[256];
            for (nextCodeword = 0; nextCodeword < 256; nextCodeword++) {
                roots[nextCodeword] = new Node(nextCodeword, nextCodeword);
            }
        }

        private static class Node {
            private final int shortChar, codeword;
            private final ArrayList<Node> subNodes;

            public Node(int shortChar, int codeword) {
                this.shortChar = shortChar;
                this.codeword = codeword;
                subNodes = new ArrayList<>();
            }
        }

        public int getPut(ByteBuffer inputBuffer) {
            int currentShortChar = Byte.toUnsignedInt(inputBuffer.get());

            return getPut(roots[currentShortChar], inputBuffer);
        }

        private int getPut(Node currentNode, ByteBuffer inputBuffer) {
            if (!inputBuffer.hasRemaining())
                return currentNode.codeword;
            int nextShortChar = Byte.toUnsignedInt(inputBuffer.get());
            Node nextNode = getNextNode(nextShortChar, currentNode);
            if (nextNode == null) {
                inputBuffer.position(inputBuffer.position() - 1);
                if (nextCodeword < maxSymbolTableSize)
                    currentNode.subNodes.add(new Node(nextShortChar, nextCodeword++));
                return currentNode.codeword;
            }
            return getPut(nextNode, inputBuffer);
        }

        private Node getNextNode(int nextShortChar, Node currentNode) {
            for (Node subNode : currentNode.subNodes) {
                if (subNode.shortChar == nextShortChar)
                    return subNode;
            }
            return null;
        }
    }

    public static void encode (File inputFile, File outputFile) throws IOException {
        FileChannel outputFileChannel = FileChannel.open(outputFile.toPath(), StandardOpenOption.CREATE,
                StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        ByteBuffer outputBuffer = encode(Files.readAllBytes(inputFile.toPath()));
        writeByteBufferToFile(outputBuffer, outputFileChannel);
    }

    public static void decode (File inputFile, File outputFile) throws IOException {
        FileChannel outputFileChannel = FileChannel.open(outputFile.toPath(), StandardOpenOption.CREATE,
                StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        ByteBuffer outputBuffer = decode(Files.readAllBytes(inputFile.toPath()));
        writeByteBufferToFile(outputBuffer, outputFileChannel);
    }

    private static void writeByteBufferToFile(ByteBuffer outputBuffer,
                                              FileChannel outputFileChannel) throws IOException {
        outputBuffer.flip();
        outputFileChannel.write(outputBuffer);
        outputBuffer.compact();
        outputBuffer.flip();
        while (outputBuffer.hasRemaining())
            outputFileChannel.write(outputBuffer);
        outputFileChannel.close();
    }

    public static ByteBuffer encode(byte[] inputArray) {
        ByteBuffer inputBuffer = ByteBuffer.wrap(inputArray);
        ByteBuffer outputBuffer = ByteBuffer.allocate(inputArray.length * 2 + 4);
        SymbolTable symbolTable = new SymbolTable();

        outputBuffer.putInt(inputArray.length);
        inputBuffer.rewind();
        while(inputBuffer.hasRemaining()) {
            outputBuffer.putShort((short) symbolTable.getPut(inputBuffer));
        }

        //remove me later
        ByteBuffer newOutput = ByteBuffer.allocate(outputBuffer.position());
        outputBuffer.flip();
        while (outputBuffer.hasRemaining())
            newOutput.put(outputBuffer.get());

        return newOutput;
    }

    public static ByteBuffer decode(byte[] inputArray) {
        ByteBuffer inputBuffer = ByteBuffer.wrap(inputArray);
        ByteBuffer outputBuffer;
        ArrayList<ArrayList<Byte>> symbolTable = new ArrayList<>();
        int nextCodeword;

        for (nextCodeword = 0; nextCodeword < 256; nextCodeword++) {
            ArrayList<Byte> nextList = new ArrayList<>();
            nextList.add((byte) nextCodeword);
            symbolTable.add(nextList);
        }

        inputBuffer.rewind();
        outputBuffer = ByteBuffer.allocate(inputBuffer.getInt());

        ArrayList<Byte> previous = new ArrayList<>();
        while (inputBuffer.hasRemaining()) {
            int nextShort = Short.toUnsignedInt(inputBuffer.getShort());
            ArrayList<Byte> next;
            if (nextShort == nextCodeword) {
                next = new ArrayList<>(previous);
                next.add(previous.get(0));
            } else
                next = new ArrayList<>(symbolTable.get(nextShort));
            for (byte nextByte : next)
                outputBuffer.put(nextByte);
            if (nextCodeword < maxSymbolTableSize && previous.size() != 0) {
                previous.add(next.get(0));
                symbolTable.add(previous);
                nextCodeword++;
            }
            previous = next;
        }

        return outputBuffer;
    }
}