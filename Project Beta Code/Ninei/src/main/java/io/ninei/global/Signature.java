package io.ninei.global;

public interface Signature extends DefaultContext {

    // 추상 메소드
    public abstract String getCode();
    public abstract String getName();

    // 디폴트 메소드 : 실행 내용까지 작성이 가능
    public default String getMsg() { return "Now is Null"; }
}
