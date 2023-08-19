package ru.netology

data class Note(
    val id: UInt = 0u,
    val ownerId: UInt = 0u,
    val title: String = "Title",
    val text: String = "text",
    val data: Int = 1692208369,
    val viewUrl: String = "note URL",
    val textWiki: String = "link WIKI",
    val canComment: Boolean = true,
    val privacy: Int = 0,
    val privacyView: String = "privacy note",
    val comments: MutableList<Comment> = arrayListOf(),
    val friendsUser: MutableList<UInt> = arrayListOf()
)

data class User(
    val id: UInt = 0u,
    val name: String = "User",
    val friends: HashSet<UInt> = hashSetOf()
) {
    var friendsUser: HashSet<UInt> = friends
}

data class Comment(
    val commentId: UInt = 0u,
    val noteId: UInt = 0u,
    val ownerId: UInt = 0u,
    val txtComment: String = "comment",
    val commentPrivacy: Int = 0,
    val privacyComment: String = "privacy comment"
)

interface CrudService<E> {
    fun add(entity: E): Int                                     // возвращает идентификатор созданной заметки
    fun delete(id: UInt, userId: UInt): Int                     // возвращает код ошибки или 1 - успех
    fun createComment(entity: E, comment: Comment): Int         // возвращает код ошибки или 1 - успех
    fun deleteComment(entity: E, id: Int, userId: UInt): Int    // возвращает код ошибки или 1 - успех
    fun edit(entity: E, userId: UInt): Int                      // возвращает код ошибки или 1 - успех
    fun editComment(entity: E, id: Int, userId: UInt): Int      // возвращает код ошибки или 1 - успех
    fun get(ownerId: UInt): Map<Int, List<E>>                   // в качестве ключа возвращает 1 - успех и список заметок пользователя или код ошибки и пустой список
    fun getById(id: UInt): Map<Int, E>                          // в качестве ключа возвращает 1 - успех и заметку или код ошибки и пустую заметку
    fun getComments(entity: E): Map<Int, List<Comment>>         // в качестве ключа возвращает 1 - успех и список коментариев к заметке или код ошибки и пустой список
    fun getFriendsNotes(id: UInt): Map<Int, List<E>>            // в качестве ключа возвращает 1 - успех и список заметок друзей пользователя или код ошибки и пустой список
    fun restoreComment(id: UInt): Int                           // возвращает код ошибки или 1 - успех
}
/*
Доступ к коментированию и редактрованию заметок
0 — все пользователи,
1 — только друзья,
2 — друзья и друзья друзей,
3 — только пользователь.
Коды ошибок
183     доступ к редактированию коментария запрещен
181     доступ к редактированию заметки запрещен
182     доступ к коментированию заметки запрещен
180     заметка не найдена

184     пользователь не найден
185     нет ни одной заметки
186     нет такого коментария
*/