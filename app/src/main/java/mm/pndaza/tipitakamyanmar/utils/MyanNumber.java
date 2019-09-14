package mm.pndaza.tipitakamyanmar.utils;

public class MyanNumber {

    public static String toMyanmar(int engNum){

        String engNumber = String.valueOf(engNum);
        String myanmarNumber = "";
        for(char ch : engNumber.toCharArray()){
            myanmarNumber += (char) ( (int) ch + 4112 );
        }
        return myanmarNumber;
    }
}
