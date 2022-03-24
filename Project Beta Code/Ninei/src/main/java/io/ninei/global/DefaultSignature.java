package io.ninei.global;

public abstract class DefaultSignature implements Signature {

    @Override
    public String getCode() { return uuCode; }
    @Override
    public String getName() { return uuName; }

    protected DefaultSignature(String id, String name) { uuCode = id; uuName = name; }

    private final String uuCode;
    private final String uuName;
}
