package engine;

import compute.Compute;
import compute.Task;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ComputeEngine implements Compute {

    public ComputeEngine() {
        super();
    }

    public <T> T executeTask(Task<T> t) {
        System.out.println("Received a new task");
        T tComplete = t.execute();
        System.out.println("Completed task\n\nWaiting for a new task...");
        return tComplete;
    }

    public static void main(String[] args) throws Exception{

        if (args.length != 1) {
            System.err.println("Usage: java -Djava.rmi.server.hostname= -Djava.rmi.server.useCodebaseOnly=false -jar " +
                    "<jarPath> <hostname:port>");
            System.exit(-1);
        }

        String postUrl = "http://" + args[0] + "/fogMiddleware/node/registration";

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(postUrl);
        HttpResponse response = httpClient.execute(post);
        if (response.getStatusLine().getStatusCode() != 201) {
            System.err.println("Error in Registration");
            return;
        }

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "Compute";
            Compute engine = new ComputeEngine();
            Compute stub =
                    (Compute) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("ComputeEngine bound\nWaiting for task...");
        } catch (Exception e) {
            System.err.println("ComputeEngine exception:");
            e.printStackTrace();
        }
    }
}