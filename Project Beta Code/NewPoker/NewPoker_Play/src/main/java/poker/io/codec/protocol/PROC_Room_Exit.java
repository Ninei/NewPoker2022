// automatically generated by the FlatBuffers compiler, do not modify

package poker.io.codec.protocol;

import com.google.flatbuffers.BaseVector;
import com.google.flatbuffers.Constants;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class PROC_Room_Exit extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_2_0_0(); }
  public static PROC_Room_Exit getRootAsPROC_Room_Exit(ByteBuffer _bb) { return getRootAsPROC_Room_Exit(_bb, new PROC_Room_Exit()); }
  public static PROC_Room_Exit getRootAsPROC_Room_Exit(ByteBuffer _bb, PROC_Room_Exit obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public PROC_Room_Exit __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public PROC_User exitUser() { return exitUser(new PROC_User()); }
  public PROC_User exitUser(PROC_User obj) { int o = __offset(4); return o != 0 ? obj.__assign(__indirect(o + bb_pos), bb) : null; }
  public PROC_HeadUser headUser() { return headUser(new PROC_HeadUser()); }
  public PROC_HeadUser headUser(PROC_HeadUser obj) { int o = __offset(6); return o != 0 ? obj.__assign(__indirect(o + bb_pos), bb) : null; }
  public PROC_Room room() { return room(new PROC_Room()); }
  public PROC_Room room(PROC_Room obj) { int o = __offset(8); return o != 0 ? obj.__assign(__indirect(o + bb_pos), bb) : null; }

  public static int createPROC_Room_Exit(FlatBufferBuilder builder,
      int exitUserOffset,
      int headUserOffset,
      int roomOffset) {
    builder.startTable(3);
    PROC_Room_Exit.addRoom(builder, roomOffset);
    PROC_Room_Exit.addHeadUser(builder, headUserOffset);
    PROC_Room_Exit.addExitUser(builder, exitUserOffset);
    return PROC_Room_Exit.endPROC_Room_Exit(builder);
  }

  public static void startPROC_Room_Exit(FlatBufferBuilder builder) { builder.startTable(3); }
  public static void addExitUser(FlatBufferBuilder builder, int exitUserOffset) { builder.addOffset(0, exitUserOffset, 0); }
  public static void addHeadUser(FlatBufferBuilder builder, int headUserOffset) { builder.addOffset(1, headUserOffset, 0); }
  public static void addRoom(FlatBufferBuilder builder, int roomOffset) { builder.addOffset(2, roomOffset, 0); }
  public static int endPROC_Room_Exit(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public PROC_Room_Exit get(int j) { return get(new PROC_Room_Exit(), j); }
    public PROC_Room_Exit get(PROC_Room_Exit obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

