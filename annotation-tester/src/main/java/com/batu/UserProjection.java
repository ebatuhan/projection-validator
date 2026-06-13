package com.batu;

import java.util.Set;

import com.batu.api.JPAProjection;

@JPAProjection(entity=User.class)
public interface UserProjection {
    String getName();
    Set<AdressProjection> getAdress();
}
