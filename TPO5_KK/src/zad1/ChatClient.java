/**
 * @author Kłodawski Kamil S24777
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ChatClient {

    private String host;
    private int port;
    private String id;
    private SocketChannel socketChannel;

    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    private String respMsg = "";

    public ChatClient() {
    }

    public ChatClient(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void login() throws IOException, InterruptedException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(host, port));
        while (!socketChannel.finishConnect()) {

        };
        send("login");
    }

    public void logout() throws IOException, InterruptedException {
        send("logout");
        socketChannel.close();
    }

    public void send(String req) throws IOException, InterruptedException {
        req = id+"|"+req;
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
        respMsg = sb.toString();

    }
    public String getChatView() {
        return respMsg;
    }
}
