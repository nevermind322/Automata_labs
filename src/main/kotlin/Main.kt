import kotlin.random.Random.Default.nextInt

typealias IntMatrix = Array<IntArray>


fun main() {
    print("n=")

    var n = readln().toIntOrNull()

    while (n == null || n <= 0) {
        print("Ошибка. Введите n опять: ")
        n = readln().toIntOrNull()
    }

    print("Случайно или с клавиатуры?(1, 2, по умолчанию 1): ")
    var matr: IntMatrix = emptyArray()
    var created = false
    val number = readln()
    while (!created)
        created = when (number) {
            "1" -> {
                matr = createRandomMatrix(n)
                true
            }

            "2" -> {
                try {
                    matr = readMatrix(n)
                    true
                } catch (e: Exception) {
                    println("Ошибка! Вводите все заново!")
                    false
                }
            }

            else -> {
                matr = createRandomMatrix(n)
                true
            }
        }


    println("Матрица:")
    printMatrix(matr)

    println("Количество изоморфных: ${countIsomorfic(n)}")
    println("Изоморфный:")
    printMatrix(createIsomorfic(matr))

    val res = countPrehod(matr)

    if (res.isEmpty()) println("Преходящих нет")
    else {
        print("Преходящие: ")
        println(res.map { it + 1 })
    }
    print("Еще?:")
    if (readln() == "Да") main()
    else return

}

fun printMatrix(matr: IntMatrix) {
    for (row in matr) {
        for (el in row) print("$el ")
        println()
    }

}

fun readMatrix(n: Int): IntMatrix =
    Array(n) { readln().split(" ").map { it.toInt() }.toIntArray() }

fun createRandomMatrix(n: Int) = Array(n) { IntArray(n) { if (nextInt() % 2 == 0) 0 else 1 } }


fun createIsomorfic(matr: IntMatrix): IntMatrix {
    if (matr.size == 1) return matr
    val random = nextInt(from = 1, until = matr.size)

    val size = matr.size
    val res = mutableListOf<IntArray>()

    res.add(IntArray(size) { matr[random][it] })
    for (i in 1..<size) {
        if (i != random) res.add(IntArray(size) { matr[i][it] })
        else res.add(IntArray(size) { matr[0][it] })
    }

    for (i in 0..<size) {
        val temp = res[i][0]
        res[i][0] = res[i][random]
        res[i][random] = temp
    }

    return res.toTypedArray()
}

fun countIsomorfic(n: Int): Int {
    var res = 1
    for (i in 2..n) res *= i
    return res
}

fun countTupic(matr: IntMatrix): List<Int> {

    val res = mutableListOf<Int>()

    for (i in matr.indices) {
        if (checkRow(i, matr[i])) res.add(i)
    }
    return res
}


fun countPrehod(matr: IntMatrix): List<Int> {

    val res = mutableListOf<Int>()
    var flag = true
    for (i in matr.indices) {
        flag = true
        for (j in matr.indices) {
            if (i != j && matr[j][i] != 0) flag = false

        }
        if (flag) res.add(i)
    }
    return res
}

fun checkRow(i: Int, row: IntArray): Boolean {
    for (j in row.indices) {
        if (i != j && row[j] != 0) return false
    }
    return true
}


