package rs.veselinromic.eref.android

object Util
{
    fun toTitleCase(input: String): String
    {
        var input = input
        input = input.toLowerCase()
        val titleCase = StringBuilder()
        var nextTitleCase = true
        var encounteredParen = false

        for (character in input.toCharArray())
        {
            var c = character

            if (c == '(')
            {
                encounteredParen = true
            }

            if (!encounteredParen)
            {
                if (Character.isSpaceChar(c))
                {
                    nextTitleCase = true
                }
                else if (nextTitleCase)
                {
                    c = Character.toTitleCase(c)
                    nextTitleCase = false
                }
            }
            else
            {
                c = Character.toUpperCase(c)
            }

            titleCase.append(c)
        }

        return titleCase.toString().replace(" I ", " i ")
    }
}