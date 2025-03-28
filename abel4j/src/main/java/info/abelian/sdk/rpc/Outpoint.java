package info.abelian.sdk.rpc;

import info.abelian.sdk.common.Bytes;
import info.abelian.sdk.common.Struct;

public class Outpoint extends Struct {
    public Bytes txid;
    public Integer index;
}
