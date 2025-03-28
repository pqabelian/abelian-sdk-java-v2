package info.abelian.sdk.rpc;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.Struct;

public class Ring extends Struct {
    public Integer version;
    public Bytes[] blockhashs;
    public Outpoint[] outpoints;
}
