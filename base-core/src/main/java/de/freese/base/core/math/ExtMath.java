package de.freese.base.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Standard Mathematik Methoden.
 *
 * @author Thomas Freese
 */
public final class ExtMath {
    public static final double LOG_E = Math.log(Math.E);
    /**
     * <pre>
     * char[] carray = new char[36];
     * int j = 0;
     * for (char i = 48; i < 58; i++, j++)
     *     carray[j] = i; // 0 - 9
     * for (char i = 65; i < 91; i++, j++)
     *     carray[j] = i; // A - Z
     * </pre>
     */
    private static final char[] CHAR_ARRAY = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static long ackermann(final long n, final long m) {
        if (n == 0) {
            return m + 1;
        }
        else if (m == 0) {
            return ackermann(n - 1, 1);
        }
        else {
            return ackermann(n - 1, ackermann(n, m - 1));
        }
    }

    /**
     * Returns a Decimal-Value of a Value with a different Base.
     *
     * @param number The String which is to be converted
     * @param base The Base of the Value
     */
    public static long base2Dec(final String number, final int base) {
        if (base <= 1 || base > 36) {
            return -1;
        }

        final char[] numArray = number.strip().toCharArray();
        long result = 0L;

        for (int i = 0; i < numArray.length; i++) {
            result += Arrays.binarySearch(CHAR_ARRAY, numArray[numArray.length - 1 - i]) * Math.pow(base, i);
        }

        return result;
    }

    /**
     * Returns the Checksum of a Value.
     */
    public static long checksum(final long n) {
        if (n == 0) {
            return 0;
        }

        return checksum(n / 10L) + (n % 10L);
    }

    /**
     * Returns a String with the new BaseFormat of a Decimal-Value.
     *
     * @param value The Value which is to be converted
     * @param base the new Base of the Value
     */
    public static String dec2Base(final long value, final int base) {
        if (base <= 1 || base > 36) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        double tmp;
        long number = value;

        while (number != 0L) {
            tmp = ((double) number) / ((double) base);
            number = (long) tmp;
            tmp -= number;
            tmp = Math.round(tmp * base);

            sb.append(CHAR_ARRAY[(int) tmp]);
        }

        sb.reverse();

        return sb.toString();
    }

    /**
     * Denormalize a Value (Wikipedia).<br>
     * x * (max(x) - min(x)) + min(x)
     */
    public static double denormalize(final double normalized, final double min, final double max) {
        return normalized * (max - min) + min;
    }

    /**
     * Returns a String-Vector of Factors.
     */
    public static List<String> factors(final BigInteger bi) {
        return factors(bi, 100);
    }

    /**
     * Returns a String-Vector of Factors.
     *
     * @param certainty Accuracy (Genauigkeit)
     */
    public static List<String> factors(final BigInteger value, final int certainty) {
        BigInteger bi = value;

        final List<String> list = new ArrayList<>();
        BigInteger n = BigInteger.valueOf(2);

        while (!bi.isProbablePrime(certainty)) {
            if ((bi.mod(n)).equals(BigInteger.ZERO)) // Teiler gefunden
            {
                list.add(n.toString());
                bi = bi.divide(n);
                n = BigInteger.ONE;
            }

            n = n.add(BigInteger.ONE);
        }

        list.add(bi.toString());

        return list;
    }

    /**
     * Liefert die Übergebene römische Zahl als <code>int</code> zurück.
     */
    public static int getIntFromRoman(final String roman) {
        if (!isValidRomanNumber(roman)) {
            throw new IllegalArgumentException("Dies \"" + roman + "\" ist keine gültige römische Zahl.");
        }

        final char[] chars = roman.toUpperCase().toCharArray();
        char lastChar = ' ';
        int value = 0;

        for (int i = chars.length - 1; i >= 0; i--) {
            value += switch (chars[i]) {
                case 'I' -> lastChar == 'X' || lastChar == 'V' ? -1 : 1;
                case 'V' -> 5;
                case 'X' -> lastChar == 'C' || lastChar == 'L' ? -10 : 10;
                case 'L' -> 50;
                case 'C' -> lastChar == 'M' || lastChar == 'D' ? -100 : 100;
                case 'D' -> 500;
                case 'M' -> 1000;
                default -> 0;
            };

            lastChar = chars[i];
        }

        return value;
    }

    /**
     * Liefert die übergebene Zahl als römische Zahl zurück.
     */
    public static String getRomanNumber(final int value) {
        if (value < 1 || value > 3999) {
            throw new IllegalArgumentException("Die eingegebene Zahl muss zwischen 1 und 3999 liegen!");
        }

        final StringBuilder sb = new StringBuilder();
        int val = value;

        while ((val / 1000) >= 1) {
            sb.append("M");
            val -= 1000;
        }

        if ((val / 900) >= 1) {
            sb.append("CM");
            val -= 900;
        }

        if ((val / 500) >= 1) {
            sb.append("D");
            val -= 500;
        }

        if ((val / 400) >= 1) {
            sb.append("CD");
            val -= 400;
        }

        while ((val / 100) >= 1) {
            sb.append("C");
            val -= 100;
        }

        if ((val / 90) >= 1) {
            sb.append("XC");
            val -= 90;
        }

        if ((val / 50) >= 1) {
            sb.append("L");
            val -= 50;
        }

        if ((val / 40) >= 1) {
            sb.append("XL");
            val -= 40;
        }

        while ((val / 10) >= 1) {
            sb.append("X");
            val -= 10;
        }

        if ((val / 9) >= 1) {
            sb.append("IX");
            val -= 9;
        }

        if ((val / 5) >= 1) {
            sb.append("V");
            val -= 5;
        }

        if ((val / 4) >= 1) {
            sb.append("IV");
            val -= 4;
        }

        while (val >= 1) {
            sb.append("I");
            val -= 1;
        }

        return sb.toString();
    }

    /**
     * Liefert true, wenn der Wert Nachkommastellen hat.
     */
    public static boolean hasFractionDigits(final double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return false;
        }

        return value > Math.floor(value) && value < Math.ceil(value);
    }

    /**
     * Checks if number is prime or not.
     */
    public static boolean isPrime(final long number) {
        boolean prim = true;

        if ((number % 2L) == 0L) {
            prim = false;
        }
        else {
            if (number > 3L) {
                long counter;

                long root = (long) Math.sqrt(number);

                // Zahl finden, deren Quadrat etwas grösser
                // ist, als die zu prüfende Zahl
                if ((root % 2L) == 0L) {
                    root++;
                }
                else {
                    root += 2L;
                }

                counter = 1L;

                do {
                    counter++;

                    if ((number % counter) == 0L) {
                        prim = false;

                        break;
                    }
                }
                while (counter < root);
            }
        }

        if (number == 1L) {
            prim = false;
        }

        if (number == 2L) {
            prim = true;
        }

        return prim;
    }

    /**
     * Falls der übergebene {@link String} eine gültige römische Zahl ist, wird <code>true</code> geliefert, sonst <code>false</code>.
     */
    public static boolean isValidRomanNumber(final String roman) {
        final String pattern = "M{0,3}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})";

        return Pattern.compile(pattern).matcher(roman.toUpperCase()).matches();
        //
        // char[] chars = roman.toCharArray();
        //
        // for (int i = 0; i < chars.length; i++)
        // {
        // char c = chars[i];
        // if ((c != 'I') && (c != 'V') && (c != 'X') && (c != 'L') && (c != 'C') && (c != 'D')
        // && (c != 'M'))
        // {
        // return false;
        // }
        // }
        //
        // return true;
    }

    /**
     * Calculates the largest common divisor.
     */
    public static long lcd(final long a, final long b) {
        if (b == 0L) {
            return a;
        }

        return lcd(b, a % b);
    }

    public static double ln(final double x) {
        return Math.log(x) / LOG_E;
    }

    /**
     * Normalise a Value (Wikipedia).<br>
     * (x - min(x)) / (max(x) - min(x))
     */
    public static double normalize(final double value, final double min, final double max) {
        return (value - min) / (max - min);
    }

    /**
     * Returns a String-Vector of Prime Factors.
     */
    public static List<String> primeFactors(final BigInteger bi) {
        return primeFactors(bi, 100);
    }

    /**
     * Returns a String-Vector of Prime Factors.
     *
     * @param certainty Accuracy (Genauigkeit)
     */
    public static List<String> primeFactors(final BigInteger value, final int certainty) {
        BigInteger bi = value;
        final List<String> list = new ArrayList<>();
        BigInteger n = BigInteger.ONE;

        while (!bi.isProbablePrime(certainty)) {
            do {
                n = (n.add(BigInteger.ONE)).add(BigInteger.ONE);
            }
            while (!n.isProbablePrime(certainty));

            if ((bi.mod(n)).equals(BigInteger.ZERO)) // Prim-Teiler gefunden
            {
                list.add(n.toString());
                bi = bi.divide(n);
                n = (n.subtract(BigInteger.ONE)).subtract(BigInteger.ONE);
            }
        }

        list.add(bi.toString());

        return list;
    }

    /**
     * Scales a Value (Wikipedia).<br>
     *
     * @param value double
     * @param minOld double
     * @param maxOld double
     * @param minNew double
     * @param maxNew double
     */
    public static double rescale(final double value, final double minOld, final double maxOld, final double minNew, final double maxNew) {
        return minNew + (((value - minOld) * (maxNew - minNew)) / (maxOld - minOld));
    }

    /**
     * Rundet ein Double Wert auf eine bestimmte Anzahl Nachkommastellen.<br>
     * Als {@link RoundingMode} wird HALF_UP verwendet.<br>
     * Ist der Wert NaN oder Infinite, wird 0.0D geliefert.
     *
     * @param scale int Anzahl Nachkommastellen
     */
    public static double round(final double value, final int scale) {
        return round(value, scale, RoundingMode.HALF_UP);
    }

    /**
     * Rundet ein Double Wert auf eine bestimmte Anzahl Nachkommastellen und des RoundingModes.<br>
     * Ist der Wert NaN oder Infinite, wird 0.0D geliefert.
     *
     * @param scale int Anzahl Nachkommastellen
     */
    public static double round(final double value, final int scale, final RoundingMode roundingMode) {
        if (Double.isNaN(value) || Double.isInfinite(value) || Double.compare(value, 0.0D) == 0) {
            return 0.0D;
        }

        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        bigDecimal = bigDecimal.setScale(scale, roundingMode);

        return bigDecimal.doubleValue();
    }

    /**
     * Liefert den abgerundeten Integer Wert des Double-Wertes.<br>
     * Ist der Wert NaN oder Infinite, wird 0.0D geliefert.
     */
    public static int roundDown(final double value) {
        if (Double.isNaN(value) || Double.isInfinite(value) || Double.compare(value, 0.0D) == 0) {
            return 0;
        }

        // double ceil = Math.ceil(value);
        final double floor = Math.floor(value);

        return (int) floor;
    }

    /**
     * Rundet ein Double Wert auf einen Integer Wert.<br>
     * Als {@link RoundingMode} wird HALF_UP verwendet.<br>
     * Ist der Wert NaN oder Infinite, wird 0 geliefert.
     */
    public static int roundToInt(final double value) {
        return (int) round(value, 0);
    }

    /**
     * Liefert den aufgerundeten Integer Wert des Double-Wertes.<br>
     * Ist der Wert NaN oder Infinite, wird 0.0D geliefert.
     */
    public static int roundUp(final double value) {
        if (Double.isNaN(value) || Double.isInfinite(value) || Double.compare(value, 0.0D) == 0) {
            return 0;
        }

        // double floor = Math.floor(value);
        final double ceil = Math.ceil(value);

        return (int) ceil;
    }

    /**
     * Calculates the smallest common multiple.
     */
    public static long scm(final long a, final long b) {
        return (a * b) / lcd(a, b);
    }

    /**
     * Subtrahiert (a-b) zwei Double (NULL safe). Sind beide NULL, wird NULL geliefert.
     */
    public static Double sub(final Double a, final Double b) {
        if (a == null && b == null) {
            return null;
        }

        final double aValue = (a == null) ? 0D : a;
        final double bValue = (b == null) ? 0D : b;

        return aValue - bValue;
    }

    /**
     * Subtrahiert (a-b) zwei Integers (NULL safe). Sind beide NULL, wird NULL geliefert.
     */
    public static Integer sub(final Integer a, final Integer b) {
        if (a == null && b == null) {
            return null;
        }

        final int aValue = (a == null) ? 0 : a;
        final int bValue = (b == null) ? 0 : b;

        return aValue - bValue;
    }

    /**
     * Subtrahiert (a-b) zwei Longs (NULL safe). Sind beide NULL, wird NULL geliefert.
     */
    public static Long sub(final Long a, final Long b) {
        if (a == null && b == null) {
            return null;
        }

        final long aValue = (a == null) ? 0L : a;
        final long bValue = (b == null) ? 0L : b;

        return aValue - bValue;
    }

    /**
     * Summiert (a+b) zwei Doubles (NULL safe). Sind beide NULL, wird NULL geliefert.
     */
    public static Double sum(final Double a, final Double b) {
        double aValue = (a == null) ? Double.NaN : a;
        double bValue = (b == null) ? Double.NaN : b;

        if (Double.isNaN(aValue) && Double.isNaN(bValue)) {
            return null;
        }

        aValue = (Double.isNaN(aValue)) ? 0D : aValue;
        bValue = (Double.isNaN(bValue)) ? 0D : bValue;

        return aValue + bValue;
    }

    /**
     * Summiert (a+b) zwei Integers (NULL safe). Sind beide NULL, wird NULL geliefert.
     */
    public static Integer sum(final Integer a, final Integer b) {
        if (a == null && b == null) {
            return null;
        }

        final int aValue = (a == null) ? 0 : a;
        final int bValue = (b == null) ? 0 : b;

        return aValue + bValue;
    }

    /**
     * Summiert (a+b) zwei Longs (NULL safe). Sind beide NULL, wird NULL geliefert.
     */
    public static Long sum(final Long a, final Long b) {
        if (a == null && b == null) {
            return null;
        }

        final long aValue = (a == null) ? 0L : a;
        final long bValue = (b == null) ? 0L : b;

        return aValue + bValue;
    }

    private ExtMath() {
        super();
    }
}
