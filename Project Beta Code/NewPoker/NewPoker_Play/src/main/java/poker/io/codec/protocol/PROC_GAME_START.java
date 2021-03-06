// automatically generated by the FlatBuffers compiler, do not modify

package poker.io.codec.protocol;

import com.google.flatbuffers.BaseVector;
import com.google.flatbuffers.Constants;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class PROC_GAME_START extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_2_0_0(); }
  public static PROC_GAME_START getRootAsPROC_GAME_START(ByteBuffer _bb) { return getRootAsPROC_GAME_START(_bb, new PROC_GAME_START()); }
  public static PROC_GAME_START getRootAsPROC_GAME_START(ByteBuffer _bb, PROC_GAME_START obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public PROC_GAME_START __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public PROC_Card cardList(int j) { return cardList(new PROC_Card(), j); }
  public PROC_Card cardList(PROC_Card obj, int j) { int o = __offset(4); return o != 0 ? obj.__assign(__indirect(__vector(o) + j * 4), bb) : null; }
  public int cardListLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public PROC_Card.Vector cardListVector() { return cardListVector(new PROC_Card.Vector()); }
  public PROC_Card.Vector cardListVector(PROC_Card.Vector obj) { int o = __offset(4); return o != 0 ? obj.__assign(__vector(o), 4, bb) : null; }

  public static int createPROC_GAME_START(FlatBufferBuilder builder,
      int cardListOffset) {
    builder.startTable(1);
    PROC_GAME_START.addCardList(builder, cardListOffset);
    return PROC_GAME_START.endPROC_GAME_START(builder);
  }

  public static void startPROC_GAME_START(FlatBufferBuilder builder) { builder.startTable(1); }
  public static void addCardList(FlatBufferBuilder builder, int cardListOffset) { builder.addOffset(0, cardListOffset, 0); }
  public static int createCardListVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startCardListVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endPROC_GAME_START(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public PROC_GAME_START get(int j) { return get(new PROC_GAME_START(), j); }
    public PROC_GAME_START get(PROC_GAME_START obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

