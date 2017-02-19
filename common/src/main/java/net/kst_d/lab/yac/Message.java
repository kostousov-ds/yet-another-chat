package net.kst_d.lab.yac;


import java.util.UUID;

import lombok.Data;

@Data
public class Message {
    private final UUID connection;
    private final String data;
}
