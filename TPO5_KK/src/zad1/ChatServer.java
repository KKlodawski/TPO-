/**
 * @author Kłodawski Kamil S24777
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Stream;

public class ChatServer {
    private String host;
    private int port;
    private Selector selector;
    boolean serverStatus = false;
    String log = "";
    Map<String,String> userLogs = new HashMap<>();

    public ChatServer(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        selector = Selector.open();
    }

    public void startServer() throws IOException {
        System.out.println("\nServer started");
        serverStatus = true;
        Thread serverThread = new Thread(
                () -> {
                    try {
                        ServerSocketChannel socketChannel = ServerSocketChannel.open();
                        socketChannel.bind(new InetSocketAddress(this.host, this.port));
                        socketChannel.configureBlocking(false);
                        socketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
                        while (this.serverStatus) {
                            selector.select();
                            if(!selector.isOpen()) continue;
                            Set<SelectionKey> selectedKeys = selector.selectedKeys();
                            Iterator<SelectionKey> iter = selectedKeys.iterator();

                            while (iter.hasNext()) {

                                SelectionKey key = iter.next();
                                iter.remove();

                                if (key.isAcceptable()) {
                                    //System.out.println(" acceptable" + key);
                                    SocketChannel socketChannel1 = socketChannel.accept();
                                    socketChannel1.configureBlocking(false);
                                    socketChannel1.register(selector, SelectionKey.OP_READ);
                                    continue;
                                }

                                if (key.isReadable()) {
                                    //System.out.println(" readable" + key);
                                    SocketChannel socketChannel1 = (SocketChannel) key.channel();
                                    answer(socketChannel1);
                                    continue;
                                }

                            }

                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        serverThread.start();
    }

    public void stopServer() throws IOException, InterruptedException {
        System.out.println("\nServer stopped");
        Thread.sleep(1000);
        serverStatus = false;
        selector.close();
    }

    public String getServerLog() {
        return log;
    }

    public void answer(SocketChannel sc) throws IOException {
        if (!sc.isOpen()) return;
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        buffer.clear();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        try {
            readLoop:
            while (true) {
                int n = sc.read(buffer);
                if (n > 0) {
                    buffer.flip();
                    CharBuffer cbuf = StandardCharsets.UTF_8.decode(buffer);
                    while (cbuf.hasRemaining()) {
                        char c = cbuf.get();
                        if (c == '\n') break readLoop;
                        sb.append(c);
                    }

                    ArrayList<String> tmp = new ArrayList<>();
                    for(String e : sb.toString().split("\\|")) tmp.add(e);
                    if(!userLogs.containsKey(tmp.get(0))) userLogs.put(tmp.get(0),"=== " + tmp.get(0) + " chat viev\n");
                    if(tmp.get(1).contains("login")) {
                        log += dtf.format(LocalDateTime.now()) + " " + tmp.get(0) + " logged in\n";
                        for (Map.Entry<String,String> entry : userLogs.entrySet()) {
                            entry.setValue(entry.getValue() + tmp.get(0) + " logged in\n");
                        }
                        writeResp(sc,userLogs.get(tmp.get(0)));
                    } else if (tmp.get(1).contains("logout")) {
                        log += dtf.format(LocalDateTime.now()) + " " + tmp.get(0) + " logged out\n";
                        for (Map.Entry<String,String> entry : userLogs.entrySet()) {
                            entry.setValue(entry.getValue() + tmp.get(0) + " logged out\n");
                        }
                        writeResp(sc,userLogs.get(tmp.get(0)));
                        userLogs.remove(tmp.get(0));
                    } else {
                        log += dtf.format(LocalDateTime.now()) + " " + tmp.get(0) + ": " + tmp.get(1) + "\n";
                        for (Map.Entry<String,String> entry : userLogs.entrySet()) {
                            entry.setValue(entry.getValue() + tmp.get(0) + ": " + tmp.get(1) + "\n");
                        }
                        writeResp(sc,userLogs.get(tmp.get(0)));
                    }

                } else {
                    return;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                sc.close();
                sc.socket().close();
            } catch (Exception ee) {

            }
        }
    }

    private void writeResp(SocketChannel sc, String message) throws IOException {
        StringBuilder remsg = new StringBuilder();
        remsg.setLength(0);
        remsg.append(message);
        remsg.append(System.lineSeparator());
        ByteBuffer buf = StandardCharsets.UTF_8.encode(CharBuffer.wrap(remsg));
        sc.write(buf);
    }
}

