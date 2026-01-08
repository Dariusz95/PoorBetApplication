package com.poorbet.oddstraining.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MatchRecord {
    private int homeAttack;
    private int homeDefence;
    private int awayAttack;
    private int awayDefence;
    private String result;
}