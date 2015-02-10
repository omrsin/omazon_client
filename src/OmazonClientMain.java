import client.OmazonClient;
import javax.jms.JMSException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author floriment
 */
public class OmazonClientMain {

    public static void main(String[] args) throws JMSException {
        OmazonClient client;

        if (args.length == 1 && args[0].equals("true")) {
            client = new OmazonClient(true, false,false);
        } else if (args.length == 2 && args[0].equals("false") && args[1].equals("true")) {
            client = new OmazonClient(false, true,false);
        } else if (args.length == 1 && args[0].equals("healthyDelayed")) {
            client = new OmazonClient(false, false,true);
        } else {
            client = new OmazonClient(false, false,true);
        }
        client.start();
    }

}
