/**
 *
 *  @author Kłodawski Kamil S24777
 *
 */

package zad1;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChatClientTask implements Runnable {

    ChatClient c;
    List<String> msgs;
    int wait;

    public ChatClientTask(ChatClient c, List<String> msgs, int wait) {
        this.c = c;
        this.msgs = msgs;
        this.wait = wait;
    }

    public static ChatClientTask create(ChatClient c, List<String> msgs, int wait) {
        return new ChatClientTask(c,msgs,wait);
    }

    @Override
    public void run() {
        try {
            c.login();
            if (wait != 0) Thread.sleep(wait);
            for (String msg : msgs) {

                c.send( msg );
                if (wait != 0) Thread.sleep(wait);
            }
            c.logout();
            if (wait != 0) Thread.sleep(wait);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void get() throws InterruptedException, ExecutionException {
    }

    public ChatClient getClient() {
        return c;
    }

}

