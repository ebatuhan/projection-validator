package com.batu;

import com.batu.api.JPAProjection;

@JPAProjection(entity=Adress.class)
public interface AdressProjection {
    String getFullAdress();
}
