/**
 *
 *  @author Kłodawski Kamil S24777
 *
 */

package zad1;


import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClientTask implements Runnable {
    Client c;
    List<String> reqs;
    boolean showSendRes;
    String clog = "";


    public ClientTask(Client c, List<String> reqs, boolean showSendRes) {
        this.c = c;
        this.reqs = reqs;
        this.showSendRes = showSendRes;
    }

    public static ClientTask create(Client c, List<String> reqList, boolean showRes) {
        return new ClientTask(c,reqList,showRes);
    }


    @Override
    public void run() {
        c.connect();
        c.send("login " + c.id);
        for(String req : reqs) {
            String res = c.send(req);
            if (showSendRes) clog += res;
        }
        String tmpclog = c.send("bye and log transfer");
        if(showSendRes) clog +=  tmpclog;
    }

    public String get() throws InterruptedException, ExecutionException {
        Thread.sleep(1500);
        return clog;
    }
}
