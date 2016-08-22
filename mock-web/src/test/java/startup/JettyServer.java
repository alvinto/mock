package startup;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {
    
    public static void main(String[] args) throws Exception {
        final Server server = new Server(8099);
        WebAppContext webAppContext = new WebAppContext("mock-web/src/main/webapp", "/mockweb");
        webAppContext.setMaxFormContentSize(1024*1024*60);
        server.setHandler(webAppContext);
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                try {
                    server.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        server.start();
        server.join();
    }
}
