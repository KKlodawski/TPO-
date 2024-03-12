/**
 * @author Kłodawski Kamil S24777
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client {
    String host;
    int port;
    String id;

    private SocketChannel socketChannel;

    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void connect() {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(host, port));
            while (!socketChannel.finishConnect()) {

            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String send(String req) {
        try {
            req = id+"|"+req;
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            byte[] bytes = req.getBytes(StandardCharsets.UTF_8);
            buffer.clear();
            buffer.put(bytes);
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
            Thread.sleep(50);
            socketChannel.read(buffer);
            buffer.flip();
            String s = StandardCharsets.UTF_8.decode(buffer).toString();
            StringBuilder sb = new StringBuilder(s);
            sb.deleteCharAt(sb.length()-1);
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
