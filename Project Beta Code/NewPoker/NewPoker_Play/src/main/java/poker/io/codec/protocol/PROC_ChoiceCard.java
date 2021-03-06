// automatically generated by the FlatBuffers compiler, do not modify

package poker.io.codec.protocol;

import com.google.flatbuffers.BaseVector;
import com.google.flatbuffers.Constants;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class PROC_ChoiceCard extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_2_0_0(); }
  public static PROC_ChoiceCard getRootAsPROC_ChoiceCard(ByteBuffer _bb) { return getRootAsPROC_ChoiceCard(_bb, new PROC_ChoiceCard()); }
  public static PROC_ChoiceCard getRootAsPROC_ChoiceCard(ByteBuffer _bb, PROC_ChoiceCard obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public PROC_ChoiceCard __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public PROC_Card choiceCard() { return choiceCard(new PROC_Card()); }
  public PROC_Card choiceCard(PROC_Card obj) { int o = __offset(4); return o != 0 ? obj.__assign(__indirect(o + bb_pos), bb) : null; }
  public PROC_HeadUser headUser() { return headUser(new PROC_HeadUser()); }
  public PROC_HeadUser headUser(PROC_HeadUser obj) { int o = __offset(6); return o != 0 ? obj.__assign(__indirect(o + bb_pos), bb) : null; }
  public PROC_Card cardList(int j) { return cardList(new PROC_Card(), j); }
  public PROC_Card cardList(PROC_Card obj, int j) { int o = __offset(8); return o != 0 ? obj.__assign(__indirect(__vector(o) + j * 4), bb) : null; }
  public int cardListLength() { int o = __offset(8); return o != 0 ? __vector_len(o) : 0; }
  public PROC_Card.Vector cardListVector() { return cardListVector(new PROC_Card.Vector()); }
  public PROC_Card.Vector cardListVector(PROC_Card.Vector obj) { int o = __offset(8); return o != 0 ? obj.__assign(__vector(o), 4, bb) : null; }

  public static int createPROC_ChoiceCard(FlatBufferBuilder builder,
      int choiceCardOffset,
      int headUserOffset,
      int cardListOffset) {
    builder.startTable(3);
    PROC_ChoiceCard.addCardList(builder, cardListOffset);
    PROC_ChoiceCard.addHeadUser(builder, headUserOffset);
    PROC_ChoiceCard.addChoiceCard(builder, choiceCardOffset);
    return PROC_ChoiceCard.endPROC_ChoiceCard(builder);
  }

  public static void startPROC_ChoiceCard(FlatBufferBuilder builder) { builder.startTable(3); }
  public static void addChoiceCard(FlatBufferBuilder builder, int choiceCardOffset) { builder.addOffset(0, choiceCardOffset, 0); }
  public static void addHeadUser(FlatBufferBuilder builder, int headUserOffset) { builder.addOffset(1, headUserOffset, 0); }
  public static void addCardList(FlatBufferBuilder builder, int cardListOffset) { builder.addOffset(2, cardListOffset, 0); }
  public static int createCardListVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startCardListVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endPROC_ChoiceCard(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public PROC_ChoiceCard get(int j) { return get(new PROC_ChoiceCard(), j); }
    public PROC_ChoiceCard get(PROC_ChoiceCard obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

