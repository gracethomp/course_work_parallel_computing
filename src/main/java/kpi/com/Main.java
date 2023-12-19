package kpi.com;

import kpi.com.client.Client;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Client client = new Client("localhost", 6666);
        client.connect();
    }
}
