package net.kst_d.lab.yac;

public class Main {

    public static void main(String[] args) {
        ChatServer server = new ChatServer(8888);
        server.start();

    }
}
