package com.example.Automata

class Builder {
    inner class StateBuilder(private val name: String) {

        private var stateLabel = stateLabels[name]!!

        infix fun String.outputs(output: String) {
            table[stateLabel].first[this] = output
        }

        infix fun String.translates(nextState: String) {
            table[stateLabel].second[this] = nextState
        }

        infix fun String.outputsTranslates(pair: Pair<String, String>) {
            this outputs pair.first
            this translates pair.second
        }

    }

    companion object;

    private fun Pair<MutableMap<String, String>, MutableMap<String, String>>.toRow(): Table.Row {
        val output = first.mapKeys { InputSymbol(it.key) }.mapValues { OutputSymbol(it.value) }
        val nextState = second.mapKeys { InputSymbol(it.key) }.mapValues { State(stateLabels[it.value]!!, it.value) }
        return Table.Row(output, nextState)
    }

    private val table = mutableListOf<Pair<MutableMap<String, String>, MutableMap<String, String>>>()
    private var currentStateNumber = 0
    private val stateLabels = hashMapOf<String, Int>()
    private val outputAlphabet: MutableSet<String> = hashSetOf()
    private val inputAlphabet: MutableSet<String> = hashSetOf()

    fun state(name: String, init: StateBuilder.() -> Unit) {
        if (name !in stateLabels) {
            stateLabels[name] = currentStateNumber
            table.add(hashMapOf<String, String>() to hashMapOf())
            currentStateNumber += 1
        }

        StateBuilder(name).init()
    }

    fun input(inputs: Collection<String>) {
        for (symbol in inputs) input(symbol)
    }

    fun output(outputs: Collection<String>) {
        for (symbol in outputs) output(symbol)
    }

    fun output(symbol: String) {
        if (symbol !in outputAlphabet) {
            outputAlphabet.add(symbol)
        }
    }

    fun input(symbol: String) {
        if (symbol !in inputAlphabet) {
            inputAlphabet.add(symbol)
        }
    }

    internal fun build(): Automata {
        val rows = table.map { it.toRow() }.toTypedArray()
        val states = stateLabels.map { State(it.value, it.key) }
        val inputs = inputAlphabet.map { InputSymbol(it) }
        val outputs = outputAlphabet.map { OutputSymbol(it) }
        val transitionTable = Table(rows, states, inputs, outputs)
        return Automata(states.toSet(), outputs.toSet(), inputs.toSet(), transitionTable)

    }
}

fun Builder.Companion.fromMatrix(matrix: Collection<Collection<Collection<Pair<String, String>>>>): Automata =
    automata {
        for ((i, row) in matrix.withIndex())
            for ((j, entry) in row.withIndex())
                for ((k, pair) in entry.withIndex()) {
                    val (_input, _output) = pair
                    input(_input)
                    output(_output)
                    state(i.toString()) {
                        _input outputsTranslates (_output to j.toString())
                    }
                }
    }
