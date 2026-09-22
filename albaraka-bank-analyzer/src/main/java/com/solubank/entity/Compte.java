package com.solubank.entity;

public abstract sealed class Compte permits CompteCourant, CompteEpargne {
}
