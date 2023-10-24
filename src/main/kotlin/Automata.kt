class Automata private constructor(
    private val matrix: AutomataMatrix = emptyMatrix(),
    private val outputList: List<OutputSymbol> = emptyList(),
    private val inputList: List<InputSymbol> = emptyList()
) {
    companion object {
        private fun emptyMatrix() = AutomataMatrix()

        private class AutomataMatrix(private val matrix: Array<Array<Array<Entry>>> = emptyArray()) {

            private val size = matrix.size

            init {
                for (row in matrix) {
                    check(row.size == size)
                }
            }

            val states = Array(size) { it.toState() }

            private val columns = mutableMapOf<State, Array<Array<Entry>>>()

            data class Entry(val input: InputSymbol, val output: OutputSymbol)

            operator fun get(stateFrom: State, stateTo: State) = matrix[stateFrom.number][stateTo.number]

            fun getRow(state: State) = matrix[state.number]

            fun getColumn(state: State): Array<Array<Entry>> {
                if (!columns.containsKey(state)) columns[state] = Array(size) { matrix[it][state.number] }
                return columns[state]!!
            }
        }

        @JvmInline
        private value class InputSymbol(val symbol: String)

        @JvmInline
        private value class OutputSymbol(val symbol: String)

        @JvmInline
        private value class State(val number: Int)

        private fun Int.toState() = State(this)

        @JvmInline
        private value class Index(val number: Int)

        private fun Int.toIndex() = Index(this)

        private class TransitionTable(matrix: AutomataMatrix) {

            data class Row(val currentOutput: Map<InputSymbol, OutputSymbol>, val nextState: Map<InputSymbol, State>)

            private val table = HashMap<State, Row>()

            init {
                for (state in matrix.states) table[state] = Row(mutableMapOf(), mutableMapOf())
                for (row in matrix.states) for (column in matrix.states) {
                    val entries = matrix[row, column]
                    val state_from = row
                    val state_to = column
                    for ((input, output) in entries) {
                        (table[state_from]!!.currentOutput as MutableMap)[input] = output
                        (table[state_from]!!.nextState as MutableMap)[input] = state_to
                    }
                }
            }

            operator fun get(state: State) = table[state]

            val states = table.keys

            operator fun iterator(): Iterator<Row> = table.values.iterator()

        }

        private class Partition(private val partition: Map<Index, List<State>>) {
            operator fun get(index: Index) = partition[index]
            val indexes = partition.keys
            val statesSets = partition.values
            val classes = partition.entries


        }
    }


    private fun TransitionTable.createFirstPartition(): Partition {
        val stateIndexed = HashMap<State, Index>()

        val outputToStates = HashMap<String, MutableList<State>>()
        for (state in states) {
            val output_encoded = inputList.fold("") { acc, input -> acc + this[state]!!.currentOutput[input] }
            if (!outputToStates.containsKey(output_encoded)) {
                outputToStates[output_encoded] = mutableListOf()
            }
            outputToStates[output_encoded]!!.add(state)
        }

        var index = -1
        val res = outputToStates.mapKeys {
            index++.toIndex()
        }
        return Partition(res)
    }

    private val table = TransitionTable(matrix)

    /* private fun Partition.toStateIndexed(): MutableMap<State, Index> {
         val stateIndexed = mutableMapOf<State, Index>()
         for ((index, stateList) in this) {
             for (state in stateList) {
                 stateIndexed[state] = index
             }
         }
         return stateIndexed
     }*/

    private fun Partition.getNextPartition(): Partition? {

        val indexToCode = HashMap<Index, String>()
        val stateIndex = mutableMapOf<State, Index>()
        for (index in indexes) {
            for (state in this[index]!!) {
                stateIndex[state] = index
            }
        }

        val partition = HashMap<Index, List<State>>()

        val getCode = { state : State ->
            inputList.fold("") { acc, symbol ->
                acc + stateIndex[table[state]!!.nextState[symbol]]
            }
        }

        for ((index, states) in classes) {
            val first = states.first()
            indexToCode[index] = getCode(first)
        }

        for ((index, states) in classes) {
            for (state in states){
                val code = getCode(state)
                if (code != indexToCode[index])
                    TODO()
            }
        }

        return null
    }

    fun getPk(): List<List<Int>> {

        var currentPartition = table.createFirstPartition()
        var nextPartition = currentPartition.getNextPartition()

        while (nextPartition != null) {
            currentPartition = nextPartition
            nextPartition = nextPartition.getNextPartition()
        }
        return currentPartition.statesSets.map { it.map { it.number } }
    }
}



