This is just a simple LZW compression program. It is made up of two separate parts:

1. The first part is not designed to actually compress files. Instead, it is designed 
    to apply the LZW compression algorithm to the string value of a text file. This
    gives the user the opportunity to visually see how the algorithm encodes by playing
    with the input file and observing the output.

2. The second part is an actual LZW compression and decompression application.

Part one is a modified version of the code provided by Sedgewick to accompany Algorithms
in Java: Volume Three. The second part was written from scratch but has similarities to
the approach used in part one.
