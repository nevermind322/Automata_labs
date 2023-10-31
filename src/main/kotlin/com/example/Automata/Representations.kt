package com.example.Automata

import java.lang.IllegalStateException
import kotlin.reflect.KProperty

abstract class Representation(
    val states: List<State>,
    val inputs: List<InputSymbol>,
    val outputs: List<OutputSymbol>
) {

    abstract val n: Int;
    abstract fun getNextState(currentState: State, input: InputSymbol): State
    abstract fun getOutput(currentState: State, input: InputSymbol): OutputSymbol
    abstract fun check(): Boolean
}

class Matrix(
    private val matrix: matrix<List<Entry>>,
    states: List<State>,
    inputs: List<InputSymbol>,
    outputs: List<OutputSymbol>
) : Representation(states, inputs, outputs) {
    private val size = matrix.size

    init {
        for (row in matrix) {
            check(row.size == size)
        }
    }

    private val labelsToState = HashMap<Int, State>().apply { states.forEach { state -> put(state.label, state) } }

    private val columns = mutableMapOf<State, Array<Array<Entry>>>()

    data class Entry(val input: InputSymbol, val output: OutputSymbol)

    operator fun get(stateFrom: State) = matrix[stateFrom.label]
    operator fun get(stateFrom: State, stateTo: State) = matrix[stateFrom.label][stateTo.label]
    override val n: Int
        get() = matrix.size

    override fun getNextState(currentState: State, input: InputSymbol): State {
        for (i in this[currentState].indices) {
            for (entry in this[currentState][i]) {
                if (entry.input == input) return labelsToState[i]!!
            }
        }
        throw IllegalStateException()
    }

    override fun getOutput(currentState: State, input: InputSymbol): OutputSymbol {
        for (i in this[currentState].indices) {
            for (entry in this[currentState][i]) {
                if (entry.input == input) return entry.output
            }
        }
        throw IllegalStateException()
    }

    override fun check(): Boolean {
        TODO("Not yet implemented")
    }

}

class Table(
    private val table: Array<Row>,
    states: List<State>,
    inputs: List<InputSymbol>,
    outputs: List<OutputSymbol>
) : Representation(states, inputs, outputs) {
    class Row(val currentOutput: Map<InputSymbol, OutputSymbol>, val nextState: Map<InputSymbol, State>)

    override val n: Int = table.size

    override fun getNextState(currentState: State, input: InputSymbol) =
        table[currentState.label].nextState[input]!!

    override fun getOutput(currentState: State, input: InputSymbol) =
        table[currentState.label].currentOutput[input]!!

    override fun check(): Boolean {
        TODO("Not yet implemented")
    }

    operator fun get(state: State) = table[state.label]

}

class Graph(
    states: List<State>,
    inputs: List<InputSymbol>,
    outputs: List<OutputSymbol>
) : Representation(states, inputs, outputs) {
    override val n: Int
        get() = TODO("Not yet implemented")

    override fun getNextState(currentState: State, input: InputSymbol): State {
        TODO("Not yet implemented")
    }

    override fun getOutput(currentState: State, input: InputSymbol): OutputSymbol {
        TODO("Not yet implemented")
    }

    override fun check(): Boolean {
        TODO("Not yet implemented")
    }
}


class RepresentationsDelegate(val list: MutableList<Representation> = mutableListOf()) {

    inline operator fun <reified T : Representation> getValue(thisRef: Nothing?, property: KProperty<*>): T {
        print(list)
        for (el in list) {
            if (el is T) return el
        }
        throw IllegalStateException("There is no representation with type: ${T::class}")
    }

    inline operator fun <reified T : Representation> getValue(thisRef: Automata, property: KProperty<*>): T {
        for (el in list) {
            if (el is T) return el
        }
        throw IllegalStateException("There is no representation with type: ${T::class}")
    }

}


fun createMatrix(representation: Representation, states: List<State>, inputs: List<InputSymbol>, outputs: List<OutputSymbol>): Matrix {

    val matrix = matrix<List<Matrix.Entry>>(representation.n) { Array(representation.n) { mutableListOf() } }

    for (state in states)
        for (input in inputs)
            (matrix[state.label][representation.getNextState(state, input).label] as MutableList).add(
                Matrix.Entry(
                    input,
                    representation.getOutput(state, input)
                )
            )
    return Matrix(matrix, states, inputs, outputs)
}