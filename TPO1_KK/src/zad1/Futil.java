package zad1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Futil {

    static void processDir(String dir, String result) {

        Path directory = Paths.get(dir);
        Path resultFile = Paths.get(result);
        Charset input = Charset.forName("Cp1250");
        Charset output = StandardCharsets.UTF_8;
        try {
            Files.walkFileTree(directory, new FileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    FileInputStream in = new FileInputStream(file.toFile());
                    FileOutputStream out = new FileOutputStream(resultFile.toFile(), true);

                    FileChannel fcin = in.getChannel();
                    FileChannel fcout = out.getChannel();

                    ByteBuffer bf = ByteBuffer.allocate((int) fcin.size());

                    fcin.read(bf);
                    bf.flip();
                    CharBuffer cb = input.decode(bf);
                    ByteBuffer obf  = output.encode(cb);
                    fcout.write(obf);

                    in.close();
                    out.close();

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
