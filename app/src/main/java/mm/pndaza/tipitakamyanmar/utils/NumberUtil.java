package mm.pndaza.tipitakamyanmar.utils;

public class NumberUtil {
    public static boolean isMyanmarNumber(String myanmarText) {
        return myanmarText.matches("[၀-၉]+");
    }

    public static String toMyanmar(int engNum) {
        String engNumber = String.valueOf(engNum);
        String myanmarNumber = "";
        for (char ch : engNumber.toCharArray()) {
            myanmarNumber += (char) ((int) ch + 4112);
        }
        return myanmarNumber;
    }

    public static int toEnglish(String mmNumber) {
        String engNumber = "";
        for (char ch : mmNumber.toCharArray()) {
            engNumber += (char) ((int) ch - 4112);
        }
        return Integer.parseInt(engNumber);
    }

}
