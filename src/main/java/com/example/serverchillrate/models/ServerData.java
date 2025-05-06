package com.example.serverchillrate.models;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class ServerData {
    String externalHost;
    String externalPort;
}
