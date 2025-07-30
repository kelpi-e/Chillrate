package com.example.serverchillrate.secutiry.service;

import com.example.serverchillrate.entity.UserApp;

public interface UdpServiceSecure {
    ///@brief add userId and user to hashmap,add jwt and uuid to hashmap
    ///@details 2 hashmap:
    ///1 hashmap-key-userId value pair User and UserData
    ///2 hashmap-key-jwt value-userId
    ///@param userApp
    ///@param jwt
    void addClientData(UserApp userApp,String jwt);
    void addJwtToClient(String jwt, UserApp userApp);
    ///@brief check  jwt
    ///@param jwt
    ///@return true - all ok,false not ok
    ///@details if jwt not contains in 1 hashmap,
    /// uuid with 1 hashmap not contains in 2 hashmap,
    /// jwt not valid -return false
    /// else true
    boolean checkToken(String jwt);
    UserApp getAdminFromToken(String jwt);
}
