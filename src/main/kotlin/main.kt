package ru.netology

fun main() {
    val notes = Noteservice()

    for (j in 1 .. 7) {
        for (i in 0..5) {
            notes.add(Note(ownerId = j.toUInt(), title = "Title note $i"))
        }
        notes.addUser(User(friends = hashSetOf(5u, 6u , 7u)))
    }
    val listnotes = notes.getAllnotes()
    val showNotes = fun() { for(note in listnotes) println(note) }
    showNotes()
    println()

//    notes.delete(1u, 5u)
//    showNotes()

//    val note = notes.getById(222u).values.elementAt(0).comments
//    println(notes.getById(222u).keys)
//    println(note)

//    for (note in notes.get(15u).values) {
//        println(notes.get(15u).keys)
//        for(nt in note) println(nt)
//    }

}

