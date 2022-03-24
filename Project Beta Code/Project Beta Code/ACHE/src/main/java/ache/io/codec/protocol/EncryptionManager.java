package ache.io.codec.protocol;

import lombok.extern.log4j.Log4j2;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Log4j2
public class EncryptionManager {

    private String[][] KEY_CODE = new String[][]{
        {"38", "KEY_UP: 상"},
        {"40", "KEY_DOWN: 하"},
        {"37", "KEY_LEFT: 좌"},
        {"39", "KEY_RIGHT: 우"},
        {"13", "KEY_ENTER: 확인(ENTER)"},
        {"8", "KEY_PREV: 이전(BACKSPACE)"},
        {"48", "KEY_0: 0"},
        {"49", "KEY_1: 1"},
        {"50", "KEY_2: 2"},
        {"51", "KEY_3: 3"},
        {"52", "KEY_4: 4"},
        {"53", "KEY_5: 5"},
        {"54", "KEY_6: 6"},
        {"55", "KEY_7: 7"},
        {"56", "KEY_8: 8"},
        {"57", "KEY_9: 9"},
        {"81", "KEY_RED: 레드(Q)"},
        {"87", "KEY_GREEN: 그린(W)"},
        {"69", "KEY_YELLOW: 엘로(E)"},
        {"82", "KEY_BLUE: 블루Ű(R)"},
        {"90", "KEY_STAR: * 획추가(Z)"},
        {"88", "KEY_SHARP: # 쌍자음(X)"},
        {"67", "KEY_INPUT: 입력모음(C)"},
        {"27", "KEY_EXIT: 나가기(ESC)"},
        {"46", "KEY_DELETE: 지우기(DELETE)"},
        {"85", "KEY_PLAY: 플레이(U)"},
        {"89", "KEY_STOP: 정지(I)"},
        {"79", "KEY_REWIND: REWIND(O)"},
        {"80", "KEY_FAST FORWARD: FAST_FWD(P)"},
        {"86", "KEY_CHANNEL_UP: 채널업(V)"},
        {"66", "KEY_CHANNEL_DOWN: 채널다운(B)"},
        {"78", "KEY_VOLUME_UP: 볼륨업(N)"},
        {"77", "KEY_VOLUME_DOWN: 볼륨다운(M)"},
        {"72", "KEY_HOME: 홈(H)"},
    };

    public void createSHA256KeyCode() {
        Encrpytor encrpytor = new Encrpytor();
        String salt = encrpytor.createSHA256Salt();
        for (int i=0; i<KEY_CODE.length; i++) {
            log.info(
                "case \"" +
                encrpytor.getSHA256(KEY_CODE[i][0], salt)
                    + "\": return "+ KEY_CODE[i][0] + "; // " + KEY_CODE[i][1]);
        }

        log.info("/n/n Script Code");
        for (int i=0; i<KEY_CODE.length; i++) {
            log.info(
                "hashEncMap.put("+KEY_CODE[i][0]+", \"" +
                    encrpytor.getMD5(KEY_CODE[i][0])
                    + "\"); // " + KEY_CODE[i][1]);
        }
    }

    public void createMD5KeyCode() {
        log.info("/n/n Java Code");
        Encrpytor encrpytor = new Encrpytor();
        for (int i=0; i<KEY_CODE.length; i++) {
            log.info(
                "case \"" +
                    encrpytor.getMD5(KEY_CODE[i][0])
            + "\": return "+ KEY_CODE[i][0] + "; // " + KEY_CODE[i][1]);
        }

        log.info("/n/n Script Code");
        for (int i=0; i<KEY_CODE.length; i++) {
            log.info(
                "hashEncMap.put("+KEY_CODE[i][0]+", \"" +
                    encrpytor.getMD5(KEY_CODE[i][0])
                    + "\"); // " + KEY_CODE[i][1]);
        }
    }

    private class Encrpytor {

        public String getMD5(String data) {
            String MD5 = "";
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(data.getBytes());
                byte byteData[] = md.digest();
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < byteData.length; i++) {
                    sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                }
                MD5 = sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
                MD5 = null;
            }
            return MD5;
        }

        public String createSHA256Salt() {
            String salt = "";
            try {
                SecureRandom random = SecureRandom.getInstance("SHA1SHOP");
                byte[] bytes = new byte[16];
                random.nextBytes(bytes);
                salt = new String(Base64.getEncoder().encode(bytes));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return salt;
        }

        public String getSHA256(String password, String hash) {
            String salt = hash + password;
            String hex = null;
            try {
                MessageDigest msg = MessageDigest.getInstance("SHA-256");
                msg.update(salt.getBytes());
                hex = String.format("%128x", new BigInteger(1, msg.digest()));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return hex;
        }
    }

    public static void main(String[] args) {
        new EncryptionManager().createMD5KeyCode();
    }
}
