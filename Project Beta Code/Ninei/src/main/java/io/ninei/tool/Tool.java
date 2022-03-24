package io.ninei.tool;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Tool {

    public static String convertHangul(BigInteger money) {
        String pattern = "###,###,###,###,###,###,###,###,###,###,###,###,###,###,###,###,###,###,###,###";
        String target = new DecimalFormat(pattern).format(money);
        String[] kor = { "원", "천", "백", "천", "억", "조", "경", "해", "서", "양", "구", "간", "정", "재", "극", "항해사", "아승기", "나유타", "불가사의", "무량대수"};

        ArrayList<String> list = new ArrayList<>(Arrays.asList(target.split(",")));
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)+kor[list.size()-i-1]);
        }

        return sb.toString();
    }
}
