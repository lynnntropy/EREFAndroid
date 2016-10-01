package rs.veselinromic.eref.android;

public class Util
{
    public static String toTitleCase(String input)
    {
        input = input.toLowerCase();
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;
        boolean encounteredParen = false;

        for (char c : input.toCharArray())
        {
            if (c == '(')
            {
                encounteredParen = true;
            }

            if (!encounteredParen)
            {
                if (Character.isSpaceChar(c))
                {
                    nextTitleCase = true;
                }
                else if (nextTitleCase)
                {
                    c = Character.toTitleCase(c);
                    nextTitleCase = false;
                }
            }
            else
            {
                c = Character.toUpperCase(c);
            }

            titleCase.append(c);
        }

        return titleCase.toString().replace(" I ", " i ");
    }
}