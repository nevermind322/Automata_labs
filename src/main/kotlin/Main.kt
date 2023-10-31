import com.example.Automata.Automata
import com.example.Automata.automata


val undAutomata = automata {
    input(listOf("a", "u", "n", " ", "*"))
    output(listOf("0", "1"))

    state("1") {
        "a" outputs "0"
        "a" translates "2"

        "u" outputs "0"
        "u" translates "2"

        "n" outputs "0"
        "n" translates "3"

        " " outputs "0"
        " " translates "1"

        "*" outputs "0"
        "*" translates "2"
    }

    state("2") {
        "a" outputs "0"
        "a" translates "2"

        "u" outputs "0"
        "u" translates "2"

        "n" outputs "0"
        "n" translates "3"

        " " outputs "0"
        " " translates "1"

        "*" outputs "0"
        "*" translates "2"
    }

    state("3") {
        "a" outputs "0"
        "a" translates "2"

        "u" outputs "0"
        "u" translates "4"

        "n" outputs "0"
        "n" translates "2"

        " " outputs "0"
        " " translates "1"

        "*" outputs "0"
        "*" translates "2"
    }

    state("4") {
        "a" outputs "0"
        "a" translates "5"

        "u" outputs "0"
        "u" translates "4"

        "n" outputs "0"
        "n" translates "4"

        " " outputs "0"
        " " translates "1"

        "*" outputs "0"
        "*" translates "4"
    }

    state("5") {
        "a" outputs "0"
        "a" translates "5"

        "u" outputs "0"
        "u" translates "4"

        "n" outputs "0"
        "n" translates "4"

        " " outputs "1"
        " " translates "1"

        "*" outputs "0"
        "*" translates "4"
    }
}

fun main() {

    val A7 = createA7()

    repeat(4) {
        A7.getPk(it+1).forEach{ list ->
            println(list)
        }
        println()
    }
}


fun createA7(): Automata =
    automata {

        val alpha = "alpha"
        val beta = "beta"
        val gamma = "gamma"
        val zero = "zero"
        val one = "one"

        val firstState = "1"
        val secondState = "2"
        val thirdState = "3"
        val fourthState = "4"
        val fifthState = "5"
        val sixthState = "6"
        val seventhState = "7"
        val eighthState = "8"
        val ninthState = "9"

        input(listOf(alpha, beta, gamma))
        output(listOf(zero, one))
        state(firstState) {
            alpha outputsTranslates (one to secondState)
            beta outputsTranslates (zero to secondState)
            gamma outputsTranslates (zero to fifthState)
        }
        state(secondState) {
            alpha outputsTranslates (zero to firstState)
            beta outputsTranslates (one to fourthState)
            gamma outputsTranslates (one to fourthState)
        }
        state(thirdState) {
            alpha outputsTranslates (one to secondState)
            beta outputsTranslates (zero to secondState)
            gamma outputsTranslates (zero to fifthState)
        }
        state(fourthState) {
            alpha outputsTranslates (zero to thirdState)
            beta outputsTranslates (one to secondState)
            gamma outputsTranslates (one to secondState)
        }
        state(fifthState) {
            alpha outputsTranslates (one to sixthState)
            beta outputsTranslates (zero to fourthState)
            gamma outputsTranslates (zero to thirdState)
        }
        state(sixthState) {
            alpha outputsTranslates (zero to eighthState)
            beta outputsTranslates (one to ninthState)
            gamma outputsTranslates (one to sixthState)
        }
        state(seventhState) {
            alpha outputsTranslates (one to sixthState)
            beta outputsTranslates (zero to secondState)
            gamma outputsTranslates (zero to eighthState)
        }
        state(eighthState) {
            alpha outputsTranslates (one to fourthState)
            beta outputsTranslates (zero to fourthState)
            gamma outputsTranslates (zero to seventhState)
        }
        state(ninthState) {
            alpha outputsTranslates (zero to seventhState)
            beta outputsTranslates (one to ninthState)
            gamma outputsTranslates (one to seventhState)
        }
    }


