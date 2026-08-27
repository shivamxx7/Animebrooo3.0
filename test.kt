fun main() {
    val a = mapOf("A" to listOf("a1", "a2"))
    val b = mapOf("B" to listOf("b1", "b2"))
    val c = a + b
    println(c.values.flatten())
}
