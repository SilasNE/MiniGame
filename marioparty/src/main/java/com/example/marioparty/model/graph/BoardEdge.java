package com.example.marioparty.model.graph;

/**
 * Gerichtete Kante im Brett-Graphen: von {@link #fromKnotId()} nach {@link #toKnotId()}.
 */
public record BoardEdge(int fromKnotId, int toKnotId) {}
