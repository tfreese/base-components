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
public final class ExtMath
{
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
    public static final char[] CHAR_ARRAY =
    {
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'A',
            'B',
            'C',
            'D',
            'E',
            'F',
            'G',
            'H',
            'I',
            'J',
            'K',
            'L',
            'M',
            'N',
            'O',
            'P',
            'Q',
            'R',
            'S',
            'T',
            'U',
            'V',
            'W',
            'X',
            'Y',
            'Z'
    };
    /**
     *
     */
    public static final double LOG_E = Math.log(Math.E);

    /**
     * @param n long
     * @param m long
     *
     * @return long
     */
    public static long ackermann(final long n, final long m)
    {
        if (n == 0)
        {
            return m + 1;
        }
        else if (m == 0)
        {
            return ackermann(n - 1, 1);
        }
        else
        {
            return ackermann(n - 1, ackermann(n, m - 1));
        }
    }

    /**
     * Returns a Decimal-Value of a Value with a different Base.
     *
     * @param number The String which is to be converted
     * @param base The Base of the Value
     *
     * @return long
     */
    public static long base2Dec(final String number, final int base)
    {
        if ((base <= 1) || (base > 36))
        {
            return -1;
        }

        char[] numArray = number.trim().toCharArray();
        long result = 0;

        for (int i = 0; i < numArray.length; i++)
        {
            result += (Arrays.binarySearch(CHAR_ARRAY, numArray[numArray.length - 1 - i]) * Math.pow(base, i));
        }

        return result;
    }

    /**
     * Returns the Checksum of a Value.
     *
     * @param n long
     *
     * @return long
     */
    public static long checksum(final long n)
    {
        if (n == 0)
        {
            return 0;
        }

        return checksum(n / 10) + (n % 10);
    }

    /**
     * Returns a String with the new Baseformat of a Decimal-Value.
     *
     * @param value The Value which is to be converted
     * @param base the new Base of the Value
     *
     * @return String
     */
    public static String dec2Base(final long value, final int base)
    {
        if ((base <= 1) || (base > 36))
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        double tmp;
        long number = value;

        while (number != 0)
        {
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
     * Returns a String-Vector of Factors.
     *
     * @param bi BigInteger Value
     *
     * @return {@link List}
     */
    public static List<String> factors(final BigInteger bi)
    {
        return factors(bi, 100);
    }

    /**
     * Returns a String-Vector of Factors.
     *
     * @param value {@link BigInteger}
     * @param certainty Accuracy (Genauigkeit)
     *
     * @return {@link List}
     */
    public static List<String> factors(final BigInteger value, final int certainty)
    {
        BigInteger bi = value;

        List<String> list = new ArrayList<>();
        BigInteger n = BigInteger.valueOf(2);

        while (!bi.isProbablePrime(certainty))
        {
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
     *
     * @param roman {@link String}
     *
     * @return int
     */
    public static int getIntFromRoman(final String roman)
    {
        if (!isValidRomanNumber(roman))
        {
            throw new IllegalArgumentException("Dies \"" + roman + "\" ist keine gültige römische Zahl.");
        }

        char[] chars = roman.toUpperCase().toCharArray();
        char lastChar = ' ';
        int value = 0;

        for (int i = chars.length - 1; i >= 0; i--)
        {
            switch (chars[i])
            {
                case 'I':
                    value += ((lastChar == 'X') || (lastChar == 'V')) ? -1 : 1;
                    break;
                case 'V':
                    value += 5;
                    break;
                case 'X':
                    value += ((lastChar == 'C') || (lastChar == 'L')) ? -10 : 10;
                    break;
                case 'L':
                    value += 50;
                    break;
                case 'C':
                    value += ((lastChar == 'M') || (lastChar == 'D')) ? -100 : 100;
                    break;
                case 'D':
                    value += 500;
                    break;
                case 'M':
                    value += 1000;
                    break;
                default:
                    break;
            }

            lastChar = chars[i];
        }

        return value;
    }

    /**
     * Liefert die übergebene Zahl als römische Zahl zurück.
     *
     * @param value int
     *
     * @return {@link String}
     */
    public static String getRomanNumber(final int value)
    {
        if ((value < 1) || (value > 3999))
        {
            throw new IllegalArgumentException("Die eingegebene Zahl muss zwischen 1 und 3999 liegen!");
        }

        StringBuilder sb = new StringBuilder();
        int val = value;

        while ((val / 1000) >= 1)
        {
            sb.append("M");
            val -= 1000;
        }

        if ((val / 900) >= 1)
        {
            sb.append("CM");
            val -= 900;
        }

        if ((val / 500) >= 1)
        {
            sb.append("D");
            val -= 500;
        }

        if ((val / 400) >= 1)
        {
            sb.append("CD");
            val -= 400;
        }

        while ((val / 100) >= 1)
        {
            sb.append("C");
            val -= 100;
        }

        if ((val / 90) >= 1)
        {
            sb.append("XC");
            val -= 90;
        }

        if ((val / 50) >= 1)
        {
            sb.append("L");
            val -= 50;
        }

        if ((val / 40) >= 1)
        {
            sb.append("XL");
            val -= 40;
        }

        while ((val / 10) >= 1)
        {
            sb.append("X");
            val -= 10;
        }

        if ((val / 9) >= 1)
        {
            sb.append("IX");
            val -= 9;
        }

        if ((val / 5) >= 1)
        {
            sb.append("V");
            val -= 5;
        }

        if ((val / 4) >= 1)
        {
            sb.append("IV");
            val -= 4;
        }

        while (val >= 1)
        {
            sb.append("I");
            val -= 1;
        }

        return sb.toString();
    }

    /**
     * Liefert true, wenn der Wert Nachkommastellen hat.
     *
     * @param value double
     *
     * @return boolean
     */
    public static boolean hasFractionDigits(final double value)
    {
        if (Double.isNaN(value) || Double.isInfinite(value))
        {
            return false;
        }

        if ((value > Math.floor(value)) && (value < Math.ceil(value)))
        {
            return true;
        }

        return false;
    }

    /**
     * Checks if number is prime or not.
     *
     * @param number Value to be checked
     *
     * @return boolean
     */
    public static boolean isPrime(final long number)
    {
        boolean prim = true;

        if ((number % 2) == 0)
        {
            prim = false;
        }
        else
        {
            if (number > 3)
            {
                long counter;

                long root = (long) Math.sqrt(number);

                // Zahl finden, deren Quadrat etwas groesser
                // ist, als die zu pruefende Zahl
                if ((root % 2) == 0)
                {
                    root++;
                }
                else
                {
                    root += 2;
                }

                counter = 1;

                do
                {
                    counter++;

                    if ((number % counter) == 0)
                    {
                        prim = false;

                        break;
                    }
                }
                while (counter < root);
            }
        }

        if (number == 1)
        {
            prim = false;
        }

        if (number == 2)
        {
            prim = true;
        }

        return prim;
    }

    /**
     * Falls der übergebene {@link String} eine gültige römische Zahl ist wird <code>true</code> geliefert, sonst <code>false</code>.
     *
     * @param roman {@link String}
     *
     * @return boolean
     */
    public static boolean isValidRomanNumber(final String roman)
    {
        String pattern = "M{0,3}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})";

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
     *
     * @param a First Value
     * @param b Second Value
     *
     * @return long
     */
    public static long lcd(final long a, final long b)
    {
        if (b == 0)
        {
            return a;
        }

        return lcd(b, a % b);
    }

    /**
     * @param x double
     *
     * @return double
     */
    public static double ln(final double x)
    {
        return Math.log(x) / LOG_E;
    }

    /**
     * Normalisiert einen Wert (Wikipedia).
     *
     * @param value double
     * @param max double
     * @param min double
     *
     * @return double
     */
    public static double normalize(final double value, final double max, final double min)
    {
        return (value - min) / (max - min);
    }

    /**
     * Returns a String-Vector of Prime Factors.
     *
     * @param bi BigInteger Value
     *
     * @return {@link List}
     */
    public static List<String> primeFactors(final BigInteger bi)
    {
        return primeFactors(bi, 100);
    }

    /**
     * Returns a String-Vector of Prime Factors.
     *
     * @param value {@link BigInteger}
     * @param certainty Accuracy (Genauigkeit)
     *
     * @return {@link List}
     */
    public static List<String> primeFactors(final BigInteger value, final int certainty)
    {
        BigInteger bi = value;
        List<String> list = new ArrayList<>();
        BigInteger n = BigInteger.ONE;

        while (!bi.isProbablePrime(certainty))
        {
            do
            {
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
     * Skaliert einen Wert (Wikipedia).<br>
     *
     * @param value double, aktueller Wert
     * @param min double; min. aller Werte
     * @param max double; max. Wert aller Werte
     * @param minNorm double; neuer min. Wert
     * @param maxNorm double; neuer max. Wert
     *
     * @return double
     */
    public static double reScale(final double value, final double min, final double max, final double minNorm, final double maxNorm)
    {
        return minNorm + (((value - min) * (maxNorm - minNorm)) / (max - min));
    }

    /**
     * Rundet ein Double Wert auf eine bestimmte Anzahl Nachkommastellen.<br>
     * Als {@link RoundingMode} wird HALF_UP verwendet.<br>
     * Ist der Wert NaN oder Infinite wird 0.0D geliefert.
     *
     * @param value double
     * @param scale int Anzahl Nachkommastellen
     *
     * @return double
     */
    public static double round(final double value, final int scale)
    {
        return round(value, scale, RoundingMode.HALF_UP);
    }

    /**
     * Rundet ein Double Wert auf eine bestimmte Anzahl Nachkommastellen und des RoundingModes.<br>
     * Ist der Wert NaN oder Infinite wird 0.0D geliefert.
     *
     * @param value double
     * @param scale int Anzahl Nachkommastellen
     * @param roundingMode {@link RoundingMode}
     *
     * @return double
     */
    public static double round(final double value, final int scale, final RoundingMode roundingMode)
    {
        if (Double.isNaN(value) || Double.isInfinite(value) || (value == 0.0D))
        {
            return 0.0D;
        }

        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        bigDecimal = bigDecimal.setScale(scale, roundingMode);

        return bigDecimal.doubleValue();
    }

    /**
     * Liefert den abgerundeten Integer Wert des Double Wertes.<br>
     * Ist der Wert NaN oder Infinite wird 0.0D geliefert.
     *
     * @param value double
     *
     * @return int
     */
    public static int roundDown(final double value)
    {
        if (Double.isNaN(value) || Double.isInfinite(value) || (value == 0.0D))
        {
            return 0;
        }

        // double ceil = Math.ceil(value);
        double floor = Math.floor(value);

        return (int) floor;
    }

    /**
     * Rundet ein Double Wert auf einen Integer Wert.<br>
     * Als {@link RoundingMode} wird HALF_UP verwendet.<br>
     * Ist der Wert NaN oder Infinite wird 0 geliefert.
     *
     * @param value double
     *
     * @return int
     */
    public static int roundToInt(final double value)
    {
        return (int) round(value, 0);
    }

    /**
     * Liefert den aufgerundeten Integer Wert des Double Wertes.<br>
     * Ist der Wert NaN oder Infinite wird 0.0D geliefert.
     *
     * @param value double
     *
     * @return int
     */
    public static int roundUp(final double value)
    {
        if (Double.isNaN(value) || Double.isInfinite(value) || (value == 0.0D))
        {
            return 0;
        }

        // double floor = Math.floor(value);
        double ceil = Math.ceil(value);

        return (int) ceil;
    }

    /**
     * Calculates the smallest common multiple.
     *
     * @param a First Value
     * @param b Second Value
     *
     * @return long
     */
    public static long scm(final long a, final long b)
    {
        return ((a * b) / lcd(a, b));
    }

    /**
     * Subtrahiert (a-b) zwei Double (NULL safe). Sind beide NULL, wird NULL geliefert.
     *
     * @param a Double
     * @param b Double
     *
     * @return Double
     */
    public static Double sub(final Double a, final Double b)
    {
        if ((a == null) && (b == null))
        {
            return null;
        }

        double aValue = (a == null) ? 0 : a;
        double bValue = (b == null) ? 0 : b;

        return aValue - bValue;
    }

    /**
     * Subtrahiert (a-b) zwei Integers (NULL safe). Sind beide NULL, wird NULL geliefert.
     *
     * @param a Integer
     * @param b Integer
     *
     * @return Integer
     */
    public static Integer sub(final Integer a, final Integer b)
    {
        if ((a == null) && (b == null))
        {
            return null;
        }

        int aValue = (a == null) ? 0 : a;
        int bValue = (b == null) ? 0 : b;

        return aValue - bValue;
    }

    /**
     * Subtrahiert (a-b) zwei Longs (NULL safe). Sind beide NULL, wird NULL geliefert.
     *
     * @param a Long
     * @param b Long
     *
     * @return Long
     */
    public static Long sub(final Long a, final Long b)
    {
        if ((a == null) && (b == null))
        {
            return null;
        }

        long aValue = (a == null) ? 0 : a;
        long bValue = (b == null) ? 0 : b;

        return aValue - bValue;
    }

    /**
     * Summiert (a+b) zwei Doubles (NULL safe). Sind beide NULL, wird NULL geliefert.
     *
     * @param a Double
     * @param b Double
     *
     * @return Double
     */
    public static Double sum(final Double a, final Double b)
    {
        double aValue = (a == null) ? Double.NaN : a;
        double bValue = (b == null) ? Double.NaN : b;

        if (Double.isNaN(aValue) && Double.isNaN(bValue))
        {
            return null;
        }

        aValue = (Double.isNaN(aValue)) ? 0 : aValue;
        bValue = (Double.isNaN(bValue)) ? 0 : bValue;

        return aValue + bValue;
    }

    /**
     * Summiert (a+b) zwei Integers (NULL safe). Sind beide NULL, wird NULL geliefert.
     *
     * @param a Integer
     * @param b Integer
     *
     * @return Integer
     */
    public static Integer sum(final Integer a, final Integer b)
    {
        if ((a == null) && (b == null))
        {
            return null;
        }

        int aValue = (a == null) ? 0 : a;
        int bValue = (b == null) ? 0 : b;

        return aValue + bValue;
    }

    /**
     * Summiert (a+b) zwei Longs (NULL safe). Sind beide NULL, wird NULL geliefert.
     *
     * @param a Long
     * @param b Long
     *
     * @return Long
     */
    public static Long sum(final Long a, final Long b)
    {
        if ((a == null) && (b == null))
        {
            return null;
        }

        long aValue = (a == null) ? 0 : a;
        long bValue = (b == null) ? 0 : b;

        return aValue + bValue;
    }

    /**
     * Erstellt ein neues {@link ExtMath} Object.
     */
    private ExtMath()
    {
        super();
    }
}
