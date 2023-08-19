package ru.netology

class Noteservice : CrudService<Note> {
    private val notes: MutableList<Note> = arrayListOf()
    private val comments: MutableList<Comment> = arrayListOf()
    private val delComments: MutableList<Comment> = arrayListOf()
    private val users: MutableList<User> = arrayListOf()
    private var countNote: Long = 0
    private var countComment: Long = 0
    private var countUser: Long = 0

    override fun add(entity: Note): Int {
        val note = entity.copy(id = countNote++.toUInt())
        notes.add(note)
        return note.id.toInt()
    }

    override fun delete(id: UInt, userId: UInt): Int {
        if (notes.any()) {
            for (note in notes) {
                if (note.id == id) {
                    if (users.none { it.id == userId }) return 184      // пользователь не найден
                    return if (checkAccess(userId, note)) {
                        val note = notes.first { it.id == id }
                        notes.remove(note)
                        1                                               // успешно удалена
                    } else 181                                          // отказано в доступе
                }
            }
            return 180                                                  // заметка не найдена
        } else return 185                                               // нет ни одной заметки
    }

    override fun get(ownerId: UInt): Map<Int, List<Note>> {
        var key = 180                                                   // заметка не найдена
        val list = notes.filter { it.ownerId == ownerId }
        if (list.isNotEmpty()) key = 1                                  // успех
        return mapOf(key to list)
    }

    override fun getById(id: UInt): Map<Int, Note> {
        var key = 1
        val note = Note(
            0u, title = "Note not exist!",
            text = "Note not exist!", viewUrl = "Note not exist!"
        )
        if (notes.none { it.id == id }) {
            key = 180
            return mapOf(key to note)
        }
        return mapOf(key to notes.first { it.id == id })
    }

    override fun getFriendsNotes(id: UInt): Map<Int, List<Note>> {
        var key = 1
        var listNotesFriends: MutableList<Note> = arrayListOf()
        for (user in users) {
            if (user.id == id) {
                val friends = user.friendsUser                          // получаем список id друзей пользователей
                for (i in friends) {
                    var list = notes.filter { it.ownerId == i }
                    listNotesFriends.addAll(list)
                }
                key = 1
                return mapOf(key to listNotesFriends)
            }
        }
        key = 180
        return mapOf(key to listNotesFriends)
    }

    override fun restoreComment(id: UInt): Int {
        if (delComments.none { it.commentId == id }) return 186
        else {
            val com = delComments.first { it.commentId == id }
            delComments.remove(com)
            comments.add(com)
            val index = notes.indexOf(notes.first { it.id == com.noteId })
            if (index == -1) return 180
            val note = notes[index]
            var listcom = note.comments
            listcom.add(com)
            notes[index] = note.copy(comments = listcom)
            return 1
        }
    }

    override fun getComments(entity: Note): Map<Int, List<Comment>> {
        var key = 1
        val list = emptyList<Comment>()
        if (notes.none { it.id == entity.id }) {
            key = 180
            return mapOf(key to list)
        }
        return mapOf(key to notes.first { it.id == entity.id }.comments)
    }

    override fun editComment(entity: Note, id: Int, userId: UInt): Int {
        if (comments.none { it.commentId == id.toUInt() }) return 186       // Такого комментария не существует
        val comm = comments.first { it.commentId == id.toUInt() }
        if (!entity.canComment) return 182
        if (checkAccess(userId, comm)) {
            var index = notes.indexOf(notes.first{it.id == entity.id})
            notes[index] = entity
            index = comments.indexOf(comm)
            val listcom = entity.comments
            comments[index] = listcom.first{it.commentId == id.toUInt()}
            return 1                                                        // Успех
        } else return 183                                                   // отказано в доступе
    }

    override fun edit(entity: Note, userId: UInt): Int {
        return if (notes.none { it.id == entity.id }) 180                   // такой заметки нет
        else {
            val index = notes.indexOf(notes.find { it.id == entity.id })
            val note = notes[index]
            if (checkAccess(userId, note)) {
                notes[index] = entity.copy()
                1                                                           // успешно отредактировано
            } else 181                                                      // отказано в доступе
        }
    }

    override fun deleteComment(entity: Note, id: Int, userId: UInt): Int {
        if (notes.none { it.id == entity.id }) return 180                   // нет такой заметки
        if (comments.none { it.commentId == id.toUInt() }) return 186       // нет такого коментария
        val comment = comments.first { it.commentId == id.toUInt() }
        return if (checkAccess(userId, comment)) {
            delComments.add(comment)
            comments.remove(comment)
            var listcom = entity.comments
            listcom.remove(comment)
            notes[notes.indexOf(entity)] = entity.copy(comments = listcom)
            1                                                               // коментарий успешно удален
        } else 183                                                          // отказано в доступе
    }

    override fun createComment(entity: Note, comment: Comment): Int {
        return if (notes.none { it.id == entity.id }) 180                   // нет такой заметки
        else {
            val com = comment.copy(commentId = countComment++.toUInt(), noteId = entity.id)
            var listcom = entity.comments
            comments.add(com)
            listcom.add(com)
            notes[notes.indexOf(entity)] = entity.copy(comments = listcom)
            1
        }
    }

    fun getAllnotes(): List<Note> {
        return notes
    }

    fun addUser(user: User): Int {
        users.add(user.copy(id = countUser++.toUInt()))
        return users.size
    }

    fun getAllUser(): List<User> {
        return users
    }

    fun <T> checkAccess(id: UInt, obj: T): Boolean {
        var idOwner: Int = -1
        if (users.none { it.id == id }) {
            println("user $id not found")
            return false
        }
        val access = when (obj) {
            is Note -> {
                idOwner = obj.ownerId.toInt()
                obj.privacy
            }

            is Comment -> {
                idOwner = obj.ownerId.toInt()
                obj.commentPrivacy
            }

            else -> -1
        }
        return if (idOwner == -1) false
        else {
            val friends = users.first { it.id == idOwner.toUInt() }.friends        // получаем список друзей автора заметки/кометария
            when (access) {
                0 -> true                                                          // разрешено всем
                1 -> friends.contains(id)                                          // разрешено только друзьям
                2 -> {                                                             // разрешено друзьям друзей 
                    for (friend in friends) {
                        val index = users.indexOf(users.first{it.id == friend})
                        if (index != -1) {
                            val listFriends = users[index].friends
                            if (listFriends.contains(id)) return true
                        }
                    }
                    false
                }
                3 -> id == idOwner.toUInt()                                        // разрешено только автору
                else -> false
            }
        }
    }
}