package com.example.Automata

typealias matrix<T> = Array<Array<T>>

class Automata private constructor(
    val states: Array<State>,
    val outputAlphabet: List<OutputSymbol>,
    val inputAlphabet: List<InputSymbol>,
    val transitionFunction: (State, InputSymbol) -> State,
    val outputFunction: (State, InputSymbol) -> OutputSymbol
) {
    private val representations = mutableListOf<Representation>()
    private val representationsDelegate = RepresentationsDelegate(representations)
    private val table : Table by representationsDelegate




    fun react(initState: String, inputs: Iterable<String>) {
        states.forEach { print("$it,") }
        var curState = states.first { it.name == initState }
        println("init state: $curState ")
        for (input in inputs.asSequence().map { InputSymbol(it) }) {
            println(outputFunction(curState, input).symbol)
            curState = transitionFunction(curState, input)
        }
    }

    internal constructor(
        states: Set<State> = emptySet(),
        outputAlphabet: Set<OutputSymbol> = emptySet(),
        inputAlphabet: Set<InputSymbol> = emptySet(),
        representation: Representation
    ) : this(
        states.toTypedArray().apply { sortBy { it.label } },
        outputAlphabet.toList(),
        inputAlphabet.toList(),
        { state: State, input: InputSymbol -> representation.getNextState(state, input) },
        { state: State, input: InputSymbol -> representation.getOutput(state, input) },
    ) {
        println("adding $representation in reprs")
        representations.add(representation)
    }


    private class Index(val number: Int)

    private fun Int.toIndex() = Index(this)
    private class Partition(private val partition: Map<Index, List<State>>) {
        operator fun get(index: Index) = partition[index]
        val indexes = partition.keys
        val statesSets = partition.values
        val classes = partition.entries
    }

    private fun Table.createFirstPartition(): Partition {

        val outputToStates = HashMap<String, MutableList<State>>()
        for (state in states) {
            val outputEncoded = inputAlphabet.fold("") { acc, input -> acc + this[state].currentOutput[input] }
            if (!outputToStates.containsKey(outputEncoded)) {
                outputToStates[outputEncoded] = mutableListOf()
            }
            outputToStates[outputEncoded]!!.add(state)
        }

        var index = -1
        val res = outputToStates.mapKeys {
            index++.toIndex()
        }
        return Partition(res)
    }

    private fun Partition.getNextPartition(): Partition? {

        val indexToCode = HashMap<Index, String>()
        val stateIndex = mutableMapOf<State, Index>()
        for (index in indexes) {
            for (state in this[index]!!) {
                stateIndex[state] = index
            }
        }

        val partition = HashMap<Index, MutableList<State>>()

        val getCode = { state: State ->
            inputAlphabet.fold("") { acc, symbol ->
                acc + stateIndex[table[state].nextState[symbol]]
            }
        }

        for ((index, states) in classes) {
            val first = states.first()
            indexToCode[index] = getCode(first)
            partition[index] = mutableListOf()
        }

        var indexCounter = indexes.size
        var same = true
        for ((index, states) in classes) {
            val newIndexes = HashMap<String, Index>()
            for (state in states) {
                val code = getCode(state)
                if (code != indexToCode[index]) {
                    same = false
                    if (code !in newIndexes)
                        newIndexes[code] = indexCounter++.toIndex()
                    val newIndex = newIndexes[code]!!
                    if (newIndex !in partition)
                        partition[newIndex] = mutableListOf()
                    partition[newIndex]!!.add(state)
                } else {
                    partition[index]!!.add(state)
                }
            }
        }
        if (same) return null
        return Partition(partition)
    }

    fun getPk(k: Int): List<List<String>> {
        require(k >= 1)

        var currentPartition = table.createFirstPartition()
        var nextPartition = currentPartition.getNextPartition()
        var i = 1
        while (nextPartition != null && i < k) {
            currentPartition = nextPartition
            nextPartition = nextPartition.getNextPartition()
            i++
        }
        return currentPartition.statesSets.map { states1 -> states1.map { it.name } }
    }

    fun getPlast(): List<List<String>> {
        var currentPartition = table.createFirstPartition()
        var nextPartition = currentPartition.getNextPartition()
        while (nextPartition != null) {
            currentPartition = nextPartition
            nextPartition = nextPartition.getNextPartition()
        }
        return currentPartition.statesSets.map { states1 -> states1.map { it.name } }
    }

}


@JvmInline
value class InputSymbol(val symbol: String)

@JvmInline
value class OutputSymbol(val symbol: String)

data class State internal constructor(internal val label: Int, val name: String)

fun automata(init: Builder.() -> Unit) = Builder().apply(init).build()