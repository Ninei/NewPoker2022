// automatically generated by the FlatBuffers compiler, do not modify

package poker.io.codec.protocol;

import com.google.flatbuffers.BaseVector;
import com.google.flatbuffers.Constants;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class PROC_HeadUser extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_2_0_0(); }
  public static PROC_HeadUser getRootAsPROC_HeadUser(ByteBuffer _bb) { return getRootAsPROC_HeadUser(_bb, new PROC_HeadUser()); }
  public static PROC_HeadUser getRootAsPROC_HeadUser(ByteBuffer _bb, PROC_HeadUser obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public PROC_HeadUser __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String userId() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer userIdAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer userIdInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }
  public byte menuType() { int o = __offset(6); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public boolean mutateMenuType(byte menuType) { int o = __offset(6); if (o != 0) { bb.put(o + bb_pos, menuType); return true; } else { return false; } }
  public PROC_MENU_ITEM menuList(int j) { return menuList(new PROC_MENU_ITEM(), j); }
  public PROC_MENU_ITEM menuList(PROC_MENU_ITEM obj, int j) { int o = __offset(8); return o != 0 ? obj.__assign(__indirect(__vector(o) + j * 4), bb) : null; }
  public int menuListLength() { int o = __offset(8); return o != 0 ? __vector_len(o) : 0; }
  public PROC_MENU_ITEM.Vector menuListVector() { return menuListVector(new PROC_MENU_ITEM.Vector()); }
  public PROC_MENU_ITEM.Vector menuListVector(PROC_MENU_ITEM.Vector obj) { int o = __offset(8); return o != 0 ? obj.__assign(__vector(o), 4, bb) : null; }

  public static int createPROC_HeadUser(FlatBufferBuilder builder,
      int userIdOffset,
      byte menuType,
      int menuListOffset) {
    builder.startTable(3);
    PROC_HeadUser.addMenuList(builder, menuListOffset);
    PROC_HeadUser.addUserId(builder, userIdOffset);
    PROC_HeadUser.addMenuType(builder, menuType);
    return PROC_HeadUser.endPROC_HeadUser(builder);
  }

  public static void startPROC_HeadUser(FlatBufferBuilder builder) { builder.startTable(3); }
  public static void addUserId(FlatBufferBuilder builder, int userIdOffset) { builder.addOffset(0, userIdOffset, 0); }
  public static void addMenuType(FlatBufferBuilder builder, byte menuType) { builder.addByte(1, menuType, 0); }
  public static void addMenuList(FlatBufferBuilder builder, int menuListOffset) { builder.addOffset(2, menuListOffset, 0); }
  public static int createMenuListVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startMenuListVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endPROC_HeadUser(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public PROC_HeadUser get(int j) { return get(new PROC_HeadUser(), j); }
    public PROC_HeadUser get(PROC_HeadUser obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

