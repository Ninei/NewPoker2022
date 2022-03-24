package ache.io.codec.protocol;

import ache.ACHEContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class HashKeyConvertor implements ACHEContext {

    public static int getKeycode(String encMsg) {
        switch (encMsg) {
            case "a5771bce93e200c36f7cd9dfd0e5deaa": return 38; // KEY_UP: 상
            case "d645920e395fedad7bbbed0eca3fe2e0": return 40; // KEY_DOWN: 하
            case "a5bfc9e07964f8dddeb95fc584cd965d": return 37; // KEY_LEFT: 좌
            case "d67d8ab4f4c10bf22aa353e27879133c": return 39; // KEY_RIGHT: 우
            case "c51ce410c124a10e0db5e4b97fc2af39": return 13; // KEY_ENTER: 확인(ENTER)
            case "c9f0f895fb98ab9159f51fd0297e236d": return 8; // KEY_PREV: 이전(BACKSPACE)
            case "642e92efb79421734881b53e1e1b18b6": return 48; // KEY_0: 0
            case "f457c545a9ded88f18ecee47145a72c0": return 49; // KEY_1: 1
            case "c0c7c76d30bd3dcaefc96f40275bdc0a": return 50; // KEY_2: 2
            case "2838023a778dfaecdc212708f721b788": return 51; // KEY_3: 3
            case "9a1158154dfa42caddbd0694a4e9bdc8": return 52; // KEY_4: 4
            case "d82c8d1619ad8176d665453cfb2e55f0": return 53; // KEY_5: 5
            case "a684eceee76fc522773286a895bc8436": return 54; // KEY_6: 6
            case "b53b3a3d6ab90ce0268229151c9bde11": return 55; // KEY_7: 7
            case "9f61408e3afb633e50cdf1b20de6f466": return 56; // KEY_8: 8
            case "72b32a1f754ba1c09b3695e0cb6cde7f": return 57; // KEY_9: 9
            case "43ec517d68b6edd3015b3edc9a11367b": return 81; // KEY_RED: 레드(Q)
            case "c7e1249ffc03eb9ded908c236bd1996d": return 87; // KEY_GREEN: 그린(W)
            case "14bfa6bb14875e45bba028a21ed38046": return 69; // KEY_YELLOW: 엘로(E)
            case "9778d5d219c5080b9a6a17bef029331c": return 82; // KEY_BLUE: 블루Ű(R)
            case "8613985ec49eb8f757ae6439e879bb2a": return 90; // KEY_STAR: * 획추가(Z)
            case "2a38a4a9316c49e5a833517c45d31070": return 88; // KEY_SHARP: # 쌍자음(X)
            case "735b90b4568125ed6c3f678819b6e058": return 67; // KEY_INPUT: 입력모음(C)
            case "02e74f10e0327ad868d138f2b4fdd6f0": return 27; // KEY_EXIT: 나가기(ESC)
            case "d9d4f495e875a2e075a1a4a6e1b9770f": return 46; // KEY_DELETE: 지우기(DELETE)
            case "3ef815416f775098fe977004015c6193": return 85; // KEY_PLAY: 플레이(U)
            case "7647966b7343c29048673252e490f736": return 89; // KEY_STOP: 정지(I)
            case "d1fe173d08e959397adf34b1d77e88d7": return 79; // KEY_REWIND: REWIND(O)
            case "f033ab37c30201f73f142449d037028d": return 80; // KEY_FAST FORWARD: FAST_FWD(P)
            case "93db85ed909c13838ff95ccfa94cebd9": return 86; // KEY_CHANNEL_UP: 채널업(V)
            case "3295c76acbf4caaed33c36b1b5fc2cb1": return 66; // KEY_CHANNEL_DOWN: 채널다운(B)
            case "35f4a8d465e6e1edc05f3d8ab658c551": return 78; // KEY_VOLUME_UP: 볼륨업(N)
            case "28dd2c7955ce926456240b2ff0100bde": return 77; // KEY_VOLUME_DOWN: 볼륨다운(M)
            case "32bb90e8976aab5298d5da10fe66f21d": return 72; // KEY_HOME: 홈(H)
            default: log.error("UnKnown KeyCode - " + encMsg); return NONE;

            // 알티캐스트키코드
//            case "957C58D5185F1A7EBA088F1FBB8A978A": return 38; // KEY_UP: 상
//            case "7A79755BB0EF4EF45E35A2B138D4BB50": return 40; // KEY_DOWN: 하
//            case "3D16409DB97ABD285558435D50B04587": return 37; // KEY_LEFT: 좌
//            case "2166DFC2BCE604EA7113BD6E12331A03": return 39; // KEY_RIGHT: 우
//            case "6C5E6706D7C69E55657AEF3CDDB0C46A": return 13; // KEY_ENTER: 확인(ENTER)
//            case "7A77B35DD186A9587E39A5E88FE203B6": return 8; // KEY_PREV: 이전(BACKSPACE)
//            case "D6CCB7644E1F6242BF4095B5E195919D": return 48; // KEY_0: 0
//            case "275A9035369178EA58D84B18D1EB4080": return 49; // KEY_1: 1
//            case "726A97BCC50C65E84E29DBABE0D335A0": return 50; // KEY_2: 2
//            case "AD0657AC38268E97915555BC1DBFF1EC": return 51; // KEY_3: 3
//            case "86ED09922630A008F41A5EA71CA8F711": return 52; // KEY_4: 4
//            case "B9A2BA5CAB1B1D2A866876E359568B5E": return 53; // KEY_5: 5
//            case "03036584BA3F87D575A02BE65AFAF382": return 54; // KEY_6: 6
//            case "DE4839CC58BEB9CB36363B09172B59B0": return 55; // KEY_7: 7
//            case "AD311257D4478AB20944B13959B4AB8D": return 56; // KEY_8: 8
//            case "B480848611147E67B109315388AA7822": return 57; // KEY_9: 9
//            case "ACE100E8FB79A0A15612A6955E623151": return 81; // KEY_RED: 레드(Q)
//            case "3203566D458F494E4D9FFC0A76DE70F9": return 87; // KEY_GREEN: 그린(W)
//            case "7E1B4440BEEFB4055C98CD898D78ED68": return 69; // KEY_YELLOW: 엘로(E)
//            case "94D00E37E38B200134F89A5BF62F1BC5": return 82; // KEY_BLUE: 블루Ű(R)
//            case "012F254EB23C124E45EA00BF5BC495B9": return 90; // KEY_STAR: * 획추가(Z)
//            case "B0E2F2A970E2113F8E35C00D4C3C2192": return 88; // KEY_SHARP: # 쌍자음(X)
//            case "A33874F2BCBDF66F5F33B973C3077736": return 67; // KEY_INPUT: 입력모음(C)
//            case "F017E97801C302BA3F7B9192FAEA01F2": return 27; // KEY_EXIT: 나가기(ESC)
//            case "929F104D62F2BF4F717019663EB475FC": return 46; // KEY_DELETE: 지우기(DELETE)
//            case "E77374F80F2952FC1F80CAA78C7057DE": return 85; // KEY_PLAY: 플레이(U)
//            case "63138FCD0EC9B64E524B55AC5F5DC890": return 89; // KEY_STOP: 정지(I)
//            case "DAFE313D991976B10F270CCA07A0F88B": return 79; // KEY_REWIND: REWIND(O)
//            case "F96903EF7548AC27A358DA6C5826DF2F": return 80; // KEY_FAST FORWARD: FAST_FWD(P)
//            case "81887F5CE5F3A628E61C30971360E907": return 86; // KEY_CHANNEL_UP: 채널업(V)
//            case "B28B9751C12340625F6871A784BB07AB": return 66; // KEY_CHANNEL_DOWN: 채널다운(B)
//            case "AB038EB08F7180DD988A14BF126B9581": return 78; // KEY_VOLUME_UP: 볼륨업(N)
//            case "356648AF22E309CFE148399816B0AE4D": return 77; // KEY_VOLUME_DOWN: 볼륨다운(M)
//            case "A8ABE7E83300CDC8268C0271238F8117": return 72; // KEY_HOME: 홈(H)
//            default: log.error("UnKnown KeyCode - " + encMsg); return NONE;
        }
    };
}
